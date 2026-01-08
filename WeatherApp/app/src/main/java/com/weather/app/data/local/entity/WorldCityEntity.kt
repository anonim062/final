package com.weather.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.weather.app.data.remote.model.GeocodingResponse

/**
 * Pre-populated city data from bundled SQLite database.
 * Contains worldwide cities for offline search.
 */
@Entity(tableName = "world_cities")
data class WorldCityEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val country: String,
    val countryCode: String,
    val state: String? = null,
    val latitude: Double,
    val longitude: Double,
    val population: Long = 0
) {
    fun toGeocodingResponse(): GeocodingResponse {
        return GeocodingResponse(
            name = name,
            latitude = latitude,
            longitude = longitude,
            country = countryCode,
            state = state
        )
    }
}
