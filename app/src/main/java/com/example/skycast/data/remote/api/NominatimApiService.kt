package com.example.skycast.data.remote.api

import com.example.skycast.data.remote.response.NominatimResponse
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface NominatimApiService {
    // Nominatim requires a User-Agent header, otherwise they block the request!
    @Headers("User-Agent: SkyCastWeatherApp/1.0")
    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5 // We only need the top 5 suggestions
    ): Response<List<NominatimResponse>>
}