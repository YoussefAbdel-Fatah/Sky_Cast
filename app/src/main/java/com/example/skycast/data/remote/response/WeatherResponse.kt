package com.example.skycast.data.remote.response

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("cod") val code: String,
    @SerializedName("message") val message: Int,
    @SerializedName("cnt") val count: Int,
    @SerializedName("list") val forecastList: List<ForecastItem>,
    @SerializedName("city") val city: City
)
