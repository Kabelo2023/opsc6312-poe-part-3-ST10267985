// app/src/main/java/com/example/smartplanner/i18n/LocaleManager.kt
package com.example.smartplanner.i18n

import android.content.Context
import java.util.Locale

object LocaleManager {
    private const val PREFS = "secure_settings"
    private const val KEY_LANG = "lang"

    // Supported locales in the same order you show in the spinner
    private val SUPPORTED = listOf(
        Locale("en"),
        Locale("af", "ZA"),
        Locale("zu", "ZA")
    )

    /** Names shown in the language spinner */
    fun supportedDisplayNames(@Suppress("UNUSED_PARAMETER") ctx: Context) =
        listOf("English", "Afrikaans", "isiZulu")

    /** Which index is currently selected */
    fun currentIndex(ctx: Context): Int {
        val code = prefs(ctx).getString(KEY_LANG, "") ?: ""
        val current = when (code) {
            "af-ZA" -> Locale("af", "ZA")
            "zu-ZA" -> Locale("zu", "ZA")
            else    -> Locale("en")
        }
        val idx = SUPPORTED.indexOfFirst { it.language == current.language && it.country == current.country }
        return if (idx >= 0) idx else 0
    }

    /** Persist selection from spinner */
    fun setByIndex(ctx: Context, index: Int) {
        val loc = SUPPORTED.getOrElse(index) { SUPPORTED[0] }
        val code = when (loc.language) {
            "af" -> "af-ZA"
            "zu" -> "zu-ZA"
            else -> "en"
        }
        prefs(ctx).edit().putString(KEY_LANG, code).apply()
    }

    /** Called from Application.onCreate() if you want */
    fun applyFromStorage(ctx: Context) {
        // no-op: Activities call wrap() in attachBaseContext; keeping for compatibility
        wrap(ctx)
    }

    /** The function your Activities call in attachBaseContext(...) */
    fun wrap(base: Context): Context {
        val code = prefs(base).getString(KEY_LANG, "") ?: ""
        val locale = when (code) {
            "af-ZA" -> Locale("af", "ZA")
            "zu-ZA" -> Locale("zu", "ZA")
            else    -> Locale("en")
        }
        return updateResources(base, locale)
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
