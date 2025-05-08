package com.emrepbu.cosmiccanvas.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.models.Translation
import com.emrepbu.cosmiccanvas.domain.usecases.GetApodUseCase
import com.emrepbu.cosmiccanvas.domain.usecases.RefreshApodUseCase
import com.emrepbu.cosmiccanvas.domain.usecases.ToggleFavoriteUseCase
import com.emrepbu.cosmiccanvas.domain.usecases.TranslateTextUseCase
import com.emrepbu.cosmiccanvas.presentation.home.TranslationState
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getApodUseCase: GetApodUseCase,
    private val refreshApodUseCase: RefreshApodUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val translateTextUseCase: TranslateTextUseCase,
    val localeManager: LocaleManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val apodDate: String = checkNotNull(savedStateHandle[Constants.NAV_ARG_APOD_DATE])
    
    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
    
    // Translation state
    private val _translationState = MutableStateFlow(TranslationState())
    val translationState: StateFlow<TranslationState> = _translationState.asStateFlow()
    
    init {
        loadApod()
    }
    
    fun loadApod() {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            
            getApodUseCase(apodDate).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val apod = result.data
                        if (apod != null) {
                            _uiState.value = DetailsUiState.Success(apod)
                        } else {
                            refreshApod()
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = DetailsUiState.Error(result.exception.message ?: "Unknown error")
                        refreshApod()
                    }
                    is Result.Loading -> {
                        _uiState.value = DetailsUiState.Loading
                    }
                }
            }
        }
    }
    
    private fun refreshApod() {
        viewModelScope.launch {
            val result = refreshApodUseCase(apodDate)
            if (result is Result.Error) {
                _uiState.value = DetailsUiState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }
    
    fun toggleFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(apodDate, isFavorite)
        }
    }
    
    /** Translate APOD to app language */
    fun translateToAppLanguage(title: String, explanation: String) {
        viewModelScope.launch {
            _translationState.value = _translationState.value.copy(
                isTitleTranslating = title.isNotBlank(),
                isExplanationTranslating = explanation.isNotBlank()
            )
            
            // Translate title
            if (title.isNotBlank()) {
                when (val result = translateTextUseCase.toAppLanguage(title)) {
                    is Result.Success -> {
                        _translationState.value = _translationState.value.copy(
                            titleTranslation = result.data,
                            isTitleTranslating = false
                        )
                    }
                    is Result.Error -> {
                        _translationState.value = _translationState.value.copy(
                            isTitleTranslating = false
                        )
                    }
                    else -> {}
                }
            }
            
            // Translate explanation
            if (explanation.isNotBlank()) {
                when (val result = translateTextUseCase.toAppLanguage(explanation)) {
                    is Result.Success -> {
                        _translationState.value = _translationState.value.copy(
                            explanationTranslation = result.data,
                            isExplanationTranslating = false
                        )
                    }
                    is Result.Error -> {
                        _translationState.value = _translationState.value.copy(
                            isExplanationTranslating = false
                        )
                    }
                    else -> {}
                }
            }
        }
    }
    
    /** Translate APOD to specific language */
    fun translateToLanguage(title: String, explanation: String, languageCode: String) {
        viewModelScope.launch {
            _translationState.value = _translationState.value.copy(
                isTitleTranslating = title.isNotBlank(),
                isExplanationTranslating = explanation.isNotBlank()
            )
            
            // Translate title
            if (title.isNotBlank()) {
                when (val result = translateTextUseCase.toLanguage(title, languageCode)) {
                    is Result.Success -> {
                        _translationState.value = _translationState.value.copy(
                            titleTranslation = result.data,
                            isTitleTranslating = false
                        )
                    }
                    is Result.Error -> {
                        _translationState.value = _translationState.value.copy(
                            isTitleTranslating = false
                        )
                    }
                    else -> {}
                }
            }
            
            // Translate explanation
            if (explanation.isNotBlank()) {
                when (val result = translateTextUseCase.toLanguage(explanation, languageCode)) {
                    is Result.Success -> {
                        _translationState.value = _translationState.value.copy(
                            explanationTranslation = result.data,
                            isExplanationTranslating = false
                        )
                    }
                    is Result.Error -> {
                        _translationState.value = _translationState.value.copy(
                            isExplanationTranslating = false
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

sealed class DetailsUiState {
    data object Loading : DetailsUiState()
    data class Success(val apod: Apod) : DetailsUiState()
    data class Error(val message: String) : DetailsUiState()
}