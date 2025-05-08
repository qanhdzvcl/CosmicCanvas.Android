package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import com.emrepbu.cosmiccanvas.domain.models.Translation
import com.emrepbu.cosmiccanvas.domain.repositories.TranslationRepository
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TranslateTextUseCase @Inject constructor(
    private val translationRepository: TranslationRepository,
    private val userPreferencesManager: UserPreferencesManager
) {
    /** Translate to app language */
    suspend fun toAppLanguage(text: String): Result<Translation> {
        // Get app language
        val languageCode = userPreferencesManager.getLanguageCode().first()
        
        // Default to English if needed
        val targetLanguage = if (languageCode.isEmpty() || languageCode == LocaleManager.LANGUAGE_ENGLISH) {
            LocaleManager.LANGUAGE_ENGLISH
        } else {
            languageCode
        }
        
        return translationRepository.translateText(
            sourceText = text,
            targetLanguage = targetLanguage
        )
    }
    
    /** Translate to specific language */
    suspend fun toLanguage(text: String, targetLanguage: String): Result<Translation> {
        // Save language to recents
        userPreferencesManager.addRecentlyUsedLanguage(targetLanguage)
        
        return translationRepository.translateText(
            sourceText = text,
            targetLanguage = targetLanguage
        )
    }
    
    /** Translate multiple texts to app language */
    suspend fun multipleToAppLanguage(texts: List<String>): Result<List<Translation>> {
        // Get app language
        val languageCode = userPreferencesManager.getLanguageCode().first()
        
        // Default to English if needed
        val targetLanguage = if (languageCode.isEmpty() || languageCode == LocaleManager.LANGUAGE_ENGLISH) {
            LocaleManager.LANGUAGE_ENGLISH
        } else {
            languageCode
        }
        
        return translationRepository.translateTexts(
            sourceTexts = texts,
            targetLanguage = targetLanguage
        )
    }
    
    /** Translate multiple texts to specific language */
    suspend fun multipleToLanguage(texts: List<String>, targetLanguage: String): Result<List<Translation>> {
        // Save language to recents
        userPreferencesManager.addRecentlyUsedLanguage(targetLanguage)
        
        return translationRepository.translateTexts(
            sourceTexts = texts,
            targetLanguage = targetLanguage
        )
    }
}