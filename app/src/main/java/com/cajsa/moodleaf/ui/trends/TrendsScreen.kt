package com.cajsa.moodleaf.ui.trends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cajsa.moodleaf.model.Mood
import com.cajsa.moodleaf.ui.components.CategoryMoodChart
import com.cajsa.moodleaf.ui.components.MoodTrendChart
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendsScreen(viewModel: TrendsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Trends") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp)
        ) {
            if (state.hasEnoughData) {
                val averageMood = Mood.fromScore(state.averageScore.roundToInt())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = averageMood.icon,
                        contentDescription = null,
                        tint = averageMood.color,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Average mood: ${averageMood.label}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                MoodTrendChart(
                    scores = state.scores,
                    modifier = Modifier.padding(top = 24.dp)
                )

                if (state.weekdayAverages.isNotEmpty()) {
                    Text(
                        text = "Mood by day of week",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                    CategoryMoodChart(
                        categories = state.weekdayAverages,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                if (state.weatherAverages.isNotEmpty()) {
                    Text(
                        text = "Mood by weather",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                    CategoryMoodChart(
                        categories = state.weatherAverages,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else {
                Text(
                    text = "Log a few entries to see your mood trend over time.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
