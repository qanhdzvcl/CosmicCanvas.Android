package com.emrepbu.cosmiccanvas.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.UriHandler
import com.emrepbu.cosmiccanvas.data.api.models.ApodDto
import com.emrepbu.cosmiccanvas.data.database.entities.ApodEntity
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.utils.DateUtils.parseNasaDate
import kotlinx.coroutines.delay

fun ApodDto.toApod(): Apod {
    return Apod(
        date = this.date,
        title = this.title,
        explanation = this.explanation,
        url = this.url,
        mediaType = this.mediaType,
        thumbnailUrl = this.thumbnailUrl ?: this.url,
        copyright = this.copyright ?: "",
        isFavorite = false
    )
}

fun ApodDto.toEntity(): ApodEntity {
    return ApodEntity(
        date = this.date,
        title = this.title,
        explanation = this.explanation,
        url = this.url,
        mediaType = this.mediaType,
        thumbnailUrl = this.thumbnailUrl ?: this.url,
        copyright = this.copyright ?: "",
        isFavorite = false
    )
}

fun ApodEntity.toApod(): Apod {
    return Apod(
        date = this.date,
        title = this.title,
        explanation = this.explanation,
        url = this.url,
        mediaType = this.mediaType,
        thumbnailUrl = this.thumbnailUrl,
        copyright = this.copyright,
        isFavorite = this.isFavorite
    )
}

fun Apod.toEntity(): ApodEntity {
    return ApodEntity(
        date = this.date,
        title = this.title,
        explanation = this.explanation,
        url = this.url,
        mediaType = this.mediaType,
        thumbnailUrl = this.thumbnailUrl,
        copyright = this.copyright,
        isFavorite = this.isFavorite
    )
}

// LazyList Utilities
fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    var previousFirstVisibleItemIndex by remember { mutableStateOf(firstVisibleItemIndex) }
    var previousFirstVisibleItemScrollOffset by remember { mutableStateOf(firstVisibleItemScrollOffset) }

    return remember {
        derivedStateOf {
            if (previousFirstVisibleItemIndex != firstVisibleItemIndex) {
                val result = previousFirstVisibleItemIndex > firstVisibleItemIndex
                previousFirstVisibleItemIndex = firstVisibleItemIndex
                previousFirstVisibleItemScrollOffset = firstVisibleItemScrollOffset
                result
            } else {
                val result = previousFirstVisibleItemScrollOffset > firstVisibleItemScrollOffset
                previousFirstVisibleItemScrollOffset = firstVisibleItemScrollOffset
                result
            }
        }
    }.value
}

// Screen Saver Timeout
@Composable
fun ScreenSaverTimeout(
    timeoutSeconds: Int = Constants.DEFAULT_SCREEN_SAVER_DELAY_SECONDS,
    onTimeout: () -> Unit
) {
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastInteractionTime) {
        while (true) {
            delay(1000)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteractionTime > timeoutSeconds * 1000) {
                onTimeout()
                break
            }
        }
    }

    fun resetTimer() {
        lastInteractionTime = System.currentTimeMillis()
    }

    LaunchedEffect(Unit) {
        resetTimer()
    }
}

// Sharing
fun Context.shareApod(apod: Apod) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, apod.title)

        val shareText = """
            ${apod.title} (${apod.date})
            
            ${apod.explanation}
            
            ${apod.url}
            
            Shared from Cosmic Canvas
        """.trimIndent()

        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    startActivity(Intent.createChooser(shareIntent, "Share APOD"))
}

fun UriHandler.openNasaApodWebsite(date: String) {
    val dateObject = parseNasaDate(date)
    val formattedDate = DateUtils.formatToNasaDate(dateObject)
    val url = "https://apod.nasa.gov/apod/ap${formattedDate.substring(2, 4)}${
        formattedDate.substring(
            5,
            7
        )
    }${formattedDate.substring(8, 10)}.html"
    openUri(url)
}

fun UriHandler.openExternalLink(url: String) {
    openUri(url)
}