package com.example.skycast.data.local

import com.example.skycast.data.local.dao.WeatherDao
import com.example.skycast.data.local.entity.WeatherEntity
import com.example.skycast.data.remote.response.WeatherResponse

interface WeatherLocalDataSource {
    suspend fun cacheWeather(weatherResponse: WeatherResponse)
    suspend fun getCachedWeather(): WeatherResponse?
}

class WeatherLocalDataSourceImpl(
    private val weatherDao: WeatherDao
) : WeatherLocalDataSource {

    override suspend fun cacheWeather(weatherResponse: WeatherResponse) {
        weatherDao.insertWeather(WeatherEntity(weatherResponse = weatherResponse))
    }

    override suspend fun getCachedWeather(): WeatherResponse? {
        return weatherDao.getCachedWeather()?.weatherResponse
    }
}