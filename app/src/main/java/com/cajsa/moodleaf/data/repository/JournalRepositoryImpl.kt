package com.cajsa.moodleaf.data.repository

import com.cajsa.moodleaf.data.local.MoodEntryDao
import com.cajsa.moodleaf.data.local.MoodEntryEntity
import com.cajsa.moodleaf.model.JournalEntry
import com.cajsa.moodleaf.model.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val dao: MoodEntryDao
) : JournalRepository {

    override fun observeEntries(): Flow<List<JournalEntry>> =
        dao.observeEntries().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getEntry(id: Long): JournalEntry? =
        dao.getEntry(id)?.toDomain()

    override suspend fun saveEntry(entry: JournalEntry): Long =
        dao.upsert(entry.toEntity())

    override suspend fun deleteEntry(entry: JournalEntry) {
        dao.delete(entry.toEntity())
    }

    private fun MoodEntryEntity.toDomain() = JournalEntry(
        id = id,
        mood = Mood.fromScore(moodScore),
        note = note,
        createdAt = createdAt,
        imagePath = imagePath,
        weatherCode = weatherCode,
        tempC = tempC
    )

    private fun JournalEntry.toEntity() = MoodEntryEntity(
        id = id,
        moodScore = mood.score,
        note = note,
        createdAt = createdAt,
        imagePath = imagePath,
        weatherCode = weatherCode,
        tempC = tempC
    )
}
