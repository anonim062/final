package com.weather.app.util

import android.content.Context
import android.content.SharedPreferences
import com.weather.app.WeatherApplication

/**
 * SharedPreferences helper for app settings.
 */
object PreferencesManager {
    
    private const val PREFS_NAME = "weather_prefs"
    private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
    private const val KEY_DEFAULT_CITY_ID = "default_city_id"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_CURRENT_USER_ID = "current_user_id"
    private const val KEY_THEME_MODE = "theme_mode"
    
    private val prefs: SharedPreferences by lazy {
        WeatherApplication.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    var temperatureUnit: TemperatureUnit
        get() {
            val value = prefs.getString(KEY_TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name)
            return TemperatureUnit.valueOf(value ?: TemperatureUnit.CELSIUS.name)
        }
        set(value) {
            prefs.edit().putString(KEY_TEMPERATURE_UNIT, value.name).apply()
        }
    
    var defaultCityId: Long
        get() = prefs.getLong(KEY_DEFAULT_CITY_ID, -1L)
        set(value) {
            prefs.edit().putLong(KEY_DEFAULT_CITY_ID, value).apply()
        }
    
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
        }

    var currentUserId: Long
        get() = prefs.getLong(KEY_CURRENT_USER_ID, -1L)
        set(value) {
            prefs.edit().putLong(KEY_CURRENT_USER_ID, value).apply()
        }

    val isLoggedIn: Boolean
        get() = currentUserId != -1L

    fun logout() {
        currentUserId = -1L
    }
    
    // Theme Mode: 0 = System, 1 = Light, 2 = Dark
    var themeMode: Int
        get() = prefs.getInt(KEY_THEME_MODE, 0)
        set(value) {
            prefs.edit().putInt(KEY_THEME_MODE, value).apply()
        }
}

/**
 * Enum for temperature units.
 */
enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;
    
    fun getApiUnit(): String {
        return when (this) {
            CELSIUS -> "metric"
            FAHRENHEIT -> "imperial"
        }
    }
    
    fun getSymbol(): String {
        return when (this) {
            CELSIUS -> "°C"
            FAHRENHEIT -> "°F"
        }
    }
}
