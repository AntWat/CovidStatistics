package com.ant_waters.covidstatistics.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class country_data(
    @PrimaryKey val geoId: String,
    @ColumnInfo(name = "population_2019") val population_2019: Int?,
    @ColumnInfo(name = "data_source") val data_source: Int?,
)