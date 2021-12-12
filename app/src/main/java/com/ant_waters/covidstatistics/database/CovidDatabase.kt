package com.ant_waters.dbroomtest.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ant_waters.covidstatistics.database.*

@Database(version = 1,
    entities = [settings::class, continent::class, country::class, data_source::class,
                country_data::class, covid_data::class, ])
abstract class CovidDatabase : RoomDatabase() {
    abstract fun settingsDao(): settingsDao
    abstract fun continentDao(): continentDao
    abstract fun countryDao(): countryDao
    abstract fun data_sourceDao(): data_sourceDao
    abstract fun country_dataDao(): country_dataDao
    abstract fun covid_dataDao(): covid_dataDao

    companion object {
        private var INSTANCE: CovidDatabase? = null
        fun getDatabase(context: Context): CovidDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, CovidDatabase::class.java, "covid_database")
                            .createFromAsset("ECPDC_CovidData.db")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}