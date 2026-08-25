package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class SessionLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Float = 100f
)

class CreateSessionViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess

    private val _currentLocation = MutableStateFlow<SessionLocation?>(null)
    val currentLocation: StateFlow<SessionLocation?> = _currentLocation

    private val _createdSessionId = MutableStateFlow<String?>(null)
    val createdSessionId: StateFlow<String?> = _createdSessionId

    private val _lecturerClasses = MutableStateFlow<List<CourseClass>>(emptyList())
    val lecturerClasses: StateFlow<List<CourseClass>> = _lecturerClasses

    init {
        fetchLecturerClasses()
    }

    private fun fetchLecturerClasses() {
        viewModelScope.launch {
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                _lecturerClasses.value = FirebaseManager.getLecturerClasses(uid)
            }
        }
    }

    fun setLocation(lat: Double, lng: Double) {
        _currentLocation.value = SessionLocation(lat, lng)
    }

    fun createSession(courseCode: String, topic: String, room: String, startTime: String, duration: String) {
        viewModelScope.launch {
            _isCreating.value = true
            try {
                val location = _currentLocation.value ?: throw Exception("Location is required for verification")
                
                val sessionData = hashMapOf(
                    "courseCode" to courseCode,
                    "topic" to topic,
                    "room" to room,
                    "startTime" to startTime,
                    "duration" to duration,
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "radiusMeters" to location.radiusMeters,
                    "timestamp" to System.currentTimeMillis(),
                    "status" to "LIVE",
                    "dynamicToken" to UUID.randomUUID().toString()
                )
                
                val docRef = firestore.collection("sessions").add(sessionData).await()
                _createdSessionId.value = docRef.id
                _createSuccess.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isCreating.value = false
            }
        }
    }
}
