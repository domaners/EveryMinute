package com.domaners.everyminute.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Login : Destination
    @Serializable
    data object Register : Destination
    @Serializable
    data object Dashboard : Destination
    @Serializable
    data object CreateTeam : Destination
    @Serializable
    data object Team : Destination
    @Serializable
    data object Fixtures : Destination
    
    @Serializable
    data class PlayerProfile(val playerId: String) : Destination
    @Serializable
    data class AddEditPlayer(val playerId: String? = null) : Destination
    
    @Serializable
    data class FixtureDetail(val fixtureId: String) : Destination
    @Serializable
    data class AddEditFixture(val fixtureId: String? = null) : Destination
}
