package com.domaners.everyminute.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domaners.everyminute.R
import com.domaners.everyminute.data.model.Player
import com.domaners.everyminute.data.model.Position
import com.domaners.everyminute.ui.MainViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupScreen(
    viewModel: MainViewModel = viewModel(),
    onBack: () -> Unit
) {
    val players by viewModel.teamPlayers.collectAsState()
    var pitchSize by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Preferred Lineup") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Pitch Area (Top Half)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val size = coordinates.size
                        if (size.width > 0 && size.height > 0) {
                            pitchSize = Offset(size.width.toFloat(), size.height.toFloat())
                        }
                    }
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.football_pitch),
                    contentDescription = "Football Pitch",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                
                // Players on pitch
                players.filter { it.pitchPosition != null }.forEach { player ->
                    key(player.id) {
                        DraggablePlayer(
                            player = player,
                            pitchSize = pitchSize,
                            onPositionChanged = { offset ->
                                if (offset == null) {
                                    viewModel.updatePlayerPosition(player.id, null)
                                } else if (pitchSize.x > 0 && pitchSize.y > 0) {
                                    val nx = (offset.x / pitchSize.x).coerceIn(0f, 1f)
                                    val ny = (offset.y / pitchSize.y).coerceIn(0f, 1f)
                                    viewModel.updatePlayerPosition(player.id, Position(nx, ny))
                                }
                            }
                        )
                    }
                }
            }

            // Squad list (Bottom Half)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Squad (Tap circle to add to pitch)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(players.filter { it.pitchPosition == null }) { player ->
                        ListItem(
                            headlineContent = { Text(player.name) },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                        .clickable {
                                            viewModel.updatePlayerPosition(player.id, Position(0.5f, 0.5f))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = player.squadNumber.toString(),
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DraggablePlayer(
    player: Player,
    pitchSize: Offset,
    onPositionChanged: (Offset?) -> Unit
) {
    val pos = player.pitchPosition ?: Position(0.5f, 0.5f)
    
    // Safety check for NaN or Infinite
    val safeX = if (pos.x.isFinite()) pos.x else 0.5f
    val safeY = if (pos.y.isFinite()) pos.y else 0.5f

    var offsetX by remember { mutableStateOf(safeX * pitchSize.x) }
    var offsetY by remember { mutableStateOf(safeY * pitchSize.y) }

    LaunchedEffect(player.pitchPosition, pitchSize) {
        if (pitchSize != Offset.Zero) {
            val px = player.pitchPosition?.x ?: 0.5f
            val py = player.pitchPosition?.y ?: 0.5f
            offsetX = (if (px.isFinite()) px else 0.5f) * pitchSize.x
            offsetY = (if (py.isFinite()) py else 0.5f) * pitchSize.y
        }
    }

    Box(
        modifier = Modifier
            .offset {
                val ix = if (offsetX.isFinite()) offsetX.roundToInt() else 0
                val iy = if (offsetY.isFinite()) offsetY.roundToInt() else 0
                IntOffset(ix - 30, iy - 30)
            }
            .size(60.dp)
            .clip(CircleShape)
            .background(Color.Red)
            .pointerInput(player.id) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        onPositionChanged(Offset(offsetX, offsetY))
                    }
                )
            }
            .pointerInput(player.id) {
                detectTapGestures(
                    onTap = {
                        onPositionChanged(null)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = player.squadNumber.toString(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
