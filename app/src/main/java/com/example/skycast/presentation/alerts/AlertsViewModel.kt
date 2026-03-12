package com.example.skycast.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skycast.data.local.entity.AlertEntity
import com.example.skycast.data.repository.AlertsRepository
import com.example.skycast.data.worker.WeatherAlertWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertsViewModel(
    private val repository: AlertsRepository,
    private val workManager: WorkManager // 1. Inject WorkManager
) : ViewModel() {

    private val WORK_NAME = "WeatherAlertWork"

    val alertsList: StateFlow<List<AlertEntity>> = repository.getAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveAlert(startHour: Int, startMin: Int, endHour: Int, endMin: Int, isAlarm: Boolean) {
        viewModelScope.launch {
            repository.addAlert(
                AlertEntity(startHour = startHour, startMinute = startMin, endHour = endHour, endMinute = endMin, isAlarm = isAlarm)
            )
            // 2. Start the worker when a new alert is saved
            startWorkManager()
        }
    }

    fun toggleAlert(alert: AlertEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateAlert(alert.copy(isEnabled = isEnabled))
            // 3. Check if we need to cancel or restart the worker
            manageWorkManagerState()
        }
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            // 4. Check if we need to cancel the worker
            manageWorkManagerState()
        }
    }

    private fun startWorkManager() {
        val workRequest = PeriodicWorkRequestBuilder<WeatherAlertWorker>(1, TimeUnit.HOURS).build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep the existing schedule if it's already running
            workRequest
        )
    }

    private suspend fun manageWorkManagerState() {
        // Read the current list of alerts directly from the database
        val activeAlerts = repository.getAlerts().first().filter { it.isEnabled }

        if (activeAlerts.isEmpty()) {
            // No active alerts left? Cancel the background work to save battery!
            workManager.cancelUniqueWork(WORK_NAME)
        } else {
            // There is still at least one active alert, ensure WorkManager is running
            startWorkManager()
        }
    }
}

// 5. Update the Factory to accept WorkManager
class AlertsViewModelFactory(
    private val repository: AlertsRepository,
    private val workManager: WorkManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertsViewModel(repository, workManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}