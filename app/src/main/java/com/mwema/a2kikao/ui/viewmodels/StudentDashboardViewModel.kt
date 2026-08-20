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

class StudentDashboardViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _attendancePercentage = MutableStateFlow(0)
    val attendancePercentage: StateFlow<Int> = _attendancePercentage

    private val _todayClasses = MutableStateFlow<List<CourseClass>>(emptyList())
    val todayClasses: StateFlow<List<CourseClass>> = _todayClasses

    private val _recentNotifications = MutableStateFlow<List<KikaoNotification>>(emptyList())
    val recentNotifications: StateFlow<List<KikaoNotification>> = _recentNotifications

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                val profile = FirebaseManager.getUserProfile(uid)
                _userProfile.value = profile
                
                if (profile != null) {
                    val classes = FirebaseManager.getStudentClasses(uid)
                    // For "today" classes, we'd normally filter by the current day of the week.
                    // For now, let's just take the first few as "today's" schedule.
                    _todayClasses.value = classes.take(3)
                    
                    val notifications = FirebaseManager.getNotificationsForStudent(uid)
                    _recentNotifications.value = notifications.take(5)
                }
            }
            // Simulated attendance until we have a real attendance tracking system
            _attendancePercentage.value = 87
        }
    }
}
