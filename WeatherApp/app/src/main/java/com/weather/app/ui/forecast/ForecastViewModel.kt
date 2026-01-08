package com.weather.app.ui.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weather.app.WeatherApplication
import com.weather.app.data.local.entity.ForecastEntity
import com.weather.app.data.repository.CityRepository
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.util.PreferencesManager
import com.weather.app.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the Forecast screen displaying 5-day forecast.
 */
class ForecastViewModel(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository
) : ViewModel() {
    
    private val _forecast = MutableLiveData<Resource<List<ForecastEntity>>>()
    val forecast: LiveData<Resource<List<ForecastEntity>>> = _forecast
    
    private val _dailyForecast = MutableLiveData<List<DailyForecast>>()
    val dailyForecast: LiveData<List<DailyForecast>> = _dailyForecast
    
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    
    private var currentCityId: Long = -1
    
    init {
        loadDefaultCityForecast()
    }
    
    /**
     * Load forecast for the default city.
     */
    fun loadDefaultCityForecast() {
        viewModelScope.launch {
            cityRepository.getDefaultCity().collectLatest { city ->
                city?.let {
                    currentCityId = it.id
                    fetchForecast(it.id)
                }
            }
        }
    }
    
    /**
     * Fetch forecast for a city.
     */
    fun fetchForecast(cityId: Long) {
        viewModelScope.launch {
            weatherRepository.getForecast(cityId).collectLatest { resource ->
                _forecast.value = resource
                
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { forecasts ->
                            _dailyForecast.value = groupByDay(forecasts)
                        }
                        _isRefreshing.value = false
                    }
                    is Resource.Error -> {
                        _isRefreshing.value = false
                    }
                    is Resource.Loading -> {
                        // Keep refreshing indicator
                    }
                }
            }
        }
    }
    
    /**
     * Fetch forecast by coordinates.
     */
    fun fetchForecastByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.getForecastByCoordinates(lat, lon).collectLatest { resource ->
                _forecast.value = resource
                
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { forecasts ->
                            _dailyForecast.value = groupByDay(forecasts)
                        }
                        _isRefreshing.value = false
                    }
                    is Resource.Error -> {
                        _isRefreshing.value = false
                    }
                    is Resource.Loading -> {
                        // Keep refreshing indicator
                    }
                }
            }
        }
    }
    
    /**
     * Refresh forecast data.
     */
    fun refresh() {
        _isRefreshing.value = true
        if (currentCityId != -1L) {
            fetchForecast(currentCityId)
        } else {
            loadDefaultCityForecast()
        }
    }
    
    /**
     * Group forecast items by day.
     */
    private fun groupByDay(forecasts: List<ForecastEntity>): List<DailyForecast> {
        return forecasts.groupBy { forecast ->
            // Group by date (YYYY-MM-DD from dateText)
            forecast.dateText.substringBefore(" ")
        }.map { (date, items) ->
            val minTemp = items.minOfOrNull { it.tempMin } ?: 0.0
            val maxTemp = items.maxOfOrNull { it.tempMax } ?: 0.0
            val avgHumidity = items.map { it.humidity }.average().toInt()
            val mainWeather = items.groupingBy { it.weatherMain }.eachCount().maxByOrNull { it.value }?.key ?: ""
            val mainIcon = items.find { it.weatherMain == mainWeather }?.weatherIcon ?: ""
            val mainDescription = items.find { it.weatherMain == mainWeather }?.weatherDescription ?: ""
            
            DailyForecast(
                date = date,
                minTemp = minTemp,
                maxTemp = maxTemp,
                avgHumidity = avgHumidity,
                weatherMain = mainWeather,
                weatherIcon = mainIcon,
                weatherDescription = mainDescription,
                hourlyForecasts = items.sortedBy { it.dateTime }
            )
        }
    }
    
    /**
     * Format temperature for display.
     */
    fun formatTemperature(temp: Double): String {
        return "${temp.toInt()}${PreferencesManager.temperatureUnit.getSymbol()}"
    }
    
    /**
     * Factory for creating ForecastViewModel.
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = WeatherApplication.instance
            return ForecastViewModel(
                weatherRepository = app.weatherRepository,
                cityRepository = app.cityRepository
            ) as T
        }
    }
}

/**
 * Data class representing a day's forecast summary.
 */
data class DailyForecast(
    val date: String,
    val minTemp: Double,
    val maxTemp: Double,
    val avgHumidity: Int,
    val weatherMain: String,
    val weatherIcon: String,
    val weatherDescription: String,
    val hourlyForecasts: List<ForecastEntity>
)
