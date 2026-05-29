package com.domaners.everyminute.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.domaners.everyminute.ui.navigation.Destination
import com.domaners.everyminute.ui.screens.CreateTeamScreen
import com.domaners.everyminute.ui.screens.DashboardScreen
import com.domaners.everyminute.ui.screens.FixturesScreen
import com.domaners.everyminute.ui.screens.LoginScreen
import com.domaners.everyminute.ui.screens.RegisterScreen
import com.domaners.everyminute.ui.screens.TeamScreen

@Composable
fun EveryMinuteApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val startDestination: Destination = if (currentUser == null) Destination.Login else Destination.Dashboard
    
    val backStack = rememberNavBackStack(startDestination)

    // Handle Auth changes to update backstack
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            if (backStack.lastOrNull() != Destination.Login && backStack.lastOrNull() != Destination.Register) {
                backStack.clear()
                backStack.add(Destination.Login)
            }
        } else if (backStack.lastOrNull() == Destination.Login || backStack.lastOrNull() == Destination.Register) {
            backStack.clear()
            backStack.add(Destination.Dashboard)
        }
    }

    val currentDestination = backStack.lastOrNull()

    Scaffold(
        bottomBar = {
            if (currentUser != null) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination == Destination.Dashboard,
                        onClick = {
                            if (currentDestination != Destination.Dashboard) {
                                backStack.clear()
                                backStack.add(Destination.Dashboard)
                            }
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == Destination.Team,
                        onClick = {
                            if (currentDestination != Destination.Team) {
                                backStack.clear()
                                backStack.add(Destination.Team)
                            }
                        },
                        icon = { Icon(Icons.Default.Groups, contentDescription = "Team") },
                        label = { Text("Team") }
                    )
                    NavigationBarItem(
                        selected = currentDestination == Destination.Fixtures,
                        onClick = {
                            if (currentDestination != Destination.Fixtures) {
                                backStack.clear()
                                backStack.add(Destination.Fixtures)
                            }
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = "Fixtures") },
                        label = { Text("Fixtures") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(innerPadding),
            backStack = backStack,
            onBack = { 
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.size - 1)
                }
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is Destination.Login -> NavEntry(key) {
                        LoginScreen(
                            onRegisterClick = {
                                backStack.add(Destination.Register)
                            },
                            onLoginSuccess = { }
                        )
                    }
                    is Destination.Register -> NavEntry(key) {
                        RegisterScreen(
                            onLoginClick = {
                                backStack.removeAt(backStack.size - 1)
                            },
                            onRegisterSuccess = { }
                        )
                    }
                    is Destination.Dashboard -> NavEntry(key) {
                        DashboardScreen(
                            onCreateTeamClick = {
                                backStack.add(Destination.CreateTeam)
                            }
                        )
                    }
                    is Destination.CreateTeam -> NavEntry(key) {
                        CreateTeamScreen(
                            onTeamCreated = {
                                backStack.removeAt(backStack.size - 1)
                            }
                        )
                    }
                    is Destination.Team -> NavEntry(key) {
                        TeamScreen()
                    }
                    is Destination.Fixtures -> NavEntry(key) {
                        FixturesScreen()
                    }
                    else -> NavEntry(key) { }
                }
            }
        )
    }
}
