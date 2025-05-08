package com.emrepbu.cosmiccanvas.domain.repositories

import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import kotlinx.coroutines.flow.Flow

interface ApodRepository {
    /**
     * Get APOD by specific date
     */
    fun getApodByDate(date: String): Flow<Result<Apod?>>
    
    /**
     * Get recent APODs for the main feed
     */
    fun getRecentApods(count: Int): Flow<Result<List<Apod>>>
    
    /**
     * Get APODs between a date range
     */
    fun getApodsBetweenDates(startDate: String, endDate: String): Flow<Result<List<Apod>>>
    
    /**
     * Get all favorite APODs
     */
    fun getFavoriteApods(): Flow<Result<List<Apod>>>
    
    /**
     * Force refresh APOD for a specific date from network
     */
    suspend fun refreshApod(date: String): Result<Apod>
    
    /**
     * Refresh multiple APODs within a date range
     */
    suspend fun refreshApodRange(startDate: String, endDate: String): Result<List<Apod>>
    
    /**
     * Toggle favorite status for an APOD
     */
    suspend fun toggleFavorite(date: String, isFavorite: Boolean)
    
    /**
     * Search APODs by keyword
     */
    fun searchApods(keyword: String): Flow<Result<List<Apod>>>
}