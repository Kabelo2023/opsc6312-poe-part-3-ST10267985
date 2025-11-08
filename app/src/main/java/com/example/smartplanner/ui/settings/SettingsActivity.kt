package com.example.smartplanner.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.smartplanner.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optional: a small title bar
        supportActionBar?.title = "Settings"

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



        // Dark Mode — load from ThemeManager and react live to user toggles
        binding.switchTheme.isChecked = ThemeManager.isDark(this)
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.setDark(this, isChecked)  // saves + applies immediately
            // Recreate this screen so its own colors flip instantly
            recreate()
        }

        // ⬇️ new: home lat/lon
        binding.etHomeLat.setText(prefs.getString("home_lat", ""))
        binding.etHomeLon.setText(prefs.getString("home_lon", ""))


        // Save button for the rest of the fields
        binding.btnSave.setOnClickListener {
            prefs.edit()
                .putString("username", binding.etUsername.text.toString().trim())
                .putString("email", binding.etEmail.text.toString().trim())
                .putString("timezone", binding.etTimezone.text.toString().trim())
                .putBoolean("notifications", binding.switchNotifications.isChecked)
                // ⬇️ new
                .putString("home_lat", binding.etHomeLat.text.toString().trim())
                .putString("home_lon", binding.etHomeLon.text.toString().trim())
                .apply()

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
