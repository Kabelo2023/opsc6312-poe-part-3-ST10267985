package com.example.smartplanner.weather.sync

import android.content.Context
import androidx.work.*
import com.example.smartplanner.weather.WeatherRepository
import com.example.smartplanner.weather.util.Settings
import java.util.concurrent.TimeUnit

class WeatherSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val (lat, lon) = Settings.homeLatLon(applicationContext) ?: DEFAULT_JHB
        return try {
            WeatherRepository(applicationContext).get(lat, lon, force = true)
            Result.success()
        } catch (_: Throwable) {
            Result.retry()
        }
    }

    companion object {
        private val DEFAULT_JHB = Pair(-26.2041, 28.0473)

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeatherSyncWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .addTag("weather-sync")
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("weather-sync", ExistingPeriodicWorkPolicy.UPDATE, request)
        }
    }
}
