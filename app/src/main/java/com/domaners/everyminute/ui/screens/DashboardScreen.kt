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
    onCreateTeamClick: () -> Unit = {}
) {
    val teams by viewModel.teams.collectAsState()
    val currentTeam by viewModel.currentTeam.collectAsState()

    LaunchedEffect(teams) {
        if (teams.isEmpty()) {
            onCreateTeamClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (currentTeam != null) {
            Text(
                text = currentTeam?.name ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Welcome to EveryMinute!")
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    EveryMinuteTheme {
        DashboardScreen()
    }
}
