package com.example.smartplanner.weather.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true,
        @Query("hourly") hourly: String =
            "temperature_2m,precipitation_probability,weathercode,windspeed_10m",
        @Query("daily") daily: String =
            "temperature_2m_max,temperature_2m_min,uv_index_max,precipitation_probability_max",
        @Query("timezone") tz: String = "auto"
    ): OpenMeteoDto

    companion object {
        const val BASE = "https://api.open-meteo.com/"
    }
}

data class OpenMeteoDto(
    val current_weather: Current?,
    val hourly: Hourly?,
    val daily: Daily?
) {
    data class Current(
        val temperature: Double?,
        val weathercode: Int?,
        val windspeed: Double?,
        val time: String?
    )
    data class Hourly(
        val time: List<String>?,
        val temperature_2m: List<Double>?,
        val precipitation_probability: List<Int>?,
        val weathercode: List<Int>?,
        val windspeed_10m: List<Double>?
    )
    data class Daily(
        val time: List<String>?,
        val temperature_2m_max: List<Double>?,
        val temperature_2m_min: List<Double>?,
        val uv_index_max: List<Double>?,
        val precipitation_probability_max: List<Int>?
    )
}
