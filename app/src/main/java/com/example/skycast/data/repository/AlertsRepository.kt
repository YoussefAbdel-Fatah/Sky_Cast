package com.example.skycast.data.repository

import com.example.skycast.data.local.dao.AlertDao
import com.example.skycast.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

class AlertsRepository(private val alertDao: AlertDao) {
    fun getAlerts(): Flow<List<AlertEntity>> = alertDao.getAllAlerts()
    suspend fun addAlert(alert: AlertEntity) = alertDao.insertAlert(alert)
    suspend fun updateAlert(alert: AlertEntity) = alertDao.updateAlert(alert)
    suspend fun deleteAlert(alert: AlertEntity) = alertDao.deleteAlert(alert)
}