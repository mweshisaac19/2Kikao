package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun signUp(email: String, password: String, profile: UserProfile) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            val result = FirebaseManager.signUp(email, password, profile)
            if (result.isSuccess) {
                _uiState.value = SignUpUiState.Success
            } else {
                _uiState.value = SignUpUiState.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
            }
        }
    }
}

sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    object Success : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}
