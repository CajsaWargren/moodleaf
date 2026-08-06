package com.cajsa.moodleaf.fake

import com.cajsa.moodleaf.data.repository.JournalRepository
import com.cajsa.moodleaf.model.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeJournalRepository : JournalRepository {

    private val state = MutableStateFlow<List<JournalEntry>>(emptyList())
    private var nextId = 1L

    fun entries(): StateFlow<List<JournalEntry>> = state

    override fun observeEntries() = state

    override suspend fun getEntry(id: Long): JournalEntry? =
        state.value.firstOrNull { it.id == id }

    override suspend fun saveEntry(entry: JournalEntry): Long {
        val id = if (entry.id != 0L) entry.id else nextId++
        val saved = entry.copy(id = id)
        state.value = state.value.filterNot { it.id == id } + saved
        return id
    }

    override suspend fun deleteEntry(entry: JournalEntry) {
        state.value = state.value.filterNot { it.id == entry.id }
    }
}
