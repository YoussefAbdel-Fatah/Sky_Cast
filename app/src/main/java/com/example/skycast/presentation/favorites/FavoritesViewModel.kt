package com.example.skycast.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.local.entity.FavoriteEntity
import com.example.skycast.data.repository.FavoritesRepository
import com.example.skycast.utils.NetworkObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoritesRepository,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    // Automatically converts the Room Flow into a Compose-friendly StateFlow
    val favoritesList: StateFlow<List<FavoriteEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isOnline = networkObserver.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun removeFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.removeFavorite(favorite)
        }
    }
}