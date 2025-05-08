package com.emrepbu.cosmiccanvas.domain.repositories

import com.emrepbu.cosmiccanvas.domain.models.Translation
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result

interface TranslationRepository {
    /** Translate single text
     * @param sourceText Text to translate
     * @param targetLanguage Target language code
     * @param sourceLanguage Source language code (auto if null)
     */
    suspend fun translateText(
        sourceText: String,
        targetLanguage: String,
        sourceLanguage: String? = null
    ): Result<Translation>
    
    /** Translate multiple texts
     * @param sourceTexts Texts to translate
     * @param targetLanguage Target language code
     * @param sourceLanguage Source language code (auto if null)
     */
    suspend fun translateTexts(
        sourceTexts: List<String>,
        targetLanguage: String,
        sourceLanguage: String? = null
    ): Result<List<Translation>>
    
    /** Check API key validity */
    suspend fun isTranslationApiKeyValid(): Boolean
    
    /** Clear translation cache */
    suspend fun clearTranslationCache()
}