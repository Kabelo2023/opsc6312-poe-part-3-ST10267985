package com.example.smartplanner.weather.ui

import android.view.View
import android.widget.TextView
import com.example.smartplanner.R
import com.example.smartplanner.weather.model.WeatherUi

/** Binds WeatherUi to view_weather_card.xml */
class WeatherCardBinder(root: View) {
    private val tvEmoji: TextView = root.findViewById(R.id.tvEmoji)
    private val tvTemp: TextView = root.findViewById(R.id.tvTemp)
    private val tvSummary: TextView = root.findViewById(R.id.tvSummary)
    private val tvRain: TextView = root.findViewById(R.id.tvRain)

    fun bind(ui: WeatherUi?) {
        if (ui == null) return
        tvEmoji.text = ui.emoji
        tvTemp.text = "${ui.tempC}°"
        // Example: "H 26° · L 15° · Mostly sunny"
        tvSummary.text = "H ${ui.hiC}° · L ${ui.loC}° · ${ui.summary}"
        tvRain.text = "${ui.precipProb}%"
    }
}
