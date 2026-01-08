package com.weather.app.ui.cities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weather.app.data.local.entity.CityEntity
import com.weather.app.data.local.entity.WeatherEntity
import com.weather.app.databinding.ItemCityBinding
import com.weather.app.util.Constants
import com.weather.app.util.PreferencesManager

class SavedCitiesAdapter(
    private val onCityClick: (CityEntity) -> Unit,
    private val onCityLongClick: (CityEntity) -> Unit,
    private val onDeleteClick: (CityEntity) -> Unit
) : ListAdapter<CityEntity, SavedCitiesAdapter.ViewHolder>(DiffCallback()) {
    
    private var weatherMap: Map<Long, WeatherEntity> = emptyMap()
    
    fun updateWeather(weather: Map<Long, WeatherEntity>) {
        weatherMap = weather
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onCityClick, onCityLongClick, onDeleteClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = getItem(position)
        val weather = weatherMap[city.id]
        holder.bind(city, weather)
    }
    
    class ViewHolder(
        private val binding: ItemCityBinding,
        private val onCityClick: (CityEntity) -> Unit,
        private val onCityLongClick: (CityEntity) -> Unit,
        private val onDeleteClick: (CityEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private var currentCity: CityEntity? = null
        private var showDelete = false
        
        init {
            binding.root.setOnClickListener {
                if (showDelete) {
                    showDelete = false
                    binding.imageDelete.visibility = View.GONE
                    binding.imageWeatherIcon.visibility = View.VISIBLE
                } else {
                    currentCity?.let { onCityClick(it) }
                }
            }
            
            binding.root.setOnLongClickListener {
                showDelete = !showDelete
                binding.imageDelete.visibility = if (showDelete) View.VISIBLE else View.GONE
                binding.imageWeatherIcon.visibility = if (showDelete) View.GONE else View.VISIBLE
                currentCity?.let { onCityLongClick(it) }
                true
            }
            
            binding.imageDelete.setOnClickListener {
                currentCity?.let { onDeleteClick(it) }
            }
        }
        
        fun bind(city: CityEntity, weather: WeatherEntity?) {
            currentCity = city
            showDelete = false
            
            val unit = PreferencesManager.temperatureUnit.getSymbol()
            
            // City name
            binding.textCityName.text = "${city.name}, ${city.country}"
            
            // Default badge
            binding.textDefaultBadge.visibility = if (city.isDefault) View.VISIBLE else View.GONE
            
            // Weather info
            if (weather != null) {
                binding.textWeatherDescription.text = weather.weatherDescription.replaceFirstChar { 
                    it.uppercase() 
                }
                binding.textTemperature.text = "${weather.temperature.toInt()}°"
                
                val iconUrl = String.format(Constants.WEATHER_ICON_URL, weather.weatherIcon)
                Glide.with(binding.root.context)
                    .load(iconUrl)
                    .into(binding.imageWeatherIcon)
            } else {
                binding.textWeatherDescription.text = ""
                binding.textTemperature.text = "--"
            }
            
            binding.imageDelete.visibility = View.GONE
            binding.imageWeatherIcon.visibility = View.VISIBLE
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<CityEntity>() {
        override fun areItemsTheSame(oldItem: CityEntity, newItem: CityEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: CityEntity, newItem: CityEntity): Boolean {
            return oldItem == newItem
        }
    }
}
