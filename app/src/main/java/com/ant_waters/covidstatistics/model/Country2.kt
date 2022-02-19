package com.ant_waters.covidstatistics.model

import com.ant_waters.covidstatistics.database.country
import com.ant_waters.covidstatistics.database.country_data
import com.ant_waters.covidstatistics.ui.IDisplayName

/**
 * Information about a country, gathered from multiple database tables
 */
data class Country2(var name: String, var geoId: String, var countryCode : String,
               var popData2019 : Int, var continent: Continent2?) : IDisplayName
{

    constructor(dbc: country, population_2019: Int) : this(
        dbc?.name?:"", dbc.geoId,
        dbc?.country_territory_code?:"", population_2019,
        dbc?.continent?:"") {    }

    constructor(theName: String, theGeoId: String, theCountryCode : String,
                thePopData2019 : Int, continentName: String) : this(
        theName, theGeoId,
        theCountryCode, thePopData2019,
        DataManager.continentFromName(continentName)) {  }

    override val displayName: String
        get() = name

}