package com.weather.app.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weather.app.data.local.entity.ForecastEntity
import com.weather.app.databinding.ItemHourlyForecastBinding
import com.weather.app.util.Constants
import com.weather.app.util.DateUtils
import com.weather.app.util.PreferencesManager

class HourlyForecastAdapter : ListAdapter<ForecastEntity, HourlyForecastAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHourlyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(
        private val binding: ItemHourlyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(forecast: ForecastEntity) {
            val unit = PreferencesManager.temperatureUnit.getSymbol()
            
            // Time
            binding.textTime.text = DateUtils.formatTime(forecast.dateTime)
            
            // Temperature
            binding.textTemp.text = "${forecast.temperature.toInt()}°"
            
            // Rain probability
            val popPercent = (forecast.pop * 100).toInt()
            binding.textPop.text = if (popPercent > 0) "$popPercent%" else ""
            
            // Weather icon
            val iconUrl = String.format(Constants.WEATHER_ICON_URL, forecast.weatherIcon)
            Glide.with(binding.root.context)
                .load(iconUrl)
                .into(binding.imageIcon)
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<ForecastEntity>() {
        override fun areItemsTheSame(oldItem: ForecastEntity, newItem: ForecastEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ForecastEntity, newItem: ForecastEntity): Boolean {
            return oldItem == newItem
        }
    }
}
