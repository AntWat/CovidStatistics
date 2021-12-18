package com.ant_waters.dbroomtest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ant_waters.covidstatistics.database.covid_data

@Dao
interface covid_dataDao {
    @Query("SELECT * FROM covid_data ORDER BY dateRep DESC")
    fun getAll(): List<covid_data>

    @Query("SELECT * FROM covid_data WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<covid_data>

    @Query("SELECT * FROM covid_data WHERE geoId LIKE :geoIdMatch LIMIT 1")
    fun findByName(geoIdMatch: String): covid_data

    @Insert
    fun insertAll(vararg covid_datas: covid_data)

    @Delete
    fun delete(covid_data: covid_data)
}