package com.example.skycast.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.media.AudioAttributes
import androidx.core.app.NotificationCompat
import com.example.skycast.R // Make sure this matches your package name!

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationChannelId = "weather_alerts_notification_channel"
    private val alarmChannelId = "weather_alerts_alarm_channel"

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Standard Notification Channel
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Weather Alerts (Notifications)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Standard notifications for extreme weather conditions"
            }
            
            // Alarm Channel
            val alarmChannel = NotificationChannel(
                alarmChannelId,
                "Weather Alerts (Alarms)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarms for extreme weather conditions"
                
                // Set the default alarm sound for the channel itself
                val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(alarmSoundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.createNotificationChannel(alarmChannel)
        }
    }

    fun triggerAlert(title: String, message: String, isAlarm: Boolean) {
        // Choose the correct channel ID based on the user's preference
        val channelId = if (isAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmChannelId
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelId
        } else {
            "weather_alerts_channel" // For older versions (fallback)
        }
        
        // Choose the sound for the builder (only strictly needed for < Android O)
        val soundUri = if (isAlarm) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Replace with your app's icon later
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (isAlarm) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_DEFAULT)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}