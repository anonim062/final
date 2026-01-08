package com.weather.app.data.local.dao

import androidx.room.*
import com.weather.app.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for city/location operations.
 */
@Dao
interface CityDao {
    
    @Query("SELECT * FROM cities ORDER BY isDefault DESC, addedAt DESC")
    fun getAllCities(): Flow<List<CityEntity>>
    
    @Query("SELECT * FROM cities ORDER BY isDefault DESC, addedAt DESC")
    suspend fun getAllCitiesOnce(): List<CityEntity>
    
    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun getCityById(cityId: Long): CityEntity?
    
    @Query("SELECT * FROM cities WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultCity(): CityEntity?
    
    @Query("SELECT * FROM cities WHERE isDefault = 1 LIMIT 1")
    fun getDefaultCityFlow(): Flow<CityEntity?>
    
    @Query("SELECT * FROM cities WHERE isCurrentLocation = 1 LIMIT 1")
    suspend fun getCurrentLocationCity(): CityEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCities(cities: List<CityEntity>)
    
    @Update
    suspend fun updateCity(city: CityEntity)
    
    @Delete
    suspend fun deleteCity(city: CityEntity)
    
    @Query("DELETE FROM cities WHERE id = :cityId")
    suspend fun deleteCityById(cityId: Long)
    
    @Query("DELETE FROM cities")
    suspend fun deleteAllCities()
    
    @Query("UPDATE cities SET isDefault = 0")
    suspend fun clearDefaultCity()
    
    @Query("UPDATE cities SET isDefault = 1 WHERE id = :cityId")
    suspend fun setDefaultCity(cityId: Long)
    
    @Query("SELECT COUNT(*) FROM cities")
    suspend fun getCityCount(): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM cities WHERE id = :cityId)")
    suspend fun cityExists(cityId: Long): Boolean
}
