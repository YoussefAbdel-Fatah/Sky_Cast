package com.example.skycast.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skycast.data.local.WeatherDatabase
import com.example.skycast.data.remote.RetrofitWeatherClient
import com.example.skycast.data.repository.SettingsRepositoryImpl
import com.example.skycast.utils.Constants
import com.example.skycast.utils.NotificationHelper
import com.example.skycast.utils.dataStore
import kotlinx.coroutines.flow.first
import java.util.Calendar

class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WeatherApp", "Worker checking weather at ${Calendar.getInstance().time}")
        // 1. Initialize our tools
        val database = WeatherDatabase.getDatabase(context)
        val alertDao = database.alertDao()
        val weatherDao = database.weatherDao()
        val apiService = RetrofitWeatherClient.weatherApiService
        val notificationHelper = NotificationHelper(context)

        // Initialize SettingsRepository to read DataStore (to read coordinates and wind unit)
        val settingsRepository = SettingsRepositoryImpl(context.dataStore)

        // 2. Get active alerts from the database
        val activeAlerts = alertDao.getAllAlerts().first().filter { it.isEnabled }
        if (activeAlerts.isEmpty()) return Result.success()

        // 3. Check if the current time falls inside ANY active alert duration
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMin = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMin

        val triggeredAlert = activeAlerts.find { alert ->
            val startInMinutes = alert.startHour * 60 + alert.startMinute
            val endInMinutes = alert.endHour * 60 + alert.endMinute

            // Handle normal duration (e.g., 8:00 AM to 8:00 PM)
            if (startInMinutes <= endInMinutes) {
                currentTimeInMinutes in startInMinutes..endInMinutes
            }
            // Handle overnight duration (e.g., 10:00 PM to 6:00 AM)
            else {
                currentTimeInMinutes >= startInMinutes || currentTimeInMinutes <= endInMinutes
            }
        }

        if (triggeredAlert == null) return Result.success() // Current time is outside alert windows

        // 4. Time is valid! Fetch weather to see if we need to warn the user
        try {
            val cachedWeather = weatherDao.getCachedWeather().first()

            // If the user has never loaded the weather, we can't check alerts
            if (cachedWeather == null) return Result.success()

            val lat = cachedWeather.weatherResponse.city.coord.lat
            val lon = cachedWeather.weatherResponse.city.coord.lon
            // Read Units from DataStore
            val tempUnit = settingsRepository.getTemperatureUnit().first()
            val windUnit = settingsRepository.getWindUnit().first()

            val response = apiService.getWeatherByCoordinates(lat = lat, lon = lon, Constants.API_KEY, tempUnit, "en")

            if (response.isSuccessful && response.body() != null) {
                val weatherData = response.body()!!
                val currentWeather = weatherData.forecastList.first()

                // 5. Check for Rain, Snow, or Extreme Wind
                val weatherCondition = currentWeather.weather.firstOrNull()?.main?.lowercase() ?: ""
                val windSpeed = currentWeather.wind.speed
                Log.d("WeatherApp", "Weather Condition: $weatherCondition, Wind Speed: $windSpeed at ${Calendar.getInstance().time}")

                // Adjust threshold based on user's selected wind unit (10 m/s is roughly 22 mph)
                val windThreshold = if (windUnit == "imperial") 22.0 else 10.0

                var title = ""
                var message = ""

                if (weatherCondition.contains("rain")) {
                    title = "Rain Alert \uD83C\uDF27\uFE0F"
                    message = "It's raining! Don't forget your umbrella."
                } else if (weatherCondition.contains("snow")) {
                    title = "Snow Alert ❄\uFE0F"
                    message = "Snow is expected. Bundle up!"
                } else if (windSpeed > 0) { //TODO windThreshold: keep it zero for testing and project discussion
                    title = "High Wind Warning \uD83C\uDF2C\uFE0F"
                    val unitSymbol = if (windUnit == "imperial") "mph" else "m/s"
                    message = "Strong winds detected (${windSpeed} $unitSymbol)."
                }

                // 6. Fire the Alert!
                if (title.isNotEmpty()) {
                    notificationHelper.triggerAlert(title, message, triggeredAlert.isAlarm)
                    // turn the alert off in the database so it doesn't fire again
                    alertDao.updateAlert(triggeredAlert.copy(isEnabled = false))
                }
            }
        } catch (e: Exception) {
            return Result.retry() // If no internet, try again later
        }

        return Result.success()
    }
}