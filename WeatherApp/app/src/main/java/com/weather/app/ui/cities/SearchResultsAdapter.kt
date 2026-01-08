package com.weather.app.ui.cities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.weather.app.data.remote.model.GeocodingResponse
import com.weather.app.databinding.ItemSearchCityBinding

class SearchResultsAdapter(
    private val onCityClick: (GeocodingResponse) -> Unit
) : ListAdapter<GeocodingResponse, SearchResultsAdapter.ViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchCityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onCityClick)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ViewHolder(
        private val binding: ItemSearchCityBinding,
        private val onCityClick: (GeocodingResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(city: GeocodingResponse) {
            binding.textCityName.text = city.name
            binding.textCityDetails.text = buildString {
                city.state?.let { append("$it, ") }
                append(city.country)
            }
            
            binding.root.setOnClickListener {
                onCityClick(city)
            }
            
            binding.imageAdd.setOnClickListener {
                onCityClick(city)
            }
        }
    }
    
    private class DiffCallback : DiffUtil.ItemCallback<GeocodingResponse>() {
        override fun areItemsTheSame(
            oldItem: GeocodingResponse,
            newItem: GeocodingResponse
        ): Boolean {
            return oldItem.latitude == newItem.latitude && 
                   oldItem.longitude == newItem.longitude
        }
        
        override fun areContentsTheSame(
            oldItem: GeocodingResponse,
            newItem: GeocodingResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}
