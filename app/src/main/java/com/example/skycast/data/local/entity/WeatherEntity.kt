package com.example.skycast.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.skycast.data.remote.response.WeatherResponse

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val id: Int = 1, // Always 1 so it overwrites the old cache
    val weatherResponse: WeatherResponse
)