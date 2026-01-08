package com.weather.app.data.local.dao

import androidx.room.*
import com.weather.app.data.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for forecast operations.
 */
@Dao
interface ForecastDao {
    
    @Query("SELECT * FROM forecast WHERE cityId = :cityId ORDER BY dateTime ASC")
    fun getForecastByCityId(cityId: Long): Flow<List<ForecastEntity>>
    
    @Query("SELECT * FROM forecast WHERE cityId = :cityId ORDER BY dateTime ASC")
    suspend fun getForecastByCityIdOnce(cityId: Long): List<ForecastEntity>
    
    @Query("SELECT * FROM forecast WHERE cityId = :cityId AND dateTime >= :startTime ORDER BY dateTime ASC")
    fun getUpcomingForecast(cityId: Long, startTime: Long): Flow<List<ForecastEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForecasts(forecasts: List<ForecastEntity>)
    
    @Update
    suspend fun updateForecast(forecast: ForecastEntity)
    
    @Delete
    suspend fun deleteForecast(forecast: ForecastEntity)
    
    @Query("DELETE FROM forecast WHERE cityId = :cityId")
    suspend fun deleteForecastByCityId(cityId: Long)
    
    @Query("DELETE FROM forecast")
    suspend fun deleteAllForecasts()
    
    @Query("DELETE FROM forecast WHERE lastUpdated < :timestamp")
    suspend fun deleteStaleForecasts(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM forecast WHERE cityId = :cityId")
    suspend fun getForecastCount(cityId: Long): Int
}
