package com.cajsa.moodleaf.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS page_elements (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                entryId INTEGER NOT NULL,
                type TEXT NOT NULL,
                content TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                rotationDegrees REAL NOT NULL,
                scale REAL NOT NULL,
                zIndex INTEGER NOT NULL,
                FOREIGN KEY(entryId) REFERENCES mood_entries(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_page_elements_entryId ON page_elements(entryId)")

        // Backfill existing entries so old journal pages open correctly in the new canvas.
        db.query("SELECT id, note, imagePath FROM mood_entries").use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow("id")
            val noteIndex = cursor.getColumnIndexOrThrow("note")
            val imagePathIndex = cursor.getColumnIndexOrThrow("imagePath")
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val note = cursor.getString(noteIndex)
                val imagePath = cursor.getString(imagePathIndex)
                if (!imagePath.isNullOrBlank()) {
                    db.execSQL(
                        "INSERT INTO page_elements (entryId, type, content, x, y, rotationDegrees, scale, zIndex) VALUES (?, 'PHOTO', ?, 0.5, 0.35, 0, 1.0, 0)",
                        arrayOf(id, imagePath)
                    )
                }
                if (!note.isNullOrBlank()) {
                    db.execSQL(
                        "INSERT INTO page_elements (entryId, type, content, x, y, rotationDegrees, scale, zIndex) VALUES (?, 'NOTE', ?, 0.5, 0.75, 0, 1.0, 1)",
                        arrayOf(id, note)
                    )
                }
            }
        }
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE page_elements ADD COLUMN colorHex TEXT")
        db.execSQL("ALTER TABLE page_elements ADD COLUMN shape TEXT NOT NULL DEFAULT 'ROUNDED'")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE mood_entries ADD COLUMN weatherCode INTEGER")
        db.execSQL("ALTER TABLE mood_entries ADD COLUMN tempC REAL")
    }
}
