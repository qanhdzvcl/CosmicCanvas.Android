package com.emrepbu.cosmiccanvas.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.emrepbu.cosmiccanvas.R
import com.emrepbu.cosmiccanvas.utils.locale.LocaleManager

/**
 * A component that displays a translate button which shows translation options in a dropdown menu.
 *
 * @param onTranslateToAppLanguage Called when the user selects to translate to the app language
 * @param onShowLanguageSelection Called when the user selects to translate to another language
 * @param localeManager The locale manager to pass to the language selection dialog
 * @param currentLanguageCode The currently selected language code
 * @param modifier Modifier for styling
 * @param showLabel Whether to show a text label below the icon
 */
@Composable
fun TranslateButton(
    onTranslateToAppLanguage: () -> Unit,
    onShowLanguageSelection: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Outlined.Translate,
                contentDescription = stringResource(id = R.string.translate)
            )
        }
        
        if (showLabel) {
            Text(
                text = stringResource(id = R.string.translate),
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall
            )
        }
        
        TranslationOptions(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            onTranslateToAppLanguage = {
                onTranslateToAppLanguage()
                showMenu = false
            },
            onTranslateToAnotherLanguage = {
                onShowLanguageSelection()
                showMenu = false
            }
        )
    }
}

/**
 * A dropdown menu component displaying translation options.
 * 
 * @param expanded Whether the dropdown menu is currently expanded/visible
 * @param onDismissRequest Called when the user dismisses the dropdown
 * @param onTranslateToAppLanguage Called when the user selects to translate to the app language
 * @param onTranslateToAnotherLanguage Called when the user selects to translate to another language
 */
@Composable
fun TranslationOptions(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onTranslateToAppLanguage: () -> Unit,
    onTranslateToAnotherLanguage: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.translate_to_app_language)) },
            onClick = onTranslateToAppLanguage
        )
        
        DropdownMenuItem(
            text = { Text(stringResource(id = R.string.translate_to_another_language)) },
            onClick = onTranslateToAnotherLanguage
        )
    }
}