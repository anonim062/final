package com.weather.app.ui.cities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.weather.app.WeatherApplication
import com.weather.app.data.local.entity.CityEntity
import com.weather.app.data.local.entity.WeatherEntity
import com.weather.app.data.remote.model.GeocodingResponse
import com.weather.app.data.repository.CityRepository
import com.weather.app.data.repository.WeatherRepository
import com.weather.app.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for Cities screen with search and city management.
 */
class CitiesViewModel(
    private val cityRepository: CityRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    
    private val _cities = MutableLiveData<List<CityEntity>>()
    val cities: LiveData<List<CityEntity>> = _cities
    
    private val _citiesWeather = MutableLiveData<Map<Long, WeatherEntity>>()
    val citiesWeather: LiveData<Map<Long, WeatherEntity>> = _citiesWeather
    
    private val _searchResults = MutableLiveData<Resource<List<GeocodingResponse>>>()
    val searchResults: LiveData<Resource<List<GeocodingResponse>>> = _searchResults
    
    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching
    
    private val _addCityResult = MutableLiveData<Resource<Long>>()
    val addCityResult: LiveData<Resource<Long>> = _addCityResult
    
    private var searchJob: Job? = null
    
    init {
        loadCities()
    }
    
    /**
     * Load all saved cities.
     */
    fun loadCities() {
        viewModelScope.launch {
            cityRepository.getAllCities().collectLatest { cityList ->
                _cities.value = cityList
                // Fetch weather for all cities
                loadCitiesWeather(cityList)
            }
        }
    }
    
    /**
     * Load weather data for all cities.
     */
    private val weatherMap = mutableMapOf<Long, WeatherEntity>()

    /**
     * Load weather data for all cities.
     */
    private fun loadCitiesWeather(cities: List<CityEntity>) {
        cities.forEach { city ->
            // Only fetch if we don't have fresh data (optional optimization, but good for flickering)
            // For now, we fetch to ensure it's up to date.
            
            viewModelScope.launch {
                weatherRepository.getCurrentWeather(city.id).collectLatest { resource ->
                    if (resource is Resource.Success && resource.data != null) {
                        val weather = resource.data
                        if (weatherMap[city.id] != weather) {
                            weatherMap[city.id] = weather
                            // Post the whole map. Creating a new map ensures LiveData emits distinct change.
                            _citiesWeather.postValue(HashMap(weatherMap))
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Search cities with debounce.
     */
    fun searchCities(query: String) {
        if (query.length < 2) {
            _searchResults.value = Resource.Success(emptyList())
            _isSearching.value = false
            return
        }
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(300) // Debounce
            
            cityRepository.searchCities(query).collectLatest { result ->
                _searchResults.value = result
                _isSearching.value = false
            }
        }
    }
    
    /**
     * Add a city from search results.
     */
    fun addCity(geocodingResponse: GeocodingResponse) {
        viewModelScope.launch {
            try {
                _addCityResult.value = Resource.Loading()
                val cityId = cityRepository.addCityFromGeocodingResponse(geocodingResponse)
                _addCityResult.value = Resource.Success(cityId)
            } catch (e: Exception) {
                _addCityResult.value = Resource.Error("Failed to add city: ${e.message}")
            }
        }
    }
    
    /**
     * Add current location as a city.
     */
    fun addCurrentLocation(name: String, country: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _addCityResult.value = Resource.Loading()
                val cityId = cityRepository.addCurrentLocationCity(name, country, lat, lon)
                _addCityResult.value = Resource.Success(cityId)
            } catch (e: Exception) {
                _addCityResult.value = Resource.Error("Failed to add location: ${e.message}")
            }
        }
    }
    
    /**
     * Remove a city.
     */
    fun removeCity(city: CityEntity) {
        viewModelScope.launch {
            cityRepository.removeCity(city)
        }
    }
    
    /**
     * Set a city as default.
     */
    fun setDefaultCity(cityId: Long) {
        viewModelScope.launch {
            cityRepository.setDefaultCity(cityId)
        }
    }
    
    /**
     * Clear search results.
     */
    fun clearSearchResults() {
        _searchResults.value = Resource.Success(emptyList())
        _isSearching.value = false
    }
    
    /**
     * Factory for creating CitiesViewModel.
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = WeatherApplication.instance
            return CitiesViewModel(
                cityRepository = app.cityRepository,
                weatherRepository = app.weatherRepository
            ) as T
        }
    }
}
