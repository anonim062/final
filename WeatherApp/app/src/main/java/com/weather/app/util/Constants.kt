package com.weather.app.util

/**
 * Constants used throughout the app.
 */
object Constants {
    
    // Cache expiry times in milliseconds
    const val WEATHER_CACHE_DURATION = 30 * 60 * 1000L // 30 minutes
    const val FORECAST_CACHE_DURATION = 60 * 60 * 1000L // 1 hour
    
    // Default location (Baku, Azerbaijan)
    const val DEFAULT_CITY_NAME = "Baku"
    const val DEFAULT_CITY_ID = 587084L
    const val DEFAULT_LATITUDE = 40.4093
    const val DEFAULT_LONGITUDE = 49.8671
    
    // Weather icon base URL
    const val WEATHER_ICON_URL = "https://openweathermap.org/img/wn/%s@2x.png"
    
    // Location request codes
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    
    // API units
    const val UNITS_METRIC = "metric"
    const val UNITS_IMPERIAL = "imperial"
}
