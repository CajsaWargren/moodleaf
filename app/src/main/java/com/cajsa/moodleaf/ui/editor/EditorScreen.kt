package com.cajsa.moodleaf.ui.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.cajsa.moodleaf.model.ElementType
import com.cajsa.moodleaf.model.NoteShape
import com.cajsa.moodleaf.model.PageElement
import com.cajsa.moodleaf.model.WeatherCategory
import com.cajsa.moodleaf.ui.components.MoodSelector
import com.cajsa.moodleaf.ui.components.StickerPickerRow
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val StickyNoteBackground = Color(0xFFFCE9A8)
private val StickyNoteInk = Color(0xFF5C4A1E)

private val NoteColorPalette = listOf(
    Color(0xFFFCE9A8), // yellow (default)
    Color(0xFFF7C6D9), // pink
    Color(0xFFC7E0F4), // sky
    Color(0xFFD9E8C9), // sage
    Color(0xFFE1D4F2) // lavender
)

private fun Color.toHex(): String = String.format("#%06X", toArgb() and 0xFFFFFF)

private fun hexToColor(hex: String?, fallback: Color): Color =
    hex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() } ?: fallback

private fun NoteShape.toCornerShape(): RoundedCornerShape = when (this) {
    NoteShape.ROUNDED -> RoundedCornerShape(4.dp)
    NoteShape.SQUARE -> RoundedCornerShape(0.dp)
    NoteShape.PILL -> RoundedCornerShape(50)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onDone: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onDone()
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::addPhotoElement) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDateMillis by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.entryId == null) "New page" else "Edit page") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "How are you feeling?",
                style = MaterialTheme.typography.titleLarge
            )

            MoodSelector(
                selected = state.mood,
                onSelect = viewModel::onMoodSelected,
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateTimeRow(
                    dateTime = state.createdAt,
                    onClick = { showDatePicker = true }
                )
                WeatherChip(
                    weatherCode = state.weatherCode,
                    tempC = state.tempC,
                    onClick = viewModel::fetchWeather
                )
            }

            PageCanvas(
                elements = state.elements,
                onBringToFront = viewModel::bringToFront,
                onTransform = viewModel::transformElement,
                onTextChanged = viewModel::updateElementText,
                onRemove = viewModel::removeElement,
                onColorChanged = viewModel::updateElementColor,
                onShapeChanged = viewModel::updateElementShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            )

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Add photo")
                }
                IconButton(onClick = viewModel::addNoteElement) {
                    Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "Add sticky note")
                }
                IconButton(onClick = viewModel::addTextElement) {
                    Icon(Icons.Filled.TextFields, contentDescription = "Add text on the page")
                }
            }

            StickerPickerRow(
                onStickerSelected = viewModel::addStickerElement,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
            ) {
                Text("Add to journal")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.createdAt.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pendingDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val existingZoned = state.createdAt.atZone(ZoneId.systemDefault())
        val timePickerState = rememberTimePickerState(
            initialHour = existingZoned.hour,
            initialMinute = existingZoned.minute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val dateMillis = pendingDateMillis ?: state.createdAt.toEpochMilli()
                    val datePart = Instant.ofEpochMilli(dateMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    val newInstant = datePart.atTime(timePickerState.hour, timePickerState.minute)
                        .atZone(ZoneId.systemDefault()).toInstant()
                    viewModel.onDateTimeChanged(newInstant)
                    showTimePicker = false
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DateTimeRow(
    dateTime: Instant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEE, MMM d - h:mm a") }
    Row(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Event,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = dateTime.atZone(ZoneId.systemDefault()).format(formatter),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
private fun WeatherChip(
    weatherCode: Int?,
    tempC: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = if (weatherCode != null && tempC != null) {
        val category = WeatherCategory.fromWmoCode(weatherCode)
        "${category.emoji} ${tempC.roundToInt()}°C"
    } else {
        "Add weather"
    }
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}

@Composable
private fun PageCanvas(
    elements: List<PageElement>,
    onBringToFront: (Long) -> Unit,
    onTransform: (Long, Offset, Float, Float) -> Unit,
    onTextChanged: (Long, String) -> Unit,
    onRemove: (Long) -> Unit,
    onColorChanged: (Long, String) -> Unit,
    onShapeChanged: (Long, NoteShape) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                RoundedCornerShape(20.dp)
            )
    ) {
        val canvasWidthPx = constraints.maxWidth.toFloat()
        val canvasHeightPx = constraints.maxHeight.toFloat()

        elements.sortedBy { it.zIndex }.forEach { element ->
            key(element.id) {
                PageElementView(
                    element = element,
                    canvasWidthPx = canvasWidthPx,
                    canvasHeightPx = canvasHeightPx,
                    onBringToFront = { onBringToFront(element.id) },
                    onTransform = { pan, rotationDelta, zoom ->
                        onTransform(
                            element.id,
                            Offset(pan.x / canvasWidthPx, pan.y / canvasHeightPx),
                            rotationDelta,
                            zoom
                        )
                    },
                    onTextChanged = { text -> onTextChanged(element.id, text) },
                    onRemove = { onRemove(element.id) },
                    onColorChanged = { hex -> onColorChanged(element.id, hex) },
                    onShapeChanged = { shape -> onShapeChanged(element.id, shape) }
                )
            }
        }
    }
}

@Composable
private fun PageElementView(
    element: PageElement,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    onBringToFront: () -> Unit,
    onTransform: (Offset, Float, Float) -> Unit,
    onTextChanged: (String) -> Unit,
    onRemove: () -> Unit,
    onColorChanged: (String) -> Unit,
    onShapeChanged: (NoteShape) -> Unit
) {
    val positionedModifier = Modifier
        .offset {
            IntOffset(
                (element.x * canvasWidthPx).roundToInt(),
                (element.y * canvasHeightPx).roundToInt()
            )
        }
        .graphicsLayer {
            rotationZ = element.rotationDegrees
            scaleX = element.scale
            scaleY = element.scale
        }

    when (element.type) {
        ElementType.PHOTO -> Box(
            modifier = positionedModifier.pageElementGestures(onBringToFront, onTransform)
        ) {
            AsyncImage(
                model = File(element.content),
                contentDescription = "Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            RemoveButton(onRemove = onRemove, modifier = Modifier.align(Alignment.TopEnd))
        }

        ElementType.STICKER -> Box(
            modifier = positionedModifier.pageElementGestures(onBringToFront, onTransform)
        ) {
            Text(text = element.content, fontSize = 40.sp)
            RemoveButton(onRemove = onRemove, modifier = Modifier.align(Alignment.TopEnd))
        }

        ElementType.NOTE -> {
            var showCustomize by remember(element.id) { mutableStateOf(false) }
            Box(modifier = positionedModifier) {
                EditableTextBody(
                    text = element.content,
                    onTextChanged = onTextChanged,
                    onBringToFront = onBringToFront,
                    onTransform = onTransform,
                    onRemove = onRemove,
                    background = hexToColor(element.colorHex, StickyNoteBackground),
                    ink = StickyNoteInk,
                    shape = element.shape.toCornerShape(),
                    onCustomize = { showCustomize = true }
                )
            }
            if (showCustomize) {
                NoteCustomizeDialog(
                    onColorSelected = { color ->
                        onColorChanged(color.toHex())
                        showCustomize = false
                    },
                    onShapeSelected = { shape ->
                        onShapeChanged(shape)
                        showCustomize = false
                    },
                    onDismiss = { showCustomize = false }
                )
            }
        }

        ElementType.TEXT -> Box(modifier = positionedModifier) {
            EditableTextBody(
                text = element.content,
                onTextChanged = onTextChanged,
                onBringToFront = onBringToFront,
                onTransform = onTransform,
                onRemove = onRemove,
                background = null,
                ink = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(4.dp),
                onCustomize = null
            )
        }
    }
}

/**
 * Shared body for both sticky notes (colored card) and plain page text (no card,
 * writes directly on the paper) — same always-editable text field and drag handle,
 * just with or without a background. [onCustomize] is only non-null for sticky notes.
 */
@Composable
private fun EditableTextBody(
    text: String,
    onTextChanged: (String) -> Unit,
    onBringToFront: () -> Unit,
    onTransform: (Offset, Float, Float) -> Unit,
    onRemove: () -> Unit,
    background: Color?,
    ink: Color,
    shape: RoundedCornerShape,
    onCustomize: (() -> Unit)?
) {
    Box(
        modifier = Modifier
            .widthIn(min = 110.dp, max = 200.dp)
            .then(
                if (background != null) {
                    Modifier.background(background, shape).clip(shape).padding(10.dp)
                } else {
                    Modifier.padding(6.dp)
                }
            )
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            textStyle = TextStyle(color = ink, fontSize = 16.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 18.dp, bottom = 18.dp)
        )
        RemoveButton(onRemove = onRemove, modifier = Modifier.align(Alignment.TopEnd))
        if (onCustomize != null) {
            IconButton(
                onClick = onCustomize,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(22.dp)
                    .background(Color.Black.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    Icons.Filled.Palette,
                    contentDescription = "Customize note",
                    tint = ink,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.DragHandle,
            contentDescription = "Drag to move",
            tint = ink.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(20.dp)
                .pageElementGestures(onBringToFront, onTransform)
        )
    }
}

@Composable
private fun NoteCustomizeDialog(
    onColorSelected: (Color) -> Unit,
    onShapeSelected: (NoteShape) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize note") },
        text = {
            Column {
                Text("Color", style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                ) {
                    NoteColorPalette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, CircleShape)
                                .border(BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onColorSelected(color) }
                                )
                        )
                    }
                }
                Text("Shape", style = MaterialTheme.typography.labelLarge)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    NoteShape.entries.forEach { shape ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(StickyNoteBackground, shape.toCornerShape())
                                .border(
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                    shape.toCornerShape()
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { onShapeSelected(shape) }
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
private fun RemoveButton(onRemove: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onRemove,
        modifier = modifier
            .size(22.dp)
            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
    ) {
        Icon(
            Icons.Filled.Close,
            contentDescription = "Remove",
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}
