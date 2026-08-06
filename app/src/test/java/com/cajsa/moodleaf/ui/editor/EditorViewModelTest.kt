package com.cajsa.moodleaf.ui.editor

import androidx.lifecycle.SavedStateHandle
import com.cajsa.moodleaf.MainDispatcherRule
import com.cajsa.moodleaf.fake.FakeJournalRepository
import com.cajsa.moodleaf.model.JournalEntry
import com.cajsa.moodleaf.model.Mood
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class EditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `new entry defaults to Okay mood and empty note`() = runTest {
        val viewModel = EditorViewModel(FakeJournalRepository(), SavedStateHandle(mapOf("entryId" to -1L)))

        assertEquals(Mood.OKAY, viewModel.uiState.value.mood)
        assertEquals("", viewModel.uiState.value.note)
    }

    @Test
    fun `loads existing entry into state when entryId is provided`() = runTest {
        val repository = FakeJournalRepository()
        val id = repository.saveEntry(JournalEntry(mood = Mood.GREAT, note = "Best day", createdAt = Instant.now()))
        val viewModel = EditorViewModel(repository, SavedStateHandle(mapOf("entryId" to id)))

        assertEquals(Mood.GREAT, viewModel.uiState.value.mood)
        assertEquals("Best day", viewModel.uiState.value.note)
    }

    @Test
    fun `save persists the selected mood and note to the repository`() = runTest {
        val repository = FakeJournalRepository()
        val viewModel = EditorViewModel(repository, SavedStateHandle(mapOf("entryId" to -1L)))

        viewModel.onMoodSelected(Mood.AWFUL)
        viewModel.onNoteChanged("Rough one")
        viewModel.save()

        val saved = repository.entries().value.single()
        assertEquals(Mood.AWFUL, saved.mood)
        assertEquals("Rough one", saved.note)
        assertTrue(viewModel.uiState.value.isSaved)
    }
}
