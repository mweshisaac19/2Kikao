package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceConfirmationViewModel : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _recordSuccess = MutableStateFlow(false)
    val recordSuccess: StateFlow<Boolean> = _recordSuccess

    fun confirmAttendance(sessionId: String) {
        viewModelScope.launch {
            _isRecording.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                val profile = FirebaseManager.getUserProfile(uid)
                if (profile != null) {
                    val result = FirebaseManager.recordAttendance(sessionId, profile)
                    _recordSuccess.value = result.isSuccess
                }
            }
            _isRecording.value = false
        }
    }
}
