package com.cajsa.moodleaf.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodScore: Int,
    val note: String,
    val createdAt: Instant,
    val imagePath: String? = null,
    val weatherCode: Int? = null,
    val tempC: Double? = null
)
