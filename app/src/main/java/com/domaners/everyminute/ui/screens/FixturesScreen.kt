package com.domaners.everyminute.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domaners.everyminute.data.model.Fixture
import com.domaners.everyminute.ui.MainViewModel
import com.domaners.everyminute.ui.theme.EveryMinuteTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    viewModel: MainViewModel = viewModel(),
    onAddFixture: () -> Unit,
    onEditFixture: (String) -> Unit
) {
    val fixtures by viewModel.teamFixtures.collectAsState()
    val currentTeam by viewModel.currentTeam.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (currentTeam != null) {
                FloatingActionButton(onClick = onAddFixture) {
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
                        FixtureItem(fixture, onClick = { onEditFixture(fixture.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun FixtureItem(fixture: Fixture, onClick: () -> Unit) {
    val dateStr = remember(fixture.date) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(fixture.date))
    }
    val timeStr = remember(fixture.date) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(fixture.date))
    }

    val resultChar: String
    val boxColor: Color
    
    if (fixture.type == "Training" || fixture.result == null) {
        resultChar = ""
        boxColor = Color.LightGray
    } else {
        val our = fixture.result.ourScore
        val opp = fixture.result.opponentScore
        if (our > opp) {
            resultChar = "W"
            boxColor = Color(0xFF4CAF50) // Green
        } else if (our < opp) {
            resultChar = "L"
            boxColor = Color(0xFFF44336) // Red
        } else {
            resultChar = "D"
            boxColor = Color(0xFFFFEB3B) // Yellow
        }
    }

    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text("vs ${fixture.opponent}", fontWeight = FontWeight.Bold) },
        supportingContent = { 
            Text("$dateStr | $timeStr | ${fixture.locationType} @ ${fixture.venue}") 
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(boxColor, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = resultChar,
                    color = if (boxColor == Color.LightGray || boxColor == Color(0xFFFFEB3B)) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        trailingContent = {
            fixture.result?.let {
                Text(
                    text = "${it.ourScore} - ${it.opponentScore}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun FixturesScreenPreview() {
    EveryMinuteTheme {
        FixturesScreen(onAddFixture = {}, onEditFixture = {})
    }
}
