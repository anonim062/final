package com.weather.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.weather.app.R
import com.weather.app.data.local.entity.WeatherEntity
import com.weather.app.databinding.FragmentHomeBinding
import com.weather.app.databinding.ItemWeatherDetailBinding
import com.weather.app.util.Constants
import com.weather.app.util.DateUtils
import com.weather.app.util.PreferencesManager
import com.weather.app.util.Resource

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory() }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSwipeRefresh()
        observeViewModel()
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
    }
    
    private fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    resource.data?.let { weather ->
                        showWeather(weather)
                    }
                }
                is Resource.Error -> {
                    showError(resource.message ?: getString(R.string.error_loading_weather))
                }
            }
        }
        
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.VISIBLE
        binding.textError.text = message
        binding.contentLayout.visibility = View.GONE
    }
    
    private fun showWeather(weather: WeatherEntity) {
        binding.progressBar.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
        
        val unit = PreferencesManager.temperatureUnit.getSymbol()
        
        // City name
        binding.textCityName.text = "${weather.cityName}, ${weather.country}"
        
        // Last updated
        binding.textLastUpdated.text = getString(
            R.string.last_updated,
            DateUtils.getRelativeTime(weather.lastUpdated)
        )
        
        // Temperature
        binding.textTemperature.text = "${weather.temperature.toInt()}$unit"
        
        // Weather description
        binding.textWeatherDescription.text = weather.weatherDescription.replaceFirstChar { 
            it.uppercase() 
        }
        
        // Feels like
        binding.textFeelsLike.text = getString(
            R.string.feels_like,
            "${weather.feelsLike.toInt()}$unit"
        )
        
        // Min/Max temperature
        binding.textTempMin.text = "${weather.tempMin.toInt()}$unit"
        binding.textTempMax.text = "${weather.tempMax.toInt()}$unit"
        
        // Weather icon
        val iconUrl = String.format(Constants.WEATHER_ICON_URL, weather.weatherIcon)
        Glide.with(this)
            .load(iconUrl)
            .into(binding.imageWeatherIcon)
        
        // Weather details
        setupDetailCard(
            binding.cardHumidity,
            R.drawable.ic_humidity,
            getString(R.string.humidity),
            "${weather.humidity}%"
        )
        
        setupDetailCard(
            binding.cardWind,
            R.drawable.ic_weather_sunny, // wind icon placeholder
            getString(R.string.wind),
            "${weather.windSpeed} m/s"
        )
        
        setupDetailCard(
            binding.cardPressure,
            R.drawable.ic_thermostat,
            getString(R.string.pressure),
            "${weather.pressure} hPa"
        )
        
        setupDetailCard(
            binding.cardVisibility,
            R.drawable.ic_search,
            getString(R.string.visibility),
            "${weather.visibility / 1000} km"
        )
        
        setupDetailCard(
            binding.cardSunrise,
            R.drawable.ic_arrow_up,
            getString(R.string.sunrise),
            DateUtils.formatTime(weather.sunrise)
        )
        
        setupDetailCard(
            binding.cardSunset,
            R.drawable.ic_arrow_down,
            getString(R.string.sunset),
            DateUtils.formatTime(weather.sunset)
        )
        
        // Open detailed forecast on click
        binding.contentLayout.setOnClickListener {
            com.weather.app.ui.forecast.ForecastActivity.start(requireContext(), weather.cityName)
        }
    }
    
    private fun setupDetailCard(
        cardBinding: ItemWeatherDetailBinding,
        iconRes: Int,
        label: String,
        value: String
    ) {
        cardBinding.imageIcon.setImageResource(iconRes)
        cardBinding.textLabel.text = label
        cardBinding.textValue.text = value
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
