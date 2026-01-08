package com.weather.app.data.remote.api

import com.weather.app.data.remote.model.CurrentWeatherResponse
import com.weather.app.data.remote.model.ForecastResponse
import com.weather.app.data.remote.model.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for OpenWeatherMap API.
 * Base URL: https://api.openweathermap.org/data/2.5/
 */
interface OpenWeatherApi {
    
    /**
     * Get current weather by city name.
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherResponse>
    
    /**
     * Get current weather by coordinates.
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherResponse>
    
    /**
     * Get current weather by city ID.
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCityId(
        @Query("id") cityId: Long,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<CurrentWeatherResponse>
    
    /**
     * Get 5-day forecast by city name.
     */
    @GET("forecast")
    suspend fun getForecastByCity(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>
    
    /**
     * Get 5-day forecast by coordinates.
     */
    @GET("forecast")
    suspend fun getForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>
    
    /**
     * Get 5-day forecast by city ID.
     */
    @GET("forecast")
    suspend fun getForecastByCityId(
        @Query("id") cityId: Long,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<ForecastResponse>
}

/**
 * Separate API interface for OpenWeatherMap Geocoding API.
 * Base URL: https://api.openweathermap.org/geo/1.0/
 */
interface GeocodingApi {
    
    /**
     * Search cities by name.
     */
    @GET("direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Response<List<GeocodingResponse>>
    
    /**
     * Reverse geocoding - get city name from coordinates.
     */
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): Response<List<GeocodingResponse>>
}
