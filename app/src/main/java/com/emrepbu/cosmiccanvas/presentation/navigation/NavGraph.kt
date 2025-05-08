package com.emrepbu.cosmiccanvas.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.emrepbu.cosmiccanvas.presentation.details.DetailsScreen
import com.emrepbu.cosmiccanvas.presentation.favorites.FavoritesScreen
import com.emrepbu.cosmiccanvas.presentation.history.HistoryScreen
import com.emrepbu.cosmiccanvas.presentation.home.HomeScreen
import com.emrepbu.cosmiccanvas.presentation.screensaver.ScreenSaverScreen
import com.emrepbu.cosmiccanvas.presentation.settings.SettingsScreen
import com.emrepbu.cosmiccanvas.utils.Constants

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onApodClick = { apodDate ->
                    navController.navigate(Screen.Details.createRoute(apodDate))
                },
                onScreenSaverTriggered = {
                    navController.navigate(Screen.ScreenSaver.route)
                }
            )
        }
        
        composable(route = Screen.History.route) {
            HistoryScreen(
                onApodClick = { apodDate ->
                    navController.navigate(Screen.Details.createRoute(apodDate))
                }
            )
        }
        
        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                onApodClick = { apodDate ->
                    navController.navigate(Screen.Details.createRoute(apodDate))
                }
            )
        }
        
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
        
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument(Constants.NAV_ARG_APOD_DATE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val apodDate = backStackEntry.arguments?.getString(Constants.NAV_ARG_APOD_DATE) ?: ""
            DetailsScreen(
                apodDate = apodDate,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(route = Screen.ScreenSaver.route) {
            ScreenSaverScreen(
                onExitScreenSaver = {
                    navController.popBackStack()
                }
            )
        }
    }
}