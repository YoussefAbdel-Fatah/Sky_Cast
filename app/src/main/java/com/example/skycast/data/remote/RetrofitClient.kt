package com.example.skycast.data.remote

import com.example.skycast.data.remote.api.WeatherApiService
import com.example.skycast.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

//    // This interceptor helps us see the network requests and responses in the Logcat
//    // which is super helpful for debugging!
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()

    // by lazy: run this block the first time this variable will be used ant the last line is returned and cached so, when we call it again, it will return the cached value
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
//            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // Tells Retrofit to use Gson for parsing
            .build()
            .create(WeatherApiService::class.java)
    }
}