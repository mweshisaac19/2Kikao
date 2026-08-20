package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.KikaoNotification
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LecturerHomeViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _lecturerClasses = MutableStateFlow<List<CourseClass>>(emptyList())
    val lecturerClasses: StateFlow<List<CourseClass>> = _lecturerClasses

    private val _recentNotifications = MutableStateFlow<List<KikaoNotification>>(emptyList())
    val recentNotifications: StateFlow<List<KikaoNotification>> = _recentNotifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchLecturerData()
    }

    private fun fetchLecturerData() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                val profile = FirebaseManager.getUserProfile(uid)
                _userProfile.value = profile
                
                if (profile != null) {
                    _lecturerClasses.value = FirebaseManager.getLecturerClasses(uid)
                    _recentNotifications.value = FirebaseManager.getNotificationsForLecturer(uid).take(5)
                }
            }
            _isLoading.value = false
        }
    }
}
