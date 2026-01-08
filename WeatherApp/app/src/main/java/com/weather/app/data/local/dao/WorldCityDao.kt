package com.weather.app.data.local.dao

import androidx.room.*
import com.weather.app.data.local.entity.WorldCityEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for searching pre-populated world cities database.
 */
@Dao
interface WorldCityDao {
    
    @Query("SELECT * FROM world_cities WHERE name LIKE :query || '%' ORDER BY population DESC LIMIT 20")
    suspend fun searchCities(query: String): List<WorldCityEntity>
    
    @Query("SELECT * FROM world_cities WHERE name LIKE :query || '%' ORDER BY population DESC LIMIT 20")
    fun searchCitiesFlow(query: String): Flow<List<WorldCityEntity>>
    
    @Query("SELECT * FROM world_cities WHERE countryCode = :countryCode ORDER BY population DESC LIMIT 50")
    suspend fun getCitiesByCountry(countryCode: String): List<WorldCityEntity>
    
    @Query("SELECT * FROM world_cities WHERE id = :cityId")
    suspend fun getCityById(cityId: Long): WorldCityEntity?
    
    @Query("SELECT * FROM world_cities ORDER BY population DESC LIMIT 100")
    suspend fun getTopCities(): List<WorldCityEntity>
    
    @Query("SELECT COUNT(*) FROM world_cities")
    suspend fun getCityCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<WorldCityEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: WorldCityEntity)
}
