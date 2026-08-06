package com.cajsa.moodleaf.model

/**
 * A single freeform item on a journal entry's scrapbook canvas: a photo, a sticky
 * note, or a decorative sticker. Position is normalized (0f..1f) as a fraction of
 * the canvas size, so layouts stay consistent across different screen sizes.
 */
data class PageElement(
    val id: Long = 0,
    val entryId: Long,
    val type: ElementType,
    val content: String,
    val x: Float,
    val y: Float,
    val rotationDegrees: Float = 0f,
    val scale: Float = 1f,
    val zIndex: Int = 0,
    val colorHex: String? = null,
    val shape: NoteShape = NoteShape.ROUNDED
)

enum class ElementType(val code: String) {
    PHOTO("PHOTO"),
    NOTE("NOTE"),
    TEXT("TEXT"),
    STICKER("STICKER");

    companion object {
        fun fromCode(code: String): ElementType = entries.firstOrNull { it.code == code } ?: NOTE
    }
}

enum class NoteShape(val code: String) {
    ROUNDED("ROUNDED"),
    SQUARE("SQUARE"),
    PILL("PILL");

    companion object {
        fun fromCode(code: String): NoteShape = entries.firstOrNull { it.code == code } ?: ROUNDED
    }
}
