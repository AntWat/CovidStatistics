package com.ant_waters.covidstatistics.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class data_source(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "tag") val tag: String?,
    @ColumnInfo(name = "organisation") val organisation: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "url") val url: String?,
)