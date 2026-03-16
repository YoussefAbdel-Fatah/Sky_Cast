package com.example.skycast.data.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
    fun hasLocationPermission(): Boolean
}