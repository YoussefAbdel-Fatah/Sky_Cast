package com.example.skycast.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.location.LocationTracker
import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.data.repository.WeatherRepository
import com.example.skycast.data.repository.SettingsRepository
import com.example.skycast.utils.NetworkObserver
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository,
    private val networkObserver: NetworkObserver,
    private val locationTracker: LocationTracker,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Sealed class UI state
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // SharedFlow for one-time events (Snackbar, Toasts, etc.)
    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    // Remember the last searched location to refresh it later
    private var lastSearchedCity: String? = null
    private var lastLat: Double? = null
    private var lastLon: Double? = null
    private var currentLocationMethod = "gps"
    private var currentLang = "en"
    private var currentTempUnit = "metric"
    private var currentWindUnit = "metric"
    private var isOnline = true



    init {
        observeNetwork()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            // Combine listens to all 4 flows. If ANY of them change, this block runs.
            combine(
                settingsRepository.getLocationMethod(),
                settingsRepository.getTemperatureUnit(),
                settingsRepository.getWindUnit(),
                settingsRepository.getLanguage()
            ) { loc, temp, wind, lang ->

                val settingsChanged = currentTempUnit != temp || currentLang != lang || currentLocationMethod != loc

                currentLocationMethod = loc
                currentLang = lang
                currentTempUnit = temp
                currentWindUnit = wind

                // Update the state with the new units (only if we already have data)
                val current = _uiState.value
                if (current is HomeUiState.Success) {
                    _uiState.value = current.copy(tempUnit = temp, windUnit = wind)
                }

                // If settings actually changed (or if it's the very first time loading), fetch new data
                if (current is HomeUiState.Loading || settingsChanged) {
                    loadWeatherInfo()
                }
            }.collect()
        }
    }
    private fun observeNetwork() {
        networkObserver.observe().onEach { online ->
            val wasOffline = !isOnline
            isOnline = online

            val current = _uiState.value
            if (current is HomeUiState.Success) {
                _uiState.value = current.copy(isOffline = !online)
            }

            // If the connection just came back, refresh the data automatically!
            if (online && wasOffline && current is HomeUiState.Success) {
                refresh()
            }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            // If offline, show a snackbar and don't attempt a network call
            if (!isOnline) {
                viewModelScope.launch {
                    _events.emit(HomeEvent.ShowError("No internet connection"))
                }
                return
            }
            _uiState.value = current.copy(isRefreshing = true)
        }
        if (lastLat != null && lastLon != null) {
            getWeatherByLocation(lastLat!!, lastLon!!, currentTempUnit, currentLang)
        } else {
            getWeatherByCity(lastSearchedCity ?: "London", currentTempUnit, currentLang)
        }
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            if (currentLocationMethod == "gps") {
                // Check if location permission is granted before requesting location
                if (!locationTracker.hasLocationPermission()) {
                    _uiState.value = HomeUiState.PermissionDenied
                    return@launch
                }
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    getWeatherByLocation(location.latitude, location.longitude, currentTempUnit, currentLang)
                } else {
                    getWeatherByCity("London", currentTempUnit, currentLang) // Fallback
                }
            } else {
                val mapLocation = settingsRepository.getMapLocation().first()

                if (mapLocation != null) {
                    val (lat, lon) = mapLocation
                    getWeatherByLocation(lat, lon, currentTempUnit, currentLang)
                } else {
                    getWeatherByCity("London", currentTempUnit, currentLang)
                }
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            loadWeatherInfo()
        } else {
            _uiState.value = HomeUiState.PermissionDenied
        }
    }

    fun getWeatherByCity(cityName: String, units: String = "metric", lang: String = "en") {
        lastSearchedCity = cityName
        lastLat = null
        lastLon = null
        viewModelScope.launch {
            repository.getWeatherByCityName(cityName, units, lang).collect { result ->
                handleResult(result)
            }
        }
    }

    fun getWeatherByLocation(lat: Double, lon: Double, units: String = "metric", lang: String = "en") {
        lastLat = lat
        lastLon = lon
        lastSearchedCity = null

        viewModelScope.launch {
            repository.getWeatherByCoordinates(lat, lon, units, lang).collect { result ->
                handleResult(result)
            }
        }
    }

    private suspend fun handleResult(result: Resource<WeatherResponse>) {
        when (result) {
            is Resource.Loading -> {
                // Only show main loading if we don't have data yet
                if (_uiState.value !is HomeUiState.Success) {
                    _uiState.value = HomeUiState.Loading
                }
            }
            is Resource.Success -> {
                val wasRefreshing = (_uiState.value as? HomeUiState.Success)?.isRefreshing == true
                _uiState.value = HomeUiState.Success(
                    weatherData = result.data!!,
                    tempUnit = currentTempUnit,
                    windUnit = currentWindUnit,
                    isRefreshing = false,
                    isOffline = !isOnline
                )
                // Emit a one-time event when a pull-to-refresh completes successfully online
                if (wasRefreshing && isOnline) {
                    _events.emit(HomeEvent.WeatherRefreshed)
                }
            }
            is Resource.Error -> {
                val current = _uiState.value
                if (current is HomeUiState.Success) {
                    // We already have cached data on screen — don't replace it with an error.
                    // Instead, emit a one-time event so the UI can show a Snackbar.
                    _uiState.value = current.copy(isRefreshing = false)
                    _events.emit(HomeEvent.ShowError(result.message ?: "Something went wrong"))
                } else {
                    _uiState.value = HomeUiState.Error(result.message ?: "Something went wrong")
                }
            }
        }
    }
}