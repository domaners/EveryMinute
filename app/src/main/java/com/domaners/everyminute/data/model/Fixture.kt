package com.domaners.everyminute.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Fixture(
    val id: String = "",
    val teamId: String = "",
    val opponent: String = "",
    val date: Long = 0L,
    val type: String = "League", // League, Cup, Tournament, Friendly, Training
    val venue: String = "",
    val locationType: String = "Home", // Home, Away, Neutral
    val availability: Map<String, AvailabilityStatus> = emptyMap(),
    val result: MatchResult? = null,
    val playerStats: List<FixturePlayerStats> = emptyList()
)

@Serializable
data class FixturePlayerStats(
    val playerId: String = "",
    val minutesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val position: String = "Forward" // Goalkeeper, Defender, Midfielder, Forward
)

@Serializable
enum class AvailabilityStatus {
    AVAILABLE, UNAVAILABLE, UNKNOWN
}

@Serializable
data class MatchResult(
    val ourScore: Int = 0,
    val opponentScore: Int = 0,
    val scorers: List<String> = emptyList(), // Player IDs
    val assists: List<String> = emptyList() // Player IDs
)
