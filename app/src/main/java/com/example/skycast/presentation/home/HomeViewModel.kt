package com.example.skycast.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.repository.WeatherRepository
import com.example.skycast.utils.NetworkObserver
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository,
    private val networkObserver: NetworkObserver // Inject the observer
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Remember the last searched location to refresh it later
    private var lastSearchedCity: String = "London"

    init {
        // Start observing network changes immediately
        observeNetwork()
        getWeatherByCity(lastSearchedCity)
    }

    private fun observeNetwork() {
        networkObserver.observe().onEach { isOnline ->
            val wasOffline = _uiState.value.isOffline
            _uiState.value = _uiState.value.copy(isOffline = !isOnline)

            // If the connection just came back, refresh the data automatically!
            if (isOnline && wasOffline) {
                refresh()
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        getWeatherByCity(lastSearchedCity)
    }

    fun getWeatherByCity(cityName: String, units: String = "metric", lang: String = "en") {
        lastSearchedCity = cityName
        viewModelScope.launch {
            repository.getWeatherByCityName(cityName, units, lang).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Only show main loading if we don't have data yet
                        if (_uiState.value.weatherData == null) {
                            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                        }
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            weatherData = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            // Don't show an error string if we are just offline and already have cached data
                            error = if (_uiState.value.weatherData == null) result.message else null
                        )
                    }
                }
            }
        }
    }
}