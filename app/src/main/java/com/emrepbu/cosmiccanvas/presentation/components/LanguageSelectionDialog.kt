package com.emrepbu.cosmiccanvas.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import com.emrepbu.cosmiccanvas.R
import com.emrepbu.cosmiccanvas.utils.locale.LanguageOption
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager

/**
 * A dialog component for selecting a language from the list of supported languages.
 * 
 * @param onDismiss Called when the dialog is dismissed without selecting a language
 * @param onLanguageSelected Called when a language is selected with the language code
 * @param currentLanguageCode The currently selected language code
 * @param localeManager The locale manager to get the list of supported languages
 */
@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    currentLanguageCode: String,
    localeManager: LocaleManager
) {
    val context = LocalContext.current
    val supportedLanguages = localeManager.getSupportedLanguages(context)
    var selectedLanguageCode by remember { mutableStateOf(currentLanguageCode) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.language_selection_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.language_selection_description),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Divider()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn {
                    items(supportedLanguages) { language ->
                        LanguageItem(
                            language = language,
                            selected = language.code == selectedLanguageCode,
                            onClick = { selectedLanguageCode = language.code }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onLanguageSelected(selectedLanguageCode)
                    onDismiss()
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

/**
 * A single language item in the language selection list.
 */
@Composable
private fun LanguageItem(
    language: LanguageOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        
        Text(
            text = language.displayName,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}