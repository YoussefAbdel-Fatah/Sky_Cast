package com.example.skycast.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    fun formatTime(dtTxt: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("h a", Locale.getDefault())
            val date = parser.parse(dtTxt)
            date?.let { formatter.format(it) } ?: dtTxt.substring(11, 16)
        } catch (e: Exception) {
            dtTxt.substring(11, 16)
        }
    }

    fun formatDate(dtTxt: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("EEEE", Locale.getDefault()) // Returns "Monday", "Tuesday", etc.
            val date = parser.parse(dtTxt)
            date?.let { formatter.format(it) } ?: dtTxt.substring(0, 10)
        } catch (e: Exception) {
            dtTxt.substring(0, 10)
        }
    }

    /**
     * Formats any number into the localized numeral representation.
     * e.g. "18" in Arabic locale will become "١٨".
     */
    fun formatNumber(number: Number): String {
        return String.format(Locale.getDefault(), "%d", number.toLong())
    }

    fun formatDouble(number: Double): String {
        return String.format(Locale.getDefault(), "%.1f", number)
    }
}
