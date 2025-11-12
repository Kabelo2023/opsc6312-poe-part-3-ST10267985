package com.example.smartplanner

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.example.smartplanner.ui.login.LoginActivity
import com.example.smartplanner.ui.settings.ThemeManager
import com.example.smartplanner.i18n.LocaleManager
import com.example.smartplanner.weather.sync.WeatherSyncWorker

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (t: Throwable) {
            Log.e("App", "Firebase init failed", t)
        }

        // Apply theme + language immediately
        ThemeManager.applyFromStorage(this)
        LocaleManager.applyFromStorage(this)

        // Background weather sync
        WeatherSyncWorker.schedule(this)

        // Subscribe to a default FCM topic for easy testing
        runCatching { FirebaseMessaging.getInstance().subscribeToTopic("all") }

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("FATAL", "Uncaught exception", e)
            Toast.makeText(this, e.message ?: "Unexpected error", Toast.LENGTH_LONG).show()
            val i = Intent(this, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
        }
    }
}
