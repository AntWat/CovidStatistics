package com.ant_waters.covidstatistics.model

import com.ant_waters.covidstatistics.database.country
import com.ant_waters.covidstatistics.database.country_data

/**
 * Information about a country, gathered from multiple database tables
 */
class Country2(val name: String, val geoId: String, val countryCode : String,
               val popData2019 : Int, val continent: String) {

    constructor(dbc: country, population_2019: Int) : this(
        dbc?.name?:"", dbc.geoId,
        dbc?.country_territory_code?:"", population_2019,
        dbc?.continent?:"") {    }
}