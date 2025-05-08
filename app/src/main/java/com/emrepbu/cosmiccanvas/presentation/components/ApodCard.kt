package com.emrepbu.cosmiccanvas.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emrepbu.cosmiccanvas.R
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.models.Translation
import com.emrepbu.cosmiccanvas.ui.components.TranslatedText
import com.emrepbu.cosmiccanvas.utils.DateUtils
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager
import com.emrepbu.cosmiccanvas.utils.shareApod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodCard(
    apod: Apod,
    onApodClick: (String) -> Unit,
    onFavoriteClick: (String, Boolean) -> Unit,
    onTranslateToAppLanguage: (String, String) -> Unit = { _, _ -> },
    onTranslateToLanguage: (String, String, String) -> Unit = { _, _, _ -> },
    titleTranslation: Translation? = null,
    explanationTranslation: Translation? = null,
    isTranslatingTitle: Boolean = false,
    isTranslatingExplanation: Boolean = false,
    localeManager: LocaleManager? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formattedDate = DateUtils.parseNasaDate(apod.date).let { DateUtils.formatToDisplayDate(it) }
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentTranslationTarget by remember { mutableStateOf("") }
    
    Card(
        onClick = { onApodClick(apod.date) },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box {
                AsyncImage(
                    model = apod.thumbnailUrl,
                    contentDescription = apod.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.medium)
                )
                
                if (apod.isVideo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Video",
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TranslatedText(
                    originalText = apod.title,
                    translatedText = titleTranslation?.translatedText,
                    isTranslating = isTranslatingTitle,
                    textStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TranslatedText(
                    originalText = apod.explanation,
                    translatedText = explanationTranslation?.translatedText,
                    isTranslating = isTranslatingExplanation,
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (apod.copyright.isNotBlank()) {
                        Text(
                            text = "© ${apod.copyright}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    IconButton(onClick = { onFavoriteClick(apod.date, !apod.isFavorite) }) {
                        Icon(
                            imageVector = if (apod.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (apod.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (apod.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(onClick = { context.shareApod(apod) }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TranslateButton(
                        onTranslateToAppLanguage = {
                            onTranslateToAppLanguage(apod.title, apod.explanation)
                        },
                        onShowLanguageSelection = {
                            showLanguageDialog = true
                            currentTranslationTarget = "both"
                        }
                    )
                }
            }
        }
    }
    
    // Language selection dialog
    if (showLanguageDialog && localeManager != null) {
        val currentLanguageCode = remember { localeManager.getCurrentLanguageCode() ?: LocaleManager.LANGUAGE_ENGLISH }
        
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { languageCode ->
                when (currentTranslationTarget) {
                    "title" -> onTranslateToLanguage(apod.title, "", languageCode)
                    "explanation" -> onTranslateToLanguage("", apod.explanation, languageCode)
                    else -> onTranslateToLanguage(apod.title, apod.explanation, languageCode)
                }
            },
            currentLanguageCode = currentLanguageCode,
            localeManager = localeManager
        )
    }
}