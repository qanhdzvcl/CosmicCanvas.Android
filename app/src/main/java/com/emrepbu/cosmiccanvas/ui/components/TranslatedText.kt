package com.emrepbu.cosmiccanvas.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth

/** Text component with translation support
 * @param originalText Source text
 * @param translatedText Translated version (optional)
 * @param isTranslating Translation in progress flag
 * @param textStyle Text styling
 * @param modifier Component modifier
 */
@Composable
fun TranslatedText(
    originalText: String,
    translatedText: String? = null,
    isTranslating: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    // Track translation availability
    val isTranslationAvailable = !translatedText.isNullOrBlank()

    // Automatically show translated text when it becomes available
    var showTranslatedText by remember { mutableStateOf(false) }

    // Update showTranslatedText when translation becomes available
    LaunchedEffect(isTranslationAvailable) {
        if (isTranslationAvailable) {
            showTranslatedText = true
        }
    }

    var showMenu by remember { mutableStateOf(false) }

    // Log translation state for debugging
    Log.d(
        "TranslatedText",
        "Text: $originalText, Translated: $translatedText, Show: $showTranslatedText, Available: $isTranslationAvailable, isTranslating: $isTranslating"
    )

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                enabled = isTranslationAvailable || isTranslating,
                onClick = { showMenu = true }
            )
        ) {
            // Display text with animation when switching between original and translated
            AnimatedContent(
                targetState = when {
                    isTranslating -> "loading"
                    showTranslatedText && isTranslationAvailable -> "translated"
                    else -> "original"
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TextAnimation"
            ) { state ->
                when (state) {
                    "loading" -> CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp
                    )

                    "translated" -> {
                        // Log when actually displaying translated text
                        val fullText = translatedText ?: ""
                        Log.d("TranslatedText", "DISPLAYING TRANSLATION: $fullText")
                        Text(
                            text = fullText,
                            style = textStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> Text(
                        text = originalText,
                        style = textStyle,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Only show translation icon if translation is available or in progress
            if (isTranslationAvailable || isTranslating) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.Translate,
                    contentDescription = "Translation available",
                    modifier = Modifier.padding(4.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Show Original") },
                onClick = {
                    showTranslatedText = false
                    showMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Show Translated") },
                onClick = {
                    showTranslatedText = true
                    showMenu = false
                },
                enabled = isTranslationAvailable
            )
        }
    }
}