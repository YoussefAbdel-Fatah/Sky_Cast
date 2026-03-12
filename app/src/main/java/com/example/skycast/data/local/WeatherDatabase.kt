package com.example.skycast.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skycast.data.local.dao.AlertDao
import com.example.skycast.data.local.dao.FavoriteDao
import com.example.skycast.data.local.dao.WeatherDao
import com.example.skycast.data.local.entity.AlertEntity
import com.example.skycast.data.local.entity.FavoriteEntity
import com.example.skycast.data.local.entity.WeatherEntity


@Database(entities = [WeatherEntity::class, FavoriteEntity::class, AlertEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao

    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration() // 3. Prevents crash when updating DB structure
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}