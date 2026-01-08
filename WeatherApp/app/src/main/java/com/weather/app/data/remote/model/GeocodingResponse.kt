package com.weather.app.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for geocoding API endpoint.
 * GET /geo/1.0/direct?q={city}&limit=5&appid={key}
 */
data class GeocodingResponse(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("local_names")
    val localNames: Map<String, String>? = null,
    
    @SerializedName("lat")
    val latitude: Double,
    
    @SerializedName("lon")
    val longitude: Double,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("state")
    val state: String? = null
)

/**
 * Response model for reverse geocoding API endpoint.
 * GET /geo/1.0/reverse?lat={lat}&lon={lon}&limit=1&appid={key}
 */
typealias ReverseGeocodingResponse = List<GeocodingResponse>
