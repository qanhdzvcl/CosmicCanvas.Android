package com.emrepbu.cosmiccanvas.domain.models

/** Translation domain model */
data class Translation(
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val timestamp: Long = System.currentTimeMillis()
)