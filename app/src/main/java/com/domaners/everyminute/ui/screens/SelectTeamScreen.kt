package com.domaners.everyminute.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domaners.everyminute.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTeamScreen(
    viewModel: MainViewModel = viewModel(),
    onCreateTeamClick: () -> Unit,
    onTeamSelected: () -> Unit
) {
    val teams by viewModel.teams.collectAsState()
    val currentTeam by viewModel.currentTeam.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Team") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTeamClick) {
                Icon(Icons.Default.Add, contentDescription = "Create Team")
            }
        }
    ) { padding ->
        if (teams.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No teams found")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onCreateTeamClick) {
                        Text("Create a Team")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(teams) { team ->
                    ListItem(
                        headlineContent = { Text(team.name) },
                        leadingContent = { Icon(Icons.Default.Groups, contentDescription = null) },
                        trailingContent = {
                            if (team.id == currentTeam?.id) {
                                RadioButton(selected = true, onClick = null)
                            }
                        },
                        modifier = Modifier.clickable {
                            viewModel.selectTeam(team.id)
                            onTeamSelected()
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
