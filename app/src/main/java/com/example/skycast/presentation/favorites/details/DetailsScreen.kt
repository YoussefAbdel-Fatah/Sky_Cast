package com.example.skycast.presentation.favorites.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skycast.presentation.components.*
import com.example.skycast.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    lat: Double,
    lon: Double,
    viewModel: DetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch weather as soon as the screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchWeatherForCoordinates(lat, lon)
    }

    val tempSymbol = when (uiState.tempUnit) {
        "imperial" -> "°F"
        "standard" -> "K"
        else -> "°C"
    }

    val windSymbol = when (uiState.windUnit) {
        "imperial" -> "mph"
        else -> "m/s"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.weatherData?.city?.name ?: "Loading...", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = SkyBlue)
            } else if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else if (uiState.weatherData != null) {

                val weatherData = uiState.weatherData!!
                val currentWeather = weatherData.forecastList.first()
                val hourlyForecast = weatherData.forecastList.take(8)
                val dailyForecast = weatherData.forecastList.filterIndexed { index, _ -> index % 8 == 0 }.take(5)

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(scrollState)) {
                    Spacer(modifier = Modifier.height(8.dp))

                    MainWeatherCard(
                        temperature = "${currentWeather.mainWeather.temp.toInt()}$tempSymbol",
                        city = weatherData.city.name,
                        description = currentWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                        highLow = "H: ${currentWeather.mainWeather.tempMax.toInt()}$tempSymbol  L: ${currentWeather.mainWeather.tempMin.toInt()}$tempSymbol",
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4 Grid Items
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherDetailsCard(painterResource(id = android.R.drawable.ic_menu_info_details), "Humidity", "${currentWeather.mainWeather.humidity}%", Modifier.weight(1f))
                        WeatherDetailsCard(painterResource(id = android.R.drawable.ic_menu_send), "Wind Speed", "${currentWeather.wind.speed} $windSymbol", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherDetailsCard(painterResource(id = android.R.drawable.ic_menu_compass), "Pressure", "${currentWeather.mainWeather.pressure} hPa", Modifier.weight(1f))
                        WeatherDetailsCard(painterResource(id = android.R.drawable.ic_menu_gallery), "Clouds", "${currentWeather.clouds.all}%", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(title = "Hourly Forecast")
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(hourlyForecast) { forecast ->
                            HourlyForecastItem(
                                time = forecast.dtTxt.substring(11, 16), // Simplified time extraction for brevity
                                icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                                temperature = "${forecast.mainWeather.temp.toInt()}$tempSymbol"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(title = "5-Day Forecast")
                    Spacer(modifier = Modifier.height(16.dp))
                    dailyForecast.forEach { forecast ->
                        DailyForecastItem(
                            day = forecast.dtTxt.substring(0, 10), // Simplified date extraction
                            icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                            status = forecast.weather.firstOrNull()?.main ?: "Clear",
                            highTemp = "${forecast.mainWeather.tempMax.toInt()}$tempSymbol",
                            lowTemp = "${forecast.mainWeather.tempMin.toInt()}$tempSymbol"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}