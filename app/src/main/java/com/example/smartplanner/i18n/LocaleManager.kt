package com.example.smartplanner.i18n

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {
    // Keep in sync with strings folders
    private val SUPPORTED = listOf(
        Locale("en"),            // English
        Locale("af"),            // Afrikaans
        Locale("zu")             // Zulu
    )

    fun supportedDisplayNames(ctx: Context): List<String> =
        SUPPORTED.map { it.getDisplayName(it).replaceFirstChar { c -> c.uppercase() } }

    fun currentIndex(ctx: Context): Int {
        val code = prefs(ctx).getString("app_lang", "en") ?: "en"
        return SUPPORTED.indexOfFirst { it.language == code }.coerceAtLeast(0)
    }

    fun setByIndex(ctx: Context, index: Int) {
        val locale = SUPPORTED.getOrNull(index) ?: SUPPORTED.first()
        prefs(ctx).edit().putString("app_lang", locale.language).apply()
        apply(locale, ctx)
    }

    fun applyFromStorage(ctx: Context) {
        val code = prefs(ctx).getString("app_lang", "en") ?: "en"
        apply(Locale(code), ctx)
    }

    private fun apply(locale: Locale, ctx: Context) {
        Locale.setDefault(locale)
        val config = Configuration(ctx.resources.configuration)
        if (Build.VERSION.SDK_INT >= 33) {
            config.setLocales(android.os.LocaleList(locale))
        } else {
            config.setLocale(locale)
        }
        ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
    }

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("secure_settings", Context.MODE_PRIVATE)
}
