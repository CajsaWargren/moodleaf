package com.cajsa.moodleaf.data.repository

import com.cajsa.moodleaf.model.WeatherReading

interface WeatherRepository {
    suspend fun fetchWeatherForCity(city: String): WeatherReading?
}
