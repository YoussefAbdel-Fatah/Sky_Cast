package com.example.skycast.presentation.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.local.entity.FavoriteEntity
import com.example.skycast.presentation.theme.AppShapes
import com.example.skycast.presentation.theme.BackgroundLight
import com.example.skycast.presentation.theme.SkyBlue
import com.example.skycast.presentation.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToMap: () -> Unit,
    onNavigateToDetails: (Double, Double) -> Unit // We'll use this later to open the details screen
) {
    val favorites by viewModel.favoritesList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Locations", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToMap,
                containerColor = SkyBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Favorite", tint = androidx.compose.ui.graphics.Color.White)
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No favorites added yet.", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { favorite ->
                    FavoriteItemCard(
                        favorite = favorite,
                        onClick = { onNavigateToDetails(favorite.lat, favorite.lon) },
                        onDelete = { viewModel.removeFavorite(favorite) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    favorite: FavoriteEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = favorite.cityName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Favorite", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}