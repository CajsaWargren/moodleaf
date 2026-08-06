package com.cajsa.moodleaf.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * The five moods a journal entry can be tagged with.
 * [score] backs the trend chart on the Trends screen (1 = lowest, 5 = highest).
 * Instead of emoji faces, each mood reads as a small botanical/weather motif —
 * a cloud settling into dew, grass, new growth, and finally a blossom.
 */
enum class Mood(val score: Int, val icon: ImageVector, val label: String, val color: Color) {
    AWFUL(1, Icons.Filled.Cloud, "Heavy", Color(0xFF8A81A0)),
    LOW(2, Icons.Filled.WaterDrop, "Low", Color(0xFFA793D1)),
    OKAY(3, Icons.Filled.Grass, "Steady", Color(0xFFACAAC4)),
    GOOD(4, Icons.Filled.Spa, "Growing", Color(0xFF86AD7C)),
    GREAT(5, Icons.Filled.LocalFlorist, "Blooming", Color(0xFF5B9350));

    companion object {
        fun fromScore(score: Int): Mood = entries.firstOrNull { it.score == score } ?: OKAY
    }
}
