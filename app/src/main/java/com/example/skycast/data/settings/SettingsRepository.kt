package com.example.skycast.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getLocationMethod(): Flow<String>
    suspend fun saveLocationMethod(method: String)

    fun getTemperatureUnit(): Flow<String>
    suspend fun saveTemperatureUnit(unit: String)

    fun getWindUnit(): Flow<String>
    suspend fun saveWindUnit(unit: String)

    fun getLanguage(): Flow<String>
    suspend fun saveLanguage(lang: String)
}