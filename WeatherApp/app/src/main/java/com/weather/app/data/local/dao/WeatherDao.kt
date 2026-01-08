package com.weather.app.data.local.dao

import androidx.room.*
import com.weather.app.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for current weather operations.
 */
@Dao
interface WeatherDao {
    
    @Query("SELECT * FROM current_weather WHERE cityId = :cityId")
    fun getWeatherByCityId(cityId: Long): Flow<WeatherEntity?>
    
    @Query("SELECT * FROM current_weather WHERE cityId = :cityId")
    suspend fun getWeatherByCityIdOnce(cityId: Long): WeatherEntity?
    
    @Query("SELECT * FROM current_weather ORDER BY lastUpdated DESC")
    fun getAllWeather(): Flow<List<WeatherEntity>>
    
    @Query("SELECT * FROM current_weather ORDER BY lastUpdated DESC LIMIT 1")
    fun getLatestWeather(): Flow<WeatherEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
    
    @Update
    suspend fun updateWeather(weather: WeatherEntity)
    
    @Delete
    suspend fun deleteWeather(weather: WeatherEntity)
    
    @Query("DELETE FROM current_weather WHERE cityId = :cityId")
    suspend fun deleteWeatherByCityId(cityId: Long)
    
    @Query("DELETE FROM current_weather")
    suspend fun deleteAllWeather()
    
    @Query("SELECT * FROM current_weather WHERE lastUpdated < :timestamp")
    suspend fun getStaleWeather(timestamp: Long): List<WeatherEntity>
    
    @Query("DELETE FROM current_weather WHERE lastUpdated < :timestamp")
    suspend fun deleteStaleWeather(timestamp: Long)
}
