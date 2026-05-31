package com.domaners.everyminute.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.domaners.everyminute.ui.navigation.Destination
import com.domaners.everyminute.ui.screens.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EveryMinuteApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val startDestination: Destination = if (currentUser == null) Destination.Login else Destination.Dashboard
    
    val backStack = rememberNavBackStack(startDestination)
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentUser != null,
        drawerContent = {
            if (currentUser != null) {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "EveryMinute",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Select Team") },
                        selected = currentDestination == Destination.SelectTeam,
                        onClick = {
                            scope.launch { drawerState.close() }
                            backStack.clear()
                            backStack.add(Destination.SelectTeam)
                        },
                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(Modifier.weight(1f))
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.signOut()
                        },
                        icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentUser != null) {
                    TopAppBar(
                        title = { Text("EveryMinute") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (currentUser != null && currentDestination != Destination.SelectTeam && currentDestination != Destination.CreateTeam) {
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
                                },
                                onLineupClick = {
                                    backStack.add(Destination.Lineup)
                                }
                            )
                        }
                        is Destination.SelectTeam -> NavEntry(key) {
                            SelectTeamScreen(
                                onCreateTeamClick = {
                                    backStack.add(Destination.CreateTeam)
                                },
                                onTeamSelected = {
                                    backStack.clear()
                                    backStack.add(Destination.Dashboard)
                                }
                            )
                        }
                        is Destination.Lineup -> NavEntry(key) {
                            LineupScreen(
                                onBack = {
                                    backStack.removeAt(backStack.size - 1)
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
                            FixturesScreen(
                                onAddFixture = {
                                    backStack.add(Destination.AddEditFixture())
                                },
                                onEditFixture = { fixtureId ->
                                    backStack.add(Destination.AddEditFixture(fixtureId))
                                }
                            )
                        }
                        is Destination.AddEditFixture -> NavEntry(key) {
                            AddEditFixtureScreen(
                                fixtureId = key.fixtureId,
                                onBack = {
                                    backStack.removeAt(backStack.size - 1)
                                }
                            )
                        }
                        else -> NavEntry(key) { }
                    }
                }
            )
        }
    }
}
