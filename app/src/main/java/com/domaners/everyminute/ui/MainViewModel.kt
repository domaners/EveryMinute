package com.domaners.everyminute.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domaners.everyminute.data.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithEmail(email, password)
            result.fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUpWithEmail(email, password)
            result.fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Sign up failed") }
            )
        }
    }

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithCredential(credential)
            result.fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Google sign in failed") }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data object Success : AuthState
    data class Error(val message: String) : AuthState
}
