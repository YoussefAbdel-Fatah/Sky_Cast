package com.example.skycast.data.local

import com.example.skycast.data.local.dao.WeatherDao
import com.example.skycast.data.local.entity.WeatherEntity
import com.example.skycast.data.remote.response.WeatherResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherLocalDataSourceImplTest {
    // Mock the DAO so we don't need a real database here
    private val mockDao = mockk<WeatherDao>()
    private val dataSource = WeatherLocalDataSourceImpl(mockDao)

    @Test
    fun cacheWeather_callsDaoInsert() = runTest {
        val mockResponse = mockk<WeatherResponse>()
        // when insertWeather is called, return Unit
        coEvery { mockDao.insertWeather(any()) } returns Unit

        dataSource.cacheWeather(mockResponse)

        coVerify(exactly = 1) { mockDao.insertWeather(any()) }
    }

    @Test
    fun getCachedWeather_returnsResponseWhenDataExists() = runTest {
        val mockResponse = mockk<WeatherResponse>()
        val mockEntity = WeatherEntity(id = 1, weatherResponse = mockResponse)
        // when getCachedWeather is called, return a Flow with mockEntity
        every { mockDao.getCachedWeather() } returns flowOf(mockEntity)

        val result = dataSource.getCachedWeather().first()

        assertEquals(mockResponse, result)
    }

    @Test
    fun getCachedWeather_returnsNullWhenDatabaseEmpty() = runTest {
        // when getCachedWeather is called, return a Flow with null
        every { mockDao.getCachedWeather() } returns flowOf(null)

        val result = dataSource.getCachedWeather().first()

        assertNull(result)
    }
}