package com.example.skycast.data.repository

import com.example.skycast.data.remote.api.WeatherApiService
import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.utils.Constants
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImp(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Resource<WeatherResponse>> = flow {
        // 1. Tell the UI to show a loading spinner
        emit(Resource.Loading())

        try {
            // 2. Make the network call
            val response = api.getWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = Constants.API_KEY,
                units = units,
                language = lang
            )

            // 3. Check if the server gave us a good response
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "An unknown error occurred"))
            }

        } catch (e: HttpException) {
            // Catches server errors (like a 404 or 500 code)
            emit(Resource.Error("Oops, something went wrong with the server!"))
        } catch (e: IOException) {
            // Catches internet connection issues
            emit(Resource.Error("Couldn't reach the server. Check your internet connection."))
        }
    }

    override suspend fun getWeatherByCityName(
        cityName: String,
        units: String,
        lang: String
    ): Flow<Resource<WeatherResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = api.getWeatherByCityName(
                cityName = cityName,
                apiKey = Constants.API_KEY,
                units = units,
                language = lang
            )

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "City not found"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong with the server!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection."))
        }
    }
}