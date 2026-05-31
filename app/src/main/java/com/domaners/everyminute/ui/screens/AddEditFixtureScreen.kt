package com.domaners.everyminute.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domaners.everyminute.data.model.Fixture
import com.domaners.everyminute.data.model.FixturePlayerStats
import com.domaners.everyminute.data.model.MatchResult
import com.domaners.everyminute.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFixtureScreen(
    viewModel: MainViewModel = viewModel(),
    fixtureId: String? = null,
    onBack: () -> Unit
) {
    val currentTeam by viewModel.currentTeam.collectAsState()
    val allPlayers by viewModel.teamPlayers.collectAsState()
    val fixtures by viewModel.teamFixtures.collectAsState()
    
    val existingFixture = remember(fixtureId, fixtures) {
        fixtures.find { it.id == fixtureId }
    }

    var opponent by remember { mutableStateOf(existingFixture?.opponent ?: "") }
    var venue by remember { mutableStateOf(existingFixture?.venue ?: "") }
    var type by remember { mutableStateOf(existingFixture?.type ?: "League") }
    var locationType by remember { mutableStateOf(existingFixture?.locationType ?: "Home") }
    var dateMillis by remember { mutableStateOf(existingFixture?.date ?: System.currentTimeMillis()) }
    
    var ourScore by remember { mutableStateOf(existingFixture?.result?.ourScore?.toString() ?: "") }
    var opponentScore by remember { mutableStateOf(existingFixture?.result?.opponentScore?.toString() ?: "") }
    
    var playerStatsList by remember { 
        mutableStateOf(existingFixture?.playerStats ?: emptyList<FixturePlayerStats>()) 
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    var showDatePicker by remember { mutableStateOf(false) }

    val types = listOf("League", "Cup", "Tournament", "Friendly", "Training")
    val locations = listOf("Home", "Away", "Neutral")
    val positions = listOf("Goalkeeper", "Defender", "Midfielder", "Forward")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (fixtureId == null) "Add Fixture" else "Edit Fixture") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    Button(onClick = {
                        val fixture = Fixture(
                            id = fixtureId ?: "",
                            teamId = currentTeam?.id ?: "",
                            opponent = opponent,
                            venue = venue,
                            type = type,
                            locationType = locationType,
                            date = datePickerState.selectedDateMillis ?: dateMillis,
                            result = if (ourScore.isNotBlank() && opponentScore.isNotBlank()) {
                                MatchResult(ourScore.toIntOrNull() ?: 0, opponentScore.toIntOrNull() ?: 0)
                            } else null,
                            playerStats = playerStatsList
                        )
                        if (fixtureId == null) viewModel.addFixture(fixture)
                        else viewModel.updateFixture(fixture)
                        onBack()
                    }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Opponent") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                DropdownField(label = "Type", options = types, selected = type, onSelect = { type = it })
            }
            item {
                DropdownField(label = "Home/Away", options = locations, selected = locationType, onSelect = { locationType = it })
            }
            item {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dateText = sdf.format(Date(datePickerState.selectedDateMillis ?: dateMillis))
                OutlinedTextField(
                    value = dateText,
                    onValueChange = {},
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Pick Date")
                        }
                    }
                )
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = ourScore,
                        onValueChange = { ourScore = it },
                        label = { Text("Our Score") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = opponentScore,
                        onValueChange = { opponentScore = it },
                        label = { Text("Opponent Score") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
            
            item {
                Text("Player Stats", style = MaterialTheme.typography.titleMedium)
            }
            
            items(playerStatsList) { stats ->
                val player = allPlayers.find { it.id == stats.playerId }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(player?.name ?: "Unknown Player", modifier = Modifier.weight(1f))
                            IconButton(onClick = { 
                                playerStatsList = playerStatsList.filter { it.playerId != stats.playerId } 
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = stats.goals.toString(),
                                onValueChange = { val g = it.toIntOrNull() ?: 0
                                    playerStatsList = playerStatsList.map { s -> if (s.playerId == stats.playerId) s.copy(goals = g) else s }
                                },
                                label = { Text("G") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = stats.assists.toString(),
                                onValueChange = { val a = it.toIntOrNull() ?: 0
                                    playerStatsList = playerStatsList.map { s -> if (s.playerId == stats.playerId) s.copy(assists = a) else s }
                                },
                                label = { Text("A") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = stats.minutesPlayed.toString(),
                                onValueChange = { val m = it.toIntOrNull() ?: 0
                                    playerStatsList = playerStatsList.map { s -> if (s.playerId == stats.playerId) s.copy(minutesPlayed = m) else s }
                                },
                                label = { Text("Min") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        DropdownField(label = "Position", options = positions, selected = stats.position, onSelect = { pos ->
                            playerStatsList = playerStatsList.map { s -> if (s.playerId == stats.playerId) s.copy(position = pos) else s }
                        })
                    }
                }
            }
            
            item {
                var showPlayerSelect by remember { mutableStateOf(false) }
                Button(onClick = { showPlayerSelect = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Add Player to Fixture")
                }
                
                if (showPlayerSelect) {
                    AlertDialog(
                        onDismissRequest = { showPlayerSelect = false },
                        title = { Text("Select Player") },
                        text = {
                            LazyColumn {
                                items(allPlayers.filter { p -> playerStatsList.none { it.playerId == p.id } }) { p ->
                                    ListItem(
                                        headlineContent = { Text(p.name) },
                                        modifier = Modifier.clickable {
                                            playerStatsList = playerStatsList + FixturePlayerStats(playerId = p.id)
                                            showPlayerSelect = false
                                        }
                                    )
                                }
                            }
                        },
                        confirmButton = { TextButton(onClick = { showPlayerSelect = false }) { Text("Cancel") } }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DropdownField(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Expand")
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
