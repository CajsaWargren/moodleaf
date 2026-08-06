package com.cajsa.moodleaf.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.cajsa.moodleaf.model.Mood
import kotlin.math.roundToInt

/**
 * A hand-rolled bar chart (same visual language as [MoodTrendChart]: same
 * canvas height/padding, same 1..5 gridlines, same draw-on reveal) showing
 * average mood score per category — used for both mood-by-weekday and
 * mood-by-weather in Trends.
 */
@Composable
fun CategoryMoodChart(
    categories: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    var progress by remember(categories) { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "barChartProgress"
    )

    LaunchedEffect(categories) {
        progress = 1f
    }

    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            if (categories.isEmpty()) return@Canvas

            val leftPad = 12.dp.toPx()
            val topPad = 16.dp.toPx()
            val bottomPad = 16.dp.toPx()
            val chartWidth = size.width - leftPad * 2
            val chartHeight = size.height - topPad - bottomPad
            val barBottom = topPad + chartHeight

            for (score in 1..5) {
                val y = topPad + chartHeight * (1 - (score - 1) / 4f)
                drawLine(
                    color = gridColor,
                    start = Offset(leftPad, y),
                    end = Offset(size.width - leftPad, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val slotWidth = chartWidth / categories.size
            val barWidth = slotWidth * 0.5f

            categories.forEachIndexed { index, (_, average) ->
                val barColor = Mood.fromScore(average.roundToInt()).color
                val fullBarTop = topPad + chartHeight * (1 - (average - 1) / 4f).toFloat()
                val animatedBarTop = barBottom - (barBottom - fullBarTop) * animatedProgress
                val barLeft = leftPad + slotWidth * index + (slotWidth - barWidth) / 2

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(barLeft, animatedBarTop),
                    size = Size(barWidth, barBottom - animatedBarTop),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            categories.forEach { (label, _) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
