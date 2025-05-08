package com.emrepbu.cosmiccanvas.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emrepbu.cosmiccanvas.data.database.entities.TranslationEntity

@Dao
interface TranslationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translation: TranslationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslations(translations: List<TranslationEntity>)
    
    @Query("SELECT * FROM translations WHERE sourceText = :sourceText AND targetLanguage = :targetLanguage")
    suspend fun getTranslation(sourceText: String, targetLanguage: String): TranslationEntity?
    
    @Query("DELETE FROM translations WHERE timestamp < :expiryTime")
    suspend fun deleteExpiredTranslations(expiryTime: Long)
    
    @Query("DELETE FROM translations")
    suspend fun clearAllTranslations()
}