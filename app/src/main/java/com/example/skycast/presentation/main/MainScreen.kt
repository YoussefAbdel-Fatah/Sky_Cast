package com.example.skycast.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skycast.presentation.alerts.AlertsScreen
import com.example.skycast.presentation.alerts.AlertsViewModel
import com.example.skycast.presentation.favorites.FavoritesScreen
import com.example.skycast.presentation.favorites.FavoritesViewModel
import com.example.skycast.presentation.favorites.details.DetailsScreen
import com.example.skycast.presentation.favorites.details.DetailsViewModel
import com.example.skycast.presentation.home.HomeScreen
import com.example.skycast.presentation.home.HomeViewModel
import com.example.skycast.presentation.map.MapScreen
import com.example.skycast.presentation.map.MapViewModel
import com.example.skycast.presentation.navigation.Screen
import com.example.skycast.presentation.settings.SettingsScreen
import com.example.skycast.presentation.settings.SettingsViewModel
import com.example.skycast.presentation.theme.BackgroundLight
import com.example.skycast.presentation.theme.SkyBlue

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    mapViewModel: MapViewModel,
    favoritesViewModel: FavoritesViewModel,
    detailsViewModel: DetailsViewModel,
    alertsViewModel: AlertsViewModel
) {
    val navController = rememberNavController()

    val screens = listOf(
        Screen.Home,
        Screen.Favorites,
        Screen.Alerts,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundLight,
                contentColor = SkyBlue
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(text = screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SkyBlue,
                            selectedTextColor = SkyBlue,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                LocalAbsoluteTonalElevation.current
                            )
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination to avoid building up a large back stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // The NavHost swaps the screens based on the current route
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = homeViewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.createRoute(isFromFavorites = true))
                    },
                    onNavigateToDetails = { lat, lon ->
                        navController.navigate(Screen.Details.createRoute(lat, lon))
                    }

                )
            }
            composable(
                route = Screen.Details.route,
                arguments = listOf(
                    navArgument("lat") { type = NavType.FloatType }, // FloatType is used because Compose Navigation doesn't have DoubleType natively
                    navArgument("lon") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0

                DetailsScreen(
                    lat = lat,
                    lon = lon,
                    viewModel = detailsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Alerts.route) {
                AlertsScreen(viewModel = alertsViewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.createRoute(isFromFavorites = false))
                    }
                )
            }
            composable(
                route = Screen.Map.route,
                arguments = listOf(navArgument("isFromFavorites") { type = NavType.BoolType })
            ) { backStackEntry ->
                val isFromFavorites = backStackEntry.arguments?.getBoolean("isFromFavorites") ?: false

                MapScreen(
                    viewModel = mapViewModel,
                    isFromFavorites = isFromFavorites,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}