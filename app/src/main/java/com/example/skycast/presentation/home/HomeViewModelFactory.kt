package com.example.skycast.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.data.location.LocationTracker
import com.example.skycast.data.repository.WeatherRepository
import com.example.skycast.data.repository.SettingsRepository
import com.example.skycast.utils.NetworkObserver

class HomeViewModelFactory(
    private val repository: WeatherRepository,
    private val networkObserver: NetworkObserver,
    private val locationTracker: LocationTracker,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, networkObserver, locationTracker, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}