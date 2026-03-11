package com.example.skycast.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Pair of Latitude and Longitude
    private val _selectedLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val selectedLocation: StateFlow<Pair<Double, Double>?> = _selectedLocation.asStateFlow()

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
}
