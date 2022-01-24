package com.ant_waters.covidstatistics.model

import java.util.*

/**
 * Covid statistics for a country and date
 */
class DailyCovid (val country: Country2, val date: Date,
                  val covidCases: Int, val covidDeaths: Int) {
}