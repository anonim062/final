package com.weather.app.ui.forecast

import android.content.Context
import android.content.Intent
import com.weather.app.databinding.ActivityForecastBinding
import com.weather.app.ui.base.BaseActivity

class ForecastActivity : BaseActivity<ActivityForecastBinding>() {

    override fun getViewBinding(): ActivityForecastBinding {
        return ActivityForecastBinding.inflate(layoutInflater)
    }

    override fun getScreenTitle(): String = "5 Day Forecast"

    override fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun setupObservers() {
        // Observe forecast data
    }

    companion object {
        private const val ARG_CITY_NAME = "city_name"
        
        fun start(context: Context, cityName: String) {
            val intent = Intent(context, ForecastActivity::class.java).apply {
                putExtra(ARG_CITY_NAME, cityName)
            }
            context.startActivity(intent)
        }
    }
}
