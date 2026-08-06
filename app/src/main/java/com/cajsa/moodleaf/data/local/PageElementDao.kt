package com.cajsa.moodleaf.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PageElementDao {

    @Query("SELECT * FROM page_elements WHERE entryId = :entryId")
    fun observeElementsForEntry(entryId: Long): Flow<List<PageElementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(elements: List<PageElementEntity>)

    @Query("DELETE FROM page_elements WHERE entryId = :entryId")
    suspend fun deleteAllForEntry(entryId: Long)

    @Transaction
    suspend fun replaceAll(entryId: Long, elements: List<PageElementEntity>) {
        deleteAllForEntry(entryId)
        upsertAll(elements)
    }
}
