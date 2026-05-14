package com.domaners.everyminute.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val stats: PlayerStats = PlayerStats()
)

@Serializable
data class PlayerStats(
    val goals: Int = 0,
    val assists: Int = 0,
    val minutesPlayed: Int = 0,
    val appearances: Int = 0
)
