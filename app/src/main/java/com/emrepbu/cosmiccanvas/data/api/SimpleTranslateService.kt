package com.emrepbu.cosmiccanvas.data.api

import android.util.Log
import com.emrepbu.cosmiccanvas.domain.models.Translation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/** URL-based Google Translate API service (no API key required) */
@Singleton
class SimpleTranslateService @Inject constructor() {

    private val TAG = "SimpleTranslateService"

    private val baseUrl = "https://clients5.google.com/translate_a/t" // Alternative endpoint to avoid rate limiting
    
    // Retry constants
    private val MAX_RETRIES = 5
    private val INITIAL_BACKOFF_MS = 2000L  // 2 seconds
    private val MAX_BACKOFF_MS = 60000L     // 60 seconds

    /** Translates text to target language
     * @param sourceText Text to translate
     * @param targetLanguage Target language code (ISO 639-1)
     * @param sourceLanguage Source language code (auto-detect if null)
     * @return Translation object containing translated text
     */
    suspend fun translateText(
        sourceText: String,
        targetLanguage: String,
        sourceLanguage: String? = null
    ): Result<Translation> = withContext(Dispatchers.IO) {
        // Use auto detection if source language is not specified
        val sl = sourceLanguage ?: "auto"

        // Encode parameters
        val encodedText = URLEncoder.encode(sourceText, "UTF-8")
        // Use different parameters for this endpoint
        val urlString = "$baseUrl?client=dict-chrome-ex&sl=$sl&tl=$targetLanguage&q=$encodedText"
        Log.d(TAG, "Attempting to translate with URL: $urlString")

        var retryCount = 0
        var currentBackoff = INITIAL_BACKOFF_MS

        while (retryCount < MAX_RETRIES) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                // Add headers to look like a browser request
                connection.setRequestProperty(
                    "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
                )
                connection.setRequestProperty(
                    "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                )
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5")

                when (connection.responseCode) {
                    HttpURLConnection.HTTP_OK -> {
                        // Read downloaded content
                        val translatedContent = connection.inputStream.bufferedReader().use { it.readText() }

                        // Log URL and content
                        Log.d(TAG, "Success! Translate URL: $urlString")
                        Log.d(TAG, "Downloaded translation content: $translatedContent")

                        // Parse translation
                        val cleanedContent = translatedContent.trim()

                        // Handle various response formats
                        val translationText = try {
                            // Log raw content
                            Log.d(TAG, "Raw translation content: $cleanedContent")

                            when {
                                // dict-chrome-ex endpoint format: ["translation", "source", null, null]
                                cleanedContent.startsWith("[") && !cleanedContent.startsWith("[[") -> {
                                    val parts = cleanedContent.trim('[', ']').split(",")
                                    if (parts.isNotEmpty()) {
                                        parts[0].trim('"', ' ')
                                    } else {
                                        cleanedContent
                                    }
                                }
                                // Nested array formats:
                                // 1. [[["translation1",source1"],["translation2",source2"]]]
                                // 2. [["translation","language_code"]]
                                cleanedContent.startsWith("[[") -> {
                                    // Simple format [["text","lang"]]
                                    if (cleanedContent.matches("\\[\\[\"(.+?)\",\"(.+?)\"\\]\\]".toRegex())) {
                                        val simpleRegex = "\\[\\[\"(.+?)\",\"(.+?)\"\\]\\]".toRegex()
                                        val matchResult = simpleRegex.find(cleanedContent)
                                        if (matchResult != null && matchResult.groupValues.size > 1) {
                                            matchResult.groupValues[1] // Return only translation
                                        } else {
                                            cleanedContent // Fallback to cleaned content
                                        }
                                    } else {
                                        // For complex format
                                        val regex = "\\[\\[\\[\"(.+?)\"".toRegex()
                                        val matches = regex.findAll(cleanedContent)
                                        val translations = matches.map { it.groupValues[1] }.toList()

                                        if (translations.isNotEmpty()) {
                                            translations.joinToString(" ")
                                        } else {
                                            // Last attempt - find text inside quotes
                                            val generalRegex = "\"([^\"]+?)\"".toRegex()
                                            val generalMatches = generalRegex.findAll(cleanedContent)
                                            val firstMatch = generalMatches.firstOrNull()?.groupValues?.get(1)

                                            firstMatch ?: cleanedContent
                                        }
                                    }
                                }
                                // Simple text response
                                !cleanedContent.contains("[") && !cleanedContent.contains("{") -> {
                                    cleanedContent
                                }
                                // All other cases
                                else -> {
                                    Log.d(TAG, "Unrecognized translation format, using raw content")
                                    cleanedContent
                                }
                            }
                        } catch (e: Exception) {
                            // Use raw content if parsing fails
                            Log.w(TAG, "Error parsing translation content, using raw content", e)
                            cleanedContent
                        }

                        // Use source text if translation is empty
                        val finalTranslationText = translationText.ifBlank {
                            Log.w(TAG, "Translation returned empty - using original text instead")
                            sourceText
                        }

                        // Log final translation
                        Log.d(TAG, "Final translation: $finalTranslationText")

                        return@withContext Result.success(
                            Translation(
                                sourceText = sourceText,
                                translatedText = finalTranslationText,
                                sourceLanguage = sl,
                                targetLanguage = targetLanguage,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }

                    429 -> {
                        val errorMessage =
                            "Rate limit exceeded (429). Retry attempt ${retryCount + 1}/$MAX_RETRIES"
                        Log.w(TAG, errorMessage)

                        retryCount++
                        if (retryCount < MAX_RETRIES) {
                            Log.d(TAG, "Waiting for $currentBackoff ms before retrying...")
                            delay(currentBackoff)
                            // Exponential backoff
                            currentBackoff = (currentBackoff * 1.5).toLong().coerceAtMost(MAX_BACKOFF_MS)
                            // Add jitter
                            currentBackoff = (currentBackoff * (0.75 + Math.random() * 0.5)).toLong()
                        }
                    }

                    else -> {
                        val errorMessage = "HTTP Error: ${connection.responseCode}"
                        Log.e(TAG, errorMessage)
                        return@withContext Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Translation error", e)
                retryCount++

                // Retry for network errors
                if (retryCount < MAX_RETRIES) {
                    Log.d(TAG, "Waiting for $currentBackoff ms before retrying...")
                    delay(currentBackoff)
                    // Exponential backoff
                    currentBackoff = (currentBackoff * 1.5).toLong().coerceAtMost(MAX_BACKOFF_MS)
                } else {
                    return@withContext Result.failure(e)
                }
            }
        }

        // All retry attempts exhausted
        return@withContext Result.failure(Exception("Exhausted all retry attempts for translation"))
    }

    /** Translates multiple texts to target language
     * @param sourceTexts List of texts to translate
     * @param targetLanguage Target language code (ISO 639-1)
     * @param sourceLanguage Source language code or null (auto-detect)
     * @return List of Translation objects
     */
    suspend fun translateTexts(
        sourceTexts: List<String>,
        targetLanguage: String,
        sourceLanguage: String? = null
    ): Result<List<Translation>> = withContext(Dispatchers.IO) {
        try {
            // Translate each text individually
            val results = mutableListOf<Translation>()

            for ((index, text) in sourceTexts.withIndex()) {
                // Add delay between requests to avoid rate limiting
                if (index > 0) {
                    Log.d(TAG, "Waiting 3000ms between batch translation requests...")
                    delay(3000) // Wait 3 seconds between requests
                }

                val result = translateText(text, targetLanguage, sourceLanguage)
                if (result.isSuccess) {
                    results.add(result.getOrThrow())
                } else {
                    return@withContext Result.failure(
                        result.exceptionOrNull() ?: Exception("Unknown error")
                    )
                }
            }

            Result.success(results)
        } catch (e: Exception) {
            Log.e(TAG, "Error in batch translation", e)
            Result.failure(e)
        }
    }

}