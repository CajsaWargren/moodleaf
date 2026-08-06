package com.cajsa.moodleaf.model

data class WeatherReading(
    val weatherCode: Int,
    val tempC: Double
)

enum class WeatherCategory(val emoji: String, val label: String) {
    CLEAR("☀️", "Clear"),
    CLOUDY("⛅", "Cloudy"),
    FOG("🌫️", "Foggy"),
    RAIN("🌧️", "Rainy"),
    SNOW("❄️", "Snowy"),
    STORM("⛈️", "Stormy"),
    UNKNOWN("🌈", "Unknown");

    companion object {
        fun fromWmoCode(code: Int): WeatherCategory = when (code) {
            0 -> CLEAR
            1, 2, 3 -> CLOUDY
            45, 48 -> FOG
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            95, 96, 99 -> STORM
            else -> UNKNOWN
        }
    }
}
