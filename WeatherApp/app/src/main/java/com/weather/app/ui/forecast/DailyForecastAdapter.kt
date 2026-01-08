package com.weather.app.ui.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weather.app.databinding.ItemDailyForecastBinding
import com.weather.app.util.Constants
import com.weather.app.util.DateUtils
import com.weather.app.util.PreferencesManager

class DailyForecastAdapter : ListAdapter<DailyForecast, DailyForecastAdapter.ViewHolder>(DiffCallback()) {
    
    private val expandedItems = mutableSetOf<Int>()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), expandedItems.contains(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemDailyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.headerLayout.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (expandedItems.contains(position)) {
                        expandedItems.remove(position)
                    } else {
                        expandedItems.add(position)
                    }
                    notifyItemChanged(position)
                }
            }
        }
        
        fun bind(forecast: DailyForecast, isExpanded: Boolean) {
            val unit = PreferencesManager.temperatureUnit.getSymbol()
            
            // Day name
            val dayName = if (DateUtils.isToday(forecast.hourlyForecasts.firstOrNull()?.dateTime ?: 0)) {
                "Today"
            } else {
                DateUtils.formatFullDate(forecast.hourlyForecasts.firstOrNull()?.dateTime ?: 0)
            }
            binding.textDay.text = dayName
            
            // Weather description
            binding.textDescription.text = forecast.weatherDescription.replaceFirstChar { 
                it.uppercase() 
            }
            
            // Temperature range
            binding.textTempRange.text = "${forecast.minTemp.toInt()}° / ${forecast.maxTemp.toInt()}°"
            
            // Weather icon
            val iconUrl = String.format(Constants.WEATHER_ICON_URL, forecast.weatherIcon)
            Glide.with(binding.root.context)
                .load(iconUrl)
                .into(binding.imageWeatherIcon)
            
            // Expand/collapse
            binding.recyclerHourly.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.imageExpand.rotation = if (isExpanded) 180f else 0f
            
            // Setup hourly forecasts
            if (isExpanded) {
                binding.recyclerHourly.layoutManager = LinearLayoutManager(
                    binding.root.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                val hourlyAdapter = HourlyForecastAdapter()
                binding.recyclerHourly.adapter = hourlyAdapter
                hourlyAdapter.submitList(forecast.hourlyForecasts)
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }
        
        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
