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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skycast.R
import com.example.skycast.presentation.components.*
import com.example.skycast.presentation.theme.*
import com.example.skycast.utils.DateUtils.formatDate
import com.example.skycast.utils.DateUtils.formatTime
import com.example.skycast.utils.DateUtils.formatNumber

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
        "imperial" -> stringResource(id = R.string.unit_fahrenheit_symbol)
        "standard" -> stringResource(id = R.string.unit_kelvin_symbol)
        else -> stringResource(id = R.string.unit_celsius_symbol)
    }

    val windSymbol = when (uiState.windUnit) {
        "imperial" -> stringResource(id = R.string.unit_mph_symbol)
        else -> stringResource(id = R.string.unit_ms_symbol)
    }
    
    val hPaSymbol = stringResource(id = R.string.unit_hpa_symbol)
    val percentSymbol = stringResource(id = R.string.unit_percent)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.weatherData?.city?.name ?: stringResource(id = R.string.loading), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back)) }
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
                        temperature = "${formatNumber(currentWeather.mainWeather.temp.toInt())}$tempSymbol",
                        city = weatherData.city.name,
                        description = currentWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: stringResource(id = R.string.clear),
                        highLow = stringResource(id = R.string.max_min_temp, formatNumber(currentWeather.mainWeather.tempMax.toInt()) + tempSymbol, formatNumber(currentWeather.mainWeather.tempMin.toInt()) + tempSymbol),
                        iconCode = currentWeather.weather.firstOrNull()?.icon
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4 Grid Items
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherDetailsCard(painterResource(id = R.drawable.ic_humidity), stringResource(id = R.string.humidity), "${formatNumber(currentWeather.mainWeather.humidity)}$percentSymbol", Modifier.weight(1f))
                        WeatherDetailsCard(painterResource(id = R.drawable.ic_wind_speed), stringResource(id = R.string.wind_speed), "${formatNumber(currentWeather.wind.speed.toInt())} $windSymbol", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherDetailsCard(painterResource(id = R.drawable.ic_pressure), stringResource(id = R.string.pressure), "${formatNumber(currentWeather.mainWeather.pressure)} $hPaSymbol", Modifier.weight(1f))
                        WeatherDetailsCard(painterResource(id = R.drawable.ic_clouds), stringResource(id = R.string.clouds), "${formatNumber(currentWeather.clouds.all)}$percentSymbol", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(title = stringResource(id = R.string.hourly_forecast))
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(hourlyForecast) { forecast ->
                            HourlyForecastItem(
                                time = formatTime(forecast.dtTxt),
                                iconCode = forecast.weather.firstOrNull()?.icon,
                                temperature = "${formatNumber(forecast.mainWeather.temp.toInt())}$tempSymbol"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionHeader(title = stringResource(id = R.string.five_day_forecast))
                    Spacer(modifier = Modifier.height(16.dp))
                    dailyForecast.forEach { forecast ->
                        DailyForecastItem(
                            day = formatDate(forecast.dtTxt),
                            iconCode = forecast.weather.firstOrNull()?.icon,
                            status = forecast.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: stringResource(id = R.string.clear),
                            highTemp = stringResource(id = R.string.max_temp, formatNumber(forecast.mainWeather.tempMax.toInt()) + tempSymbol),
                            lowTemp = stringResource(id = R.string.min_temp, formatNumber(forecast.mainWeather.tempMin.toInt()) + tempSymbol)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}