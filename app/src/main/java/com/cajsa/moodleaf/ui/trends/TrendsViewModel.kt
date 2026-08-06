package com.cajsa.moodleaf.ui.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajsa.moodleaf.data.repository.JournalRepository
import com.cajsa.moodleaf.model.WeatherCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.ZoneId
import javax.inject.Inject

data class TrendsUiState(
    val scores: List<Int> = emptyList(),
    val averageScore: Double = 0.0,
    val hasEnoughData: Boolean = false,
    val weekdayAverages: List<Pair<String, Double>> = emptyList(),
    val weatherAverages: List<Pair<String, Double>> = emptyList()
)

@HiltViewModel
class TrendsViewModel @Inject constructor(
    repository: JournalRepository
) : ViewModel() {

    val uiState: StateFlow<TrendsUiState> = repository.observeEntries()
        .map { entries ->
            val chronological = entries.sortedBy { it.createdAt }.takeLast(30)
            val scores = chronological.map { it.mood.score }

            val zone = ZoneId.systemDefault()
            val weekdayAverages = entries
                .groupBy { it.createdAt.atZone(zone).dayOfWeek }
                .toSortedMap()
                .map { (day, es) -> day.name.take(3) to es.map { it.mood.score }.average() }

            val weatherAverages = entries
                .filter { it.weatherCode != null }
                .groupBy { WeatherCategory.fromWmoCode(it.weatherCode!!) }
                .map { (category, es) -> category.emoji to es.map { it.mood.score }.average() }

            TrendsUiState(
                scores = scores,
                averageScore = if (scores.isEmpty()) 0.0 else scores.average(),
                hasEnoughData = scores.size >= 2,
                weekdayAverages = weekdayAverages,
                weatherAverages = weatherAverages
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrendsUiState())
}
