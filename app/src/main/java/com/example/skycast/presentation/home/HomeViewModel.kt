package com.example.skycast.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.repository.WeatherRepository
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    // 2. Mutable state that only the ViewModel can update
    private val _uiState = MutableStateFlow(HomeUiState())

    // 3. Public read-only state that the Compose UI will observe
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Let's load a default city as soon as the app starts so the screen isn't empty!
        getWeatherByCity("London")
    }

    fun getWeatherByCity(cityName: String, units: String = "metric", lang: String = "en") {
        viewModelScope.launch {
            // Collect the flow from the repository
            repository.getWeatherByCityName(cityName, units, lang).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                }
            }
        }
    }

    fun getWeatherByLocation(lat: Double, lon: Double, units: String = "metric", lang: String = "en") {
        viewModelScope.launch {
            repository.getWeatherByCoordinates(lat, lon, units, lang).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            weatherData = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}