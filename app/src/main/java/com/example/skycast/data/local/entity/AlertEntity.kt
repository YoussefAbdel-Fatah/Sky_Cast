package com.example.skycast.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts_table")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val isAlarm: Boolean, // true = Alarm Sound, false = Notification
    val isEnabled: Boolean = true // Option to turn off the alarm
)