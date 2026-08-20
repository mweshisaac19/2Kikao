package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.AttendanceRequestData
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LecturerProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _leaveRequests = MutableStateFlow<List<AttendanceRequestData>>(emptyList())
    val leaveRequests: StateFlow<List<AttendanceRequestData>> = _leaveRequests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                _userProfile.value = FirebaseManager.getUserProfile(uid)
                _leaveRequests.value = FirebaseManager.getPendingLeaveRequests(uid)
            }
            _isLoading.value = false
        }
    }

    fun updateRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            try {
                firestore.collection("attendance_requests").document(requestId)
                    .update("status", status)
                // Refresh list
                val uid = FirebaseManager.currentUserUId
                if (uid != null) {
                    _leaveRequests.value = FirebaseManager.getPendingLeaveRequests(uid)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
