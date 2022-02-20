package com.ant_waters.dbroomtest.database

import androidx.room.*
import com.ant_waters.covidstatistics.database.country
import com.ant_waters.covidstatistics.database.country_data

@Dao
interface country_dataDao {
    @Query("SELECT * FROM country_data")
    fun getAll(): List<country_data>

//    @Query("SELECT * FROM country_data WHERE geoId IN (:geoIds)")
//    fun loadAllByIds(geoIds: List<String>): List<country_data>

    @Query("SELECT * FROM country_data WHERE geoId LIKE :geoIdMatch LIMIT 1")
    fun findByGeoId(geoIdMatch: String): country_data

    @Insert
    fun insertAll(vararg country_datas: country_data)

    @Delete
    fun delete(country_data: country_data)

    @Update
    fun update(country_data: country_data)

}