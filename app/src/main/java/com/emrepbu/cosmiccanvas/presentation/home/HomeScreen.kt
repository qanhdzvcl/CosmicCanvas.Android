package com.emrepbu.cosmiccanvas.presentation.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emrepbu.cosmiccanvas.R
import com.emrepbu.cosmiccanvas.presentation.components.ApodCard
import com.emrepbu.cosmiccanvas.presentation.components.ErrorMessage
import com.emrepbu.cosmiccanvas.presentation.components.LoadingIndicator
import com.emrepbu.cosmiccanvas.utils.ScreenSaverTimeout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onApodClick: (String) -> Unit,
    onScreenSaverTriggered: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val screenSaverTimeoutSeconds by viewModel.screenSaverTimeoutSeconds.collectAsStateWithLifecycle()
    val translationStates by viewModel.translationStates.collectAsStateWithLifecycle()

    ScreenSaverTimeout(
        timeoutSeconds = screenSaverTimeoutSeconds,
        onTimeout = onScreenSaverTriggered
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                modifier = Modifier.shadow(4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    LoadingIndicator()
                }

                is HomeUiState.Error -> {
                    val errorState = uiState as HomeUiState.Error
                    ErrorMessage(
                        message = errorState.message,
                        onRetry = { viewModel.loadRecentApods() },
                        isNetworkError = errorState.message.contains(
                            "network",
                            ignoreCase = true
                        ) ||
                                errorState.message.contains("internet", ignoreCase = true) ||
                                errorState.message.contains("connection", ignoreCase = true)
                    )
                }

                is HomeUiState.Success -> {
                    val successState = uiState as HomeUiState.Success

                    if (successState.recentApods.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_apods_found),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        // Display recent APODs in a horizontal pager
                        val pagerState = rememberPagerState(
                            initialPage = 0,
                            pageCount = { successState.recentApods.size }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.recent_astronomy_pictures),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) { page ->
                                val apod = successState.recentApods[page]
                                val translationState =
                                    translationStates[apod.date] ?: TranslationState()

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ApodCard(
                                        apod = apod,
                                        onApodClick = onApodClick,
                                        onFavoriteClick = viewModel::toggleFavorite,
                                        onTranslateToAppLanguage = { title, explanation ->
                                            viewModel.translateToAppLanguage(
                                                apod.date,
                                                title,
                                                explanation
                                            )
                                        },
                                        onTranslateToLanguage = { title, explanation, languageCode ->
                                            viewModel.translateToLanguage(
                                                apod.date,
                                                title,
                                                explanation,
                                                languageCode
                                            )
                                        },
                                        titleTranslation = translationState.titleTranslation,
                                        explanationTranslation = translationState.explanationTranslation,
                                        isTranslatingTitle = translationState.isTitleTranslating,
                                        isTranslatingExplanation = translationState.isExplanationTranslating,
                                        localeManager = viewModel.localeManager
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}