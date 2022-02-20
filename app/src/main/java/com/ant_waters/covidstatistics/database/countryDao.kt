package com.ant_waters.dbroomtest.database

import android.content.ClipData
import androidx.room.*
import com.ant_waters.covidstatistics.database.country

@Dao
interface countryDao {
    @Query("SELECT * FROM country")
    fun getAll(): List<country>

    @Query("SELECT * FROM country WHERE geoId IN (:geoIds)")
    fun loadAllByIds(geoIds: List<String>): List<country>

    @Query("SELECT * FROM country WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): country

    @Insert
    fun insertAll(vararg countrys: country)

    @Delete
    fun delete(country: country)

    @Update
    fun update(country: country)
}