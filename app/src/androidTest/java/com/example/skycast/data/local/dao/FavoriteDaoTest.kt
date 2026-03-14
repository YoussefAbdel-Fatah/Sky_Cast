package com.example.skycast.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.data.local.WeatherDatabase
import com.example.skycast.data.local.entity.FavoriteEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: FavoriteDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.favoriteDao()
    }

    @After
    fun teardown() = database.close()

    @Test
    fun insertFavorite_savesToDatabase() = runTest {
        // Given
        val favorite = FavoriteEntity(id = 1, cityName = "Test City", lat = 0.0, lon = 0.0)

        // When
        dao.insertFavorite(favorite)
        val favorites = dao.getAllFavorites().first()

        // Then
        assertTrue(favorites.contains(favorite))
    }

    @Test
    fun deleteFavorite_removesFromDatabase() = runTest {
        val favorite = FavoriteEntity(id = 1, cityName = "Paris", lat = 48.8, lon = 2.3)
        dao.insertFavorite(favorite)
        dao.deleteFavorite(favorite)

        val list = dao.getAllFavorites().first()
        assertTrue(list.isEmpty())
    }

    @Test
    fun getAllFavorites_returnsMultipleItems() = runTest {
        dao.insertFavorite(FavoriteEntity(id = 1, cityName = "Paris", lat = 48.8, lon = 2.3))
        dao.insertFavorite(FavoriteEntity(id = 2, cityName = "London", lat = 51.5, lon = -0.1))

        val list = dao.getAllFavorites().first()
        assertEquals(2, list.size)
    }
}