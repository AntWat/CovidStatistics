package com.ant_waters.dbroomtest.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ant_waters.covidstatistics.database.*

// Note from: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7
    /* When you modify the database schema, you'll need to update the version number and define a migration strategy.
      For example, a destroy and re-create strategy can be sufficient.
      But for a real app, you must implement a migration strategy.
      See Understanding migrations with Room: https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
    * */

@Database(version = 2,
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
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Nothing to do in this case, but for examples of what might be needed see:
                // https://developer.android.com/training/data-storage/room/migrating-db-versions
                // E.g: database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " + "PRIMARY KEY(`id`))")
            }
        }

        private var INSTANCE: CovidDatabase? = null
        fun getDatabase(context: Context): CovidDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, CovidDatabase::class.java, "covid_database")
                            .createFromAsset("ECPDC_CovidData.db")
                            .addMigrations(MIGRATION_1_2)       // or use .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}