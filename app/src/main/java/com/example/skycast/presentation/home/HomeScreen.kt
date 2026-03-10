package com.example.skycast.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.presentation.components.*
import com.example.skycast.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel // 1. Pass the ViewModel in
) {
    // 2. Observe the UI State
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("SkyCast", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Open Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->

        // 3. Handle the different states (Loading, Error, Success)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = SkyBlue
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Unknown Error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.weatherData != null -> {
                    val weatherData = uiState.weatherData!!
                    val currentWeather = weatherData.forecastList.first() // Get current weather
                    val hourlyForecast = weatherData.forecastList.take(8) // Next 24 hours
                    // Grab one reading per day for the 5-day forecast
                    val dailyForecast = weatherData.forecastList.filterIndexed { index, _ -> index % 8 == 0 }.take(5)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Main Top Card
                        MainWeatherCard(
                            temperature = "${currentWeather.main.temp.toInt()}°C",
                            city = weatherData.city.name,
                            description = currentWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                            highLow = "H: ${currentWeather.main.tempMax.toInt()}°  L: ${currentWeather.main.tempMin.toInt()}°",
                            icon = painterResource(id = android.R.drawable.ic_menu_gallery) // We will map dynamic icons later!
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4 Grid Items
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            WeatherDetailsCard(
                                icon = painterResource(id = android.R.drawable.ic_menu_info_details),
                                label = "Humidity",
                                value = "${currentWeather.main.humidity}%",
                                modifier = Modifier.weight(1f)
                            )
                            WeatherDetailsCard(
                                icon = painterResource(id = android.R.drawable.ic_menu_send),
                                label = "Wind Speed",
                                value = "${currentWeather.wind.speed} m/s",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            WeatherDetailsCard(
                                icon = painterResource(id = android.R.drawable.ic_menu_compass),
                                label = "Pressure",
                                value = "${currentWeather.main.pressure} hPa",
                                modifier = Modifier.weight(1f)
                            )
                            WeatherDetailsCard(
                                icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                                label = "Clouds",
                                value = "${currentWeather.clouds.all}%",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Hourly Forecast Section
                        SectionHeader(title = "Hourly Forecast", actionText = "View all")
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(hourlyForecast) { forecast ->
                                HourlyForecastItem(
                                    time = formatTime(forecast.dtTxt), // "12 PM"
                                    icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    temperature = "${forecast.main.temp.toInt()}°",
                                    isActive = hourlyForecast.indexOf(forecast) == 0 // Highlight the current hour
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 5-Day Forecast Section
                        SectionHeader(title = "5-Day Forecast")
                        Spacer(modifier = Modifier.height(16.dp))

                        dailyForecast.forEach { forecast ->
                            DailyForecastItem(
                                day = formatDate(forecast.dtTxt), // "Tuesday"
                                icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                                status = forecast.weather.firstOrNull()?.main ?: "Clear",
                                highTemp = "${forecast.main.tempMax.toInt()}°",
                                lowTemp = "${forecast.main.tempMin.toInt()}°"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

// Small helper functions to format the dates coming from the API
private fun formatTime(dtTxt: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("h a", Locale.getDefault())
        val date = parser.parse(dtTxt)
        date?.let { formatter.format(it) } ?: dtTxt
    } catch (e: Exception) {
        dtTxt.substring(11, 16)
    }
}

private fun formatDate(dtTxt: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("EEEE", Locale.getDefault()) // Returns "Monday", "Tuesday", etc.
        val date = parser.parse(dtTxt)
        date?.let { formatter.format(it) } ?: dtTxt.substring(0, 10)
    } catch (e: Exception) {
        dtTxt.substring(0, 10)
    }
}