package com.ant_waters.covidstatistics.model

import com.ant_waters.covidstatistics.database.continent
import com.ant_waters.covidstatistics.ui.IDisplayName

class Continent2(val name: String) : IDisplayName
{

    constructor(dbc: continent) : this(
        dbc?.name?:"") {    }

    override val displayName: String
        get() = name

}