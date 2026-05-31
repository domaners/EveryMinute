package com.domaners.everyminute.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String = "",
    val teamId: String = "",
    val name: String = "",
    val squadNumber: Int = 0,
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val stats: PlayerStats = PlayerStats(),
    val pitchPosition: Position? = null
)

@Serializable
data class Position(
    val x: Float,
    val y: Float
)

@Serializable
data class PlayerStats(
    val goals: Int = 0,
    val assists: Int = 0,
    val minutesPlayed: Int = 0,
    val appearances: Int = 0
)
