package com.ant_waters.covidstatistics.model

import java.util.*

//import kotlin.math.

// Aggregated statistics for a country over a date range
class CountryAggregate(
    val country: Country, val dateStart: Date, val dateEnd: Date,
    val totalCovidCases: Int=5, val totalCovidDeaths: Int=5
) {

    val proportionalCases: Double
        get() {
            //Log.i("CountryAggregate", "totalCovidCases:${totalCovidCases.toString()}")
            var dval: Double = totalCovidCases.toDouble() * DataManager.PopulationScaler.toDouble() / country.popData2019
//            Log.i("CountryAggregate", "dval:${dval.toString()}")
            return dval.toDouble()
        }


            val proportionalDeaths: Double
                get() {
                    //Log.i("CountryAggregate", "totalCovidCases:${totalCovidCases.toString()}")
                    var dval: Double = totalCovidDeaths.toDouble() * DataManager.PopulationScaler.toDouble() / country.popData2019
//            Log.i("CountryAggregate", "dval:${dval.toString()}")
                    return dval.toDouble()
                }

}