package com.weather.app.ui.search

import android.content.Context
import android.content.Intent
import com.weather.app.databinding.ActivitySearchCityBinding
import com.weather.app.ui.base.BaseActivity

class SearchCityActivity : BaseActivity<ActivitySearchCityBinding>() {

    override fun getViewBinding(): ActivitySearchCityBinding {
        return ActivitySearchCityBinding.inflate(layoutInflater)
    }

    override fun getScreenTitle(): String = "Add City"

    override fun setupUI() {
        // setSupportActionBar(binding.toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun setupObservers() {
        // Observe search results
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SearchCityActivity::class.java))
        }
    }
}
