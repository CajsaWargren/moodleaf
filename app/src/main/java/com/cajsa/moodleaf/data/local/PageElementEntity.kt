package com.cajsa.moodleaf.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "page_elements",
    foreignKeys = [
        ForeignKey(
            entity = MoodEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("entryId")]
)
data class PageElementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryId: Long,
    val type: String,
    val content: String,
    val x: Float,
    val y: Float,
    val rotationDegrees: Float = 0f,
    val scale: Float = 1f,
    val zIndex: Int = 0,
    val colorHex: String? = null,
    val shape: String = "ROUNDED"
)
