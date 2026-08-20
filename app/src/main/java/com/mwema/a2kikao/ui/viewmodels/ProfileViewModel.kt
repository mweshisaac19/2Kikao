package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _resetState = MutableStateFlow<ResetUiState>(ResetUiState.Idle)
    val resetState: StateFlow<ResetUiState> = _resetState

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                _userProfile.value = FirebaseManager.getUserProfile(uid)
            }
            _isLoading.value = false
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetState.value = ResetUiState.Loading
            val result = FirebaseManager.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _resetState.value = ResetUiState.Success
            } else {
                _resetState.value = ResetUiState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset link")
            }
        }
    }

    fun clearResetState() {
        _resetState.value = ResetUiState.Idle
    }
}
