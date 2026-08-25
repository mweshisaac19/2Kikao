package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.CourseClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LecturerAddClassViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _classToEdit = MutableStateFlow<CourseClass?>(null)
    val classToEdit: StateFlow<CourseClass?> = _classToEdit

    fun fetchClassToEdit(classId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("classes").document(classId).get().await()
                _classToEdit.value = doc.toObject(CourseClass::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun saveClass(
        id: String? = null,
        code: String,
        name: String,
        targetCourse: String,
        room: String,
        days: List<String>,
        time: String
    ) {
        val lecturerId = auth.currentUser?.uid ?: return
        val lecturerName = auth.currentUser?.displayName ?: "Lecturer"

        viewModelScope.launch {
            _isSaving.value = true
            try {
                val classId = id ?: firestore.collection("classes").document().id
                val newClass = CourseClass(
                    id = classId,
                    code = code,
                    name = name,
                    lecturer = lecturerName,
                    lecturerId = lecturerId,
                    time = time,
                    room = room,
                    day = if (days.size == 1) days.first() else "Multiple Days",
                    days = days,
                    targetCourse = targetCourse,
                    studentsEnrolled = _classToEdit.value?.studentsEnrolled ?: emptyList()
                )
                
                firestore.collection("classes").document(classId).set(newClass).await()
                _success.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetSuccess() {
        _success.value = false
    }
}
