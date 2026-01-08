package com.weather.app.ui.settings

import android.content.Context
import android.content.Intent
import com.weather.app.databinding.ActivitySettingsBinding
import com.weather.app.ui.base.BaseActivity
import com.weather.app.util.AppTheme
import com.weather.app.util.PreferencesManager

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    override fun getViewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun getScreenTitle(): String = "Settings"

    override fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Setup theme selection radio group
        val currentTheme = PreferencesManager.themeMode
        when (currentTheme) {
            1 -> binding.rbLight.isChecked = true
            2 -> binding.rbDark.isChecked = true
            else -> binding.rbSystem.isChecked = true
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                binding.rbLight.id -> AppTheme.LIGHT
                binding.rbDark.id -> AppTheme.DARK
                else -> AppTheme.SYSTEM
            }
            applyTheme(theme)
        }
    }

    override fun setupObservers() {
        // Observe settings changes
    }

    private fun applyTheme(theme: AppTheme) {
        val mode = when(theme) {
            AppTheme.LIGHT -> 1
            AppTheme.DARK -> 2
            AppTheme.SYSTEM -> 0
        }
        PreferencesManager.themeMode = mode
        
        val nightMode = when (theme) {
            AppTheme.LIGHT -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            AppTheme.DARK -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            AppTheme.SYSTEM -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
