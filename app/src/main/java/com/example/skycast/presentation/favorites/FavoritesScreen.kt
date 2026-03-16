package com.example.skycast.presentation.favorites

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.R
import com.example.skycast.data.local.entity.FavoriteEntity
import com.example.skycast.presentation.theme.AppShapes
import com.example.skycast.presentation.theme.BackgroundLight
import com.example.skycast.presentation.theme.SkyBlue
import com.example.skycast.presentation.theme.SurfaceWhite
import com.example.skycast.presentation.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    context: Context,
    viewModel: FavoritesViewModel,
    onNavigateToMap: () -> Unit,
    onNavigateToDetails: (Double, Double) -> Unit // We'll use this later to open the details screen
) {
    val favorites by viewModel.favoritesList.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    var itemToDelete by remember { mutableStateOf<FavoriteEntity?>(null) }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text(stringResource(id = R.string.remove_favorite_title)) },
            text = { Text(stringResource(id = R.string.remove_favorite_message, itemToDelete?.cityName ?: "")) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFavorite(itemToDelete!!)
                        itemToDelete = null // Hide dialog
                    }
                ) {
                    Text(
                        stringResource(id = R.string.yes),
                        color = MaterialTheme.colorScheme.error
                    ) // Red color for delete action
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text(stringResource(id = R.string.no), color = SkyBlue)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.favorite_locations), fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isOnline) {
                        onNavigateToMap()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.internet_required_map),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                containerColor = SkyBlue
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_favorite),
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(id = R.string.no_favorites_added),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(favorites) { favorite ->
                    FavoriteItemCard(
                        favorite = favorite,
                        onClick = {
                            if (isOnline) {
                                onNavigateToDetails(favorite.lat, favorite.lon)
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.internet_required_details),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onDelete = { itemToDelete = favorite }
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
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
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, spotColor = SkyBlue, ambientColor = SkyBlue, shape = AppShapes.medium)
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
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.remove_favorite_title),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}