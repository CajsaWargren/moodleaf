package com.cajsa.moodleaf.data.repository

import com.cajsa.moodleaf.data.remote.fetchCurrentWeather
import com.cajsa.moodleaf.data.remote.geocodeCity
import com.cajsa.moodleaf.model.WeatherReading
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor() : WeatherRepository {

    override suspend fun fetchWeatherForCity(city: String): WeatherReading? {
        if (city.isBlank()) return null
        val (lat, lon) = geocodeCity(city) ?: return null
        return fetchCurrentWeather(lat, lon)
    }
}
