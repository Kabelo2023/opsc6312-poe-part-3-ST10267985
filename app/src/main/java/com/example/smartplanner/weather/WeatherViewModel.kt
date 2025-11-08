package com.example.smartplanner.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smartplanner.weather.model.WeatherUi

/**
 * MOCK WeatherViewModel:
 * Emits static demo weather so the card always shows.
 * Matches WeatherUi fields: tempC, hiC, loC, precipProb, emoji, summary.
 */
class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val _ui = MutableLiveData<WeatherUi?>()
    val ui: LiveData<WeatherUi?> = _ui

    fun refresh() {
        _ui.value = WeatherUi(
            tempC = 23,          // current °C
            hiC = 26,            // high °C
            loC = 15,            // low °C
            precipProb = 5,      // rain %
            emoji = "🌤️",
            summary = "Mostly sunny"
        )
    }

    init {
        // show something immediately
        refresh()
    }
}
