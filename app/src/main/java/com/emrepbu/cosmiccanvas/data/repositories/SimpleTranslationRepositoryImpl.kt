package com.emrepbu.cosmiccanvas.data.repositories

import android.util.Log
import com.emrepbu.cosmiccanvas.data.api.SimpleTranslateService
import com.emrepbu.cosmiccanvas.data.database.dao.TranslationDao
import com.emrepbu.cosmiccanvas.data.database.entities.TranslationEntity
import com.emrepbu.cosmiccanvas.domain.models.Translation
import com.emrepbu.cosmiccanvas.domain.repositories.TranslationRepository
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Implementation of [TranslationRepository] using URL-based Google Translate API */
@Singleton
class SimpleTranslationRepositoryImpl @Inject constructor(
    private val translateService: SimpleTranslateService,
    private val translationDao: TranslationDao
) : TranslationRepository {

    override suspend fun translateText(
        sourceText: String, 
        targetLanguage: String, 
        sourceLanguage: String?
    ): NetworkUtils.Result<Translation> = withContext(Dispatchers.IO) {
        try {
            // Check cache first
            val cachedTranslation = translationDao.getTranslation(sourceText, targetLanguage)
            if (cachedTranslation != null) {
                // Check if cache is still valid
                val cacheDurationMillis = TimeUnit.DAYS.toMillis(Constants.TRANSLATION_CACHE_EXPIRY_DAYS.toLong())
                val now = System.currentTimeMillis()
                Log.d("TranslationRepo", "From cache: "+cachedTranslation.translatedText)

                if (now - cachedTranslation.timestamp < cacheDurationMillis) {
                    return@withContext NetworkUtils.Result.Success(
                        Translation(
                            sourceText = cachedTranslation.sourceText,
                            translatedText = cachedTranslation.translatedText,
                            sourceLanguage = cachedTranslation.sourceLanguage,
                            targetLanguage = cachedTranslation.targetLanguage,
                            timestamp = cachedTranslation.timestamp
                        )
                    )
                }
            }
            
            // Not in cache or expired, get from service
            val result = translateService.translateText(sourceText, targetLanguage, sourceLanguage)
            
            return@withContext if (result.isSuccess) {
                val translation = result.getOrNull()!!
                
                // Cache the translation
                val translationEntity = TranslationEntity(
                    sourceText = translation.sourceText,
                    translatedText = translation.translatedText,
                    sourceLanguage = translation.sourceLanguage ?: sourceLanguage ?: "en",
                    targetLanguage = translation.targetLanguage,
                    timestamp = System.currentTimeMillis()
                )
                translationDao.insertTranslation(translationEntity)
                
                NetworkUtils.Result.Success(translation)
            } else {
                NetworkUtils.Result.Error(result.exceptionOrNull() ?: Throwable("Unknown translation error"))
            }
        } catch (e: Exception) {
            NetworkUtils.Result.Error(e)
        }
    }

    override suspend fun translateTexts(
        sourceTexts: List<String>, 
        targetLanguage: String, 
        sourceLanguage: String?
    ): NetworkUtils.Result<List<Translation>> = withContext(Dispatchers.IO) {
        if (sourceTexts.isEmpty()) {
            return@withContext NetworkUtils.Result.Success(emptyList())
        }
        
        try {
            // Check if any translations are cached
            val results = mutableListOf<Translation>()
            val textsToTranslate = mutableListOf<String>()
            val cacheMap = mutableMapOf<String, TranslationEntity>()
            
            // Check cache for each text
            sourceTexts.forEach { text ->
                val cachedTranslation = translationDao.getTranslation(text, targetLanguage)
                if (cachedTranslation != null) {
                    val cacheDurationMillis = TimeUnit.DAYS.toMillis(Constants.TRANSLATION_CACHE_EXPIRY_DAYS.toLong())
                    val now = System.currentTimeMillis()
                    
                    if (now - cachedTranslation.timestamp < cacheDurationMillis) {
                        cacheMap[text] = cachedTranslation // Valid cache
                    } else {
                        textsToTranslate.add(text) // Expired
                    }
                } else {
                    textsToTranslate.add(text) // Not in cache
                }
            }
            
            // Add cached translations to results
            results.addAll(cacheMap.values.map {
                Translation(
                    sourceText = it.sourceText,
                    translatedText = it.translatedText,
                    sourceLanguage = it.sourceLanguage,
                    targetLanguage = it.targetLanguage,
                    timestamp = it.timestamp
                )
            })
            
            // If all translations were cached, return results
            if (textsToTranslate.isEmpty()) {
                return@withContext NetworkUtils.Result.Success(results)
            }
            
            // Translate remaining texts
            val result = translateService.translateTexts(textsToTranslate, targetLanguage, sourceLanguage)
            
            return@withContext if (result.isSuccess) {
                val translations = result.getOrNull()!!
                
                // Cache new translations
                val newTranslationEntities = translations.map { translation ->
                    TranslationEntity(
                        sourceText = translation.sourceText,
                        translatedText = translation.translatedText,
                        sourceLanguage = translation.sourceLanguage ?: sourceLanguage ?: "en",
                        targetLanguage = translation.targetLanguage,
                        timestamp = System.currentTimeMillis()
                    )
                }
                translationDao.insertTranslations(newTranslationEntities)
                
                // Add new translations to results
                results.addAll(translations)
                
                NetworkUtils.Result.Success(results)
            } else {
                NetworkUtils.Result.Error(result.exceptionOrNull() ?: Throwable("Unknown translation error"))
            }
        } catch (e: Exception) {
            NetworkUtils.Result.Error(e)
        }
    }

    override suspend fun isTranslationApiKeyValid(): Boolean {
        // This API doesn't use a key, so it's always valid
        return true
    }

    override suspend fun clearTranslationCache() {
        // Clear all translations from cache
        translationDao.clearAllTranslations()
    }
}