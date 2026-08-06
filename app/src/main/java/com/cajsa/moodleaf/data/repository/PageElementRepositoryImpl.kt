package com.cajsa.moodleaf.data.repository

import com.cajsa.moodleaf.data.local.PageElementDao
import com.cajsa.moodleaf.data.local.PageElementEntity
import com.cajsa.moodleaf.model.ElementType
import com.cajsa.moodleaf.model.NoteShape
import com.cajsa.moodleaf.model.PageElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PageElementRepositoryImpl @Inject constructor(
    private val dao: PageElementDao
) : PageElementRepository {

    override fun observeElements(entryId: Long): Flow<List<PageElement>> =
        dao.observeElementsForEntry(entryId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun replaceAll(entryId: Long, elements: List<PageElement>) {
        // Always re-insert fresh: replaceAll deletes every existing row for this entry
        // first, so any id carried on the in-memory elements (including the negative
        // temp ids new, unsaved elements use) is stale and must be dropped.
        dao.replaceAll(entryId, elements.map { it.copy(id = 0, entryId = entryId).toEntity() })
    }

    private fun PageElementEntity.toDomain() = PageElement(
        id = id,
        entryId = entryId,
        type = ElementType.fromCode(type),
        content = content,
        x = x,
        y = y,
        rotationDegrees = rotationDegrees,
        scale = scale,
        zIndex = zIndex,
        colorHex = colorHex,
        shape = NoteShape.fromCode(shape)
    )

    private fun PageElement.toEntity() = PageElementEntity(
        id = id,
        entryId = entryId,
        type = type.code,
        content = content,
        x = x,
        y = y,
        rotationDegrees = rotationDegrees,
        scale = scale,
        zIndex = zIndex,
        colorHex = colorHex,
        shape = shape.code
    )
}
