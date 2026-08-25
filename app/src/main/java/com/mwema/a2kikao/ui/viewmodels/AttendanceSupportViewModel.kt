package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.ui.screens.student.AttendanceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AttendanceSupportViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted

    fun submitRequest(request: AttendanceRequest) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val profile = FirebaseManager.getUserProfile(uid)
                val data = mapOf(
                    "studentId" to uid,
                    "studentName" to (profile?.fullName ?: "Student"),
                    "type" to request.type.name,
                    "reason" to request.reason,
                    "details" to request.details,
                    "startDate" to request.startDate,
                    "endDate" to request.endDate,
                    "affectedSessionDate" to request.affectedSessionDate,
                    "classId" to request.classId,
                    "status" to "PENDING",
                    "submittedAt" to System.currentTimeMillis()
                )
                firestore.collection("attendance_requests").add(data).await()
                _isSubmitted.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
