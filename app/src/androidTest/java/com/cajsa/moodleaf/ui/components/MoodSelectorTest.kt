package com.cajsa.moodleaf.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.cajsa.moodleaf.model.Mood
import org.junit.Rule
import org.junit.Test

class MoodSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickingAMoodOption_invokesOnSelectWithThatMood() {
        var selected: Mood? = null

        composeTestRule.setContent {
            MoodSelector(selected = Mood.OKAY, onSelect = { selected = it })
        }

        composeTestRule.onNodeWithContentDescription(Mood.GREAT.label).performClick()

        assert(selected == Mood.GREAT) { "Expected GREAT to be selected but was $selected" }
    }
}
