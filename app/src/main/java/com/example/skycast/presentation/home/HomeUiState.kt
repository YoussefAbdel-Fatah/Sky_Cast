package com.example.skycast.presentation.home

import com.example.skycast.data.remote.response.WeatherResponse

// Gap 2: Sealed class for UI State Management
sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val weatherData: WeatherResponse,
        val tempUnit: String = "metric",
        val windUnit: String = "metric",
        val isRefreshing: Boolean = false,
        val isOffline: Boolean = false
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

// Gap 3: Sealed class for one-time SharedFlow events
sealed class HomeEvent {
    data class ShowError(val message: String) : HomeEvent()
    data object WeatherRefreshed : HomeEvent()
}