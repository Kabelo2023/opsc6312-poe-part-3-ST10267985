package com.example.smartplanner.weather.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDb : RoomDatabase() {
    abstract fun dao(): WeatherDao

    companion object {
        @Volatile private var instance: WeatherDb? = null
        fun get(context: Context): WeatherDb =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDb::class.java, "weather.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
