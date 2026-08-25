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

data class StudentDashboardClass(
    val course: CourseClass,
    val isAttended: Boolean,
    val isMissed: Boolean
)

class StudentDashboardViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _attendancePercentage = MutableStateFlow(0)
    val attendancePercentage: StateFlow<Int> = _attendancePercentage

    private val _dashboardClasses = MutableStateFlow<List<StudentDashboardClass>>(emptyList())
    val dashboardClasses: StateFlow<List<StudentDashboardClass>> = _dashboardClasses

    private val _recentNotifications = MutableStateFlow<List<KikaoNotification>>(emptyList())
    val recentNotifications: StateFlow<List<KikaoNotification>> = _recentNotifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchDashboardData()
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                val profile = FirebaseManager.getUserProfile(uid)
                _userProfile.value = profile
                
                if (profile != null) {
                    val classes = FirebaseManager.getStudentClasses(uid)
                    val todaySessions = FirebaseManager.getTodaySessions(classes.map { it.code })
                    
                    processDashboardClasses(uid, classes, todaySessions)
                    
                    val notifications = FirebaseManager.getNotificationsForStudent(uid)
                    _recentNotifications.value = notifications.take(5)
                }
            }
            // Simulated overall attendance
            _attendancePercentage.value = 87
            _isLoading.value = false
        }
    }

    private suspend fun processDashboardClasses(uid: String, classes: List<CourseClass>, sessions: List<Map<String, Any>>) {
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
            val session = sessions.find { it["courseCode"] == cls.code }
            val sessionId = session?.get("id") as? String
            
            val isAttended = if (sessionId != null) {
                FirebaseManager.getStudentAttendanceForSession(sessionId, uid)
            } else false

            // Parse start time (e.g., "08:00")
            val startTimeStr = cls.time.substringBefore("-").trim()
            val startHour = startTimeStr.substringBefore(":").toIntOrNull() ?: 0
            val startMin = startTimeStr.substringAfter(":").take(2).toIntOrNull() ?: 0
            val startTimeInMinutes = startHour * 60 + startMin

            // A class is "missed" if the session happened (or should have started) 
            // and the student didn't attend, and the time has passed.
            val isTimePassed = currentTimeInMinutes > (startTimeInMinutes + 60) // 1 hour grace

            StudentDashboardClass(
                course = cls,
                isAttended = isAttended,
                isMissed = !isAttended && (sessionId != null || isTimePassed)
            )
        }.sortedBy { it.course.time.substringBefore("-").trim() }

        _dashboardClasses.value = processed
    }
}
