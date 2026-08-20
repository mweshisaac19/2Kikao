package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.KikaoNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LecturerNotificationViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess

    fun postNotification(title: String, message: String, type: String, classId: String? = null) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isPosting.value = true
            try {
                val notification = KikaoNotification(
                    id = firestore.collection("notifications").document().id,
                    title = title,
                    message = message,
                    sender = auth.currentUser?.displayName ?: "Lecturer",
                    timestamp = System.currentTimeMillis(),
                    type = type,
                    classId = classId
                )
                firestore.collection("notifications").document(notification.id).set(notification).await()
                _postSuccess.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isPosting.value = false
            }
        }
    }

    fun resetSuccess() {
        _postSuccess.value = false
    }
}
