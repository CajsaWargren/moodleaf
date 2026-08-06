package com.cajsa.moodleaf.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cajsa.moodleaf.model.Mood

/**
 * A row of mood options that scale up and tint their background when selected.
 * The spring animations (rather than fixed-duration tweens) are what make the
 * selection feel tactile instead of mechanical.
 */
@Composable
fun MoodSelector(
    selected: Mood,
    onSelect: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Mood.entries.forEach { mood ->
                MoodOption(
                    mood = mood,
                    isSelected = mood == selected,
                    onClick = { onSelect(mood) }
                )
            }
        }
        Text(
            text = selected.label,
            style = MaterialTheme.typography.titleMedium,
            color = selected.color,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MoodOption(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val size by animateDpAsState(
        targetValue = if (isSelected) 56.dp else 44.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "moodSize"
    )
    val background by animateColorAsState(
        targetValue = if (isSelected) mood.color.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant,
        label = "moodBackground"
    )
    val tint by animateColorAsState(
        targetValue = if (isSelected) mood.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        label = "moodTint"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .background(background, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .semantics { contentDescription = mood.label },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = mood.icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}
