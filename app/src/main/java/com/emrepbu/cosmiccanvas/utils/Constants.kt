package com.emrepbu.cosmiccanvas.utils

object Constants {
    // Database
    const val DATABASE_NAME = "cosmic_canvas.db"

    // Preferences
    const val USER_PREFERENCES_NAME = "cosmic_canvas_prefs"
    const val PREF_DARK_THEME = "dark_theme"
    const val PREF_SCREEN_SAVER_DELAY = "screen_saver_delay"
    const val PREF_WATCHED_KEYWORDS = "watched_keywords"
    const val PREF_ENABLE_NOTIFICATIONS = "enable_notifications"
    const val PREF_NASA_API_KEY = "nasa_api_key"
    const val PREF_LANGUAGE = "app_language"
    const val PREF_TRANSLATION_API_KEY = "translation_api_key"
    const val PREF_RECENTLY_USED_LANGUAGES = "recently_used_languages"

    // WorkManager
    const val APOD_SYNC_WORK_NAME = "apod_sync_work"

    // Notifications
    const val NOTIFICATION_CHANNEL_APOD = "apod_channel"
    const val NOTIFICATION_CHANNEL_KEYWORDS = "keywords_channel"
    const val NOTIFICATION_ID_APOD = 1001
    const val NOTIFICATION_ID_KEYWORD = 2001

    // Navigation
    const val NAV_ARG_APOD_DATE = "apodDate"

    // Screen saver
    const val DEFAULT_SCREEN_SAVER_DELAY_SECONDS = 3 * 60 // 3 minutes
    const val SCREEN_SAVER_TRANSITION_DURATION = 1500 // 1.5 seconds

    // UI
    const val RECENT_APODS_COUNT = 7 // Number of recent APODs to display in the home screen

    // Translation
    const val TRANSLATION_CACHE_EXPIRY_DAYS = 7 // Cache translation results for a week
}