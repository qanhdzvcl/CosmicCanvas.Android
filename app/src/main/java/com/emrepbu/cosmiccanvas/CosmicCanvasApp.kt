package com.emrepbu.cosmiccanvas

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.emrepbu.cosmiccanvas.data.workers.ApodSyncWorker
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CosmicCanvasApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var localeManager: LocaleManager

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        setupNotificationChannels()
        schedulePeriodicSync()
        setupAppCompatDelegate()
    }

    private fun setupAppCompatDelegate() {
        // Set the locale from preferences
        runBlocking {
            val languageCode = localeManager.userPreferencesManager.getLanguageCode().first()
            localeManager.applyLanguageToAppLocale(languageCode)
        }
    }

    /**
     * Apply custom locale to app context whenever any context is created/updated
     */
    override fun attachBaseContext(base: Context) {
        // We cannot use DataStore or Hilt here since this method is called before Hilt is initialized
        // Just use the default locale for now, the correct locale will be set in onCreate
        super.attachBaseContext(base)
    }

    private fun setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel for APOD updates
            val apodChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_APOD,
                getString(R.string.notification_apod_title),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for daily astronomy picture updates"
                enableVibration(true)
            }

            // Channel for keyword matches
            val keywordChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_KEYWORDS,
                "Keyword Matches",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when APOD content matches your selected keywords"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(apodChannel, keywordChannel))
        }
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<ApodSyncWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            Constants.APOD_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}