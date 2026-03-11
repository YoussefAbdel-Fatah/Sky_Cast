package com.example.skycast.data.local

import androidx.room.TypeConverter
import com.example.skycast.data.remote.response.WeatherResponse
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherResponse(weatherResponse: WeatherResponse): String {
        return gson.toJson(weatherResponse)
    }

    @TypeConverter
    fun toWeatherResponse(jsonString: String): WeatherResponse {
        return gson.fromJson(jsonString, WeatherResponse::class.java)
    }
}