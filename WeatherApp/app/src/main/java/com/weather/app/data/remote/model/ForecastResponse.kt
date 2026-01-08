package com.weather.app.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for 5-day forecast API endpoint.
 * GET /forecast?q={city}&appid={key}&units=metric
 */
data class ForecastResponse(
    @SerializedName("cod")
    val cod: String,
    
    @SerializedName("message")
    val message: Int,
    
    @SerializedName("cnt")
    val count: Int,
    
    @SerializedName("list")
    val list: List<ForecastItem>,
    
    @SerializedName("city")
    val city: City
)

data class ForecastItem(
    @SerializedName("dt")
    val dateTime: Long,
    
    @SerializedName("main")
    val main: Main,
    
    @SerializedName("weather")
    val weather: List<Weather>,
    
    @SerializedName("clouds")
    val clouds: Clouds,
    
    @SerializedName("wind")
    val wind: Wind,
    
    @SerializedName("visibility")
    val visibility: Int,
    
    @SerializedName("pop")
    val pop: Double,
    
    @SerializedName("sys")
    val sys: ForecastSys? = null,
    
    @SerializedName("dt_txt")
    val dateText: String
)

data class ForecastSys(
    @SerializedName("pod")
    val pod: String // "d" for day, "n" for night
)

data class City(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("coord")
    val coordinates: Coordinates,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("population")
    val population: Int,
    
    @SerializedName("timezone")
    val timezone: Int,
    
    @SerializedName("sunrise")
    val sunrise: Long,
    
    @SerializedName("sunset")
    val sunset: Long
)
