package com.example.skycast.data.remote

import com.example.skycast.data.remote.api.NominatimApiService
import com.example.skycast.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitNominatimClient {

    val nominatimApiService: NominatimApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.NOMINATIM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Tells Retrofit to use Gson for parsing
            .build()
            .create(NominatimApiService::class.java)
    }
}