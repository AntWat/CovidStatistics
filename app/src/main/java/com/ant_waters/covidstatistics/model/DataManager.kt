package com.ant_waters.covidstatistics.model

import android.content.Context
import android.util.Log
import com.ant_waters.covidstatistics.MainActivity
import java.util.*
import com.ant_waters.covidstatistics.Utils.readCsv
import com.ant_waters.covidstatistics.database.country_data
import com.ant_waters.dbroomtest.database.CovidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import kotlin.math.ceil


class DataManager {
    companion object {
        lateinit var DateStart: Date
        lateinit var DateEnd: Date

        private var _dailyCovidsByDate = listOf<Pair<Date, MutableList<DailyCovid>>>()
        val DailyCovidsByDate get() = this._dailyCovidsByDate

        private var _countryAggregates: CountryAggregates = CountryAggregates()
        val CountryAggregates get() = this._countryAggregates

        val PopulationScaler = 100000      // cases are usually reported per 100k of population
        val PopulationScalerLabel = "100k"
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    suspend fun LoadData(context: Context): Boolean {
        return withContext(Dispatchers.IO)
        {LoadDataFromDatabase(context)}
        //return LoadDataFromCsv(context)
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private fun LoadDataFromDatabase(context: Context): Boolean {
        Log.i(MainActivity.LOG_TAG, "LoadDataFromDatabase: Started")

        try {
            val covidDatabase = CovidDatabase.getDatabase(context)

            // ---------------------------
            Log.i(MainActivity.LOG_TAG, "Loading countries")
            val dbCountries = covidDatabase.countryDao().getAll()

            val mapCountriesByName = mutableMapOf</*name*/String, Country>()
            val mapCountriesByGeoId = mutableMapOf</*geoId*/String, Country>()

            for (dbc in dbCountries)
            {
                val cdata: country_data? = covidDatabase.country_dataDao().findByGeoId(dbc.geoId)

                val c = Country(dbc, cdata?.population_2019?:0)
                mapCountriesByName.put(dbc.name!!, c)
                mapCountriesByGeoId.put(dbc.geoId!!, c)
            }

            // ---------------------------
            Log.i(MainActivity.LOG_TAG, "Loading daily data")
            val dbCovid_data = covidDatabase.covid_dataDao().getAll()

            val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

            var dateStart: Date? = null
            var dateEnd: Date? = null

            for (dbcd in dbCovid_data)
            {
                if (mapCountriesByGeoId.containsKey(dbcd.geoId))
                {
                    val c: Country = mapCountriesByGeoId[dbcd.geoId]!!

                    val d = SimpleDateFormat("yyyy-MM-dd").parse(dbcd.dateRep)
                    if ( (dateStart == null) || (d < dateStart) ) { dateStart = d }
                    if ( (dateEnd == null) || (d > dateEnd) ) { dateEnd = d }

                    if (!dailyCovidsByDate.containsKey(d)) {
                        dailyCovidsByDate.put(d, mutableListOf<DailyCovid>())
                    }
                    val cases:Int = dbcd.cases?:0
                    val deaths:Int = dbcd.deaths?:0

                    dailyCovidsByDate[d]!!.add(DailyCovid(c, d, cases, deaths))
                }
            }

            DateStart = dateStart!!
            DateEnd = dateEnd!!
            _dailyCovidsByDate  = dailyCovidsByDate.toList()

            //val countries = LoadCountries_HardCoded()

            //_dailyCovidsByDate = LoadCasesFromCsvButMakeUpDeaths(context, mapCountries).toList()
            //_dailyCovidsByDate = LoadTestData(countries).toList()

            // ---------------------------
            Log.i(MainActivity.LOG_TAG, "Creating aggregates")
            _countryAggregates.SetData(DateStart, DateEnd, _dailyCovidsByDate)

            // ---------------------------
            Log.i(MainActivity.LOG_TAG, "LoadDataFromDatabase: Finished")
            return true
        } catch (ex: Exception) {
            Log.i(MainActivity.LOG_TAG, "Error: ${ex.message}")
            // TODO: Errorhandling?
            return false
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private fun LoadDataFromCsv(context: Context): Boolean {
        // TODO: Load data from CSV or Database

        try {
            val countries = LoadCountries_HardCoded()

            _dailyCovidsByDate = LoadCasesFromCsvButMakeUpDeaths(context, countries).toList()
            //_dailyCovidsByDate = LoadTestData(countries).toList()

            _countryAggregates.SetData(DateStart, DateEnd, _dailyCovidsByDate)
            return true
        } catch (ex: Exception) {
            // TODO: Errorhandling or logging
            return false
        }
    }


    // Real cases numbers are read from a csv file but deaths are made up in the method below
    private fun LoadCasesFromCsvButMakeUpDeaths(context: Context, countries: MutableMap</*name*/String, Country>)
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
                    val c: Country? = countries[csv.Headers[i]]
                    if (c == null)
                    {
                        Log.e(MainActivity.LOG_TAG,"Header is not a recognised country: '${csv.Headers[i]}'")
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