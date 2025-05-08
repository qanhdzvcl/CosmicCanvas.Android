package com.emrepbu.cosmiccanvas.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import com.emrepbu.cosmiccanvas.domain.repositories.TranslationRepository
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.locale.LanguageOption
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val localeManager: LocaleManager,
    private val translationRepository: TranslationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val _showApiKeySavedMessage = MutableStateFlow(false)
    val showApiKeySavedMessage: StateFlow<Boolean> = _showApiKeySavedMessage.asStateFlow()
    
    private val _showTranslationApiKeySavedMessage = MutableStateFlow(false)
    val showTranslationApiKeySavedMessage: StateFlow<Boolean> = _showTranslationApiKeySavedMessage.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            userPreferencesManager.isDarkThemeFlow.collectLatest { isDarkTheme ->
                val screenSaverDelay = userPreferencesManager.screenSaverDelayFlow.first()
                val notificationsEnabled = userPreferencesManager.notificationsEnabledFlow.first()
                val hasCustomApiKey = userPreferencesManager.hasCustomApiKeyFlow.first()
                val languageCode = userPreferencesManager.getLanguageCode().first()
                val hasCustomTranslationApiKey = userPreferencesManager.hasCustomTranslationApiKeyFlow.first()
                
                _uiState.value = SettingsUiState(
                    isDarkTheme = isDarkTheme,
                    screenSaverDelayMinutes = screenSaverDelay / 60,
                    notificationsEnabled = notificationsEnabled,
                    hasCustomApiKey = hasCustomApiKey,
                    languageCode = languageCode,
                    hasCustomTranslationApiKey = hasCustomTranslationApiKey
                )
            }
        }
        
        viewModelScope.launch {
            userPreferencesManager.screenSaverDelayFlow.collectLatest { screenSaverDelay ->
                _uiState.value = _uiState.value.copy(
                    screenSaverDelayMinutes = screenSaverDelay / 60
                )
            }
        }
        
        viewModelScope.launch {
            userPreferencesManager.notificationsEnabledFlow.collectLatest { notificationsEnabled ->
                _uiState.value = _uiState.value.copy(
                    notificationsEnabled = notificationsEnabled
                )
            }
        }
        
        viewModelScope.launch {
            userPreferencesManager.hasCustomApiKeyFlow.collectLatest { hasCustomApiKey ->
                _uiState.value = _uiState.value.copy(
                    hasCustomApiKey = hasCustomApiKey
                )
            }
        }
        
        viewModelScope.launch {
            userPreferencesManager.getLanguageCode().collectLatest { languageCode ->
                _uiState.value = _uiState.value.copy(
                    languageCode = languageCode
                )
            }
        }
        
        viewModelScope.launch {
            userPreferencesManager.hasCustomTranslationApiKeyFlow.collectLatest { hasCustomTranslationApiKey ->
                _uiState.value = _uiState.value.copy(
                    hasCustomTranslationApiKey = hasCustomTranslationApiKey
                )
            }
        }
    }
    
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setDarkTheme(enabled)
        }
    }
    
    fun setScreenSaverDelay(minutes: Int) {
        if (minutes < 1) return
        viewModelScope.launch {
            userPreferencesManager.setScreenSaverDelay(minutes * 60)
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setNotificationsEnabled(enabled)
        }
    }
    
    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            userPreferencesManager.saveApiKey(apiKey)
            _showApiKeySavedMessage.value = true
            // Reset message visibility after a delay
            kotlinx.coroutines.delay(3000)
            _showApiKeySavedMessage.value = false
        }
    }
    
    fun resetApiKey() {
        viewModelScope.launch {
            userPreferencesManager.saveApiKey("")
        }
    }
    
    fun getLanguageOptions(context: Context): List<LanguageOption> {
        return localeManager.getSupportedLanguages(context)
    }
    
    fun setAppLanguage(languageCode: String) {
        viewModelScope.launch {
            localeManager.setAppLanguage(languageCode)
        }
    }
    
    fun saveTranslationApiKey(apiKey: String) {
        viewModelScope.launch {
            userPreferencesManager.saveTranslationApiKey(apiKey)
            _showTranslationApiKeySavedMessage.value = true
            // Reset message visibility after a delay
            kotlinx.coroutines.delay(3000)
            _showTranslationApiKeySavedMessage.value = false
        }
    }
    
    fun resetTranslationApiKey() {
        viewModelScope.launch {
            userPreferencesManager.saveTranslationApiKey("")
        }
    }
    
    fun isTranslationApiKeyValid(): Boolean {
        var isValid = false
        viewModelScope.launch {
            isValid = translationRepository.isTranslationApiKeyValid()
        }
        return isValid
    }
}

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val screenSaverDelayMinutes: Int = Constants.DEFAULT_SCREEN_SAVER_DELAY_SECONDS / 60,
    val notificationsEnabled: Boolean = true,
    val hasCustomApiKey: Boolean = false,
    val languageCode: String = "",
    val hasCustomTranslationApiKey: Boolean = false
)