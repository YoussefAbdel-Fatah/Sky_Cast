package com.example.skycast.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.local.entity.AlertEntity
import com.example.skycast.data.repository.AlertsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: AlertsRepository) : ViewModel() {

    val alertsList: StateFlow<List<AlertEntity>> = repository.getAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveAlert(startHour: Int, startMin: Int, endHour: Int, endMin: Int, isAlarm: Boolean) {
        viewModelScope.launch {
            repository.addAlert(
                AlertEntity(startHour = startHour, startMinute = startMin, endHour = endHour, endMinute = endMin, isAlarm = isAlarm)
            )
            // Note: We will trigger WorkManager here in the next step!
        }
    }

    fun toggleAlert(alert: AlertEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateAlert(alert.copy(isEnabled = isEnabled))
            // Note: We will cancel/restart WorkManager here in the next step!
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch { repository.deleteAlert(alert) }
    }
}

class AlertsViewModelFactory(private val repository: AlertsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AlertsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}