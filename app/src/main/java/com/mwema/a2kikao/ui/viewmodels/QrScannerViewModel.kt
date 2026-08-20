package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.ui.screens.student.ClassLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class QrVerificationState {
    object Idle : QrVerificationState()
    object Verifying : QrVerificationState()
    data class Success(val location: ClassLocation) : QrVerificationState()
    data class Error(val message: String) : QrVerificationState()
}

class QrScannerViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _verificationState = MutableStateFlow<QrVerificationState>(QrVerificationState.Idle)
    val verificationState: StateFlow<QrVerificationState> = _verificationState

    fun verifyQrCode(qrData: String) {
        // qrData format: "sessionId|dynamicToken"
        val parts = qrData.split("|")
        if (parts.size != 2) {
            _verificationState.value = QrVerificationState.Error("This QR code is in an invalid format.")
            return
        }

        val sessionId = parts[0]
        val dynamicToken = parts[1]

        viewModelScope.launch {
            _verificationState.value = QrVerificationState.Verifying
            try {
                val doc = firestore.collection("sessions").document(sessionId).get().await()
                if (doc.exists()) {
                    val firestoreToken = doc.getString("dynamicToken")
                    
                    if (firestoreToken == dynamicToken) {
                        val location = ClassLocation(
                            className = doc.getString("courseCode") ?: "Class",
                            roomName = doc.getString("room") ?: "Room",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            allowedRadiusMeters = (doc.getDouble("radiusMeters") ?: 100.0).toFloat()
                        )
                        _verificationState.value = QrVerificationState.Success(location)
                    } else {
                        _verificationState.value = QrVerificationState.Error("This QR code has expired. Please scan the latest code from your lecturer.")
                    }
                } else {
                    _verificationState.value = QrVerificationState.Error("The session for this QR code was not found.")
                }
            } catch (e: Exception) {
                _verificationState.value = QrVerificationState.Error("Verification failed. Please check your internet connection.")
            }
        }
    }

    fun resetState() {
        _verificationState.value = QrVerificationState.Idle
    }
}
