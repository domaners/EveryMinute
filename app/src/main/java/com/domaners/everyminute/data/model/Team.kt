package com.domaners.everyminute.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: String = "",
    val name: String = "",
    val coachId: String = "",
    val playerIds: List<String> = emptyList()
)
