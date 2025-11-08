package com.example.smartplanner.weather.util

import android.content.Context

object Settings {
    fun homeLatLon(ctx: Context): Pair<Double, Double>? {
        val p = ctx.getSharedPreferences("secure_settings", Context.MODE_PRIVATE)
        val lat = p.getString("home_lat", null)?.toDoubleOrNull()
        val lon = p.getString("home_lon", null)?.toDoubleOrNull()
        return if (lat != null && lon != null) Pair(lat, lon) else null
    }
}
