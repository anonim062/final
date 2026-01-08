# Weather App for Android

A comprehensive Android Weather Application built with Kotlin, featuring real-time weather data from OpenWeatherMap API, local caching with Room database, GPS location services, and offline support.

## 📱 Features

- **Current Weather** - Temperature, humidity, wind speed, pressure, sunrise/sunset
- **5-Day Forecast** - Hourly breakdown with expandable daily cards
- **Multiple Locations** - Add and manage multiple cities
- **City Search** - Search cities via OpenWeatherMap Geocoding API
- **Offline Cache** - View previous weather data when offline
- **Temperature Toggle** - Switch between Celsius and Fahrenheit
- **Pull-to-Refresh** - Manual update for weather data
- **Current Location** - Get weather for GPS location

## 🔧 Technical Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| Architecture | MVVM + Repository Pattern |
| Database | Room (3 tables) |
| Networking | Retrofit + OkHttp |
| API | OpenWeatherMap REST API |
| Location | Google Play Services |
| Async | Kotlin Coroutines + Flow |
| UI | Material Design 3 |
| Navigation | Jetpack Navigation |
| Image Loading | Glide |

## 📁 Project Structure

```
app/src/main/java/com/weather/app/
├── data/
│   ├── local/
│   │   ├── dao/           # Room DAOs
│   │   ├── entity/        # Room Entities
│   │   └── WeatherDatabase.kt
│   ├── remote/
│   │   ├── api/           # Retrofit API interfaces
│   │   └── model/         # API response models
│   └── repository/        # Repository classes
├── ui/
│   ├── home/              # Current weather screen
│   ├── forecast/          # 5-day forecast screen
│   ├── cities/            # City management screen
│   └── settings/          # Settings screen
├── util/                  # Utility classes
└── WeatherApplication.kt
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 17
- Android SDK 34
- OpenWeatherMap API key (free tier)

### Setup

1. Clone the repository
2. Open in Android Studio
3. Get a free API key from [OpenWeatherMap](https://openweathermap.org/api)
4. Add your API key in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "WEATHER_API_KEY", "\"YOUR_API_KEY_HERE\"")
   ```
5. Sync Gradle and run the app

## 📦 Build

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

## 🗃️ Database Schema

### Tables

1. **current_weather** - Cached current weather data
2. **forecast** - 5-day forecast with 3-hour intervals
3. **cities** - Saved city locations

## 📄 License

This project is for educational purposes.

## 🙏 Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/) for weather data API
- [Material Design](https://material.io/) for design guidelines
