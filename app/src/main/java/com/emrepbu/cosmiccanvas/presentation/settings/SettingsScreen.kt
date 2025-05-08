package com.emrepbu.cosmiccanvas.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.sharp.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emrepbu.cosmiccanvas.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showApiKeySavedMessage by viewModel.showApiKeySavedMessage.collectAsStateWithLifecycle()
    val showTranslationApiKeySavedMessage by viewModel.showTranslationApiKeySavedMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var apiKeyText by rememberSaveable { mutableStateOf("") }
    var translationApiKeyText by rememberSaveable { mutableStateOf("") }
    var sliderPosition by remember { mutableFloatStateOf(uiState.screenSaverDelayMinutes.toFloat()) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val translationFocusRequester = remember { FocusRequester() }
    
    val apiKeySavedMessage = stringResource(R.string.settings_api_key_saved)
    
    LaunchedEffect(showApiKeySavedMessage) {
        if (showApiKeySavedMessage) {
            snackbarHostState.showSnackbar(message = apiKeySavedMessage)
        }
    }
    
    LaunchedEffect(showTranslationApiKeySavedMessage) {
        if (showTranslationApiKeySavedMessage) {
            snackbarHostState.showSnackbar(message = apiKeySavedMessage)
        }
    }
    
    LaunchedEffect(uiState.screenSaverDelayMinutes) {
        sliderPosition = uiState.screenSaverDelayMinutes.toFloat()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                modifier = Modifier.shadow(4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Theme Settings
            SettingsSection(title = "Theme") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dark Theme",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.isDarkTheme,
                        onCheckedChange = { viewModel.setDarkTheme(it) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Screen Saver Settings
            SettingsSection(title = "Screen Saver") {
                Text(
                    text = "Timeout: ${sliderPosition.toInt()} minutes",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 1f..20f,
                    steps = 19,
                    onValueChangeFinished = {
                        viewModel.setScreenSaverDelay(sliderPosition.toInt())
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Language Settings
            SettingsSection(title = stringResource(R.string.settings_language)) {
                val languageOptions = viewModel.getLanguageOptions(LocalContext.current)
                val selectedLanguageCode = uiState.languageCode
                
                // Find the display name for the selected language
                val selectedLanguage = languageOptions.find { it.code == selectedLanguageCode }?.displayName
                    ?: stringResource(R.string.settings_language_system)
                
                var showLanguageDialog by remember { mutableStateOf(false) }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageDialog = true }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_language),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = selectedLanguage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Sharp.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        title = { Text(stringResource(R.string.settings_language)) },
                        text = {
                            LazyColumn {
                                items(languageOptions) { language ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setAppLanguage(language.code)
                                                showLanguageDialog = false
                                            }
                                            .padding(vertical = 12.dp, horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = language.code == selectedLanguageCode,
                                            onClick = {
                                                viewModel.setAppLanguage(language.code)
                                                showLanguageDialog = false
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = language.displayName,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text(stringResource(android.R.string.cancel))
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notification Settings
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_enable_notifications),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // API Key Settings
            SettingsSection(title = stringResource(R.string.settings_api_key)) {
                if (uiState.hasCustomApiKey) {
                    Text(
                        text = stringResource(R.string.settings_api_key_custom_set),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FilledTonalButton(
                        onClick = { viewModel.resetApiKey() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.settings_api_key_reset))
                    }
                } else {
                    Text(
                        text = stringResource(R.string.settings_api_key_info),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = stringResource(R.string.settings_api_key_get),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = apiKeyText,
                        onValueChange = { apiKeyText = it },
                        label = { Text(stringResource(R.string.settings_api_key_field)) },
                        placeholder = { Text(stringResource(R.string.settings_api_key_placeholder)) },
                        leadingIcon = { Icon(Icons.Rounded.Key, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { 
                                focusManager.clearFocus()
                                if (apiKeyText.isNotBlank()) {
                                    viewModel.saveApiKey(apiKeyText)
                                    apiKeyText = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        Text(
                            text = stringResource(R.string.settings_api_key_security_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            if (apiKeyText.isNotBlank()) {
                                viewModel.saveApiKey(apiKeyText)
                                apiKeyText = ""
                                focusManager.clearFocus()
                            }
                        },
                        enabled = apiKeyText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.settings_api_key_save))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Google Translate API Key Settings
            SettingsSection(title = stringResource(R.string.google_cloud_translation_api_key)) {
                if (uiState.hasCustomTranslationApiKey) {
                    Text(
                        text = stringResource(R.string.settings_api_key_custom_set),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FilledTonalButton(
                        onClick = { viewModel.resetTranslationApiKey() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteForever,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.settings_api_key_reset))
                    }
                } else {
                    Text(
                        text = stringResource(R.string.google_cloud_translation_api_key_info),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = translationApiKeyText,
                        onValueChange = { translationApiKeyText = it },
                        label = { Text(stringResource(R.string.google_cloud_translation_api_key)) },
                        placeholder = { Text(stringResource(R.string.google_cloud_translation_api_key_placeholder)) },
                        leadingIcon = { Icon(Icons.Rounded.Key, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { 
                                focusManager.clearFocus()
                                if (translationApiKeyText.isNotBlank()) {
                                    viewModel.saveTranslationApiKey(translationApiKeyText)
                                    translationApiKeyText = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(translationFocusRequester)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        
                        Text(
                            text = stringResource(R.string.settings_api_key_security_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            if (translationApiKeyText.isNotBlank()) {
                                viewModel.saveTranslationApiKey(translationApiKeyText)
                                translationApiKeyText = ""
                                focusManager.clearFocus()
                            }
                        },
                        enabled = translationApiKeyText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.settings_api_key_save))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}