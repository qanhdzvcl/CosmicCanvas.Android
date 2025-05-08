package com.emrepbu.cosmiccanvas.utils.locale

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor(
    val userPreferencesManager: UserPreferencesManager
) {
    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_TURKISH = "tr"
        const val LANGUAGE_GERMAN = "de"
        const val LANGUAGE_SPANISH = "es"
        const val LANGUAGE_SYSTEM = ""
    }

    // Apply locale to context
    fun setLocale(context: Context): ContextWrapper {
        // Get the saved language code
        val languageCode = runBlocking {
            userPreferencesManager.getLanguageCode().first()
        }

        // If system default is selected, return the original context
        if (languageCode.isEmpty()) {
            return ContextWrapper(context)
        }

        // Set the locale programmatically
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleForApi24(configuration, locale)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale = locale
        }

        val updatedContext = context.createConfigurationContext(configuration)
        return ContextWrapper(updatedContext)
    }

    // Set app language runtime
    suspend fun setAppLanguage(languageCode: String) {
        // Save the selected language code
        userPreferencesManager.setLanguageCode(languageCode)

        // Apply immediately using AppCompatDelegate
        applyLanguageToAppLocale(languageCode)
    }

    // Apply via AppCompatDelegate
    fun applyLanguageToAppLocale(languageCode: String) {
        val localeList = if (languageCode.isEmpty()) {
            // Use system default
            LocaleListCompat.getEmptyLocaleList()
        } else {
            // Use selected language
            LocaleListCompat.create(Locale(languageCode))
        }

        AppCompatDelegate.setApplicationLocales(localeList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocaleForApi24(config: Configuration, locale: Locale) {
        config.setLocale(locale)
    }

    // Get supported languages
    fun getSupportedLanguages(context: Context): List<LanguageOption> {
        return listOf(
            LanguageOption(LANGUAGE_SYSTEM, getLocalizedLanguageName(context, LANGUAGE_SYSTEM)),
            LanguageOption(LANGUAGE_ENGLISH, getLocalizedLanguageName(context, LANGUAGE_ENGLISH)),
            LanguageOption(LANGUAGE_TURKISH, getLocalizedLanguageName(context, LANGUAGE_TURKISH)),
            LanguageOption(LANGUAGE_GERMAN, getLocalizedLanguageName(context, LANGUAGE_GERMAN)),
            LanguageOption(LANGUAGE_SPANISH, getLocalizedLanguageName(context, LANGUAGE_SPANISH))
        )
    }

    // Get localized language name
    private fun getLocalizedLanguageName(context: Context, languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_SYSTEM -> context.getString(com.emrepbu.cosmiccanvas.R.string.settings_language_system)
            LANGUAGE_ENGLISH -> context.getString(com.emrepbu.cosmiccanvas.R.string.settings_language_english)
            LANGUAGE_TURKISH -> context.getString(com.emrepbu.cosmiccanvas.R.string.settings_language_turkish)
            LANGUAGE_GERMAN -> context.getString(com.emrepbu.cosmiccanvas.R.string.settings_language_german)
            LANGUAGE_SPANISH -> context.getString(com.emrepbu.cosmiccanvas.R.string.settings_language_spanish)
            else -> languageCode
        }
    }

    /** Get current language code (null if system default) */
    fun getCurrentLanguageCode(): String? {
        return runBlocking {
            val languageCode = userPreferencesManager.getLanguageCode().first()
            languageCode.ifEmpty { null }
        }
    }
}

data class LanguageOption(
    val code: String,
    val displayName: String
)