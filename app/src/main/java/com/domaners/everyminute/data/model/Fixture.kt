package com.domaners.everyminute.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Fixture(
    val id: String = "",
    val teamId: String = "",
    val opponent: String = "",
    val date: Long = 0L,
    val venue: String = "",
    val availability: Map<String, AvailabilityStatus> = emptyMap(),
    val result: MatchResult? = null
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
