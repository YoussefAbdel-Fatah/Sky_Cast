package com.example.skycast.data.remote.api

import com.example.skycast.data.remote.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

//    val API_KEY = "e1848fc18c64af75e36cd81141cf4ad0"

    // 1. Get weather using Latitude and Longitude (GPS or Map click)
    @GET("data/2.5/forecast")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // metric (Celsius), imperial (Fahrenheit), standard (Kelvin)
        @Query("lang") language: String = "en"    // en (English), ar (Arabic)
    ): Response<WeatherResponse>

    // 2. Get weather by City Name (Auto-complete Search)
    @GET("data/2.5/forecast")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): Response<WeatherResponse>
}