package com.emrepbu.cosmiccanvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import com.emrepbu.cosmiccanvas.presentation.navigation.NavGraph
import com.emrepbu.cosmiccanvas.presentation.navigation.Screen
import com.emrepbu.cosmiccanvas.ui.theme.CosmicCanvasTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by userPreferencesManager.isDarkThemeFlow.collectAsStateWithLifecycle(
                initialValue = false
            )

            CosmicCanvasTheme(darkTheme = isDarkTheme) {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavRoutes = remember {
        listOf("home", "history", "favorites", "settings")
    }

    val showBottomBar = remember(currentDestination) {
        currentDestination?.hierarchy?.any { destination ->
            destination.route in bottomNavRoutes
        } ?: false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar) {
                NavigationBar {
                    // Home Tab
                    val homeSelected =
                        currentDestination?.hierarchy?.any { it.route == "home" } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (homeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text(text = "Home") },
                        selected = homeSelected,
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // History Tab
                    val historySelected =
                        currentDestination?.hierarchy?.any { it.route == "history" } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (historySelected) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = "History"
                            )
                        },
                        label = { Text(text = "History") },
                        selected = historySelected,
                        onClick = {
                            navController.navigate("history") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Favorites Tab
                    val favoritesSelected =
                        currentDestination?.hierarchy?.any { it.route == "favorites" } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (favoritesSelected) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                contentDescription = "Favorites"
                            )
                        },
                        label = { Text(text = "Favorites") },
                        selected = favoritesSelected,
                        onClick = {
                            navController.navigate("favorites") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    // Settings Tab
                    val settingsSelected =
                        currentDestination?.hierarchy?.any { it.route == "settings" } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (settingsSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text(text = "Settings") },
                        selected = settingsSelected,
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavGraph(navController = navController)
        }
    }
}