package com.example.skycast.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.settings.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    // Convert Flows to StateFlows for Compose UI
    val locationMethod: StateFlow<String> = repository.getLocationMethod()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gps")

    val tempUnit: StateFlow<String> = repository.getTemperatureUnit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    val language: StateFlow<String> = repository.getLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val windUnit: StateFlow<String> = repository.getWindUnit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "metric")

    fun updateLocationMethod(method: String) = viewModelScope.launch { repository.saveLocationMethod(method) }
    fun updateTemperatureUnit(unit: String) = viewModelScope.launch { repository.saveTemperatureUnit(unit) }
    fun updateLanguage(lang: String) = viewModelScope.launch { repository.saveLanguage(lang) }
    fun updateWindUnit(unit: String) = viewModelScope.launch { repository.saveWindUnit(unit) }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}