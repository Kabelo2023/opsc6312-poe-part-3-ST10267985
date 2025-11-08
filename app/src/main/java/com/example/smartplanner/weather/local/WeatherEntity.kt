package com.example.smartplanner.weather.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val areaId: String,
    val fetchedAtEpoch: Long,
    val currentJson: String,
    val hourlyJson: String,
    val dailyJson: String
)
