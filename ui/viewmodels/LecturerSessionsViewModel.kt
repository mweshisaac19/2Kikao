package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class LecturerSessionsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _sessions = MutableStateFlow<List<LecturerSessionData>>(emptyList())
    val sessions: StateFlow<List<LecturerSessionData>> = _sessions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchSessions()
    }

    private fun fetchSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                try {
                    val classes = FirebaseManager.getLecturerClasses(uid)
                    val courseCodes = classes.map { it.code }.distinct()
                    
                    if (courseCodes.isNotEmpty()) {
                        val sessionMaps = FirebaseManager.getSessionsByCourseCodes(courseCodes)
                        val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                        
                        val sessionDataList = mutableListOf<LecturerSessionData>()
                        
                        for (map in sessionMaps) {
                            val sessionId = map["id"] as? String ?: ""
                            val courseCode = map["courseCode"] as? String ?: ""
                            val timestamp = map["timestamp"] as? Long ?: 0
                            val statusStr = map["status"] as? String ?: "COMPLETED"
                            
                            // 1. Get real attendance count from subcollection
                            val attendeesSnapshot = firestore.collection("sessions")
                                .document(sessionId).collection("attendees").get().await()
                            val attendanceCount = attendeesSnapshot.size()
                            
                            // 2. Get real student count for this course program
                            val courseClass = classes.find { it.code == courseCode }
                            val targetProgram = courseClass?.targetCourse ?: ""
                            val studentCount = if (targetProgram.isNotEmpty()) {
                                FirebaseManager.getStudentsByCourses(listOf(targetProgram)).size
                            } else {
                                0
                            }

                            sessionDataList.add(
                                LecturerSessionData(
                                    id = sessionId,
                                    courseCode = courseCode,
                                    courseName = map["courseName"] as? String ?: "Course",
                                    topic = map["topic"] as? String ?: "No Topic",
                                    dateLabel = dateFormatter.format(Date(timestamp)),
                                    time = map["startTime"] as? String ?: "",
                                    duration = map["duration"] as? String ?: "",
                                    room = map["room"] as? String ?: "",
                                    studentCount = studentCount,
                                    attendanceCount = attendanceCount,
                                    status = when (statusStr) {
                                        "LIVE" -> "LIVE"
                                        "UPCOMING" -> "UPCOMING"
                                        else -> "COMPLETED"
                                    }
                                )
                            )
                        }
                        
                        _sessions.value = sessionDataList.sortedByDescending { 
                            mapOf("LIVE" to 2, "UPCOMING" to 1, "COMPLETED" to 0)[it.status] ?: 0 
                        }
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
            _isLoading.value = false
        }
    }
}

data class LecturerSessionData(
    val id: String,
    val courseCode: String,
    val courseName: String,
    val topic: String,
    val dateLabel: String,
    val time: String,
    val duration: String,
    val room: String,
    val studentCount: Int,
    val attendanceCount: Int,
    val status: String
)
