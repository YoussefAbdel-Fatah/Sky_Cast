package com.example.skycast.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.skycast.R

sealed class Screen(val route: String, val titleResId: Int, val icon: ImageVector = Icons.Default.Home) {
    object Home : Screen("home", R.string.home, Icons.Default.Home)
    object Favorites : Screen("favorites", R.string.favorites, Icons.Default.Favorite)
    object Alerts : Screen("alerts", R.string.alerts, Icons.Default.Notifications)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
    object Map : Screen("map/{isFromFavorites}", R.string.map) {
        fun createRoute(isFromFavorites: Boolean) = "map/$isFromFavorites"
    }
    object Details : Screen("details/{lat}/{lon}", R.string.details) {
        fun createRoute(lat: Double, lon: Double) = "details/$lat/$lon"

    }
}