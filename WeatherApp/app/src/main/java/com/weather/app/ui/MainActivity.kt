package com.weather.app.ui

import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.weather.app.R
import com.weather.app.databinding.ActivityMainBinding
import com.weather.app.ui.base.BaseActivity
import com.weather.app.ui.settings.SettingsActivity
import com.weather.app.ui.profile.ProfileActivity
import com.weather.app.ui.about.AboutActivity
import com.weather.app.ui.search.SearchCityActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun getScreenTitle(): String = "Weather App"

    override fun setupUI() {
        setSupportActionBar(binding.toolbar)
        setupNavigation()
    }
    
    override fun setupObservers() {
        // Observers
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
        
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = destination.label
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                SettingsActivity.start(this)
                true
            }
            R.id.action_profile -> {
                ProfileActivity.start(this)
                true
            }
            R.id.action_about -> {
                AboutActivity.start(this)
                true
            }
            R.id.action_add_city -> {
                SearchCityActivity.start(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
