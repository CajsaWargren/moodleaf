package com.cajsa.moodleaf.ui.home

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

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state is empty when repository has no entries`() = runTest {
        val viewModel = HomeViewModel(FakeJournalRepository())

        assertTrue(viewModel.uiState.value.isEmpty)
    }

    @Test
    fun `uiState reflects entries emitted by the repository`() = runTest {
        val repository = FakeJournalRepository()
        repository.saveEntry(JournalEntry(mood = Mood.GOOD, note = "Nice walk", createdAt = Instant.now()))
        val viewModel = HomeViewModel(repository)

        assertEquals(1, viewModel.uiState.value.entries.size)
        assertEquals(Mood.GOOD, viewModel.uiState.value.entries.first().mood)
    }

    @Test
    fun `deleteEntry removes the entry from the repository`() = runTest {
        val repository = FakeJournalRepository()
        val id = repository.saveEntry(JournalEntry(mood = Mood.LOW, note = "", createdAt = Instant.now()))
        val viewModel = HomeViewModel(repository)
        val entry = viewModel.uiState.value.entries.first()

        viewModel.deleteEntry(entry)

        assertTrue(repository.entries().value.none { it.id == id })
    }
}
