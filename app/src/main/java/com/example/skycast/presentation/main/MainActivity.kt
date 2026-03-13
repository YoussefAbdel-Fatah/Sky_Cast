package com.example.skycast.presentation.main

import LocationSearchRepository
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skycast.data.local.WeatherDatabase
import com.example.skycast.data.local.WeatherLocalDataSourceImpl
import com.example.skycast.data.location.DefaultLocationTracker
import com.example.skycast.data.remote.RetrofitNominatimClient
import com.example.skycast.data.remote.RetrofitWeatherClient
import com.example.skycast.data.remote.WeatherRemoteDataSourceImp
import com.example.skycast.data.repository.AlertsRepository
import com.example.skycast.data.repository.FavoritesRepository
import com.example.skycast.data.repository.WeatherRepositoryImp
import com.example.skycast.data.repository.SettingsRepositoryImpl
import com.example.skycast.data.worker.WeatherAlertWorker
import com.example.skycast.presentation.alerts.AlertsViewModel
import com.example.skycast.presentation.alerts.AlertsViewModelFactory
import com.example.skycast.presentation.favorites.FavoritesViewModel
import com.example.skycast.presentation.favorites.FavoritesViewModelFactory
import com.example.skycast.presentation.favorites.details.DetailsViewModel
import com.example.skycast.presentation.favorites.details.DetailsViewModelFactory
import com.example.skycast.presentation.home.HomeViewModel
import com.example.skycast.presentation.home.HomeViewModelFactory
import com.example.skycast.presentation.map.MapViewModel
import com.example.skycast.presentation.map.MapViewModelFactory
import com.example.skycast.presentation.settings.SettingsViewModel
import com.example.skycast.presentation.settings.SettingsViewModelFactory
import com.example.skycast.presentation.theme.WeatherAppTheme
import com.example.skycast.utils.NetworkObserver
import com.example.skycast.utils.dataStore
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // 1. Manually instantiate our dependencies
    private val weatherApiService by lazy { RetrofitWeatherClient.weatherApiService }
    private val nominatimApiService by lazy { RetrofitNominatimClient.nominatimApiService }
    private val locationSearchRepository by lazy { LocationSearchRepository(nominatimApiService) }

    // Add the data source here:
    private val remoteDataSource by lazy { WeatherRemoteDataSourceImp(weatherApiService) }
    private val weatherDao by lazy { WeatherDatabase.getDatabase(applicationContext).weatherDao() }
    private val localDataSource by lazy { WeatherLocalDataSourceImpl(weatherDao) }
    private val networkObserver by lazy { NetworkObserver(applicationContext) }

    // Pass the remoteDataSource to the repository:
    private val repository by lazy { WeatherRepositoryImp(remoteDataSource, localDataSource) }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationTracker by lazy { DefaultLocationTracker(fusedLocationClient, application) }
    private val settingsRepository by lazy { SettingsRepositoryImpl(applicationContext.dataStore) }
    private val settingsFactory by lazy { SettingsViewModelFactory(settingsRepository) }
    private val settingsViewModel: SettingsViewModel by viewModels { settingsFactory }
    private val mapFactory by lazy { MapViewModelFactory(settingsRepository, locationSearchRepository, favoritesRepository, repository) }
    private val mapViewModel: MapViewModel by viewModels { mapFactory }
    private val favoritesRepository by lazy {
        FavoritesRepository(WeatherDatabase.getDatabase(applicationContext).favoriteDao())
    }
    private val favoritesFactory by lazy {
        FavoritesViewModelFactory(favoritesRepository, networkObserver)
    }
    private val favoritesViewModel: FavoritesViewModel by viewModels { favoritesFactory }
    private val detailsFactory by lazy {
        DetailsViewModelFactory(repository, settingsRepository)
    }
    private val detailsViewModel: DetailsViewModel by viewModels { detailsFactory }
    private val factory by lazy { HomeViewModelFactory(repository, networkObserver, locationTracker, settingsRepository) }
    // by viewModels is like by lazy but for ViewModels and it is used to initialize the ViewModel only once.
    private val viewModel: HomeViewModel by viewModels { factory }
    private val alertsRepository by lazy {
        AlertsRepository(WeatherDatabase.getDatabase(applicationContext).alertDao())
    }
    private val workManager by lazy { WorkManager.getInstance(applicationContext) }
    private val alertsFactory by lazy {
        AlertsViewModelFactory(alertsRepository, workManager)
    }
    private val alertsViewModel: AlertsViewModel by viewModels { alertsFactory }


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
                    MainScreen(
                        homeViewModel = viewModel,
                        settingsViewModel = settingsViewModel,
                        mapViewModel = mapViewModel,
                        favoritesViewModel = favoritesViewModel,
                        detailsViewModel = detailsViewModel,
                        alertsViewModel = alertsViewModel
                    )
                }
            }
        }
    }
}
