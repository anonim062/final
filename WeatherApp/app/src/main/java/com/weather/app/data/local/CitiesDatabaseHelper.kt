package com.weather.app.data.local

import android.content.Context
import android.util.Log
import com.weather.app.data.local.entity.WorldCityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

/**
 * Helper class to download and populate the cities database from internet.
 * Uses OpenWeatherMap city list or a custom JSON endpoint.
 */
object CitiesDatabaseHelper {
    
    private const val TAG = "CitiesDatabaseHelper"
    
    // Sample cities data URL (you can replace with actual cities JSON URL)
    private const val CITIES_JSON_URL = "https://raw.githubusercontent.com/lutangar/cities.json/master/cities.json"
    
    /**
     * Check if cities database needs to be populated.
     */
    suspend fun needsPopulation(database: WeatherDatabase): Boolean {
        return database.worldCityDao().getCityCount() == 0
    }
    
    /**
     * Populate cities database from bundled data.
     * This provides initial data even without internet.
     */
    suspend fun populateFromBundledData(database: WeatherDatabase) {
        withContext(Dispatchers.IO) {
            val cities = getBundledCities()
            database.worldCityDao().insertCities(cities)
            Log.d(TAG, "Populated ${cities.size} cities from bundled data")
        }
    }
    
    /**
     * Download and populate cities from internet.
     */
    suspend fun downloadAndPopulateCities(database: WeatherDatabase): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(CITIES_JSON_URL)
                val jsonString = url.readText()
                val jsonArray = JSONArray(jsonString)
                
                val cities = mutableListOf<WorldCityEntity>()
                
                for (i in 0 until minOf(jsonArray.length(), 5000)) { // Limit to 5000 cities
                    val cityJson = jsonArray.getJSONObject(i)
                    
                    val city = WorldCityEntity(
                        id = i.toLong(),
                        name = cityJson.getString("name"),
                        country = cityJson.optString("country", ""),
                        countryCode = cityJson.optString("country", ""),
                        latitude = cityJson.optDouble("lat", 0.0),
                        longitude = cityJson.optDouble("lng", 0.0),
                        population = cityJson.optLong("population", 0)
                    )
                    cities.add(city)
                }
                
