package com.weather.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.weather.app.data.local.dao.CityDao
import com.weather.app.data.local.dao.ForecastDao
import com.weather.app.data.local.dao.WeatherDao
import com.weather.app.data.local.dao.WorldCityDao
import com.weather.app.data.local.entity.CityEntity
import com.weather.app.data.local.entity.ForecastEntity
import com.weather.app.data.local.entity.WeatherEntity
import com.weather.app.data.local.entity.WorldCityEntity
import com.weather.app.data.local.entity.UserEntity
import com.weather.app.data.local.dao.UserDao

/**
 * Room database for the Weather App.
 * Contains 4 tables: current_weather, forecast, cities, world_cities
 * The world_cities table is pre-populated with city data from SQLite.
 */
@Database(
    entities = [
        WeatherEntity::class,
        ForecastEntity::class,
        CityEntity::class,
        WorldCityEntity::class,
        UserEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
    abstract fun cityDao(): CityDao
    abstract fun worldCityDao(): WorldCityDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        
        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
