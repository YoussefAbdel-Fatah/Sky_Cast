package com.example.skycast.data.model

import com.google.gson.annotations.SerializedName

data class ForecastItem(
    @SerializedName("dt") val dt: Long, // Time of data forecasted, unix, UTC
    @SerializedName("main") val mainWeather: MainWeather,
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("dt_txt") val dtTxt: String // Time of data forecasted, ISO, UTC
)

data class MainWeather(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int
)

data class WeatherDescription(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String, // e.g., "Rain", "Snow", "Clear"
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class Clouds(
    @SerializedName("all") val all: Int // Cloudiness, %
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg") val deg: Int
)