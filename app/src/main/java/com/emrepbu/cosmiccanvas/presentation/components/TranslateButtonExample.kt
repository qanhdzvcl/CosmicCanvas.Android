package com.emrepbu.cosmiccanvas.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emrepbu.cosmiccanvas.utils.locale.LanguageOption
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager

/**
 * Example of how to use the TranslateButton component in ApodCard
 *
 * This example shows how to integrate the TranslateButton into an ApodCard
 */
@Composable
fun ApodCardWithTranslateButtonExample(localeManager: LocaleManager) {
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguageCode by remember {
        mutableStateOf(
            localeManager.getCurrentLanguageCode() ?: LocaleManager.LANGUAGE_ENGLISH
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sample APOD Title",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sample APOD explanation text...",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Copyright information or any other content
                Text(
                    text = "© Sample Author",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                // Add TranslateButton alongside other action buttons (favorite, share, etc.)
                TranslateButton(
                    onTranslateToAppLanguage = {
                        // Implement translate to app language functionality
                        println("Translating to app language...")
                    },
                    onShowLanguageSelection = {
                        showLanguageDialog = true
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Other action buttons would go here...
            }
        }
    }

    // Show language selection dialog when needed
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { languageCode ->
                currentLanguageCode = languageCode
                // Implement translation to the selected language
                println("Translating to language: $languageCode")
            },
            currentLanguageCode = currentLanguageCode,
            localeManager = localeManager
        )
    }
}

/**
 * Example of how to use the TranslationOptions separately from the button
 *
 * This demonstrates standalone usage of the TranslationOptions dropdown
 */
@Composable
fun StandaloneTranslationOptionsExample(localeManager: LocaleManager) {
    val context = LocalContext.current
    var showTranslationOptions by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguageCode by remember {
        mutableStateOf(
            localeManager.getCurrentLanguageCode() ?: LocaleManager.LANGUAGE_ENGLISH
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Some UI element that will trigger the translation options
        Text(
            text = "Click to show translation options",
            modifier = Modifier.clickable { showTranslationOptions = true }
        )

        TranslationOptions(
            expanded = showTranslationOptions,
            onDismissRequest = { showTranslationOptions = false },
            onTranslateToAppLanguage = {
                // Implement translate to app language functionality
                println("Translating to app language...")
                showTranslationOptions = false
            },
            onTranslateToAnotherLanguage = {
                showLanguageDialog = true
                showTranslationOptions = false
            }
        )
    }

    // Show language selection dialog when needed
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { languageCode ->
                currentLanguageCode = languageCode
                // Implement translation to the selected language
                println("Translating to language: $languageCode")
            },
            currentLanguageCode = currentLanguageCode,
            localeManager = localeManager
        )
    }
}