package com.domaners.everyminute.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domaners.everyminute.data.model.Player
import com.domaners.everyminute.data.repository.AuthRepository
import com.domaners.everyminute.data.repository.PlayerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository = PlayerRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val userId: String? get() = authRepository.currentUser.value?.uid

    val players: StateFlow<List<Player>> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user != null) playerRepository.getPlayers(user.uid)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPlayer(player: Player) {
        val uid = userId ?: return
        viewModelScope.launch {
            playerRepository.addPlayer(uid, player)
        }
    }

    fun updatePlayer(player: Player) {
        val uid = userId ?: return
        viewModelScope.launch {
            playerRepository.updatePlayer(uid, player)
        }
    }

    fun deletePlayer(playerId: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            playerRepository.deletePlayer(uid, playerId)
        }
    }
}
