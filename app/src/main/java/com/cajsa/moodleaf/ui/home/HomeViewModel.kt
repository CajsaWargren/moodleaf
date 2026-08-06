package com.cajsa.moodleaf.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cajsa.moodleaf.data.repository.JournalRepository
import com.cajsa.moodleaf.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true
) {
    val isEmpty: Boolean get() = !isLoading && entries.isEmpty()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: JournalRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.observeEntries()
        .map { entries -> HomeUiState(entries = entries, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}
