package com.example.skycast.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.skycast.data.local.WeatherDatabase
import com.example.skycast.data.local.WeatherLocalDataSourceImpl
import com.example.skycast.data.remote.RetrofitClient
import com.example.skycast.data.remote.WeatherRemoteDataSourceImp
import com.example.skycast.data.repository.WeatherRepositoryImp
import com.example.skycast.presentation.home.HomeScreen
import com.example.skycast.presentation.home.HomeViewModel
import com.example.skycast.presentation.home.HomeViewModelFactory
import com.example.skycast.presentation.theme.WeatherAppTheme
import com.example.skycast.utils.NetworkObserver

class MainActivity : ComponentActivity() {

    // 1. Manually instantiate our dependencies
    private val apiService by lazy { RetrofitClient.weatherApiService }
    // Add the data source here:
    private val remoteDataSource by lazy { WeatherRemoteDataSourceImp(apiService) }
    private val weatherDao by lazy { WeatherDatabase.getDatabase(applicationContext).weatherDao() }
    private val localDataSource by lazy { WeatherLocalDataSourceImpl(weatherDao) }
    private val networkObserver by lazy { NetworkObserver(applicationContext) }

    // Pass the remoteDataSource to the repository:
    private val repository by lazy { WeatherRepositoryImp(remoteDataSource, localDataSource) }
    private val factory by lazy { HomeViewModelFactory(repository, networkObserver) }

    // by viewModels is like by lazy but for ViewModels and it is used to initialize the ViewModel only once.
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
