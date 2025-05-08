package com.emrepbu.cosmiccanvas.utils.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.emrepbu.cosmiccanvas.MainActivity
import com.emrepbu.cosmiccanvas.R
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    suspend fun showApodNotification(apod: Apod) {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("apodDate", apod.date)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = loadImageBitmap(apod.thumbnailUrl)

        val displayDate = DateUtils.parseNasaDate(apod.date).let { DateUtils.formatToDisplayDate(it) }

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_APOD)
            .setContentTitle("Today's Astronomy Picture")
            .setContentText(apod.title)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${apod.title} ($displayDate)")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_APOD, notification)
    }

    suspend fun showKeywordMatchNotification(apod: Apod, matchedKeyword: String) {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("apodDate", apod.date)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = loadImageBitmap(apod.thumbnailUrl)

        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_KEYWORDS)
            .setContentTitle("Keyword Match: $matchedKeyword")
            .setContentText("Today's APOD features your watched topic: $matchedKeyword")
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(largeIcon)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Today's APOD \"${apod.title}\" features your watched topic: $matchedKeyword")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID_KEYWORD, notification)
    }

    private suspend fun loadImageBitmap(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val drawable = imageLoader.execute(request).drawable
            drawable?.toBitmap()
        } catch (e: Exception) {
            null
        }
    }
}