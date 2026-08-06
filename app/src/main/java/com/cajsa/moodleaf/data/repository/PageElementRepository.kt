package com.cajsa.moodleaf.data.repository

import com.cajsa.moodleaf.model.PageElement
import kotlinx.coroutines.flow.Flow

interface PageElementRepository {
    fun observeElements(entryId: Long): Flow<List<PageElement>>
    suspend fun replaceAll(entryId: Long, elements: List<PageElement>)
}
