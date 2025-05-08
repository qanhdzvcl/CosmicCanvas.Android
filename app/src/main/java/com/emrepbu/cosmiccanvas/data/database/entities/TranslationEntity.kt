package com.emrepbu.cosmiccanvas.data.database.entities

import androidx.room.Entity
import androidx.room.Index

/** Translation cache entity */
@Entity(
    tableName = "translations",
    primaryKeys = ["sourceText", "targetLanguage"],
    indices = [
        Index("sourceText"),
        Index("targetLanguage")
    ]
)
data class TranslationEntity(
    val sourceText: String,
    val translatedText: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val timestamp: Long = System.currentTimeMillis()
)