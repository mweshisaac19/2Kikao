package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LecturerStudentsViewModel : ViewModel() {
    private val _students = MutableStateFlow<List<UserProfile>>(emptyList())
    val students: StateFlow<List<UserProfile>> = _students

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchStudents()
    }

    private fun fetchStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                try {
                    // 1. Get classes taught by this lecturer
                    val classes = FirebaseManager.getLecturerClasses(uid)
                    
                    // 2. Identify target courses (degree programs) linked to these classes
                    val targetCourses = classes.map { it.targetCourse }.filter { it.isNotEmpty() }.distinct()
                    
                    // 3. Fetch all students enrolled in those courses
                    val studentsFromCourses = FirebaseManager.getStudentsByCourses(targetCourses)
                    
                    // 4. Also fetch specifically enrolled students by ID (legacy fallback)
                    val specificallyEnrolledIds = classes.flatMap { it.studentsEnrolled }.distinct()
                    val specificallyEnrolledStudents = specificallyEnrolledIds.mapNotNull { studentId ->
                        FirebaseManager.getUserProfile(studentId)
                    }

                    // Combine and remove duplicates by UID
                    val combinedStudents = (studentsFromCourses + specificallyEnrolledStudents)
                        .distinctBy { it.uid }
                    
                    _students.value = combinedStudents
                } catch (e: Exception) {
                    // Handle error
                }
            }
            _isLoading.value = false
        }
    }
}
