package com.example.skycast.presentation.map

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import com.example.skycast.presentation.theme.BackgroundLight
import com.example.skycast.presentation.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit // Tells the NavHost to go back to previous screen
) {
    val context = LocalContext.current
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    // osmdroid strictly requires setting the User Agent before rendering the map
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Location", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            // Only show the save button if a location is actually selected
            if (selectedLocation != null) {
                FloatingActionButton(
                    onClick = {
                        viewModel.saveLocationAndSetMethod {
                            onNavigateBack() // Go back after saving
                        }
                    },
                    containerColor = SkyBlue
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save Location", tint = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setMultiTouchControls(true) // Enable pinch to zoom
                        controller.setZoom(5.0)

                        // Default starting point (e.g., center of the world)
                        controller.setCenter(GeoPoint(48.8566, 2.3522)) // Paris as default center

                        // Set up the tap listener
                        val mapEventsReceiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                viewModel.updateSelectedLocation(p.latitude, p.longitude)
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        }
                        overlays.add(MapEventsOverlay(mapEventsReceiver))
                    }
                },
                update = { mapView ->
                    // This block runs every time the Composable recomposes (e.g., when selectedLocation changes)

                    // Clear old markers first (keeping the MapEventsOverlay at index 0)
                    if (mapView.overlays.size > 1) {
                        mapView.overlays.subList(1, mapView.overlays.size).clear()
                    }

                    // Add new marker if a location is selected
                    selectedLocation?.let { (lat, lon) ->
                        val geoPoint = GeoPoint(lat, lon)
                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Selected Location"
                        mapView.overlays.add(marker)

                        // Optional: Animate pan to the dropped marker
                        mapView.controller.animateTo(geoPoint)
                    }
                    mapView.invalidate() // Force map to redraw
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}