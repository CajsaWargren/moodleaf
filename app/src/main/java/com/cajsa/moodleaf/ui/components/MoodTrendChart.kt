package com.cajsa.moodleaf.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cajsa.moodleaf.model.Mood

/**
 * A hand-rolled line chart (no charting library) plotting mood score over time.
 * The line animates in on first composition by lerping a `progress` value and
 * only drawing the path up to that fraction — a common trick for "draw-on" reveals.
 */
@Composable
fun MoodTrendChart(
    scores: List<Int>,
    modifier: Modifier = Modifier
) {
    var progress by remember(scores) { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "chartProgress"
    )

    LaunchedEffect(scores) {
        progress = 1f
    }

    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    val lineColor = MaterialTheme.colorScheme.primary
    val moodColors = Mood.entries.associate { it.score to it.color }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        if (scores.size < 2) return@Canvas

        val leftPad = 12.dp.toPx()
        val topPad = 16.dp.toPx()
        val bottomPad = 16.dp.toPx()
        val chartWidth = size.width - leftPad * 2
        val chartHeight = size.height - topPad - bottomPad

        // Horizontal gridlines for each mood score (1..5)
        for (score in 1..5) {
            val y = topPad + chartHeight * (1 - (score - 1) / 4f)
            drawLine(
                color = gridColor,
                start = Offset(leftPad, y),
                end = Offset(size.width - leftPad, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val stepX = chartWidth / (scores.size - 1)
        val points = scores.mapIndexed { index, score ->
            Offset(
                x = leftPad + stepX * index,
                y = topPad + chartHeight * (1 - (score - 1) / 4f)
            )
        }

        val visibleCount = (points.size * animatedProgress).coerceIn(1f, points.size.toFloat())
        val visiblePoints = points.take(visibleCount.toInt().coerceAtLeast(1))

        if (visiblePoints.size >= 2) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(visiblePoints.first().x, visiblePoints.first().y)
                for (i in 1 until visiblePoints.size) {
                    val prev = visiblePoints[i - 1]
                    val curr = visiblePoints[i]
                    val midX = (prev.x + curr.x) / 2
                    cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                }
            }

            val fillPath = androidx.compose.ui.graphics.Path().apply {
                addPath(path)
                lineTo(visiblePoints.last().x, topPad + chartHeight)
                lineTo(visiblePoints.first().x, topPad + chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                    startY = topPad,
                    endY = topPad + chartHeight
                )
            )

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        visiblePoints.forEachIndexed { index, point ->
            val score = scores[index]
            drawCircle(
                color = moodColors[score] ?: lineColor,
                radius = 5.dp.toPx(),
                center = point
            )
        }
    }
}
