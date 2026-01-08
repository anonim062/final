package com.weather.app.data.repository

import com.weather.app.BuildConfig
import com.weather.app.data.local.dao.ForecastDao
import com.weather.app.data.local.dao.WeatherDao
import com.weather.app.data.local.entity.ForecastEntity
import com.weather.app.data.local.entity.WeatherEntity
import com.weather.app.data.remote.api.OpenWeatherApi
import com.weather.app.data.remote.model.CurrentWeatherResponse
import com.weather.app.data.remote.model.ForecastResponse
import com.weather.app.util.Constants
import com.weather.app.util.NetworkUtils
import com.weather.app.util.PreferencesManager
import com.weather.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for weather data operations.
 * Implements single source of truth pattern with caching.
 */
class WeatherRepository(
    private val weatherApi: OpenWeatherApi,
    private val weatherDao: WeatherDao,
    private val forecastDao: ForecastDao
) {
    
    private val apiKey = BuildConfig.WEATHER_API_KEY
    
    /**
     * Get current weather for a city by ID.
     * Fetches from API if online, otherwise returns cached data.
     */
    fun getCurrentWeather(cityId: Long): Flow<Resource<WeatherEntity>> = flow {
        emit(Resource.Loading())
        
        // Try to get cached data first
        val cachedWeather = weatherDao.getWeatherByCityIdOnce(cityId)
        
        if (NetworkUtils.isNetworkAvailable()) {
            try {
                val response = weatherApi.getCurrentWeatherByCityId(
                    cityId = cityId,
                    units = PreferencesManager.temperatureUnit.getApiUnit(),
                    apiKey = apiKey
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherEntity = mapToWeatherEntity(response.body()!!)
                    weatherDao.insertWeather(weatherEntity)
                    emit(Resource.Success(weatherEntity))
                } else {
                    // API failed, return cached data if available
                    if (cachedWeather != null) {
                        emit(Resource.Success(cachedWeather))
                    } else {
                        emit(Resource.Error("Failed to fetch weather: ${response.message()}"))
                    }
                }
            } catch (e: Exception) {
                if (cachedWeather != null) {
                    emit(Resource.Success(cachedWeather))
                } else {
                    emit(Resource.Error("Network error: ${e.message}"))
                }
            }
        } else {
            // Offline - return cached data
            if (cachedWeather != null) {
                emit(Resource.Success(cachedWeather))
            } else {
                emit(Resource.Error("No internet connection and no cached data available"))
            }
        }
    }
    
    /**
     * Get current weather by coordinates.
     */
    fun getCurrentWeatherByCoordinates(lat: Double, lon: Double): Flow<Resource<WeatherEntity>> = flow {
        emit(Resource.Loading())
        
        if (NetworkUtils.isNetworkAvailable()) {
            try {
                val response = weatherApi.getCurrentWeatherByCoordinates(
                    latitude = lat,
                    longitude = lon,
                    units = PreferencesManager.temperatureUnit.getApiUnit(),
                    apiKey = apiKey
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherEntity = mapToWeatherEntity(response.body()!!)
                    weatherDao.insertWeather(weatherEntity)
                    emit(Resource.Success(weatherEntity))
                } else {
                    emit(Resource.Error("Failed to fetch weather: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Network error: ${e.message}"))
            }
        } else {
            emit(Resource.Error("No internet connection"))
        }
    }
    
    /**
     * Get current weather by city name.
     */
    fun getCurrentWeatherByCity(cityName: String): Flow<Resource<WeatherEntity>> = flow {
        emit(Resource.Loading())
        
        if (NetworkUtils.isNetworkAvailable()) {
            try {
                val response = weatherApi.getCurrentWeatherByCity(
                    cityName = cityName,
                    units = PreferencesManager.temperatureUnit.getApiUnit(),
                    apiKey = apiKey
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherEntity = mapToWeatherEntity(response.body()!!)
                    weatherDao.insertWeather(weatherEntity)
                    emit(Resource.Success(weatherEntity))
                } else {
                    emit(Resource.Error("City not found: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Network error: ${e.message}"))
            }
        } else {
            emit(Resource.Error("No internet connection"))
        }
    }
    
    /**
     * Get 5-day forecast for a city.
     */
    fun getForecast(cityId: Long): Flow<Resource<List<ForecastEntity>>> = flow {
        emit(Resource.Loading())
        
        val cachedForecast = forecastDao.getForecastByCityIdOnce(cityId)
        
        if (NetworkUtils.isNetworkAvailable()) {
            try {
                val response = weatherApi.getForecastByCityId(
                    cityId = cityId,
                    units = PreferencesManager.temperatureUnit.getApiUnit(),
                    apiKey = apiKey
                )
                
                if (response.isSuccessful && response.body() != null) {
                    // Clear old forecast data for this city
                    forecastDao.deleteForecastByCityId(cityId)
                    
                    val forecastEntities = mapToForecastEntities(response.body()!!, cityId)
                    forecastDao.insertAllForecasts(forecastEntities)
                    emit(Resource.Success(forecastEntities))
                } else {
                    if (cachedForecast.isNotEmpty()) {
                        emit(Resource.Success(cachedForecast))
                    } else {
                        emit(Resource.Error("Failed to fetch forecast: ${response.message()}"))
                    }
                }
            } catch (e: Exception) {
                if (cachedForecast.isNotEmpty()) {
                    emit(Resource.Success(cachedForecast))
                } else {
                    emit(Resource.Error("Network error: ${e.message}"))
                }
            }
        } else {
            if (cachedForecast.isNotEmpty()) {
                emit(Resource.Success(cachedForecast))
            } else {
                emit(Resource.Error("No internet connection and no cached data available"))
            }
        }
    }
    
    /**
     * Get forecast by coordinates.
     */
    fun getForecastByCoordinates(lat: Double, lon: Double): Flow<Resource<List<ForecastEntity>>> = flow {
        emit(Resource.Loading())
        
        if (NetworkUtils.isNetworkAvailable()) {
            try {
                val response = weatherApi.getForecastByCoordinates(
                    latitude = lat,
                    longitude = lon,
                    units = PreferencesManager.temperatureUnit.getApiUnit(),
                    apiKey = apiKey
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val cityId = response.body()!!.city.id
                    forecastDao.deleteForecastByCityId(cityId)
                    
                    val forecastEntities = mapToForecastEntities(response.body()!!, cityId)
                    forecastDao.insertAllForecasts(forecastEntities)
                    emit(Resource.Success(forecastEntities))
                } else {
                    emit(Resource.Error("Failed to fetch forecast: ${response.message()}"))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Network error: ${e.message}"))
            }
        } else {
            emit(Resource.Error("No internet connection"))
        }
    }
    
    /**
     * Get cached weather for offline display.
     */
    fun getCachedWeather(cityId: Long): Flow<WeatherEntity?> {
        return weatherDao.getWeatherByCityId(cityId)
    }
    
    /**
     * Get all cached weather data.
     */
    fun getAllCachedWeather(): Flow<List<WeatherEntity>> {
        return weatherDao.getAllWeather()
    }
    
    /**
     * Check if cache is stale (older than WEATHER_CACHE_DURATION).
     */
    suspend fun isCacheStale(cityId: Long): Boolean {
        val weather = weatherDao.getWeatherByCityIdOnce(cityId) ?: return true
        val cacheAge = System.currentTimeMillis() - weather.lastUpdated
        return cacheAge > Constants.WEATHER_CACHE_DURATION
    }
    
    /**
     * Clear all cached weather data.
     */
    suspend fun clearCache() {
        weatherDao.deleteAllWeather()
        forecastDao.deleteAllForecasts()
    }
    
    /**
     * Clear stale cache data.
     */
    suspend fun clearStaleCache() {
        val staleTimestamp = System.currentTimeMillis() - Constants.WEATHER_CACHE_DURATION
        weatherDao.deleteStaleWeather(staleTimestamp)
        forecastDao.deleteStaleForecasts(staleTimestamp)
    }
    
    // Mapping functions
    private fun mapToWeatherEntity(response: CurrentWeatherResponse): WeatherEntity {
        val weather = response.weather.firstOrNull()
        return WeatherEntity(
            cityId = response.id,
            cityName = response.name,
            country = response.sys.country,
            temperature = response.main.temperature,
            feelsLike = response.main.feelsLike,
            tempMin = response.main.tempMin,
            tempMax = response.main.tempMax,
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            windSpeed = response.wind.speed,
            windDegree = response.wind.degree,
            weatherDescription = weather?.description ?: "",
            weatherIcon = weather?.icon ?: "",
            weatherMain = weather?.main ?: "",
            clouds = response.clouds.all,
            visibility = response.visibility,
            sunrise = response.sys.sunrise,
            sunset = response.sys.sunset,
            timezone = response.timezone,
            latitude = response.coordinates.latitude,
            longitude = response.coordinates.longitude,
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    private fun mapToForecastEntities(response: ForecastResponse, cityId: Long): List<ForecastEntity> {
        return response.list.map { item ->
            val weather = item.weather.firstOrNull()
            ForecastEntity(
                cityId = cityId,
                dateTime = item.dateTime,
                temperature = item.main.temperature,
                feelsLike = item.main.feelsLike,
                tempMin = item.main.tempMin,
                tempMax = item.main.tempMax,
                humidity = item.main.humidity,
                pressure = item.main.pressure,
                weatherDescription = weather?.description ?: "",
                weatherIcon = weather?.icon ?: "",
                weatherMain = weather?.main ?: "",
                windSpeed = item.wind.speed,
                windDegree = item.wind.degree,
                clouds = item.clouds.all,
                visibility = item.visibility,
                pop = item.pop,
                dateText = item.dateText,
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}
