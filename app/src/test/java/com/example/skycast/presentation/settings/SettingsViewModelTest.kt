package com.example.skycast.presentation.settings

import com.example.skycast.data.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    // 1. Define the test dispatcher natively
    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = mockk<SettingsRepository>()
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        // 2. Set the main dispatcher BEFORE the tests run
        Dispatchers.setMain(testDispatcher)

        // We have to mock the flows before initializing the ViewModel
        every { mockRepository.getLocationMethod() } returns flowOf("gps")
        every { mockRepository.getTemperatureUnit() } returns flowOf("metric")
        every { mockRepository.getWindUnit() } returns flowOf("metric")
        every { mockRepository.getLanguage() } returns flowOf("en")

        viewModel = SettingsViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        // 3. Reset the main dispatcher AFTER the tests finish to prevent memory leaks
        Dispatchers.resetMain()
    }

    @Test
    fun updateLocationMethod_callsRepository() = runTest {
        coEvery { mockRepository.saveLocationMethod("map") } returns Unit

        viewModel.updateLocationMethod("map")

        coVerify(exactly = 1) { mockRepository.saveLocationMethod("map") }
    }

    @Test
    fun updateTemperatureUnit_callsRepository() = runTest {
        coEvery { mockRepository.saveTemperatureUnit("imperial") } returns Unit

        viewModel.updateTemperatureUnit("imperial")

        coVerify(exactly = 1) { mockRepository.saveTemperatureUnit("imperial") }
    }

    @Test
    fun updateLanguage_callsRepository() = runTest {
        coEvery { mockRepository.saveLanguage("ar") } returns Unit

        viewModel.updateLanguage("ar")

        coVerify(exactly = 1) { mockRepository.saveLanguage("ar") }
    }
}