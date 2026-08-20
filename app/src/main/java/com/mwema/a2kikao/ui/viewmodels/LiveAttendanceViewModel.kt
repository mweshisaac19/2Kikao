package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class LiveAttendanceViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _currentQrToken = MutableStateFlow("")
    val currentQrToken: StateFlow<String> = _currentQrToken
    
    private var qrJob: Job? = null
    
    fun startQrRotation(sessionId: String) {
        qrJob?.cancel()
        qrJob = viewModelScope.launch {
            while (true) {
                val newToken = UUID.randomUUID().toString()
                _currentQrToken.value = newToken
                
                // Update Firestore so students can verify
                firestore.collection("sessions").document(sessionId)
                    .update("dynamicToken", newToken)
                
                delay(15000) // 15 seconds
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        qrJob?.cancel()
    }
}
