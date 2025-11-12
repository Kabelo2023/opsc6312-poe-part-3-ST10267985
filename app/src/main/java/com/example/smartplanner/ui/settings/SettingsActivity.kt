package com.example.smartplanner.ui.settings

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.smartplanner.databinding.ActivitySettingsBinding
import com.example.smartplanner.i18n.LocaleManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(com.example.smartplanner.R.string.settings)

        // Secure prefs (fallback handled if crypto fails)
        val prefs = try {
            val key = MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
            EncryptedSharedPreferences.create(
                this,
                "secure_settings",
                key,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Throwable) {
            getSharedPreferences("secure_settings", MODE_PRIVATE)
        }

        // Load saved values
        binding.etUsername.setText(prefs.getString("username", ""))
        binding.etEmail.setText(prefs.getString("email", ""))
        binding.etTimezone.setText(prefs.getString("timezone", ""))
        binding.switchNotifications.isChecked = prefs.getBoolean("notifications", true)

        // Theme toggle
        binding.switchTheme.isChecked = ThemeManager.isDark(this)
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDark(this, isChecked)
            recreate()
        }

        // Home lat/lon
        binding.etHomeLat.setText(prefs.getString("home_lat", ""))
        binding.etHomeLon.setText(prefs.getString("home_lon", ""))

        // Language picker
        val langs = LocaleManager.supportedDisplayNames(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, langs)
        binding.spinnerLanguage.adapter = adapter
        val currentIdx = LocaleManager.currentIndex(this)
        if (currentIdx in langs.indices) binding.spinnerLanguage.setSelection(currentIdx)

        // Save button
        binding.btnSave.setOnClickListener {
            prefs.edit()
                .putString("username", binding.etUsername.text.toString().trim())
                .putString("email", binding.etEmail.text.toString().trim())
                .putString("timezone", binding.etTimezone.text.toString().trim())
                .putBoolean("notifications", binding.switchNotifications.isChecked)
                .putString("home_lat", binding.etHomeLat.text.toString().trim())
                .putString("home_lon", binding.etHomeLon.text.toString().trim())
                .apply()

            // Apply language
            val sel = binding.spinnerLanguage.selectedItemPosition
            LocaleManager.setByIndex(this, sel)

            Toast.makeText(this, getString(com.example.smartplanner.R.string.settings_saved), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
