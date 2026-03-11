package com.example.skycast.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()

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
        bottomBar = {
            // 2. The Offline Banner
            AnimatedVisibility(
                visible = uiState.isOffline,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE53935)) // A nice error red
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Internet connection lost",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isRefreshing,
                    color = SkyBlue,
                    containerColor = BackgroundLight,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {

            // Show main loading only if we have NO cached data
            if (uiState.isLoading && uiState.weatherData == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SkyBlue
                )
            } else if (uiState.weatherData != null) {

                val weatherData = uiState.weatherData!!
                val currentWeather = weatherData.forecastList.first()
                val hourlyForecast = weatherData.forecastList.take(8)
                val dailyForecast = weatherData.forecastList.filterIndexed { index, _ -> index % 8 == 0 }.take(5)

                // The scrollable Column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Main Top Card
                    MainWeatherCard(
                        temperature = "${currentWeather.mainWeather.temp.toInt()}°C",
                        city = weatherData.city.name,
                        description = currentWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Clear",
                        highLow = "H: ${currentWeather.mainWeather.tempMax.toInt()}°  L: ${currentWeather.mainWeather.tempMin.toInt()}°",
                        icon = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4 Grid Items
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherDetailsCard(
                            icon = painterResource(id = android.R.drawable.ic_menu_info_details),
                            label = "Humidity",
                            value = "${currentWeather.mainWeather.humidity}%",
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
                            value = "${currentWeather.mainWeather.pressure} hPa",
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
                                time = formatTime(forecast.dtTxt),
                                icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                                temperature = "${forecast.mainWeather.temp.toInt()}°",
                                isActive = hourlyForecast.indexOf(forecast) == 0
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 5-Day Forecast Section
                    SectionHeader(title = "5-Day Forecast")
                    Spacer(modifier = Modifier.height(16.dp))

                    dailyForecast.forEach { forecast ->
                        DailyForecastItem(
                            day = formatDate(forecast.dtTxt),
                            icon = painterResource(id = android.R.drawable.ic_menu_gallery),
                            status = forecast.weather.firstOrNull()?.main ?: "Clear",
                            highTemp = "${forecast.mainWeather.tempMax.toInt()}°",
                            lowTemp = "${forecast.mainWeather.tempMin.toInt()}°"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))
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