package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _resetState = MutableStateFlow<ResetUiState>(ResetUiState.Idle)
    val resetState: StateFlow<ResetUiState> = _resetState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = FirebaseManager.signIn(email, password)
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Sign in failed")
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _resetState.value = ResetUiState.Loading
            val result = FirebaseManager.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _resetState.value = ResetUiState.Success
            } else {
                _resetState.value = ResetUiState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }

    fun clearResetState() {
        _resetState.value = ResetUiState.Idle
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val profile: UserProfile) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
