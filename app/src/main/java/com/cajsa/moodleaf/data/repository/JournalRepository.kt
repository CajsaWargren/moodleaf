package com.cajsa.moodleaf.data.repository

import com.cajsa.moodleaf.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun observeEntries(): Flow<List<JournalEntry>>
    suspend fun getEntry(id: Long): JournalEntry?
    suspend fun saveEntry(entry: JournalEntry): Long
    suspend fun deleteEntry(entry: JournalEntry)
}
