package com.weather.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing 5-day forecast data with 3-hour intervals.
 * Each row represents one forecast time slot.
 */
@Entity(tableName = "forecast")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityId: Long,
    val dateTime: Long,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val weatherDescription: String,
    val weatherIcon: String,
    val weatherMain: String,
    val windSpeed: Double,
    val windDegree: Int,
    val clouds: Int,
    val visibility: Int,
    val pop: Double, // Probability of precipitation
    val dateText: String, // Human readable date string
    val lastUpdated: Long = System.currentTimeMillis()
)
