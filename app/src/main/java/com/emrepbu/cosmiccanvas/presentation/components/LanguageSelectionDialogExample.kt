package com.emrepbu.cosmiccanvas.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager

/**
 * Example usage of the LanguageSelectionDialog component.
 * This demonstrates how to show the dialog and handle language selection.
 * 
 * @param localeManager The locale manager instance
 */
@Composable
fun LanguageSelectionDialogExample(
    localeManager: LocaleManager
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedLanguageCode by remember { mutableStateOf("") } // Empty means system default
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Get display name for the currently selected language
        val languageDisplayName = localeManager.getSupportedLanguages(context)
            .find { it.code == selectedLanguageCode }?.displayName ?: "System Default"
        
        Text(text = "Selected language: $languageDisplayName")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select Translation Language")
        }
        
        // Show the language selection dialog when requested
        if (showDialog) {
            LanguageSelectionDialog(
                onDismiss = { showDialog = false },
                onLanguageSelected = { languageCode ->
                    selectedLanguageCode = languageCode
                    // In a real app, you would use this language code for translation
                    // For example: translationViewModel.setTargetLanguage(languageCode)
                },
                currentLanguageCode = selectedLanguageCode,
                localeManager = localeManager
            )
        }
    }
}