package com.emrepbu.cosmiccanvas.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emrepbu.cosmiccanvas.BuildConfig
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.EncryptionUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES_NAME)

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    // Theme preferences
    val isDarkThemeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_KEY] ?: false
    }
    
    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }
    
    // Screen saver preferences
    val screenSaverDelayFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[SCREEN_SAVER_DELAY_KEY] ?: Constants.DEFAULT_SCREEN_SAVER_DELAY_SECONDS
    }
    
    suspend fun setScreenSaverDelay(delaySeconds: Int) {
        dataStore.edit { preferences ->
            preferences[SCREEN_SAVER_DELAY_KEY] = delaySeconds
        }
    }
    
    // Watched keywords preferences
    val watchedKeywordsFlow: Flow<Set<String>> = dataStore.data.map { preferences ->
        val keywordsString = preferences[WATCHED_KEYWORDS_KEY] ?: ""
        if (keywordsString.isBlank()) {
            emptySet()
        } else {
            keywordsString.split(",").toSet()
        }
    }
    
    suspend fun addWatchedKeyword(keyword: String) {
        dataStore.edit { preferences ->
            val currentKeywords = preferences[WATCHED_KEYWORDS_KEY]?.split(",")?.toMutableSet() ?: mutableSetOf()
            currentKeywords.add(keyword.trim())
            preferences[WATCHED_KEYWORDS_KEY] = currentKeywords.joinToString(",")
        }
    }
    
    suspend fun removeWatchedKeyword(keyword: String) {
        dataStore.edit { preferences ->
            val currentKeywords = preferences[WATCHED_KEYWORDS_KEY]?.split(",")?.toMutableSet() ?: mutableSetOf()
            currentKeywords.remove(keyword.trim())
            preferences[WATCHED_KEYWORDS_KEY] = currentKeywords.joinToString(",")
        }
    }
    
    // Notification preferences
    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }
    
    // NASA API Key preferences (with encryption)
    val hasCustomApiKeyFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        !preferences[NASA_API_KEY_KEY].isNullOrBlank()
    }
    
    suspend fun saveApiKey(apiKey: String) {
        if (apiKey.isBlank()) {
            // If blank, remove the stored key
            dataStore.edit { preferences ->
                preferences.remove(NASA_API_KEY_KEY)
            }
        } else {
            // Encrypt and store the API key
            val encryptedKey = EncryptionUtils.encrypt(context, apiKey)
            dataStore.edit { preferences ->
                preferences[NASA_API_KEY_KEY] = encryptedKey
            }
        }
    }
    
    suspend fun getApiKey(): String {
        val preferences = dataStore.data.first()
        val encryptedKey = preferences[NASA_API_KEY_KEY]
        
        return if (encryptedKey.isNullOrBlank()) {
            // Return the default API key from BuildConfig if no custom key is set
            BuildConfig.NASA_API_KEY
        } else {
            try {
                // Decrypt and return the stored API key
                EncryptionUtils.decrypt(context, encryptedKey)
            } catch (e: Exception) {
                // If decryption fails, return the default API key
                BuildConfig.NASA_API_KEY
            }
        }
    }
    
    // Language preferences
    fun getLanguageCode(): Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: ""
    }
    
    suspend fun setLanguageCode(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
    
    // Google Cloud Translation API Key preferences (with encryption)
    val hasCustomTranslationApiKeyFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        !preferences[TRANSLATION_API_KEY_KEY].isNullOrBlank()
    }
    
    suspend fun saveTranslationApiKey(apiKey: String) {
        if (apiKey.isBlank()) {
            // If blank, remove the stored key
            dataStore.edit { preferences ->
                preferences.remove(TRANSLATION_API_KEY_KEY)
            }
        } else {
            // Encrypt and store the API key
            val encryptedKey = EncryptionUtils.encrypt(context, apiKey)
            dataStore.edit { preferences ->
                preferences[TRANSLATION_API_KEY_KEY] = encryptedKey
            }
        }
    }
    
    suspend fun getTranslationApiKey(): String? {
        val preferences = dataStore.data.first()
        val encryptedKey = preferences[TRANSLATION_API_KEY_KEY] ?: return null
        
        return try {
            // Decrypt and return the stored API key
            EncryptionUtils.decrypt(context, encryptedKey)
        } catch (e: Exception) {
            null
        }
    }
    
    // Recently used languages for translation
    val recentlyUsedLanguagesFlow: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[RECENTLY_USED_LANGUAGES_KEY] ?: emptySet()
    }
    
    suspend fun addRecentlyUsedLanguage(languageCode: String) {
        dataStore.edit { preferences ->
            val currentLanguages = preferences[RECENTLY_USED_LANGUAGES_KEY]?.toMutableSet() ?: mutableSetOf()
            
            // Keep only the most recent 5 languages (including the new one)
            if (currentLanguages.size >= 5 && !currentLanguages.contains(languageCode)) {
                val oldestLanguage = currentLanguages.first()
                currentLanguages.remove(oldestLanguage)
            }
            
            // Add to the end (most recent)
            if (currentLanguages.contains(languageCode)) {
                currentLanguages.remove(languageCode)
            }
            currentLanguages.add(languageCode)
            
            preferences[RECENTLY_USED_LANGUAGES_KEY] = currentLanguages
        }
    }
    
    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey(Constants.PREF_DARK_THEME)
        private val SCREEN_SAVER_DELAY_KEY = intPreferencesKey(Constants.PREF_SCREEN_SAVER_DELAY)
        private val WATCHED_KEYWORDS_KEY = stringPreferencesKey(Constants.PREF_WATCHED_KEYWORDS)
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey(Constants.PREF_ENABLE_NOTIFICATIONS)
        private val NASA_API_KEY_KEY = stringPreferencesKey(Constants.PREF_NASA_API_KEY)
        private val LANGUAGE_KEY = stringPreferencesKey(Constants.PREF_LANGUAGE)
        private val TRANSLATION_API_KEY_KEY = stringPreferencesKey(Constants.PREF_TRANSLATION_API_KEY)
        private val RECENTLY_USED_LANGUAGES_KEY = stringSetPreferencesKey(Constants.PREF_RECENTLY_USED_LANGUAGES)
    }
}