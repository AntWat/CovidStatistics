package com.ant_waters.covidstatistics.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class settings(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "version") val version: Int?,
)