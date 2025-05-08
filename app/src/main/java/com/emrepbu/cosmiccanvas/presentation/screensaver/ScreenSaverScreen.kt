package com.emrepbu.cosmiccanvas.presentation.screensaver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.presentation.home.HomeUiState
import com.emrepbu.cosmiccanvas.presentation.home.HomeViewModel
import com.emrepbu.cosmiccanvas.utils.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenSaverScreen(
    onExitScreenSaver: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var apods by remember { mutableStateOf<List<Apod>>(emptyList()) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { apods.size }
    )
    
    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.Success) {
            apods = (uiState as HomeUiState.Success).recentApods
        }
    }
    
    // Auto-advance to next image
    LaunchedEffect(apods) {
        if (apods.isNotEmpty()) {
            while (true) {
                delay(Constants.SCREEN_SAVER_TRANSITION_DURATION + 5000L) // Show image for 5 seconds + transition time
                val nextPage = (pagerState.currentPage + 1) % apods.size
                pagerState.animateScrollToPage(
                    nextPage,
                    animationSpec = tween(
                        durationMillis = Constants.SCREEN_SAVER_TRANSITION_DURATION.toInt(),
                        easing = LinearEasing
                    )
                )
            }
        }
    }
    
    // Exit on tap
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onExitScreenSaver
            )
    ) {
        if (apods.isNotEmpty()) {
            // Animated crossfade between images
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
                userScrollEnabled = false
            ) { page ->
                val apod = apods[page]
                val context = LocalContext.current
                
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background image
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(apod.url)
                            .crossfade(true)
                            .build(),
                        contentDescription = apod.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Overlay with image info
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(24.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = "${apod.title}\n${apod.date}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        } else {
            // Show loading or error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading images...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}