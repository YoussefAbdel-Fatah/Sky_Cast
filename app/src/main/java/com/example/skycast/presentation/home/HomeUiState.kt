package com.example.skycast.presentation.home

import com.example.skycast.data.remote.response.WeatherResponse

data class HomeUiState(
    val isLoading: Boolean = false,
    val weatherData: WeatherResponse? = null,
    val error: String? = null
)