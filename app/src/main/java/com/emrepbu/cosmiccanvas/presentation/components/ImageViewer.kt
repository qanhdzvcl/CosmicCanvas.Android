package com.emrepbu.cosmiccanvas.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emrepbu.cosmiccanvas.domain.models.Apod

@Composable
fun ImageViewer(
    apod: Apod,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableFloatStateOf(0f) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clip(RectangleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(apod.url)
                .crossfade(true)
                .build(),
            contentDescription = apod.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = rotation
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotationChange ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        
                        // Adjust pan based on scale
                        val maxX = (size.width * (scale - 1)) / 2
                        val maxY = (size.height * (scale - 1)) / 2
                        
                        val newOffset = Offset(
                            x = offset.x + pan.x,
                            y = offset.y + pan.y
                        )
                        
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxX, maxX),
                            y = newOffset.y.coerceIn(-maxY, maxY)
                        )
                        
                        // Optional: Enable rotation
                        // rotation += rotationChange
                    }
                }
        )
    }
}