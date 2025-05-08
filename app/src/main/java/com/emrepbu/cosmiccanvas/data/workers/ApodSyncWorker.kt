package com.emrepbu.cosmiccanvas.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.DateUtils
import com.emrepbu.cosmiccanvas.utils.NetworkUtils
import com.emrepbu.cosmiccanvas.utils.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@HiltWorker
class ApodSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apodRepository: ApodRepository,
    private val userPreferencesManager: UserPreferencesManager,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get today's APOD
            val todayDate = DateUtils.getTodayDateString()
            val result = apodRepository.refreshApod(todayDate)
            
            // Show notification if enabled
            val notificationsEnabled = userPreferencesManager.notificationsEnabledFlow.firstOrNull() ?: true
            
            if (notificationsEnabled && result is NetworkUtils.Result.Success) {
                val apod = result.data
                if (apod != null) {
                    // Show general APOD notification
                    notificationHelper.showApodNotification(apod)
                    
                    // Check for keyword matches
                    checkForKeywordMatches(apod)
                }
            }
            
            // Sync recent APODs in the background (last 7 days)
            syncRecentApods()
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun syncRecentApods() {
        val endDate = DateUtils.getTodayDateString()
        val calendar = DateUtils.parseNasaDate(endDate).toCalendar()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
        val startDate = DateUtils.formatToNasaDate(calendar.time)
        
        apodRepository.refreshApodRange(startDate, endDate)
    }
    
    private suspend fun checkForKeywordMatches(apod: Apod) {
        val watchedKeywords = userPreferencesManager.watchedKeywordsFlow.firstOrNull() ?: emptySet()
        
        if (watchedKeywords.isNotEmpty()) {
            // Check title and explanation for keyword matches
            val contentToCheck = "${apod.title.lowercase()} ${apod.explanation.lowercase()}"
            
            for (keyword in watchedKeywords) {
                if (contentToCheck.contains(keyword.lowercase())) {
                    notificationHelper.showKeywordMatchNotification(apod, keyword)
                    break // Only show one notification even if multiple keywords match
                }
            }
        }
    }
    
    private fun java.util.Date.toCalendar(): java.util.Calendar {
        return java.util.Calendar.getInstance().apply { time = this@toCalendar }
    }
}