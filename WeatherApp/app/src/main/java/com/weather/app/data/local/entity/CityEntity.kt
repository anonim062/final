package com.weather.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing saved cities/locations.
 * Users can add multiple cities to track weather.
 */
@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val country: String,
    val state: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false,
    val isDefault: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
