package com.ant_waters.covidstatistics.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: Foreign key constraints - see: https://stackoverflow.com/questions/47511750/how-to-use-foreign-key-in-room-persistence-library
@Entity
data class country(
    @PrimaryKey val geoId: String,
    @ColumnInfo(name = "country_territory_code") val country_territory_code: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "continent") val continent: String?,
)