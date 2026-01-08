package com.weather.app.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for date/time formatting and conversions.
 */
object DateUtils {
    
    private const val HOUR_FORMAT = "HH:mm"
    private const val DAY_FORMAT = "EEE"
    private const val DATE_FORMAT = "MMM dd"
    private const val FULL_DATE_FORMAT = "EEE, MMM dd"
    private const val TIME_DATE_FORMAT = "HH:mm, MMM dd"
    
    /**
     * Format Unix timestamp to hour:minute format.
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(HOUR_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    
    /**
     * Format Unix timestamp to day of week.
     */
    fun formatDay(timestamp: Long): String {
        val sdf = SimpleDateFormat(DAY_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    
    /**
     * Format Unix timestamp to month and day.
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    
    /**
     * Format Unix timestamp to full date with day name.
     */
    fun formatFullDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(FULL_DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    
    /**
     * Format Unix timestamp to time and date.
     */
    fun formatTimeAndDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(TIME_DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
    
    /**
     * Check if timestamp is today.
     */
    fun isToday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = timestamp * 1000
        return calendar.get(Calendar.DAY_OF_YEAR) == today
    }
    
    /**
     * Get relative time description (e.g., "5 minutes ago").
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} min ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            else -> "${diff / 86400000} days ago"
        }
    }
}
