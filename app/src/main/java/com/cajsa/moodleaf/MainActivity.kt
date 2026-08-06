package com.cajsa.moodleaf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cajsa.moodleaf.data.datastore.UserPreferences
import com.cajsa.moodleaf.ui.navigation.MoodleafNavGraph
import com.cajsa.moodleaf.ui.settings.SettingsViewModel
import com.cajsa.moodleaf.ui.theme.MoodleafTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val preferences by settingsViewModel.preferences.collectAsState(
                initial = UserPreferences(useDarkTheme = false, useDynamicColor = true, weatherCity = "")
            )

            MoodleafTheme(
                darkTheme = preferences.useDarkTheme,
                dynamicColor = preferences.useDynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MoodleafNavGraph()
                }
            }
        }
    }
}
