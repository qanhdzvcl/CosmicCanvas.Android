package com.emrepbu.cosmiccanvas.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.models.Translation
import com.emrepbu.cosmiccanvas.domain.usecases.GetRecentApodsUseCase
import com.emrepbu.cosmiccanvas.domain.usecases.RefreshApodRangeUseCase
import com.emrepbu.cosmiccanvas.domain.usecases.ToggleFavoriteUseCase
import com.emrepbu.cosmiccanvas.domain.usecases.TranslateTextUseCase
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.DateUtils
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentApodsUseCase: GetRecentApodsUseCase,
    private val refreshApodRangeUseCase: RefreshApodRangeUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    private val userPreferencesManager: UserPreferencesManager,
    val localeManager: LocaleManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Translation state for each APOD
    private val translations = mutableMapOf<String, TranslationState>()
    private val _translationStates = MutableStateFlow<Map<String, TranslationState>>(emptyMap())
    val translationStates: StateFlow<Map<String, TranslationState>> = _translationStates.asStateFlow()
    
    private val _screenSaverTimeoutSeconds = MutableStateFlow(Constants.DEFAULT_SCREEN_SAVER_DELAY_SECONDS)
    val screenSaverTimeoutSeconds: StateFlow<Int> = _screenSaverTimeoutSeconds.asStateFlow()
    
    init {
        loadRecentApods()
        loadUserPreferences()
    }
    
    private fun loadUserPreferences() {
        viewModelScope.launch {
            userPreferencesManager.screenSaverDelayFlow.collectLatest { delaySeconds ->
                _screenSaverTimeoutSeconds.value = delaySeconds
            }
        }
    }
    
    fun loadRecentApods() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            try {
                getRecentApodsUseCase().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isEmpty()) {
                                refreshRecentApods()
                            } else {
                                _uiState.value = HomeUiState.Success(result.data)
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
                            refreshRecentApods()
                        }
                        is Result.Loading -> {
                            _uiState.value = HomeUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    private fun refreshRecentApods() {
        viewModelScope.launch {
            val endDate = DateUtils.getTodayDateString()
            val recentDates = DateUtils.getRecentDates(Constants.RECENT_APODS_COUNT)
            val startDate = recentDates.lastOrNull() ?: endDate
            
            val result = refreshApodRangeUseCase(startDate, endDate)
            if (result is Result.Error) {
                _uiState.value = HomeUiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }
    
    fun toggleFavorite(date: String, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(date, isFavorite)
        }
    }
    
    /** Translate APOD to app language */
    fun translateToAppLanguage(apodDate: String, title: String, explanation: String) {
        viewModelScope.launch {
            val currentState = translations[apodDate] ?: TranslationState()
            translations[apodDate] = currentState.copy(
                isTitleTranslating = title.isNotBlank(),
                isExplanationTranslating = explanation.isNotBlank()
            )
            updateTranslationStates()
            
            // Translate title
            if (title.isNotBlank()) {
                when (val result = translateTextUseCase.toAppLanguage(title)) {
                    is Result.Success -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            titleTranslation = result.data,
                            isTitleTranslating = false
                        )
                    }
                    is Result.Error -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            isTitleTranslating = false
                        )
                    }
                    else -> {}
                }
                updateTranslationStates()
            }
            
            // Translate explanation
            if (explanation.isNotBlank()) {
                when (val result = translateTextUseCase.toAppLanguage(explanation)) {
                    is Result.Success -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            explanationTranslation = result.data,
                            isExplanationTranslating = false
                        )
                    }
                    is Result.Error -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            isExplanationTranslating = false
                        )
                    }
                    else -> {}
                }
                updateTranslationStates()
            }
        }
    }
    
    /** Translate APOD to specific language */
    fun translateToLanguage(apodDate: String, title: String, explanation: String, languageCode: String) {
        viewModelScope.launch {
            val currentState = translations[apodDate] ?: TranslationState()
            translations[apodDate] = currentState.copy(
                isTitleTranslating = title.isNotBlank(),
                isExplanationTranslating = explanation.isNotBlank()
            )
            updateTranslationStates()
            
            // Translate title
            if (title.isNotBlank()) {
                when (val result = translateTextUseCase.toLanguage(title, languageCode)) {
                    is Result.Success -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            titleTranslation = result.data,
                            isTitleTranslating = false
                        )
                    }
                    is Result.Error -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            isTitleTranslating = false
                        )
                    }
                    else -> {}
                }
                updateTranslationStates()
            }
            
            // Translate explanation
            if (explanation.isNotBlank()) {
                when (val result = translateTextUseCase.toLanguage(explanation, languageCode)) {
                    is Result.Success -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            explanationTranslation = result.data,
                            isExplanationTranslating = false
                        )
                    }
                    is Result.Error -> {
                        val translationState = translations[apodDate] ?: TranslationState()
                        translations[apodDate] = translationState.copy(
                            isExplanationTranslating = false
                        )
                    }
                    else -> {}
                }
                updateTranslationStates()
            }
        }
    }
    
    private fun updateTranslationStates() {
        _translationStates.value = translations.toMap()
    }
    
    /** Get APOD translation state */
    fun getTranslationState(apodDate: String): TranslationState {
        return translations[apodDate] ?: TranslationState()
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val recentApods: List<Apod>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/** APOD translation state tracker */
data class TranslationState(
    val titleTranslation: Translation? = null,
    val explanationTranslation: Translation? = null,
    val isTitleTranslating: Boolean = false,
    val isExplanationTranslating: Boolean = false
)