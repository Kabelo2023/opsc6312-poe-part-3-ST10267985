package com.example.smartplanner.ui.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ThemeManager {
    private const val PREFS_NAME = "secure_settings"
    private const val KEY_DARK = "dark_mode"

    private fun prefs(ctx: Context) = try {
        val key = MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(
            ctx, PREFS_NAME, key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (_: Throwable) {
        // Fallback if crypto unavailable
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isDark(ctx: Context) = prefs(ctx).getBoolean(KEY_DARK, false)

    fun setDark(ctx: Context, enabled: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_DARK, enabled).apply()
        apply(enabled)
    }

    fun applyFromStorage(ctx: Context) = apply(isDark(ctx))

    private fun apply(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
