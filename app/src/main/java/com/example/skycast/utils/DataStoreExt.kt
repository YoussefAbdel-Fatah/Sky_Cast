package com.example.skycast.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// 1. Move this here so ANY class (like our Worker) can access it
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "skycast_settings")