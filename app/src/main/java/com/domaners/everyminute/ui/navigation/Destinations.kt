package com.domaners.everyminute.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Login : Destination
    @Serializable
    data object Dashboard : Destination
    @Serializable
    data object Team : Destination
    @Serializable
    data object Fixtures : Destination
}
