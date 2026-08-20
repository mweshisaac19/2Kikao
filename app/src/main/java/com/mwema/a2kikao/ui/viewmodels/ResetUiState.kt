package com.mwema.a2kikao.ui.viewmodels

sealed class ResetUiState {
    object Idle : ResetUiState()
    object Loading : ResetUiState()
    object Success : ResetUiState()
    data class Error(val message: String) : ResetUiState()
}
