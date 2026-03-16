package com.example.skycast.presentation.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.skycast.R
import com.example.skycast.presentation.components.*
import com.example.skycast.presentation.theme.*
import com.example.skycast.utils.DateUtils.formatDate
import com.example.skycast.utils.DateUtils.formatTime
import com.example.skycast.utils.DateUtils.formatNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel // Pass the ViewModel in
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current
    val activity = context as Activity

    // Snackbar host for SharedFlow events
    val snackbarHostState = remember { SnackbarHostState() }

    // Setup the Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            viewModel.onPermissionResult(allGranted)
        }
    )

    // Request the permissions as soon as the screen opens
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Re-check permission when returning from App Settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadWeatherInfo()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Collect one-time SharedFlow events and show Snackbar
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is HomeEvent.WeatherRefreshed -> {
                    snackbarHostState.showSnackbar("Weather updated!")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("SkyCast", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        bottomBar = {
            // 2. The Offline Banner (shown when in Success state and offline)
            val isOffline = (uiState as? HomeUiState.Success)?.isOffline == true
            AnimatedVisibility(
                visible = isOffline,
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
                        text = stringResource(id = R.string.internet_connection_lost),
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BackgroundLight
    ) { paddingValues ->

        // Use sealed class with 'when' for UI state management
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SkyBlue)
                }
            }

            is HomeUiState.PermissionDenied -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LocationPermissionDenied(
                        onGrantClicked = {
                            // Check if the system will show the permission dialog
                            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                                activity, Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            if (shouldShowRationale) {
                                // User denied once but didn't select "Don't ask again" — re-ask
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                // User permanently denied or 2nd deny on Android 11+ — go to App Settings
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    OfflineEmptyState(onRetry = { viewModel.loadWeatherInfo() })
                }
            }

            is HomeUiState.Success -> {
                val tempSymbol = when (state.tempUnit) {
                    "imperial" -> stringResource(id = R.string.unit_fahrenheit_symbol)
                    "standard" -> stringResource(id = R.string.unit_kelvin_symbol)
                    else -> stringResource(id = R.string.unit_celsius_symbol)
                }
                val windSymbol = when (state.windUnit) {
                    "imperial" -> stringResource(id = R.string.unit_mph_symbol)
                    else -> stringResource(id = R.string.unit_ms_symbol)
                }
                // Convert wind speed: API returns m/s, multiply by 2.23694 to get mph
                val windConversionFactor = if (state.windUnit == "imperial") 2.23694 else 1.0
                val hPaSymbol = stringResource(id = R.string.unit_hpa_symbol)
                val percentSymbol = stringResource(id = R.string.unit_percent)

                val weatherData = state.weatherData
                val currentWeather = weatherData.forecastList.first()
                val hourlyForecast = weatherData.forecastList.take(8)
                val dailyForecast = weatherData.forecastList.filterIndexed { index, _ -> index % 8 == 0 }.take(5)

                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    state = pullToRefreshState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            state = pullToRefreshState,
                            isRefreshing = state.isRefreshing,
                            color = SkyBlue,
                            containerColor = BackgroundLight,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                ) {
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
                            temperature = "${formatNumber(currentWeather.mainWeather.temp.toInt())}$tempSymbol",
                            city = weatherData.city.name,
                            description = currentWeather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: stringResource(id = R.string.clear),
                            highLow = stringResource(
                                id = R.string.max_min_temp,
                                formatNumber(currentWeather.mainWeather.tempMax.toInt()) + tempSymbol,
                                formatNumber(currentWeather.mainWeather.tempMin.toInt()) + tempSymbol
                            ),
                            iconCode = currentWeather.weather.firstOrNull()?.icon
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4 Grid Items
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            WeatherDetailsCard(
                                icon = painterResource(id = R.drawable.ic_humidity),
                                label = stringResource(id = R.string.humidity),
                                value = "${formatNumber(currentWeather.mainWeather.humidity)}$percentSymbol",
                                modifier = Modifier.weight(1f)
                            )
                            WeatherDetailsCard(
                                icon = painterResource(id = R.drawable.ic_wind_speed),
                                label = stringResource(id = R.string.wind_speed),
                                value = "${formatNumber((currentWeather.wind.speed * windConversionFactor).toInt())} $windSymbol",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            WeatherDetailsCard(
                                icon = painterResource(id = R.drawable.ic_pressure),
                                label = stringResource(id = R.string.pressure),
                                value = "${formatNumber(currentWeather.mainWeather.pressure)} $hPaSymbol",
                                modifier = Modifier.weight(1f)
                            )
                            WeatherDetailsCard(
                                icon = painterResource(id = R.drawable.ic_clouds),
                                label = stringResource(id = R.string.clouds),
                                value = "${formatNumber(currentWeather.clouds.all)}$percentSymbol",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Hourly Forecast Section
                        SectionHeader(title = stringResource(id = R.string.hourly_forecast))
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(hourlyForecast) { forecast ->
                                HourlyForecastItem(
                                    time = formatTime(forecast.dtTxt),
                                    iconCode = forecast.weather.firstOrNull()?.icon,
                                    temperature = "${formatNumber(forecast.mainWeather.temp.toInt())}$tempSymbol",
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 5-Day Forecast Section
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
}

