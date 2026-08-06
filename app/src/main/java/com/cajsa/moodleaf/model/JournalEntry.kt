package com.cajsa.moodleaf.model

import java.time.Instant

/**
 * Domain-level representation of a journal entry, independent of how it's persisted.
 * Keeping this separate from the Room entity means the UI and ViewModels never
 * depend on a database annotation.
 */
data class JournalEntry(
    val id: Long = 0,
    val mood: Mood,
    val note: String,
    val createdAt: Instant,
    val imagePath: String? = null,
    val weatherCode: Int? = null,
    val tempC: Double? = null
)
