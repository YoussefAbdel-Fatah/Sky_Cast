package com.example.skycast.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    // 1. Define the strongly-typed keys
    private companion object {
        val KEY_LOCATION = stringPreferencesKey("location_method")
        val KEY_TEMP = stringPreferencesKey("temp_unit")
        val KEY_WIND = stringPreferencesKey("wind_unit")
        val KEY_LANG = stringPreferencesKey("language")
    }

    // 2. Read data (DataStore natively returns a Flow!)
    override fun getLocationMethod(): Flow<String> = dataStore.data.map { it[KEY_LOCATION] ?: "gps" }
    override fun getTemperatureUnit(): Flow<String> = dataStore.data.map { it[KEY_TEMP] ?: "metric" }
    override fun getWindUnit(): Flow<String> = dataStore.data.map { it[KEY_WIND] ?: "metric" } // metric = m/s, imperial = mph
    override fun getLanguage(): Flow<String> = dataStore.data.map { it[KEY_LANG] ?: "en" }

    // 3. Write data (Suspend functions run safely off the main thread)
    override suspend fun saveLocationMethod(method: String) {
        dataStore.edit { preferences -> preferences[KEY_LOCATION] = method }
    }

    override suspend fun saveTemperatureUnit(unit: String) {
        dataStore.edit { preferences -> preferences[KEY_TEMP] = unit }
    }

    override suspend fun saveWindUnit(unit: String) {
        dataStore.edit { preferences -> preferences[KEY_WIND] = unit }
    }

    override suspend fun saveLanguage(lang: String) {
        dataStore.edit { preferences -> preferences[KEY_LANG] = lang }
    }
}