package com.ant_waters.covidstatistics.model

import java.util.*

/**
 * Aggregated statistics for a country over a date range
 */
class CountryAggregate(
    val country: Country2, val dateStart: Date, val dateEnd: Date,
    val totalCovidCases: Int = 5, val totalCovidDeaths: Int = 5
) {

    val proportionalCases: Double
        get() {
            val dval: Double =
                totalCovidCases.toDouble() * DataManager.PopulationScaler.toDouble() / country.popData2019
            return dval
        }


    val proportionalDeaths: Double
        get() {
            val dval: Double =
                totalCovidDeaths.toDouble() * DataManager.PopulationScaler.toDouble() / country.popData2019
            return dval
        }
}