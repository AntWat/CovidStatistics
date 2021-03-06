package com.ant_waters.covidstatistics.model

import android.content.Context
import android.util.Log
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.Utils.daysDiff
import com.ant_waters.covidstatistics.Utils.SimpleTable2
import java.util.*
import com.ant_waters.covidstatistics.Utils.readCsv
import com.ant_waters.covidstatistics.database.country
import com.ant_waters.covidstatistics.database.country_data
import com.ant_waters.covidstatistics.database.covid_data
import com.ant_waters.dbroomtest.database.CovidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import kotlin.math.ceil
import com.ant_waters.covidstatistics.enDataLoaded

/**
 * The main class for reading from the database and storing references to the data classes
 */
class DataManager {
    companion object {
        lateinit var DateStart: Date
        lateinit var DateEnd: Date

        private var _continents = mutableListOf<Continent2>()
        val Continents get() = this._continents

        val _continentsByName = mutableMapOf<String, Continent2>()
        val ContinentsByName: Map<String, Continent2>
            get() {
                return _continentsByName.toMap()
            }

        public fun continentFromName(continentName: String) : Continent2? {
            if ((continentName != null) && continentName != "") {
                return DataManager.ContinentsByName[continentName]!!
            }
            return null
        }


        private var _countries = mutableListOf<Country2>()
        val Countries get() = this._countries

        val _countriesByName = mutableMapOf<String, Country2>()
        val CountriesByName: Map<String, Country2>
            get() {
                return _countriesByName.toMap()
            }

        public fun AddCountry(c : Country2) {
            _countries.add(c)
            _countriesByName.put(c.name, c)
            _countryAggregates.AddCountry(c)
            _countries.sortBy { it.name }
        }
        public fun DeleteCountry(c : Country2) {
            _countries.remove(c)
            _countriesByName.remove(c.name)
            _countryAggregates.RemoveCountry(c)
            _countries.sortBy { it.name }
        }

        private var _dailyCovidsByDate = listOf<Pair<Date, MutableList<DailyCovid>>>()
        val DailyCovidsByDate get() = this._dailyCovidsByDate

        private var _countryAggregates : CountryAggregates = CountryAggregates()
        val CountryAggregates get() = this._countryAggregates

        val PopulationScaler = 100000      // cases are usually reported per 100k of population
        val PopulationScalerLabel = "100k"

        val AggregationPeriodInDays = 10     // cases are grouped into periods to reduce number of data points
        val AggregationPeriodLabel = "10 days"

        private var _covidCasesTable = SimpleTable2<Date, Int>()
        val CovidCasesTable get() = this._covidCasesTable

        private var _covidDeathsTable = SimpleTable2<Date, Int>()
        val CovidDeathsTable get() = this._covidDeathsTable

        private var _proportionalCovidCasesTable = SimpleTable2<Date, Double>()
        val ProportionalCovidCasesTable get() = this._proportionalCovidCasesTable

        private var _proportionalCovidDeathsTable = SimpleTable2<Date, Double>()
        val ProportionalCovidDeathsTable get() = this._proportionalCovidDeathsTable

        val MinPopulationForRanking = 1000000
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    suspend fun loadData(context: Context, onDataLoaded:(enDataLoaded)->Unit): Boolean {
        return withContext(Dispatchers.IO)
        {loadDataFromDatabase(context, onDataLoaded)}
        //return LoadDataFromCsv(context)
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private fun loadDataFromDatabase(context: Context, onDataLoaded:(enDataLoaded)->Unit): Boolean {
        Log.i(MainViewModel.LOG_TAG, "LoadDataFromDatabase: Started")

        try {
            val covidDatabase = CovidDatabase.getDatabase(context)

            // ---------------------------
            val dbContinents = covidDatabase.continentDao().getAll()

            for (dbcontinent in dbContinents) {
                val continent = Continent2(dbcontinent)
                _continents.add(continent)
                _continentsByName.put(continent.name, continent)
            }

            // ---------------------------
            Log.i(MainViewModel.LOG_TAG, "Loading countries")
            val dbCountries = covidDatabase.countryDao().getAll()

            val mapCountriesByName = mutableMapOf</*name*/String, Country2>()
            val mapCountriesByGeoId = mutableMapOf</*geoId*/String, Country2>()

            for (dbc in dbCountries)
            {
                val cdata: country_data? = covidDatabase.country_dataDao().findByGeoId(dbc.geoId)

                val c = Country2(dbc, cdata?.population_2019?:0)
                _countries.add(c)
                _countriesByName.put(c.name, c)
                mapCountriesByName.put(dbc.name!!, c)
                mapCountriesByGeoId.put(dbc.geoId!!, c)
            }
            _countries.sortBy { it.name }

            onDataLoaded(enDataLoaded.CountriesOnly)

            // ---------------------------
            Log.i(MainViewModel.LOG_TAG, "Loading daily data")
            val dbCovid_data = covidDatabase.covid_dataDao().getAll()

            val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

            val columnMap = mutableMapOf<Country2, Int>()
            val rowMap = mutableMapOf<Date, Int>()
            var headers = mutableListOf<String>()       // Includes blank row header
            headers.add("")
            var casesRows = mutableListOf<Pair<Date, MutableList<Int/*Value*/>>>()
            var deathsRows = mutableListOf<Pair<Date, MutableList<Int/*Value*/>>>()
            var proportionalCasesRows = mutableListOf<Pair<Date, MutableList<Double/*Value*/>>>()
            var proportionalDeathsRows = mutableListOf<Pair<Date, MutableList<Double/*Value*/>>>()

            var dateStart: Date? = null
            var dateEnd: Date? = null

            var aggregatedDate: Date? = null

            for (dbcd in dbCovid_data)
            {
                if (mapCountriesByGeoId.containsKey(dbcd.geoId))
                {
                    val d = SimpleDateFormat("yyyy-MM-dd").parse(dbcd.dateRep)

                    var diffInDays:Long = AggregationPeriodInDays.toLong()+1
                    if (aggregatedDate != null) {
                        diffInDays = daysDiff(d, aggregatedDate)
                    }

                    if (diffInDays>=AggregationPeriodInDays) {
                        aggregatedDate = d
                        if (!rowMap.containsKey(d)) {
                            casesRows.add(Pair<Date, MutableList<Int>>(d, mutableListOf<Int>()))
                            deathsRows.add(Pair<Date, MutableList<Int>>(d, mutableListOf<Int>()))
                            proportionalCasesRows.add(Pair<Date, MutableList<Double>>(d, mutableListOf<Double>()))
                            proportionalDeathsRows.add(Pair<Date, MutableList<Double>>(d, mutableListOf<Double>()))

                            val newRowIndex: Int = casesRows.size - 1
                            rowMap.put(d, newRowIndex)

                            for (c in 1..headers.size - 1) {
                                casesRows[newRowIndex].second.add(0)
                                deathsRows[newRowIndex].second.add(0)
                                proportionalCasesRows[newRowIndex].second.add(0.0)
                                proportionalDeathsRows[newRowIndex].second.add(0.0)
                            }
                        }
                    }

                    if ( (dateStart == null) || (d < dateStart) ) { dateStart = d }
                    if ( (dateEnd == null) || (d > dateEnd) ) { dateEnd = d }

                    if (!dailyCovidsByDate.containsKey(d)) {
                        dailyCovidsByDate.put(d, mutableListOf<DailyCovid>())
                    }

                    // --------------------
                    val c: Country2 = mapCountriesByGeoId[dbcd.geoId]!!

                    if (!columnMap.containsKey(c))
                    {
                        for (r in casesRows) { r.second.add(0) }
                        for (r in deathsRows) { r.second.add(0) }
                        for (r in proportionalCasesRows) { r.second.add(0.0) }
                        for (r in proportionalDeathsRows) { r.second.add(0.0) }

                        headers.add(c.name)
                        columnMap.put(c, headers.size-2)
                    }

                    // --------------------
                    val cases:Int = dbcd.cases?:0
                    val deaths:Int = dbcd.deaths?:0

                    dailyCovidsByDate[d]!!.add(DailyCovid(c, d, cases, deaths))

                    casesRows[rowMap[aggregatedDate]!!].second[columnMap[c]!!] += cases
                    deathsRows[rowMap[aggregatedDate]!!].second[columnMap[c]!!] += deaths
                    proportionalCasesRows[rowMap[aggregatedDate]!!].second[columnMap[c]!!] += (cases.toDouble() * DataManager.PopulationScaler.toDouble() / c.popData2019)
                    proportionalDeathsRows[rowMap[aggregatedDate]!!].second[columnMap[c]!!] += (deaths.toDouble() * DataManager.PopulationScaler.toDouble() / c.popData2019)

                }
            }

            // ---------------------------
            DateStart = dateStart!!
            DateEnd = dateEnd!!
            _dailyCovidsByDate  = dailyCovidsByDate.toList()

            MainViewModel.DisplayOptions.startDate = DateStart
            MainViewModel.DisplayOptions.endDate = DateEnd

            _covidCasesTable.addHeaders(headers)
            for (p in casesRows) {
                _covidCasesTable.addRow(p.first, p.second)
            }

            _covidDeathsTable.addHeaders(headers)
            for (p in deathsRows) {
                _covidDeathsTable.addRow(p.first, p.second)
            }

            _proportionalCovidCasesTable.addHeaders(headers)
            for (p in proportionalCasesRows) {
                _proportionalCovidCasesTable.addRow(p.first, p.second)
            }

            _proportionalCovidDeathsTable.addHeaders(headers)
            for (p in proportionalDeathsRows) {
                _proportionalCovidDeathsTable.addRow(p.first, p.second)
            }


            // ---------------------------
            Log.i(MainViewModel.LOG_TAG, "Creating aggregates")
            _countryAggregates.setData(DateStart, DateEnd, _dailyCovidsByDate, _countries)

            // ---------------------------
            Log.i(MainViewModel.LOG_TAG, "LoadDataFromDatabase: Finished")
            onDataLoaded(enDataLoaded.All)
            return true
        } catch (ex: Exception) {
            Log.e(MainViewModel.LOG_TAG, "Error: ${ex.message}")
            // TODO: Errorhandling?
            return false
        }
        finally {
        }
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Test data, no longer used
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

//    private fun LoadDataFromCsv(context: Context): Boolean {
//        try {
//            val countries = LoadCountries_HardCoded()
//
//            _dailyCovidsByDate = LoadCasesFromCsvButMakeUpDeaths(context, countries).toList()
//
//            _countryAggregates.setData(DateStart, DateEnd, _dailyCovidsByDate, countries)
//            return true
//        } catch (ex: Exception) {
//            // TODO: Errorhandling or logging
//            return false
//        }
//    }
//
//
//    // Real cases numbers are read from a csv file but deaths are made up in the method below
//    private fun LoadCasesFromCsvButMakeUpDeaths(context: Context, countries: MutableMap</*name*/String, Country2>)
//                                            : MutableMap<Date, MutableList<DailyCovid>> {
//        try {
//            val inputStream: InputStream = context.assets.open("ECPDC_CovidData1.csv")
//            val csv = readCsv(inputStream)
//
//            val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()
//
//            for (iRow in 0..csv.Rows.count()-1) {
//                val row = csv.Rows[iRow]
//                val d = SimpleDateFormat("dd/MM/yyyy").parse(row[0])
//                if (iRow == 0) {
//                    DateStart = d
//                } else if (iRow == csv.Rows.count()-1) {
//                    DateEnd = d
//                }
//
//                val daylies = mutableListOf<DailyCovid>()
//
//                for (i:Int in 1..row.count()-1) {
//                    val c: Country2? = countries[csv.Headers[i]]
//                    if (c == null)
//                    {
//                        Log.e(MainViewModel.LOG_TAG,"Header is not a recognised country: '${csv.Headers[i]}'")
//                        //throw Exception("Header is not a recognised country: '${csv.Headers[i]}'")
//                    }
//                    else {
//                        val numCases:Int = if (row[i] == "") 0 else row[i].toInt()
//                        daylies.add(DailyCovid(c, d,
//                            numCases,
//                            /*Make up deaths*/ceil(numCases * .01).toInt()))
//                    }
//                }
//                dailyCovidsByDate.put(d, daylies)
//            }
//
//            return dailyCovidsByDate
//        }
//        catch (ex: Exception)
//        {
//            throw(ex)
//        }
//    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Update database
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // Returns: Error message if fail, otherwise ""
    suspend fun UpdateDatabase(newCountrys: List<Country2>?, newDailyCovids: List<DailyCovid>?,
                   modifiedCountrys: List<Country2>?, modifiedDailyCovids: List<DailyCovid>?,
                   deletedCountrys: List<Country2>?, deletedDailyCovids: List<DailyCovid>?,
                   context: Context): String {
        return withContext(Dispatchers.IO)
        {UpdateDatabase2(newCountrys, newDailyCovids, modifiedCountrys, modifiedDailyCovids,
                                        deletedCountrys, deletedDailyCovids, context)}
    }

    suspend private fun UpdateDatabase2(
                    newCountrys: List<Country2>?, newDailyCovids: List<DailyCovid>?,
                    modifiedCountrys: List<Country2>?, modifiedDailyCovids: List<DailyCovid>?,
                    deletedCountrys: List<Country2>?, deletedDailyCovids: List<DailyCovid>?,
                    context: Context): String {
        Log.i(MainViewModel.LOG_TAG, "UpdateDatabase2: Started")
        try {
            var errMsg = ""
            val covidDatabase = CovidDatabase.getDatabase(context)

            errMsg = InsertToDatabase(newCountrys, newDailyCovids, covidDatabase)
            if (errMsg.length > 0) { return errMsg }

            errMsg = ModifyDatabaseItems(modifiedCountrys, modifiedDailyCovids, covidDatabase)
            if (errMsg.length > 0) { return errMsg }

            errMsg = DeleteFromDatabase(deletedCountrys, deletedDailyCovids, covidDatabase)
            if (errMsg.length > 0) { return errMsg }

            Log.i(MainViewModel.LOG_TAG, "InsertToDatabase: Finished")
            return  ""
        } catch (ex: Exception) {
            Log.e(MainViewModel.LOG_TAG, "UpdateDatabase2 Error: ${ex.message}")
            return ex.message.toString()
        }
    }

    suspend private fun InsertToDatabase(newCountry2s: List<Country2>?, newDailyCovids: List<DailyCovid>?,
                            covidDatabase: CovidDatabase): String {
        Log.i(MainViewModel.LOG_TAG, "InsertToDatabase: Started")
        try {
            if ((newCountry2s != null) && (newCountry2s.count() > 0)) {
                val newCountrys = mutableListOf<country>()
                val newCountry_datas = mutableListOf<country_data>()

                for (c in newCountry2s) {
                    newCountrys.add(country(c.geoId, c.countryCode, c.name, c.continent!!.name))
                    newCountry_datas.add(
                        country_data(c.geoId, c.popData2019, 1 /*TODO: data_source assumed as 1 for now*/))
                }

                covidDatabase.countryDao().insertAll(*newCountrys.toTypedArray<country>())
                covidDatabase.country_dataDao().insertAll(*newCountry_datas.toTypedArray<country_data>())
            }

            if ((newDailyCovids != null) && (newDailyCovids.count() > 0)) {
                val newCovid_datas = mutableListOf<covid_data>()

                for (dc in newDailyCovids) {
                    val dateAsString: String = SimpleDateFormat("dd-MMM-yy").format(dc.date)
                    newCovid_datas.add(
                        covid_data(dateAsString, dc.country.geoId, dc.covidCases, dc.covidDeaths,
                            0.0 /* Not using Cumulative_number_for_14_days_of_COVID_19_cases_per_100000 */))
                }

                covidDatabase.covid_dataDao().insertAll(*newCovid_datas.toTypedArray<covid_data>())
            }

            Log.i(MainViewModel.LOG_TAG, "InsertToDatabase: Finished")
            return ""
        } catch (ex: Exception) {
            Log.e(MainViewModel.LOG_TAG, "InsertToDatabase Error: ${ex.message}")
            return ex.message.toString()
        }
    }

    suspend private fun ModifyDatabaseItems(modifiedCountrys: List<Country2>?, modifiedDailyCovids: List<DailyCovid>?,
                                         covidDatabase: CovidDatabase): String {
        Log.i(MainViewModel.LOG_TAG, "ModifyDatabaseItems: Started")
        try {
            if ((modifiedCountrys != null) && (modifiedCountrys.count() > 0)) {
                for (modC in modifiedCountrys) {
                    covidDatabase.countryDao().update(
                        country(modC.geoId, modC.countryCode, modC.name, modC.continent!!.name))
                    covidDatabase.country_dataDao().update(
                        country_data(modC.geoId, modC.popData2019, 1 /*TODO: data_source assumed as 1 for now*/))
                }
            }

            if ((modifiedDailyCovids != null) && (modifiedDailyCovids.count() > 0)) {
                for (dc in modifiedDailyCovids) {
                    val dateAsString: String = SimpleDateFormat("dd-MMM-yy").format(dc.date)
                    covidDatabase.covid_dataDao().update(
                        covid_data(dateAsString, dc.country.geoId, dc.covidCases, dc.covidDeaths,
                            0.0 /* Not using Cumulative_number_for_14_days_of_COVID_19_cases_per_100000 */))
                }
            }

            Log.i(MainViewModel.LOG_TAG, "ModifyDatabaseItems: Finished")
            return ""
        } catch (ex: Exception) {
            Log.e(MainViewModel.LOG_TAG, "ModifyDatabaseItems Error: ${ex.message}")
            return ex.message.toString()
        }
    }

    suspend private fun DeleteFromDatabase(deletedCountrys: List<Country2>?, deletedDailyCovids: List<DailyCovid>?,
                                         covidDatabase: CovidDatabase): String {
        Log.i(MainViewModel.LOG_TAG, "DeleteFromDatabase: Started")
        try {
            if ((deletedCountrys != null) && (deletedCountrys.count() > 0)) {
                for (c in deletedCountrys) {
                    // Delete data first to ensure referential integrity
                    covidDatabase.country_dataDao().delete(
                        country_data(c.geoId, 0, 0))
                    covidDatabase.countryDao().delete(country(c.geoId, "", "", ""))
                }
            }

            if ((deletedDailyCovids != null) && (deletedDailyCovids.count() > 0)) {
                val deletingCovid_datas = mutableListOf<covid_data>()

                for (dc in deletedDailyCovids) {
                    val dateAsString: String = SimpleDateFormat("dd-MMM-yy").format(dc.date)
                    for (dbDc in covidDatabase.covid_dataDao()
                                    .loadAllByGeoIdAndDate(dc.country.geoId, dateAsString)) {
                        deletingCovid_datas.add(dbDc)
                    }
                }

                for (dbDc in deletingCovid_datas) {
                    covidDatabase.covid_dataDao().delete(dbDc)
                }
            }

            Log.i(MainViewModel.LOG_TAG, "DeleteFromDatabase: Finished")
            return ""
        } catch (ex: Exception) {
            Log.e(MainViewModel.LOG_TAG, "DeleteFromDatabase Error: ${ex.message}")
            return ex.message.toString()
        }
    }


}