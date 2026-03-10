package com.example.skycast.data.repository

import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Resource<WeatherResponse>>

    suspend fun getWeatherByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Flow<Resource<WeatherResponse>>
}