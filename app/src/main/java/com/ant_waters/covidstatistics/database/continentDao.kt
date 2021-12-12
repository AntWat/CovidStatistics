package com.ant_waters.dbroomtest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ant_waters.covidstatistics.database.continent

@Dao
interface continentDao {
    @Query("SELECT * FROM continent")
    fun getAll(): List<continent>

    @Query("SELECT * FROM continent WHERE name IN (:names)")
    fun loadAllByIds(names: List<String>): List<continent>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User
//
//    @Insert
//    fun insertAll(vararg users: User)
//
//    @Delete
//    fun delete(user: User)
}