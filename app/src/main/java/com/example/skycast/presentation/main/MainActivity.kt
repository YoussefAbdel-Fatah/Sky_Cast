package com.example.skycast.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.repository.WeatherRepositoryImp
import com.example.skycast.presentation.home.HomeScreen
import com.example.skycast.presentation.home.HomeViewModel
import com.example.skycast.presentation.home.HomeViewModelFactory
import com.example.skycast.presentation.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {

    // 1. Manually instantiate our dependencies
    private val apiService by lazy { RetrofitClient.weatherApiService }
    private val repository by lazy { WeatherRepositoryImp(apiService) }
    private val factory by lazy { HomeViewModelFactory(repository) }

    // 2. Initialize the ViewModel using the factory
    private val viewModel: HomeViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Display the HomeScreen and pass the ViewModel
                    HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}