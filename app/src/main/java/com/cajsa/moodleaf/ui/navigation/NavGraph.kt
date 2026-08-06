package com.cajsa.moodleaf.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.cajsa.moodleaf.ui.calendar.CalendarScreen
import com.cajsa.moodleaf.ui.editor.EditorScreen
import com.cajsa.moodleaf.ui.home.HomeScreen
import com.cajsa.moodleaf.ui.settings.SettingsScreen
import com.cajsa.moodleaf.ui.trends.TrendsScreen

private sealed class TopLevelDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : TopLevelDestination("home", "Journal", Icons.Filled.Home)
    data object Calendar : TopLevelDestination("calendar", "Calendar", Icons.Filled.CalendarMonth)
    data object Trends : TopLevelDestination("trends", "Trends", Icons.AutoMirrored.Filled.ShowChart)
    data object Settings : TopLevelDestination("settings", "Settings", Icons.Filled.Settings)
}

private val topLevelDestinations = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.Calendar,
    TopLevelDestination.Trends,
    TopLevelDestination.Settings
)

private const val EDITOR_ROUTE = "editor"
private const val ENTRY_ID_ARG = "entryId"
private const val DATE_ARG = "date"

@Composable
fun MoodleafNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(TopLevelDestination.Home.route) {
                HomeScreen(
                    onAddEntry = { navController.navigate("$EDITOR_ROUTE?$ENTRY_ID_ARG=-1") },
                    onOpenEntry = { id -> navController.navigate("$EDITOR_ROUTE?$ENTRY_ID_ARG=$id") }
                )
            }
            composable(TopLevelDestination.Calendar.route) {
                CalendarScreen(
                    onDaySelected = { date, existingEntry ->
                        if (existingEntry != null) {
                            navController.navigate("$EDITOR_ROUTE?$ENTRY_ID_ARG=${existingEntry.id}")
                        } else {
                            navController.navigate("$EDITOR_ROUTE?$ENTRY_ID_ARG=-1&$DATE_ARG=${date.toEpochDay()}")
                        }
                    }
                )
            }
            composable(TopLevelDestination.Trends.route) {
                TrendsScreen()
            }
            composable(TopLevelDestination.Settings.route) {
                SettingsScreen()
            }
            composable(
                route = "$EDITOR_ROUTE?$ENTRY_ID_ARG={$ENTRY_ID_ARG}&$DATE_ARG={$DATE_ARG}",
                arguments = listOf(
                    navArgument(ENTRY_ID_ARG) {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                    navArgument(DATE_ARG) {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                EditorScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
