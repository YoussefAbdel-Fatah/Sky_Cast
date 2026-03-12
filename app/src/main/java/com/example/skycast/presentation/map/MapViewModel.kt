package com.example.skycast.presentation.map

import LocationSearchRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.local.entity.FavoriteEntity
import com.example.skycast.data.remote.response.NominatimResponse
import com.example.skycast.data.repository.FavoritesRepository
import com.example.skycast.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val settingsRepository: SettingsRepository,
    private val searchRepository: LocationSearchRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    // Pair of Latitude and Longitude
    private val _selectedLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val selectedLocation: StateFlow<Pair<Double, Double>?> = _selectedLocation.asStateFlow()
    // Search States
    private val _searchResults = MutableStateFlow<List<NominatimResponse>>(emptyList())
    val searchResults: StateFlow<List<NominatimResponse>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null
    // Call this from the UI when the user types
    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel() // Cancel previous search if they are typing fast
        if (query.length < 3) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce: wait 500ms after they stop typing before searching
            _isSearching.value = true
            searchRepository.searchLocation(query).collect { results ->
                _searchResults.value = results
                _isSearching.value = false
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun updateSelectedLocation(lat: Double, lon: Double) {
        _selectedLocation.value = Pair(lat, lon)
    }

    fun saveLocationAndSetMethod(onSaveComplete: () -> Unit) {
        val location = _selectedLocation.value
        if (location != null) {
            viewModelScope.launch {
                // Save the exact coordinates
                settingsRepository.saveMapLocation(location.first, location.second)
                // Automatically switch the location method to "map" so Home Screen updates
                settingsRepository.saveLocationMethod("map")

                onSaveComplete()
            }
        }
    }

    fun saveToFavorites(cityName: String, onSaveComplete: () -> Unit) {
        val location = _selectedLocation.value
        if (location != null) {
            viewModelScope.launch {
                val favorite = FavoriteEntity(
                    cityName = cityName,
                    lat = location.first,
                    lon = location.second
                )
                favoritesRepository.addFavorite(favorite)
                onSaveComplete()
            }
        }
    }
}
