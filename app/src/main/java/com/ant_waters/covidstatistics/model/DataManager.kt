package com.ant_waters.covidstatistics.model

import android.content.Context
import android.util.Log
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.Utils.DaysDiff
import com.ant_waters.covidstatistics.Utils.SimpleTable2
import java.util.*
import com.ant_waters.covidstatistics.Utils.readCsv
import com.ant_waters.covidstatistics.database.country_data
import com.ant_waters.dbroomtest.database.CovidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import kotlin.math.ceil
import com.ant_waters.covidstatistics.enDataLoaded

// The main class for reading from the database and storing references to the data classes
class DataManager {
    companion object {
        lateinit var DateStart: Date
        lateinit var DateEnd: Date

        private var _countries = mutableListOf<Country2>()
        val Countries get() = this._countries

        private var _dailyCovidsByDate = listOf<Pair<Date, MutableList<DailyCovid>>>()
        val DailyCovidsByDate get() = this._dailyCovidsByDate

        private var _countryAggregates: CountryAggregates = CountryAggregates()
        val CountryAggregates get() = this._countryAggregates

        val PopulationScaler = 100000      // cases are usually reported per 100k of population
        val PopulationScalerLabel = "100k"

        val AggregationPeriodInDays = 10     // cases are grouped into periods to reduce number of data points
        val AggregationPeriodLabel = "10 days"

        private var _covidCasesTable = SimpleTable2<Date, Int>()
        val CovidCasesTable get() = this._covidCasesTable

        private var _covidDeathsTable = SimpleTable2<Date, Int>()
        val CovidDeathsTable get() = this._covidDeathsTable

        val MinPopulationForRanking = 1000000
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    suspend fun LoadData(context: Context, onDataLoaded:(enDataLoaded)->Unit): Boolean {
        return withContext(Dispatchers.IO)
        {LoadDataFromDatabase(context, onDataLoaded)}
        //return LoadDataFromCsv(context)
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private fun LoadDataFromDatabase(context: Context, onDataLoaded:(enDataLoaded)->Unit): Boolean {
        Log.i(MainViewModel.LOG_TAG, "LoadDataFromDatabase: Started")

        try {
            val covidDatabase = CovidDatabase.getDatabase(context)

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
                mapCountriesByName.put(dbc.name!!, c)
                mapCountriesByGeoId.put(dbc.geoId!!, c)
            }

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
                        diffInDays = DaysDiff(d, aggregatedDate)
                    }

                    if (diffInDays>=AggregationPeriodInDays) {
                        aggregatedDate = d
                        if (!rowMap.containsKey(d)) {
                            casesRows.add(Pair<Date, MutableList<Int>>(d, mutableListOf<Int>()))
                            deathsRows.add(Pair<Date, MutableList<Int>>(d, mutableListOf<Int>()))

                            val newRowIndex: Int = casesRows.size - 1
                            rowMap.put(d, newRowIndex)

                            for (c in 1..headers.size - 1) {
                                casesRows[newRowIndex].second.add(0)
                                deathsRows[newRowIndex].second.add(0)
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

                        headers.add(c.name)
                        columnMap.put(c, headers.size-2)
                    }

                    // --------------------
                    val cases:Int = dbcd.cases?:0
                    val deaths:Int = dbcd.deaths?:0

                    dailyCovidsByDate[d]!!.add(DailyCovid(c, d, cases, deaths))

                    casesRows[rowMap[aggregatedDate]!!].second[columnMap[c]!!] += cases
                    deathsRows[rowMap[aggregatedDate]!!].second[columnMap[c]!!] += deaths
                }
            }

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

            // ---------------------------
            Log.i(MainViewModel.LOG_TAG, "Creating aggregates")
            _countryAggregates.SetData(DateStart, DateEnd, _dailyCovidsByDate)

            // ---------------------------
            Log.i(MainViewModel.LOG_TAG, "LoadDataFromDatabase: Finished")
            onDataLoaded(enDataLoaded.All)
            return true
        } catch (ex: Exception) {
            Log.i(MainViewModel.LOG_TAG, "Error: ${ex.message}")
            // TODO: Errorhandling?
            return false
        }
        finally {
        }
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Test data, no longer used
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private fun LoadDataFromCsv(context: Context): Boolean {
        try {
            val countries = LoadCountries_HardCoded()

            _dailyCovidsByDate = LoadCasesFromCsvButMakeUpDeaths(context, countries).toList()

            _countryAggregates.SetData(DateStart, DateEnd, _dailyCovidsByDate)
            return true
        } catch (ex: Exception) {
            // TODO: Errorhandling or logging
            return false
        }
    }


    // Real cases numbers are read from a csv file but deaths are made up in the method below
    private fun LoadCasesFromCsvButMakeUpDeaths(context: Context, countries: MutableMap</*name*/String, Country2>)
                                            : MutableMap<Date, MutableList<DailyCovid>> {
        try {
            val inputStream: InputStream = context.assets.open("ECPDC_CovidData1.csv")
            val csv = readCsv(inputStream)

            val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

            for (iRow in 0..csv.Rows.count()-1) {
                val row = csv.Rows[iRow]
                val d = SimpleDateFormat("dd/MM/yyyy").parse(row[0])
                if (iRow == 0) {
                    DateStart = d
                } else if (iRow == csv.Rows.count()-1) {
                    DateEnd = d
                }

                val daylies = mutableListOf<DailyCovid>()

                for (i:Int in 1..row.count()-1) {
                    val c: Country2? = countries[csv.Headers[i]]
                    if (c == null)
                    {
                        Log.e(MainViewModel.LOG_TAG,"Header is not a recognised country: '${csv.Headers[i]}'")
                        //throw Exception("Header is not a recognised country: '${csv.Headers[i]}'")
                    }
                    else {
                        val numCases:Int = if (row[i] == "") 0 else row[i].toInt()
                        daylies.add(DailyCovid(c, d,
                            numCases,
                            /*TODO*/ceil(numCases * .01).toInt()))
                    }
                }
                dailyCovidsByDate.put(d, daylies)
            }

            return dailyCovidsByDate
        }
        catch (ex: Exception)
        {
            throw(ex)
        }
    }


}