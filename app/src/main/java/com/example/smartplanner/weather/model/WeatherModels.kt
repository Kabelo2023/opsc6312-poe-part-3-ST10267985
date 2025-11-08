package com.example.smartplanner.weather.model

data class WeatherUi(
    val tempC: Int,
    val summary: String,
    val precipProb: Int,
    val hiC: Int?,
    val loC: Int?,
    val emoji: String
)
