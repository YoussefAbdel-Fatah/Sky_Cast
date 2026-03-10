package com.example.skycast.data.remote

import com.example.skycast.data.remote.api.WeatherApiService
import com.example.skycast.data.remote.response.WeatherResponse
import retrofit2.Response

// The Interface
interface WeatherRemoteDataSource {
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double, apiKey: String, units: String, lang: String): Response<WeatherResponse>
    suspend fun getWeatherByCityName(cityName: String, apiKey: String, units: String, lang: String): Response<WeatherResponse>
}

// The Implementation
class WeatherRemoteDataSourceImp(
    private val apiService: WeatherApiService
) : WeatherRemoteDataSource {

    override suspend fun getWeatherByCoordinates(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): Response<WeatherResponse> {
        return apiService.getWeatherByCoordinates(lat, lon, apiKey, units, lang)
    }

    override suspend fun getWeatherByCityName(
        cityName: String, apiKey: String, units: String, lang: String
    ): Response<WeatherResponse> {
        return apiService.getWeatherByCityName(cityName, apiKey, units, lang)
    }
}