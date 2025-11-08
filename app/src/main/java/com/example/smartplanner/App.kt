package com.example.smartplanner

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.example.smartplanner.ui.login.LoginActivity
import com.example.smartplanner.ui.settings.ThemeManager   // ← add this import
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

        // ✅ Apply saved dark/light mode immediately for the whole app
        ThemeManager.applyFromStorage(this)

        // ⬇️ schedule background weather sync (hourly, on network)
        WeatherSyncWorker.schedule(this)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e("FATAL", "Uncaught exception", e)
            Toast.makeText(this, e.message ?: "Unexpected error", Toast.LENGTH_LONG).show()
            val i = Intent(this, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
        }
    }
}
