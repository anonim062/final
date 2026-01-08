package com.weather.app

import android.app.Application
import com.weather.app.data.local.CitiesDatabaseHelper
import com.weather.app.data.local.WeatherDatabase
import com.weather.app.data.remote.api.RetrofitClient
import com.weather.app.data.repository.CityRepository
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WeatherApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Database instance
    val database: WeatherDatabase by lazy {
        WeatherDatabase.getDatabase(this)
    }
    
    // Repository instances
    val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(
            weatherApi = RetrofitClient.weatherApi,
            weatherDao = database.weatherDao(),
            forecastDao = database.forecastDao()
        )
    }
    
    val cityRepository: CityRepository by lazy {
        CityRepository(
            cityDao = database.cityDao(),
            weatherApi = RetrofitClient.weatherApi
        )
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }
    
    companion object {
        lateinit var instance: WeatherApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize cities database on first launch
        initializeCitiesDatabase()

        // Apply theme
        val themeMode = com.weather.app.util.PreferencesManager.themeMode
        val nightMode = when (themeMode) {
            1 -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            2 -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            else -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    
    /**
     * Populate the world cities database if empty.
     * First tries bundled data, then optionally downloads from internet.
     */
    private fun initializeCitiesDatabase() {
        applicationScope.launch {
            if (CitiesDatabaseHelper.needsPopulation(database)) {
                // First populate from bundled data (immediate, offline)
                CitiesDatabaseHelper.populateFromBundledData(database)
                
                // Optionally try to download more cities from internet
                // Uncomment the line below if you want to download additional cities
                // CitiesDatabaseHelper.downloadAndPopulateCities(database)
            }
        }
    }
}
