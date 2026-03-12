package com.example.skycast.presentation.favorites.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.data.repository.SettingsRepository
import com.example.skycast.data.repository.WeatherRepository
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailsUiState(
    val isLoading: Boolean = true,
    val weatherData: WeatherResponse? = null,
    val error: String? = null,
    val tempUnit: String = "metric",
    val windUnit: String = "metric"
)

class DetailsViewModel(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private var currentLang = "en"

    init {
        // Observe units so the screen displays the correct symbols
        viewModelScope.launch {
            combine(
                settingsRepository.getTemperatureUnit(),
                settingsRepository.getWindUnit(),
                settingsRepository.getLanguage()
            ) { temp, wind, lang ->
                currentLang = lang
                _uiState.value = _uiState.value.copy(tempUnit = temp, windUnit = wind)
            }.collect()
        }
    }

    // Call this from the UI when it opens
    fun fetchWeatherForCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getWeatherByCoordinates(lat, lon, _uiState.value.tempUnit, currentLang).collect { result ->
                when (result) {
                    is Resource.Loading -> { } // Handled initially
                    is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, weatherData = result.data, error = null)
                    is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}

class DetailsViewModelFactory(
    private val repository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(repository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}