package com.cajsa.moodleaf.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajsa.moodleaf.data.repository.JournalRepository
import com.cajsa.moodleaf.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class CalendarUiState(
    val month: YearMonth = YearMonth.now(),
    val entriesByDate: Map<LocalDate, JournalEntry> = emptyMap()
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    repository: JournalRepository
) : ViewModel() {

    private val visibleMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<CalendarUiState> = combine(
        visibleMonth,
        repository.observeEntries()
    ) { month, entries ->
        // entries are already sorted newest-first, so the first entry seen
        // per date is the one shown on that day's cell.
        val entriesByDate = entries
            .groupBy { it.createdAt.atZone(ZoneId.systemDefault()).toLocalDate() }
            .mapValues { (_, dayEntries) -> dayEntries.first() }
        CalendarUiState(month = month, entriesByDate = entriesByDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    fun previousMonth() {
        visibleMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        visibleMonth.update { it.plusMonths(1) }
    }
}
