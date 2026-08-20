package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            delay(2000) // Minimum splash time
            // Stop autologin: Sign out and always navigate to login screen
            FirebaseManager.signOut()
            _uiState.value = SplashUiState.Unauthenticated
        }
    }
}

sealed class SplashUiState {
    object Idle : SplashUiState()
    object Unauthenticated : SplashUiState()
    data class Authenticated(val profile: UserProfile) : SplashUiState()
}
