package com.ant_waters.covidstatistics.model

import android.content.Context
import java.util.*
import com.ant_waters.covidstatistics.Utils.readCsv
import com.ant_waters.covidstatistics.database.country_data
import com.ant_waters.dbroomtest.database.CovidDatabase
import java.io.InputStream
import java.text.SimpleDateFormat
import kotlin.math.ceil


class DataManager {
    companion object {
        lateinit var DateStart: Date
        lateinit var DateEnd: Date

        private var _dailyCovidsByDate = listOf<Pair<Date, MutableList<DailyCovid>>>()
        val DailyCovidsByDate get() = this._dailyCovidsByDate

        private var _countryAggregates: CountryAggregates? = null
        val CountryAggregates get() = this._countryAggregates

        val PopulationScaler = 100000      // cases are usually reported per 100k of population
        val PopulationScalerLabel = "100k"
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    suspend fun LoadData(context: Context): Boolean {
        return LoadDataFromDatabase(context)
        //return LoadDataFromCsv(context)
    }


    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private suspend fun LoadDataFromDatabase(context: Context): Boolean {
        // TODO: Load data from CSV or Database

        try {
            val covidDatabase = CovidDatabase.getDatabase(context)

            val dbCountries = covidDatabase.countryDao().getAll()

            val mapCountries = mutableMapOf</*name*/String, Country>()

            for (dbc in dbCountries)
            {
                val cdata: country_data? = covidDatabase.country_dataDao().findByGeoId(dbc.geoId)

                mapCountries.put(dbc.name!!, Country(dbc, cdata?.population_2019?:0))
            }

            //val countries = LoadCountries_HardCoded()

            _dailyCovidsByDate = LoadCasesFromCsvButMakeUpDeaths(context, mapCountries).toList()
            //_dailyCovidsByDate = LoadTestData(countries).toList()

            _countryAggregates = CountryAggregates(DateStart, DateEnd, _dailyCovidsByDate)
            return true
        } catch (ex: Exception) {
            // TODO: Errorhandling or logging
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

            _countryAggregates = CountryAggregates(DateStart, DateEnd, _dailyCovidsByDate)
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
                    if (c == null) { throw Exception("Header is not a recognised country: '${csv.Headers[i]}'") }
                    val numCases:Int = if (row[i] == "") 0 else row[i].toInt()
                    daylies.add(DailyCovid(c, d,
                        numCases,
                        /*TODO*/ceil(numCases * .01).toInt()))
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