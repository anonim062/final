package com.weather.app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weather.app.util.PreferencesManager
import com.weather.app.util.TemperatureUnit

/**
 * ViewModel for Settings screen.
 */
class SettingsViewModel : ViewModel() {
    
    private val _temperatureUnit = MutableLiveData<TemperatureUnit>()
    val temperatureUnit: LiveData<TemperatureUnit> = _temperatureUnit

    private val _themeMode = MutableLiveData<Int>()
    val themeMode: LiveData<Int> = _themeMode

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> = _logoutEvent
    
    init {
        _temperatureUnit.value = PreferencesManager.temperatureUnit
        _themeMode.value = PreferencesManager.themeMode
    }
    
    fun setTemperatureUnit(unit: TemperatureUnit) {
        if (PreferencesManager.temperatureUnit != unit) {
            PreferencesManager.temperatureUnit = unit
            _temperatureUnit.value = unit
        }
    }

    fun setThemeMode(mode: Int) {
        if (PreferencesManager.themeMode != mode) {
            PreferencesManager.themeMode = mode
            _themeMode.value = mode
            applyTheme(mode)
        }
    }

    private fun applyTheme(mode: Int) {
        val nightMode = when (mode) {
            1 -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            2 -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            else -> androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    fun logout() {
        PreferencesManager.logout()
        _logoutEvent.value = true
    }
}