                database.worldCityDao().insertCities(cities)
                Log.d(TAG, "Downloaded and inserted ${cities.size} cities from internet")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to download cities: ${e.message}")
                false
            }
        }
    }
    
    /**
     * Get bundled cities data (fallback when no internet).
     * Contains major cities worldwide.
     */
    private fun getBundledCities(): List<WorldCityEntity> {
        return listOf(
            // Azerbaijan
            WorldCityEntity(587084, "Baku", "Azerbaijan", "AZ", null, 40.4093, 49.8671, 2300000),
            WorldCityEntity(587085, "Ganja", "Azerbaijan", "AZ", null, 40.6828, 46.3606, 335600),
            WorldCityEntity(587086, "Sumqayit", "Azerbaijan", "AZ", null, 40.5897, 49.6686, 341200),
            
            // USA
            WorldCityEntity(5128581, "New York", "United States", "US", "New York", 40.7128, -74.0060, 8336817),
            WorldCityEntity(5368361, "Los Angeles", "United States", "US", "California", 34.0522, -118.2437, 3979576),
            WorldCityEntity(4887398, "Chicago", "United States", "US", "Illinois", 41.8781, -87.6298, 2693976),
            WorldCityEntity(4699066, "Houston", "United States", "US", "Texas", 29.7604, -95.3698, 2320268),
            WorldCityEntity(5308655, "Phoenix", "United States", "US", "Arizona", 33.4484, -112.0740, 1680992),
            WorldCityEntity(5391959, "San Francisco", "United States", "US", "California", 37.7749, -122.4194, 883305),
            WorldCityEntity(5809844, "Seattle", "United States", "US", "Washington", 47.6062, -122.3321, 744955),
            WorldCityEntity(4164138, "Miami", "United States", "US", "Florida", 25.7617, -80.1918, 467963),
            
            // UK
            WorldCityEntity(2643743, "London", "United Kingdom", "GB", "England", 51.5074, -0.1278, 8982000),
            WorldCityEntity(2643123, "Manchester", "United Kingdom", "GB", "England", 53.4808, -2.2426, 553230),
            WorldCityEntity(2644210, "Liverpool", "United Kingdom", "GB", "England", 53.4084, -2.9916, 498042),
            WorldCityEntity(2640194, "Oxford", "United Kingdom", "GB", "England", 51.7520, -1.2577, 154600),
            
            // Germany
            WorldCityEntity(2950159, "Berlin", "Germany", "DE", null, 52.5200, 13.4050, 3644826),
            WorldCityEntity(2867714, "Munich", "Germany", "DE", "Bavaria", 48.1351, 11.5820, 1471508),
            WorldCityEntity(2911298, "Hamburg", "Germany", "DE", null, 53.5511, 9.9937, 1841179),
            WorldCityEntity(2925533, "Frankfurt", "Germany", "DE", "Hesse", 50.1109, 8.6821, 753056),
            
            // France
            WorldCityEntity(2988507, "Paris", "France", "FR", "Île-de-France", 48.8566, 2.3522, 2161000),
            WorldCityEntity(2995469, "Marseille", "France", "FR", null, 43.2965, 5.3698, 861635),
            WorldCityEntity(2996944, "Lyon", "France", "FR", null, 45.7640, 4.8357, 513275),
            WorldCityEntity(3031582, "Bordeaux", "France", "FR", null, 44.8378, -0.5792, 254436),
            
            // Italy
            WorldCityEntity(3169070, "Rome", "Italy", "IT", "Lazio", 41.9028, 12.4964, 2872800),
            WorldCityEntity(3173435, "Milan", "Italy", "IT", "Lombardy", 45.4642, 9.1900, 1352000),
            WorldCityEntity(3176959, "Florence", "Italy", "IT", "Tuscany", 43.7696, 11.2558, 383084),
            WorldCityEntity(3164527, "Venice", "Italy", "IT", "Veneto", 45.4408, 12.3155, 261905),
            
            // Spain
            WorldCityEntity(3117735, "Madrid", "Spain", "ES", null, 40.4168, -3.7038, 3223334),
            WorldCityEntity(3128760, "Barcelona", "Spain", "ES", "Catalonia", 41.3851, 2.1734, 1620343),
            WorldCityEntity(2510911, "Seville", "Spain", "ES", "Andalusia", 37.3891, -5.9845, 688711),
            WorldCityEntity(2509954, "Valencia", "Spain", "ES", null, 39.4699, -0.3763, 791413),
            
            // Russia
            WorldCityEntity(524901, "Moscow", "Russia", "RU", null, 55.7558, 37.6173, 12537954),
            WorldCityEntity(498817, "Saint Petersburg", "Russia", "RU", null, 59.9343, 30.3351, 5383890),
            WorldCityEntity(1496747, "Novosibirsk", "Russia", "RU", null, 55.0084, 82.9357, 1612833),
            
            // Turkey
            WorldCityEntity(745044, "Istanbul", "Turkey", "TR", null, 41.0082, 28.9784, 15462452),
            WorldCityEntity(323786, "Ankara", "Turkey", "TR", null, 39.9334, 32.8597, 5445026),
            WorldCityEntity(311046, "Izmir", "Turkey", "TR", null, 38.4192, 27.1287, 2937000),
            
            // China
            WorldCityEntity(1816670, "Beijing", "China", "CN", null, 39.9042, 116.4074, 21540000),
            WorldCityEntity(1796236, "Shanghai", "China", "CN", null, 31.2304, 121.4737, 24280000),
            WorldCityEntity(1795565, "Shenzhen", "China", "CN", "Guangdong", 22.5431, 114.0579, 12528300),
            WorldCityEntity(1809858, "Guangzhou", "China", "CN", "Guangdong", 23.1291, 113.2644, 14904400),
            
            // Japan
            WorldCityEntity(1850147, "Tokyo", "Japan", "JP", null, 35.6762, 139.6503, 13960000),
            WorldCityEntity(1853909, "Osaka", "Japan", "JP", null, 34.6937, 135.5023, 2752000),
            WorldCityEntity(1856057, "Nagoya", "Japan", "JP", null, 35.1815, 136.9066, 2296000),
            WorldCityEntity(1863967, "Fukuoka", "Japan", "JP", null, 33.5902, 130.4017, 1603000),
            
            // India
            WorldCityEntity(1275339, "Mumbai", "India", "IN", "Maharashtra", 19.0760, 72.8777, 20411274),
            WorldCityEntity(1261481, "New Delhi", "India", "IN", null, 28.6139, 77.2090, 16787941),
            WorldCityEntity(1277333, "Bangalore", "India", "IN", "Karnataka", 12.9716, 77.5946, 11440000),
            WorldCityEntity(1275004, "Chennai", "India", "IN", "Tamil Nadu", 13.0827, 80.2707, 10971108),
            
            // Australia
            WorldCityEntity(2147714, "Sydney", "Australia", "AU", "New South Wales", -33.8688, 151.2093, 5312000),
            WorldCityEntity(2158177, "Melbourne", "Australia", "AU", "Victoria", -37.8136, 144.9631, 5078000),
            WorldCityEntity(2063523, "Perth", "Australia", "AU", "Western Australia", -31.9505, 115.8605, 2085973),
            WorldCityEntity(2174003, "Brisbane", "Australia", "AU", "Queensland", -27.4698, 153.0251, 2514184),
            
            // Canada
            WorldCityEntity(6167865, "Toronto", "Canada", "CA", "Ontario", 43.6532, -79.3832, 2930000),
            WorldCityEntity(6077243, "Montreal", "Canada", "CA", "Quebec", 45.5017, -73.5673, 1780000),
            WorldCityEntity(5946768, "Vancouver", "Canada", "CA", "British Columbia", 49.2827, -123.1207, 675218),
            WorldCityEntity(5913490, "Calgary", "Canada", "CA", "Alberta", 51.0447, -114.0719, 1336000),
            
            // Brazil
            WorldCityEntity(3448439, "São Paulo", "Brazil", "BR", null, -23.5505, -46.6333, 12325232),
            WorldCityEntity(3451190, "Rio de Janeiro", "Brazil", "BR", null, -22.9068, -43.1729, 6748000),
            WorldCityEntity(3463030, "Brasília", "Brazil", "BR", null, -15.7942, -47.8822, 3055149),
            
            // UAE
            WorldCityEntity(292223, "Dubai", "United Arab Emirates", "AE", null, 25.2048, 55.2708, 3137000),
            WorldCityEntity(292968, "Abu Dhabi", "United Arab Emirates", "AE", null, 24.4539, 54.3773, 1483000),
            
            // South Korea
            WorldCityEntity(1835848, "Seoul", "South Korea", "KR", null, 37.5665, 126.9780, 9776000),
            WorldCityEntity(1838524, "Busan", "South Korea", "KR", null, 35.1796, 129.0756, 3429000),
            
            // Singapore
            WorldCityEntity(1880252, "Singapore", "Singapore", "SG", null, 1.3521, 103.8198, 5850342),
            
            // South Africa
            WorldCityEntity(3369157, "Cape Town", "South Africa", "ZA", "Western Cape", -33.9249, 18.4241, 4618000),
            WorldCityEntity(993800, "Johannesburg", "South Africa", "ZA", "Gauteng", -26.2041, 28.0473, 5635127),
            
            // Egypt
            WorldCityEntity(360630, "Cairo", "Egypt", "EG", null, 30.0444, 31.2357, 9539673),
            WorldCityEntity(361058, "Alexandria", "Egypt", "EG", null, 31.2001, 29.9187, 5200000),
            
            // Mexico
            WorldCityEntity(3530597, "Mexico City", "Mexico", "MX", null, 19.4326, -99.1332, 21581000),
            WorldCityEntity(4005539, "Guadalajara", "Mexico", "MX", "Jalisco", 20.6597, -103.3496, 1495182),
            
            // Argentina
            WorldCityEntity(3435910, "Buenos Aires", "Argentina", "AR", null, -34.6037, -58.3816, 15180000),
            WorldCityEntity(3860259, "Córdoba", "Argentina", "AR", null, -31.4201, -64.1888, 1391000)
        )
    }
}
