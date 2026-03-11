package com.example.skycast.presentation.home

import com.example.skycast.data.remote.response.WeatherResponse

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false, // Track network status
    val weatherData: WeatherResponse? = null,
    val error: String? = null
)