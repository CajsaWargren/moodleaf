package com.cajsa.moodleaf.data.remote

import com.cajsa.moodleaf.model.WeatherReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private const val TIMEOUT_MILLIS = 8000

private fun readUrl(urlString: String): String {
    val connection = URL(urlString).openConnection() as HttpURLConnection
    connection.connectTimeout = TIMEOUT_MILLIS
    connection.readTimeout = TIMEOUT_MILLIS
    return try {
        connection.inputStream.bufferedReader().use { it.readText() }
    } finally {
        connection.disconnect()
    }
}

suspend fun geocodeCity(city: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    runCatching {
        val encoded = URLEncoder.encode(city, "UTF-8")
        val json = JSONObject(readUrl("https://geocoding-api.open-meteo.com/v1/search?count=1&name=$encoded"))
        val result = json.getJSONArray("results").getJSONObject(0)
        result.getDouble("latitude") to result.getDouble("longitude")
    }.getOrNull()
}

suspend fun fetchCurrentWeather(lat: Double, lon: Double): WeatherReading? = withContext(Dispatchers.IO) {
    runCatching {
        val json = JSONObject(
            readUrl("https://api.open-meteo.com/v1/forecast?current_weather=true&latitude=$lat&longitude=$lon")
        )
        val current = json.getJSONObject("current_weather")
        WeatherReading(
            weatherCode = current.getInt("weathercode"),
            tempC = current.getDouble("temperature")
        )
    }.getOrNull()
}
