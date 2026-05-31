package com.domaners.everyminute.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domaners.everyminute.data.model.Fixture
import com.domaners.everyminute.ui.MainViewModel
import com.domaners.everyminute.ui.theme.EveryMinuteTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    viewModel: MainViewModel = viewModel()
) {
    val fixtures by viewModel.teamFixtures.collectAsState()
    val currentTeam by viewModel.currentTeam.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (currentTeam != null) {
                FloatingActionButton(onClick = { /* Navigate to add fixture */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Fixture")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                text = "Fixtures",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            if (fixtures.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No fixtures scheduled yet")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(fixtures) { fixture ->
                        FixtureItem(fixture)
                    }
                }
            }
        }
    }
}

@Composable
fun FixtureItem(fixture: Fixture) {
    val dateStr = remember(fixture.date) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(fixture.date))
    }

    ListItem(
        headlineContent = { Text("vs ${fixture.opponent}") },
        supportingContent = { Text("$dateStr @ ${fixture.venue}") },
        leadingContent = {
            Icon(Icons.Default.Event, contentDescription = null)
        },
        trailingContent = {
            fixture.result?.let {
                Text(
                    text = "${it.ourScore} - ${it.opponentScore}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (it.ourScore > it.opponentScore) Color.Green else if (it.ourScore < it.opponentScore) Color.Red else Color.Gray
                )
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Preview(showBackground = true)
@Composable
fun FixturesScreenPreview() {
    EveryMinuteTheme {
        FixturesScreen()
    }
}
