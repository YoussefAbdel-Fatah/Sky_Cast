package com.example.skycast.data.repository

import com.example.skycast.data.remote.WeatherRemoteDataSource // Updated Import
import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.utils.Constants
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImp(
    // It now takes the Data Source instead of the ApiService!
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override suspend fun getWeatherByCoordinates(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<Resource<WeatherResponse>> = flow {
        emit(Resource.Loading())
        try {
            // Call the data source
            val response = remoteDataSource.getWeatherByCoordinates(
                lat = lat, lon = lon, apiKey = Constants.API_KEY, units = units, lang = lang
            )

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.message() ?: "An unknown error occurred"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Oops, something went wrong with the server!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection."))
        }
    }

    override suspend fun getWeatherByCityName(
        cityName: String, units: String, lang: String
    ): Flow<Resource<WeatherResponse>> = flow {
        emit(Resource.Loading())
        try {
            // Call the data source
            val response = remoteDataSource.getWeatherByCityName(
                cityName = cityName, apiKey = Constants.API_KEY, units = units, lang = lang
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