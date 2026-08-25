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
import java.util.Calendar
import java.util.Locale

data class LecturerHomeClass(
    val course: CourseClass,
    val isDone: Boolean,
    val isMissed: Boolean // Scheduled time passed but no session started
)

class LecturerHomeViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _homeClasses = MutableStateFlow<List<LecturerHomeClass>>(emptyList())
    val homeClasses: StateFlow<List<LecturerHomeClass>> = _homeClasses

    private val _nextClass = MutableStateFlow<CourseClass?>(null)
    val nextClass: StateFlow<CourseClass?> = _nextClass

    private val _recentNotifications = MutableStateFlow<List<KikaoNotification>>(emptyList())
    val recentNotifications: StateFlow<List<KikaoNotification>> = _recentNotifications

    private val _pendingRequestsCount = MutableStateFlow(0)
    val pendingRequestsCount: StateFlow<Int> = _pendingRequestsCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchLecturerData()
    }

    fun fetchLecturerData() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                val profile = FirebaseManager.getUserProfile(uid)
                _userProfile.value = profile
                
                if (profile != null) {
                    val classes = FirebaseManager.getLecturerClasses(uid)
                    val todaySessions = FirebaseManager.getTodaySessions(classes.map { it.code })
                    
                    processHomeClasses(classes, todaySessions)
                    _recentNotifications.value = FirebaseManager.getNotificationsForLecturer(uid).take(5)
                    _pendingRequestsCount.value = FirebaseManager.getPendingLeaveRequests(uid).size
                }
            }
            _isLoading.value = false
        }
    }

    private fun processHomeClasses(classes: List<CourseClass>, sessions: List<Map<String, Any>>) {
        val now = Calendar.getInstance()
        val currentDay = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) ?: ""
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        val todayClasses = classes.filter { 
            it.day.equals(currentDay, ignoreCase = true) || 
            it.days.any { day -> day.equals(currentDay, ignoreCase = true) }
        }

        val processed = todayClasses.map { cls ->
            val sessionExists = sessions.any { it["courseCode"] == cls.code }
            
            // Parse start time (e.g., "08:00")
            val startTimeStr = cls.time.substringBefore("-").trim()
            val startHour = startTimeStr.substringBefore(":").toIntOrNull() ?: 0
            val startMin = startTimeStr.substringAfter(":").take(2).toIntOrNull() ?: 0
            val startTimeInMinutes = startHour * 60 + startMin
            
            val isTimePassed = currentTimeInMinutes > (startTimeInMinutes + 30) // 30 min grace period

            LecturerHomeClass(
                course = cls,
                isDone = sessionExists,
                isMissed = !sessionExists && isTimePassed
            )
        }.sortedBy { it.course.time.substringBefore("-").trim() }

        _homeClasses.value = processed
        
        // Next class logic
        _nextClass.value = processed.find { !it.isDone && !it.isMissed }?.course ?: todayClasses.firstOrNull()
    }
}
