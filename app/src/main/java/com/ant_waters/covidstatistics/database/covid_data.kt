package com.ant_waters.covidstatistics.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class covid_data(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "dateRep") val dateRep: String?,     // TODO: date?
    @ColumnInfo(name = "geoId") val geoId: String?,
    @ColumnInfo(name = "cases") val cases: Int?,
    @ColumnInfo(name = "deaths") val deaths: Int?,
    @ColumnInfo(name = "Cumulative_number_for_14_days_of_COVID-19_cases_per_100000")
                  val Cumulative_number_for_14_days_of_COVID_19_cases_per_100000: Double,
)