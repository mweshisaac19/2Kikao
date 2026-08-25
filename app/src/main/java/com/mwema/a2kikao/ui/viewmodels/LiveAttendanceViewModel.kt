package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.ui.screens.lecturer.VerifiedStudent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LiveAttendanceViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _currentQrToken = MutableStateFlow("")
    val currentQrToken: StateFlow<String> = _currentQrToken
    
    private val _verifiedStudents = MutableStateFlow<List<VerifiedStudent>>(emptyList())
    val verifiedStudents: StateFlow<List<VerifiedStudent>> = _verifiedStudents

    private val _totalExpectedStudents = MutableStateFlow(0)
    val totalExpectedStudents: StateFlow<Int> = _totalExpectedStudents
    
    private var qrJob: Job? = null
    private var attendanceListener: ListenerRegistration? = null
    
    fun startSession(sessionId: String, courseCode: String) {
        startQrRotation(sessionId)
        observeAttendance(sessionId)
        fetchTotalExpectedStudents(courseCode)
    }

    private fun startQrRotation(sessionId: String) {
        qrJob?.cancel()
        qrJob = viewModelScope.launch {
            while (true) {
                val newToken = UUID.randomUUID().toString()
                _currentQrToken.value = newToken
                
                // Update Firestore so students can verify
                firestore.collection("sessions").document(sessionId)
                    .update("dynamicToken", newToken)
                
                delay(15000) // 15 seconds
            }
        }
    }

    private fun fetchTotalExpectedStudents(courseCode: String) {
        viewModelScope.launch {
            try {
                // Get the class to find the target course program
                val classesSnapshot = firestore.collection("classes")
                    .whereEqualTo("code", courseCode)
                    .limit(1)
                    .get().await()
                
                val targetCourse = classesSnapshot.documents.firstOrNull()?.getString("targetCourse") ?: ""
                if (targetCourse.isNotEmpty()) {
                    val students = FirebaseManager.getStudentsByCourses(listOf(targetCourse))
                    _totalExpectedStudents.value = students.size
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun observeAttendance(sessionId: String) {
        attendanceListener?.remove()
        
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        
        attendanceListener = firestore.collection("sessions").document(sessionId)
            .collection("attendees")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val attendees = snapshot?.documents?.mapNotNull { doc ->
                    val name = doc.getString("studentName") ?: "Student"
                    VerifiedStudent(
                        id = doc.id,
                        name = name,
                        registrationNumber = doc.getString("regNo") ?: "",
                        verificationTime = timeFormatter.format(Date(doc.getLong("timestamp") ?: 0)),
                        initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase()
                    )
                } ?: emptyList()
                
                _verifiedStudents.value = attendees
            }
    }

    fun endSession(sessionId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                firestore.collection("sessions").document(sessionId)
                    .update("status", "COMPLETED")
                    .await()
                onComplete()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        qrJob?.cancel()
        attendanceListener?.remove()
    }
}
