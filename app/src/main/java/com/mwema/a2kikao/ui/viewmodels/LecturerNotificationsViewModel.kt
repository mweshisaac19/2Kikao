package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.KikaoNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LecturerNotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<KikaoNotification>>(emptyList())
    val notifications: StateFlow<List<KikaoNotification>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                _notifications.value = FirebaseManager.getNotificationsForLecturer(uid)
            }
            _isLoading.value = false
        }
    }
}
