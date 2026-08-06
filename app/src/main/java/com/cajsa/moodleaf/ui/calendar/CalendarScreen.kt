package com.cajsa.moodleaf.ui.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cajsa.moodleaf.model.JournalEntry
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onDaySelected: (date: LocalDate, existingEntry: JournalEntry?) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Calendar") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            WallCalendarCard(
                state = state,
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                onDayClick = { date -> onDaySelected(date, state.entriesByDate[date]) }
            )
        }
    }
}

@Composable
private fun WallCalendarCard(
    state: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                MonthHeader(state.month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + state.month.year, onPreviousMonth, onNextMonth)

                WeekdayHeader(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

                val weeks = remember(state.month) { buildWeeks(state.month) }
                weeks.forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            DayCell(
                                date = date,
                                entry = date?.let { state.entriesByDate[it] },
                                onClick = { date?.let(onDayClick) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Two small "binder ring" notches to sell the hung-wall-calendar feel.
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(88.dp)
        ) {
            val surfaceColor = MaterialTheme.colorScheme.surface
            val outlineColor = MaterialTheme.colorScheme.outline
            repeat(2) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.18f),
                        radius = size.minDimension / 2f,
                        center = center + Offset(0f, 1.dp.toPx())
                    )
                    drawCircle(color = surfaceColor, radius = size.minDimension / 2.4f)
                    drawCircle(
                        color = outlineColor,
                        radius = size.minDimension / 2.4f,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(title: String, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun WeekdayHeader(modifier: Modifier = Modifier) {
    val labels = listOf("S", "M", "T", "W", "T", "F", "S")
    Row(modifier = modifier.fillMaxWidth()) {
        labels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    entry: JournalEntry?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = date != null && date == LocalDate.now()

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(CircleShape)
            .then(
                if (isToday) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)) else Modifier
            )
            .clickable(
                enabled = date != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(6.dp)
                        .background(
                            color = entry?.mood?.color ?: Color.Transparent,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

private fun buildWeeks(month: YearMonth): List<List<LocalDate?>> {
    val firstOfMonth = month.atDay(1)
    val leadingBlanks = firstOfMonth.dayOfWeek.value % 7 // Sunday-first grid
    val daysInMonth = month.lengthOfMonth()

    val cells = mutableListOf<LocalDate?>()
    repeat(leadingBlanks) { cells.add(null) }
    for (day in 1..daysInMonth) cells.add(month.atDay(day))
    while (cells.size % 7 != 0) cells.add(null)

    return cells.chunked(7)
}
