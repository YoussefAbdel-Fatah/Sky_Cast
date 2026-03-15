package com.example.skycast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    // If the ID is the same (which it always is: 1), it replaces the old data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_cache WHERE id = 1")
    fun getCachedWeather(): Flow<WeatherEntity?>
}