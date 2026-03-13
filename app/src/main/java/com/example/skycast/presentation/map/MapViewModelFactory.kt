package com.example.skycast.presentation.map

import LocationSearchRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.data.repository.FavoritesRepository
import com.example.skycast.data.repository.SettingsRepository
import com.example.skycast.data.repository.WeatherRepository


class MapViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val searchRepository: LocationSearchRepository,
    private val favoritesRepository: FavoritesRepository,
    private val weatherRepository: WeatherRepository
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(settingsRepository, searchRepository, favoritesRepository, weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}