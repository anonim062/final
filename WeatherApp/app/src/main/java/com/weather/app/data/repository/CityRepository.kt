package com.weather.app.data.repository

import com.weather.app.BuildConfig
import com.weather.app.data.local.dao.CityDao
import com.weather.app.data.local.dao.WorldCityDao
import com.weather.app.data.local.entity.CityEntity
import com.weather.app.data.local.entity.WorldCityEntity
import com.weather.app.data.remote.api.GeocodingApi
import com.weather.app.data.remote.api.OpenWeatherApi
import com.weather.app.data.remote.api.RetrofitClient
import com.weather.app.data.remote.model.GeocodingResponse
import com.weather.app.util.NetworkUtils
import com.weather.app.util.Resource
import com.weather.app.WeatherApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for city/location operations.
 * Uses both online API and offline SQLite database for city search.
 */
class CityRepository(
    private val cityDao: CityDao,
    private val weatherApi: OpenWeatherApi,
    private val geocodingApi: GeocodingApi = RetrofitClient.geocodingApi,
    private val worldCityDao: WorldCityDao = WeatherApplication.instance.database.worldCityDao()
) {
    
    private val apiKey = BuildConfig.WEATHER_API_KEY
    
    /**
     * Get all saved cities.
     */
    fun getAllCities(): Flow<List<CityEntity>> {
        return cityDao.getAllCities()
    }
    
    /**
     * Get default city.
     */
    fun getDefaultCity(): Flow<CityEntity?> {
        return cityDao.getDefaultCityFlow()
    }
    
    /**
     * Search cities by name - uses API when online, falls back to offline SQLite database.
     */
    fun searchCities(query: String): Flow<Resource<List<GeocodingResponse>>> = flow {
        emit(Resource.Loading())
        
        if (NetworkUtils.isNetworkAvailable()) {
            // Online: Use OpenWeatherMap Geocoding API
            try {
                val response = geocodingApi.searchCities(
                    query = query,
                    limit = 5,
                    apiKey = apiKey
                )
                
                if (response.isSuccessful && response.body() != null) {
                    emit(Resource.Success(response.body()!!))
                } else {
                    // API failed, try offline database
                    val offlineResults = searchOffline(query)
                    emit(Resource.Success(offlineResults))
                }
            } catch (e: Exception) {
                // Network error, try offline database
                val offlineResults = searchOffline(query)
                if (offlineResults.isNotEmpty()) {
                    emit(Resource.Success(offlineResults))
                } else {
                    emit(Resource.Error("Network error: ${e.message}"))
                }
            }
        } else {
            // Offline: Use bundled SQLite database
            val offlineResults = searchOffline(query)
            if (offlineResults.isNotEmpty()) {
                emit(Resource.Success(offlineResults))
            } else {
                emit(Resource.Error("No internet connection and no matching cities found offline"))
            }
        }
    }
    
    /**
     * Search cities from offline SQLite database.
     */
    private suspend fun searchOffline(query: String): List<GeocodingResponse> {
        val worldCities = worldCityDao.searchCities(query)
        return worldCities.map { it.toGeocodingResponse() }
    }
    
    /**
     * Get top cities from offline database.
     */
    suspend fun getTopCities(): List<WorldCityEntity> {
        return worldCityDao.getTopCities()
    }
    
    /**
     * Reverse geocode coordinates to get city name.
     */
    fun reverseGeocode(lat: Double, lon: Double): Flow<Resource<GeocodingResponse>> = flow {
        emit(Resource.Loading())
        
        if (!NetworkUtils.isNetworkAvailable()) {
            emit(Resource.Error("No internet connection"))
            return@flow
        }
        
        try {
            val response = geocodingApi.reverseGeocode(
                latitude = lat,
                longitude = lon,
                limit = 1,
                apiKey = apiKey
            )
            
            if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                emit(Resource.Success(response.body()!!.first()))
            } else {
                emit(Resource.Error("Location not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
    
    /**
     * Add a city to saved cities.
     */
    suspend fun addCity(city: CityEntity) {
        // Check if this is the first city being added
        val cityCount = cityDao.getCityCount()
        val cityWithDefault = if (cityCount == 0) {
            city.copy(isDefault = true)
        } else {
            city
        }
        cityDao.insertCity(cityWithDefault)
    }
    
    /**
     * Add city from geocoding response.
     */
    suspend fun addCityFromGeocodingResponse(response: GeocodingResponse): Long {
        // Generate a unique ID from coordinates
        val cityId = ((response.latitude * 1000000).toLong() * 1000000L) + (response.longitude * 1000000).toLong()
        
        val city = CityEntity(
            id = cityId,
            name = response.name,
            country = response.country,
            state = response.state,
            latitude = response.latitude,
            longitude = response.longitude,
            isCurrentLocation = false,
            isDefault = cityDao.getCityCount() == 0,
            addedAt = System.currentTimeMillis()
        )
        cityDao.insertCity(city)
        return cityId
    }
    
    /**
     * Add current location as a city.
     */
    suspend fun addCurrentLocationCity(name: String, country: String, lat: Double, lon: Double): Long {
        // Remove any existing current location city
        val existingCurrentLocation = cityDao.getCurrentLocationCity()
        existingCurrentLocation?.let {
            cityDao.deleteCity(it)
        }
        
        // Generate unique ID from coordinates
        val cityId = ((lat * 1000000).toLong() * 1000000L) + (lon * 1000000).toLong()
        
        val city = CityEntity(
            id = cityId,
            name = name,
            country = country,
            latitude = lat,
            longitude = lon,
            isCurrentLocation = true,
            isDefault = cityDao.getCityCount() == 0,
            addedAt = System.currentTimeMillis()
        )
        cityDao.insertCity(city)
        return cityId
    }
    
    /**
     * Remove a city.
     */
    suspend fun removeCity(city: CityEntity) {
        cityDao.deleteCity(city)
        
        // If the removed city was default, set another city as default
        if (city.isDefault) {
            val cities = cityDao.getAllCitiesOnce()
            cities.firstOrNull()?.let {
                cityDao.setDefaultCity(it.id)
            }
        }
    }
    
    /**
     * Remove city by ID.
     */
    suspend fun removeCityById(cityId: Long) {
        val city = cityDao.getCityById(cityId)
        city?.let { removeCity(it) }
    }
    
    /**
     * Set a city as default.
     */
    suspend fun setDefaultCity(cityId: Long) {
        cityDao.clearDefaultCity()
        cityDao.setDefaultCity(cityId)
    }
    
    /**
     * Check if a city is already saved.
     */
    suspend fun isCitySaved(cityId: Long): Boolean {
        return cityDao.cityExists(cityId)
    }
    
    /**
     * Get city by ID.
     */
    suspend fun getCityById(cityId: Long): CityEntity? {
        return cityDao.getCityById(cityId)
    }
    
    /**
     * Get the default city (non-flow version).
     */
    suspend fun getDefaultCityOnce(): CityEntity? {
        return cityDao.getDefaultCity()
    }
}
