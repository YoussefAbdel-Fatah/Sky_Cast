package com.example.skycast.presentation.favorites.details

import com.example.skycast.data.remote.response.WeatherResponse
import com.example.skycast.data.repository.WeatherRepository
import com.example.skycast.data.repository.SettingsRepository
import com.example.skycast.utils.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    // 1. Define the test dispatcher natively
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockWeatherRepo = mockk<WeatherRepository>()
    private val mockSettingsRepo = mockk<SettingsRepository>()

    private lateinit var viewModel: DetailsViewModel

    @Before
    fun setup() {
        // 2. Set the main dispatcher BEFORE the tests run
        Dispatchers.setMain(testDispatcher)

        // The DetailsViewModel 'init' block listens to these settings right away,
        // so we must mock them before instantiating the ViewModel.
        every { mockSettingsRepo.getTemperatureUnit() } returns flowOf("imperial")
        every { mockSettingsRepo.getWindUnit() } returns flowOf("imperial")
        every { mockSettingsRepo.getLanguage() } returns flowOf("en")

        viewModel = DetailsViewModel(mockWeatherRepo, mockSettingsRepo)
    }

    @After
    fun tearDown() {
        // 3. Reset the main dispatcher AFTER the tests finish to prevent memory leaks
        Dispatchers.resetMain()
    }

    @Test
    fun initialization_setsCorrectUnitsFromSettings() = runTest {
        // Assert: The init block should have combined the flows and updated the UI State automatically
        assertEquals("imperial", viewModel.uiState.value.tempUnit)
        assertEquals("imperial", viewModel.uiState.value.windUnit)
    }

    @Test
    fun fetchWeatherForCoordinates_onSuccess_updatesStateWithWeatherData() = runTest {
        // Arrange
        val mockResponse = mockk<WeatherResponse>()
        coEvery {
            mockWeatherRepo.getWeatherByCoordinates(any(), any(), any(), any())
        } returns flowOf(Resource.Success(mockResponse))

        // Act
        viewModel.fetchWeatherForCoordinates(40.71, -74.00)

        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(mockResponse, viewModel.uiState.value.weatherData)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun fetchWeatherForCoordinates_onError_updatesStateWithErrorMessage() = runTest {
        // Arrange
        val errorMessage = "No internet connection"
        coEvery {
            mockWeatherRepo.getWeatherByCoordinates(any(), any(), any(), any())
        } returns flowOf(Resource.Error(errorMessage))

        // Act
        viewModel.fetchWeatherForCoordinates(40.71, -74.00)

        // Assert
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(errorMessage, viewModel.uiState.value.error)
        assertEquals(null, viewModel.uiState.value.weatherData) // Data should remain null on error
    }
}