package com.example.skycast.data.repository

import com.example.skycast.data.local.WeatherLocalDataSource
import com.example.skycast.data.remote.WeatherRemoteDataSource // Updated Import
import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.utils.Constants
import com.example.skycast.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImp(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override suspend fun getWeatherByCoordinates(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<Resource<WeatherResponse>> = flow {

        emit(Resource.Loading())

        val cachedWeather = localDataSource.getCachedWeather()
        if (cachedWeather != null) {
            emit(Resource.Success(cachedWeather))
        }

        try {
            // Call the data source
            val response = remoteDataSource.getWeatherByCoordinates(
                lat = lat, lon = lon, apiKey = Constants.API_KEY, units = units, lang = lang
            )

            if (response.isSuccessful && response.body() != null) {

                val freshWeather: WeatherResponse = response.body() as WeatherResponse
                localDataSource.cacheWeather(freshWeather)
                emit(Resource.Success(freshWeather))
            } else if (cachedWeather == null) {

                // Only show an error if we didn't have any cached data to fall back on
                emit(Resource.Error(response.message() ?: "City not found"))
            }
        } catch (e: HttpException) {

            if (cachedWeather == null) emit(Resource.Error("Oops, something went wrong with the server!"))
        } catch (e: IOException) {

            // No internet connection! If we have cached data, we silently ignore the error
            // because the UI is already showing the cached weather.
            if (cachedWeather == null) emit(Resource.Error("No internet connection and no cached data."))
        }
    }

    override suspend fun getWeatherByCityName(
        cityName: String, units: String, lang: String
    ): Flow<Resource<WeatherResponse>> = flow {

        emit(Resource.Loading())

        val cachedWeather = localDataSource.getCachedWeather()
        if (cachedWeather != null) {
            emit(Resource.Success(cachedWeather))
        }
        try {
            // Call the data source
            val response = remoteDataSource.getWeatherByCityName(
                cityName = cityName, apiKey = Constants.API_KEY, units = units, lang = lang
            )

            if (response.isSuccessful && response.body() != null) {

                val freshWeather: WeatherResponse = response.body() as WeatherResponse
                localDataSource.cacheWeather(freshWeather)
                emit(Resource.Success(response.body()!!))
            } else if (cachedWeather == null) {
                emit(Resource.Error(response.message() ?: "City not found"))
            }
        } catch (e: HttpException) {
            if (cachedWeather == null) emit(Resource.Error("Oops, something went wrong with the server!"))
        } catch (e: IOException) {
            if (cachedWeather == null) emit(Resource.Error("No internet connection and no cached data."))
        }
    }
}