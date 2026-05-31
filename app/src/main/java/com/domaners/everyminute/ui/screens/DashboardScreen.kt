package com.domaners.everyminute.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domaners.everyminute.ui.MainViewModel
import com.domaners.everyminute.ui.theme.EveryMinuteTheme

@Composable
fun DashboardScreen(
    viewModel: MainViewModel = viewModel(),
    onCreateTeamClick: () -> Unit = {},
    onLineupClick: () -> Unit = {}
) {
    val currentTeam by viewModel.currentTeam.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (currentTeam != null) {
            // Team Management UI
            Text(
                text = currentTeam?.name ?: "",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Navigate to Team details */ }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Team Management", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Manage your players, coaches, and parents.")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Navigate to Fixtures */ }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Fixtures", style = MaterialTheme.typography.titleLarge)
                    Text(text = "View upcoming games and results.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLineupClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Preferred Lineup")
            }
        } else {
            // No Team UI
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Welcome to EveryMinute!",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "You haven't set up a team yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onCreateTeamClick) {
                Text("Set Up Your Team")
            }
            Spacer(modifier = Modifier.weight(1.2f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    EveryMinuteTheme {
        DashboardScreen()
    }
}
