package com.example.smartplanner.weather

import android.content.Context
import com.example.smartplanner.weather.local.WeatherDb
import com.example.smartplanner.weather.local.WeatherEntity
import com.example.smartplanner.weather.model.WeatherUi
import com.example.smartplanner.weather.remote.OpenMeteoDto
import com.example.smartplanner.weather.remote.WeatherApiClient
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class WeatherRepository(private val context: Context) {

    private val api = WeatherApiClient.create()
    private val dao = WeatherDb.get(context).dao()
    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(OpenMeteoDto::class.java)

    private fun areaId(lat: Double, lon: Double) = "%.3f_%.3f".format(lat, lon)

    suspend fun get(lat: Double, lon: Double, force: Boolean = false): WeatherUi? =
        withContext(Dispatchers.IO) {
            val id = areaId(lat, lon)
            val now = System.currentTimeMillis()
            val cached = dao.get(id)
            val fresh = cached?.let { now - it.fetchedAtEpoch < 45 * 60 * 1000 } == true
            if (cached != null && fresh && !force) return@withContext decode(cached)

            val dto = api.forecast(lat, lon)
            val json = adapter.toJson(dto)
            val entity = WeatherEntity(
                areaId = id,
                fetchedAtEpoch = now,
                currentJson = json,
                hourlyJson = json,
                dailyJson = json
            )
            dao.upsert(entity)
            decode(entity)
        }

    private fun decode(entity: WeatherEntity): WeatherUi? {
        val dto = adapter.fromJson(entity.currentJson) ?: return null
        val temp = dto.current_weather?.temperature?.roundToInt() ?: return null
        val code = dto.current_weather.weathercode ?: 0
        val precip = dto.daily?.precipitation_probability_max?.firstOrNull() ?: 0
        val hi = dto.daily?.temperature_2m_max?.firstOrNull()?.roundToInt()
        val lo = dto.daily?.temperature_2m_min?.firstOrNull()?.roundToInt()
        return WeatherUi(
            tempC = temp,
            summary = mapCodeToSummary(code),
            precipProb = precip,
            hiC = hi,
            loC = lo,
            emoji = mapCodeToEmoji(code)
        )
    }

    private fun mapCodeToSummary(code: Int): String = when (code) {
        0 -> "Clear"
        1,2 -> "Partly cloudy"
        3 -> "Cloudy"
        45,48 -> "Fog"
        in 51..57 -> "Drizzle"
        in 61..67 -> "Rain"
        in 71..77 -> "Snow"
        in 80..82 -> "Showers"
        95,96,99 -> "Thunder"
        else -> "Weather"
    }

    private fun mapCodeToEmoji(code: Int): String = when (code) {
        0 -> "☀️"
        1,2 -> "⛅"
        3 -> "☁️"
        in 61..67, in 80..82 -> "🌧️"
        95,96,99 -> "⛈️"
        else -> "🌤️"
    }
}
