package com.emrepbu.cosmiccanvas.presentation.details

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.emrepbu.cosmiccanvas.R
import com.emrepbu.cosmiccanvas.presentation.components.ErrorMessage
import com.emrepbu.cosmiccanvas.presentation.components.ImageViewer
import com.emrepbu.cosmiccanvas.presentation.components.LanguageSelectionDialog
import com.emrepbu.cosmiccanvas.presentation.components.LoadingIndicator
import com.emrepbu.cosmiccanvas.presentation.components.TranslateButton
import com.emrepbu.cosmiccanvas.ui.components.TranslatedText
import com.emrepbu.cosmiccanvas.utils.DateUtils
import com.emrepbu.cosmiccanvas.utils.openNasaApodWebsite
import com.emrepbu.cosmiccanvas.utils.shareApod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    apodDate: String,
    onBackClick: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val translationState by viewModel.translationState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    
    var showFullScreenImage by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(showFullScreenImage) {
        if (showFullScreenImage) {
            // Auto-hide controls after a delay
            kotlinx.coroutines.delay(3000)
            showControls = false
        }
    }
    
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = !showFullScreenImage || showControls,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                TopAppBar(
                    title = {
                        when (uiState) {
                            is DetailsUiState.Success -> {
                                val apod = (uiState as DetailsUiState.Success).apod
                                TranslatedText(
                                    originalText = apod.title,
                                    translatedText = translationState.titleTranslation?.translatedText,
                                    isTranslating = translationState.isTitleTranslating,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            else -> {
                                Text(
                                    text = stringResource(id = R.string.apod_details),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is DetailsUiState.Loading -> {
                    LoadingIndicator()
                }
                is DetailsUiState.Error -> {
                    val errorState = uiState as DetailsUiState.Error
                    ErrorMessage(
                        message = errorState.message,
                        onRetry = { viewModel.loadApod() },
                        isNetworkError = errorState.message.contains("network", ignoreCase = true) ||
                                errorState.message.contains("internet", ignoreCase = true) ||
                                errorState.message.contains("connection", ignoreCase = true)
                    )
                }
                is DetailsUiState.Success -> {
                    val apod = (uiState as DetailsUiState.Success).apod
                    val formattedDate = DateUtils.parseNasaDate(apod.date).let { DateUtils.formatToDisplayDate(it) }
                    
                    if (showFullScreenImage) {
                        // Full-screen image viewer
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            if (event.changes.isNotEmpty()) {
                                                showControls = !showControls
                                            }
                                        }
                                    }
                                }
                        ) {
                            ImageViewer(
                                apod = apod,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Controls overlay (fade in/out)
                            AnimatedVisibility(
                                visible = showControls,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Top controls: Back button
                                    IconButton(
                                        onClick = { showFullScreenImage = false },
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(16.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                                shape = MaterialTheme.shapes.small
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(id = R.string.exit_fullscreen),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    
                                    // Bottom controls: Actions
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        IconButton(onClick = { viewModel.toggleFavorite(!apod.isFavorite) }) {
                                            Icon(
                                                imageVector = if (apod.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = if (apod.isFavorite) stringResource(id = R.string.remove_from_favorites) else stringResource(id = R.string.add_to_favorites),
                                                tint = if (apod.isFavorite) MaterialTheme.colorScheme.primary else Color.White
                                            )
                                        }
                                        
                                        IconButton(onClick = { context.shareApod(apod) }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Share,
                                                contentDescription = stringResource(id = R.string.share),
                                                tint = Color.White
                                            )
                                        }
                                        
                                        IconButton(onClick = { uriHandler.openNasaApodWebsite(apod.date) }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Face,
                                                contentDescription = stringResource(id = R.string.open_in_browser),
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Regular detail view
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = apod.url,
                                    contentDescription = apod.title,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .clickable(onClick = { showFullScreenImage = true })
                                )
                                
                                if (apod.isVideo) {
                                    // Show play button overlay for videos
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(64.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                                shape = MaterialTheme.shapes.small
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayArrow,
                                            contentDescription = stringResource(id = R.string.play_video),
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                            
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Title with translation support
                                TranslatedText(
                                    originalText = apod.title,
                                    translatedText = translationState.titleTranslation?.translatedText,
                                    isTranslating = translationState.isTitleTranslating,
                                    textStyle = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                if (apod.copyright.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = "© ${apod.copyright}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Explanation with translation support
                                TranslatedText(
                                    originalText = apod.explanation,
                                    translatedText = translationState.explanationTranslation?.translatedText,
                                    isTranslating = translationState.isExplanationTranslating,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    IconButton(onClick = { viewModel.toggleFavorite(!apod.isFavorite) }) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = if (apod.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = if (apod.isFavorite) stringResource(id = R.string.remove_from_favorites) else stringResource(id = R.string.add_to_favorites),
                                                tint = if (apod.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (apod.isFavorite) stringResource(id = R.string.favorited) else stringResource(id = R.string.favorite),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                    
                                    IconButton(onClick = { context.shareApod(apod) }) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Rounded.Share,
                                                contentDescription = stringResource(id = R.string.share)
                                            )
                                            Text(
                                                text = stringResource(id = R.string.share),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                    
                                    IconButton(onClick = { uriHandler.openNasaApodWebsite(apod.date) }) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Rounded.Face,
                                                contentDescription = stringResource(id = R.string.open_in_browser)
                                            )
                                            Text(
                                                text = stringResource(id = R.string.nasa_site),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                    
                                    TranslateButton(
                                        onTranslateToAppLanguage = {
                                            viewModel.translateToAppLanguage(apod.title, apod.explanation)
                                        },
                                        onShowLanguageSelection = {
                                            showLanguageDialog = true
                                        },
                                        showLabel = true
                                    )
                                }
                            }
                        }
                    }
                    
                    // Language selection dialog
                    if (showLanguageDialog) {
                        val currentLanguageCode = remember { viewModel.localeManager.getCurrentLanguageCode() ?: "" }
                        
                        LanguageSelectionDialog(
                            onDismiss = { showLanguageDialog = false },
                            onLanguageSelected = { languageCode ->
                                viewModel.translateToLanguage(apod.title, apod.explanation, languageCode)
                            },
                            currentLanguageCode = currentLanguageCode,
                            localeManager = viewModel.localeManager
                        )
                    }
                }
            }
        }
    }
}

