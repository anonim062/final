package com.weather.app.data.local.entity

import com.weather.app.data.remote.model.GeocodingResponse

/**
 * Extension function to convert WorldCityEntity to GeocodingResponse.
 * This allows seamless integration between offline and online city search.
 */
fun WorldCityEntity.toGeocodingResponse(): GeocodingResponse {
    return GeocodingResponse(
        name = this.name,
        localNames = null,
        latitude = this.latitude,
        longitude = this.longitude,
        country = this.countryCode,
        state = this.state
    )
}

/**
 * Extension function to convert GeocodingResponse to WorldCityEntity.
 */
fun GeocodingResponse.toWorldCityEntity(): WorldCityEntity {
    val cityId = ((this.latitude * 1000000).toLong() * 1000000L) + (this.longitude * 1000000).toLong()
    return WorldCityEntity(
        id = cityId,
        name = this.name,
        country = this.country,
        countryCode = this.country,
        state = this.state,
        latitude = this.latitude,
        longitude = this.longitude,
        population = 0
    )
}
