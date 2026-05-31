package com.domaners.everyminute.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: String = "",
    val name: String = "",
    val iconUrl: String? = null,
    val coachIds: List<String> = emptyList(),
    val parentIds: List<String> = emptyList(),
    val playerIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList() // All users who have access
)
