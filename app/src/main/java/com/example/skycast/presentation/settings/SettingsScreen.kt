package com.example.skycast.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToMap: () -> Unit
    ) {
    val locationMethod by viewModel.locationMethod.collectAsState()
    val tempUnit by viewModel.tempUnit.collectAsState()
    val language by viewModel.language.collectAsState()
    val windUnit by viewModel.windUnit.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // 1. Location Method
                SettingsOptionCard(
                    title = "Location Method",
                    options = listOf("gps" to "Use GPS", "map" to "Pick from Map"),
                    selectedValue = locationMethod,
                    onSelectionChanged = { viewModel.updateLocationMethod(it) }
                )
            }
            item {
                AnimatedVisibility(visible = locationMethod == "map") {
                    Button(
                        onClick = onNavigateToMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = AppShapes.medium
                    ) {
                        Text("Open Map to Pick Location", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            item {
                // 2. Temperature Unit
                SettingsOptionCard(
                    title = "Temperature Unit",
                    options = listOf(
                        "metric" to "Celsius (°C)",
                        "imperial" to "Fahrenheit (°F)",
                        "standard" to "Kelvin (K)"
                    ),
                    selectedValue = tempUnit,
                    onSelectionChanged = { viewModel.updateTemperatureUnit(it) }
                )
            }
            item {
                // 3. Language
                SettingsOptionCard(
                    title = "Language",
                    options = listOf("en" to "English", "ar" to "Arabic"),
                    selectedValue = language,
                    onSelectionChanged = { viewModel.updateLanguage(it) }
                )
            }

            item {
                // 4. Wind Speed Unit
                SettingsOptionCard(
                    title = "Wind Speed Unit",
                    options = listOf(
                        "metric" to "Meter/Sec (m/s)",
                        "imperial" to "Miles/Hour (mph)"
                    ),
                    selectedValue = windUnit,
                    onSelectionChanged = { viewModel.updateWindUnit(it) }
                )
            }
        }
    }
}

// Reusable UI Component for Settings
@Composable
fun SettingsOptionCard(
    title: String,
    options: List<Pair<String, String>>, // Pair of <Value to Save, Label to Display>
    selectedValue: String,
    onSelectionChanged: (String) -> Unit
) {
    Card(
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = AppTypography.headlineMedium, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            options.forEach { (value, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = selectedValue == value,
                        onClick = { onSelectionChanged(value) },
                        colors = RadioButtonDefaults.colors(selectedColor = SkyBlue)
                    )
                    Text(text = label, style = AppTypography.bodyLarge, color = TextPrimary)
                }
            }
        }
    }
}