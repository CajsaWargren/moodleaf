package com.cajsa.moodleaf.ui.theme

import androidx.compose.ui.graphics.Color

// A sage-and-lavender "forest fairy" palette — soft, botanical, a little mystical.
val Mist10 = Color(0xFFF8F5FC)
val Mist20 = Color(0xFFEFE9F7)
val Lavender30 = Color(0xFFE1D4F2)
val Lavender50 = Color(0xFF8760C4)
val Lavender70 = Color(0xFF6E5A99)
val Sage30 = Color(0xFFE1EBDA)
val Sage50 = Color(0xFF5E9E4C)
val Fern70 = Color(0xFF4E7A46)
val Ink90 = Color(0xFF332B47)
val Ink10 = Color(0xFFF3EFFA)
val SageInk90 = Color(0xFF1F3318)

val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Lavender50,
    onPrimary = Mist10,
    primaryContainer = Lavender30,
    onPrimaryContainer = Ink90,
    secondary = Sage50,
    onSecondary = Mist10,
    secondaryContainer = Sage30,
    onSecondaryContainer = SageInk90,
    background = Mist10,
    onBackground = Ink90,
    surface = Mist10,
    onSurface = Ink90,
    surfaceVariant = Mist20,
    onSurfaceVariant = Lavender70,
    outline = Lavender70.copy(alpha = 0.6f)
)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Lavender30,
    onPrimary = Lavender70,
    primaryContainer = Lavender70,
    onPrimaryContainer = Lavender30,
    secondary = Sage50,
    onSecondary = Color(0xFF17240F),
    secondaryContainer = Fern70,
    onSecondaryContainer = Sage30,
    background = Color(0xFF201A2E),
    onBackground = Ink10,
    surface = Color(0xFF201A2E),
    onSurface = Ink10,
    surfaceVariant = Color(0xFF322A45),
    onSurfaceVariant = Lavender30,
    outline = Lavender30.copy(alpha = 0.5f)
)
