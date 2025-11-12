package com.example.smartplanner.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartplanner.R
import com.example.smartplanner.databinding.ActivitySettingsBinding
import com.example.smartplanner.i18n.LocaleManager
import android.content.Context


class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrap(newBase))
    }

    private lateinit var binding: ActivitySettingsBinding

    // Ask for POST_NOTIFICATIONS (Android 13+)
    private val askNotifPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Toast.makeText(
            this,
            if (granted) getString(R.string.notifications_enabled) else getString(R.string.permission_denied),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.settings)

        val prefs = getSharedPreferences("secure_settings", MODE_PRIVATE)

        // Load saved toggle
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications", true)

        // Language picker
        val langs = LocaleManager.supportedDisplayNames(this)
        binding.spinnerLanguage.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, langs)
        val currentIdx = LocaleManager.currentIndex(this)
        if (currentIdx in langs.indices) binding.spinnerLanguage.setSelection(currentIdx)

        // Ask for notif permission up-front
        ensureNotifPermission()

        // Test notification
        binding.btnTestNotif.setOnClickListener { sendTestNotification() }

        // Save
        binding.btnSave.setOnClickListener {
            prefs.edit()
                .putBoolean("notifications", binding.switchNotifications.isChecked)
                .apply()

            val sel = binding.spinnerLanguage.selectedItemPosition
            LocaleManager.setByIndex(this, sel)

            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun ensureNotifPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            askNotifPerm.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun sendTestNotification() {
        val notif = NotificationCompat.Builder(this, "task_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.test_notif_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).notify(1001, notif)
    }
}
