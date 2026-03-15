package com.example.skycast.data.local

import com.example.skycast.data.local.dao.WeatherDao
import com.example.skycast.data.local.entity.WeatherEntity
import com.example.skycast.data.remote.response.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface WeatherLocalDataSource {
    suspend fun cacheWeather(weatherResponse: WeatherResponse)
    fun getCachedWeather(): Flow<WeatherResponse?>
}

class WeatherLocalDataSourceImpl(
    private val weatherDao: WeatherDao
) : WeatherLocalDataSource {

    override suspend fun cacheWeather(weatherResponse: WeatherResponse) {
        weatherDao.insertWeather(WeatherEntity(weatherResponse = weatherResponse))
    }

    override fun getCachedWeather(): Flow<WeatherResponse?> {
        return weatherDao.getCachedWeather().map { it?.weatherResponse }
    }
}