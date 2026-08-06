package com.cajsa.moodleaf.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC")
    fun observeEntries(): Flow<List<MoodEntryEntity>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getEntry(id: Long): MoodEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: MoodEntryEntity): Long

    @Update
    suspend fun update(entry: MoodEntryEntity)

    @Delete
    suspend fun delete(entry: MoodEntryEntity)
}
