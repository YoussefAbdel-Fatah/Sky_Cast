package com.example.skycast.data.repository

import com.example.skycast.data.local.dao.FavoriteDao
import com.example.skycast.data.local.entity.FavoriteEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoritesRepositoryTest {
    private val mockDao = mockk<FavoriteDao>()
    private val repository = FavoritesRepository(mockDao)

    @Test
    fun getFavorites_returnsFlowFromDao() = runTest {
        val mockList = listOf(FavoriteEntity(1, "Tokyo", 0.0, 0.0))
        // when getAllFavorites is called, return mockList
        every { mockDao.getAllFavorites() } returns flowOf(mockList)

        val result = repository.getFavorites().first()

        assertEquals(mockList, result)
    }

    @Test
    fun addFavorite_callsDaoInsert() = runTest {
        val favorite = FavoriteEntity(1, "Tokyo", 0.0, 0.0)
        // when insertFavorite is called, return Unit
        coEvery { mockDao.insertFavorite(favorite) } returns Unit

        repository.addFavorite(favorite)

        coVerify(exactly = 1) { mockDao.insertFavorite(favorite) }
    }

    @Test
    fun removeFavorite_callsDaoDelete() = runTest {
        val favorite = FavoriteEntity(1, "Tokyo", 0.0, 0.0)
        coEvery { mockDao.deleteFavorite(favorite) } returns Unit

        repository.removeFavorite(favorite)

        coVerify(exactly = 1) { mockDao.deleteFavorite(favorite) }
    }
}