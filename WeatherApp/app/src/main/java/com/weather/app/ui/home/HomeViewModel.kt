package com.weather.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weather.app.WeatherApplication
import com.weather.app.data.local.entity.CityEntity
import com.weather.app.data.local.entity.WeatherEntity
import com.weather.app.data.repository.CityRepository
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.util.PreferencesManager
import com.weather.app.util.Resource
import com.weather.app.util.TemperatureUnit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen displaying current weather.
 */
class HomeViewModel(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository
) : ViewModel() {
    
    private val _currentWeather = MutableLiveData<Resource<WeatherEntity>>()
    val currentWeather: LiveData<Resource<WeatherEntity>> = _currentWeather
    
    private val _currentCity = MutableLiveData<CityEntity?>()
    val currentCity: LiveData<CityEntity?> = _currentCity
    
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    
    private val _temperatureUnit = MutableLiveData<TemperatureUnit>()
    val temperatureUnit: LiveData<TemperatureUnit> = _temperatureUnit
    
    init {
        _temperatureUnit.value = PreferencesManager.temperatureUnit
        loadDefaultCity()
    }
    
    /**
     * Load the default city and fetch its weather.
     */
    fun loadDefaultCity() {
        viewModelScope.launch {
            cityRepository.getDefaultCity().collectLatest { city ->
                _currentCity.value = city
                city?.let {
                    fetchWeather(it.id)
                }
            }
        }
    }
    
    /**
     * Fetch current weather for a city.
     */
    fun fetchWeather(cityId: Long) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(cityId).collectLatest { resource ->
                _currentWeather.value = resource
                if (resource !is Resource.Loading) {
                    _isRefreshing.value = false
                }
            }
        }
    }
    
    /**
     * Fetch weather by coordinates.
     */
    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeatherByCoordinates(lat, lon).collectLatest { resource ->
                _currentWeather.value = resource
                if (resource !is Resource.Loading) {
                    _isRefreshing.value = false
                }
            }
        }
    }
    
    /**
     * Refresh weather data.
     */
    fun refresh() {
        _isRefreshing.value = true
        _currentCity.value?.let {
            fetchWeather(it.id)
        } ?: run {
            _isRefreshing.value = false
        }
    }
    
    /**
     * Update temperature unit and refresh data.
     */
    fun setTemperatureUnit(unit: TemperatureUnit) {
        PreferencesManager.temperatureUnit = unit
        _temperatureUnit.value = unit
        refresh()
    }
    
    /**
     * Convert temperature based on current unit setting.
     */
    fun formatTemperature(temp: Double): String {
        return "${temp.toInt()}${PreferencesManager.temperatureUnit.getSymbol()}"
    }
    
    /**
     * Factory for creating HomeViewModel.
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = WeatherApplication.instance
            return HomeViewModel(
                weatherRepository = app.weatherRepository,
                cityRepository = app.cityRepository
            ) as T
        }
    }
}
