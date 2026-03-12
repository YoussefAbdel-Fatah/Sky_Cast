package com.example.skycast.data.repository

import com.example.skycast.data.local.dao.FavoriteDao
import com.example.skycast.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val favoriteDao: FavoriteDao) {

    fun getFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun addFavorite(favorite: FavoriteEntity) {
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(favorite: FavoriteEntity) {
        favoriteDao.deleteFavorite(favorite)
    }
}