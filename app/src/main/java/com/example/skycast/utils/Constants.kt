package com.example.skycast.utils

import com.example.skycast.BuildConfig

object Constants {
    const val API_KEY = BuildConfig.WEATHER_API_KEY
    const val BASE_URL = "https://api.openweathermap.org/"

    // We can also add default shared preferences keys here later
    const val PREF_UNITS = "units_preference"
    const val PREF_LANGUAGE = "language_preference"
}