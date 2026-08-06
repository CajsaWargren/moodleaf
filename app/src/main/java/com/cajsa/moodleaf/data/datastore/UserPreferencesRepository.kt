package com.cajsa.moodleaf.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class UserPreferences(
    val useDarkTheme: Boolean,
    val useDynamicColor: Boolean,
    val weatherCity: String
)

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val DARK_THEME = booleanPreferencesKey("use_dark_theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val WEATHER_CITY = stringPreferencesKey("weather_city")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            useDarkTheme = prefs[Keys.DARK_THEME] ?: false,
            useDynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            weatherCity = prefs[Keys.WEATHER_CITY] ?: ""
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setWeatherCity(city: String) {
        dataStore.edit { it[Keys.WEATHER_CITY] = city }
    }
}
