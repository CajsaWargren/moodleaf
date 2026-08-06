package com.cajsa.moodleaf.ui.editor

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Drag, rotate, and pinch-to-scale a page element in one gesture. [onBringToFront] fires
 * on every touch so the element being manipulated always renders above its siblings.
 * [onTransform]'s `zoom` is a multiplicative factor (apply as `scale *= zoom`, not `+=`) —
 * that's what [detectTransformGestures] reports.
 */
fun Modifier.pageElementGestures(
    onBringToFront: () -> Unit,
    onTransform: (pan: Offset, rotationDelta: Float, zoom: Float) -> Unit
): Modifier = this.pointerInput(Unit) {
    detectTransformGestures { _, pan, zoom, rotation ->
        onBringToFront()
        onTransform(pan, rotation, zoom)
    }
}
