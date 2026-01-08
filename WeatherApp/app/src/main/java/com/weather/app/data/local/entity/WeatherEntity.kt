package com.weather.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing current weather data for a city.
 * This is cached locally for offline support.
 */
@Entity(tableName = "current_weather")
data class WeatherEntity(
    @PrimaryKey
    val cityId: Long,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDegree: Int,
    val weatherDescription: String,
    val weatherIcon: String,
    val weatherMain: String,
    val clouds: Int,
    val visibility: Int,
    val sunrise: Long,
    val sunset: Long,
    val timezone: Int,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)
