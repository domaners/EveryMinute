package com.domaners.everyminute.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domaners.everyminute.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    fun signOut() {
        authRepository.signOut()
    }
}
