package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class LecturerResultsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _courseOptions = MutableStateFlow<List<CourseOptionData>>(emptyList())
    val courseOptions: StateFlow<List<CourseOptionData>> = _courseOptions

    private val _assessments = MutableStateFlow<List<AssessmentData>>(emptyList())
    val assessments: StateFlow<List<AssessmentData>> = _assessments

    private val _studentPerformances = MutableStateFlow<List<StudentPerformanceData>>(emptyList())
    val studentPerformances: StateFlow<List<StudentPerformanceData>> = _studentPerformances

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isDataLoading = MutableStateFlow(false)
    val isDataLoading: StateFlow<Boolean> = _isDataLoading

    init {
        fetchCourses()
    }

    private fun fetchCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                try {
                    val classes = FirebaseManager.getLecturerClasses(uid)
                    _courseOptions.value = classes.map { CourseOptionData(it.id, it.code, it.name, it.targetCourse) }
                } catch (e: Exception) {
                    // Handle error
                }
            }
            _isLoading.value = false
        }
    }

    fun fetchCourseData(classId: String, targetCourse: String) {
        viewModelScope.launch {
            _isDataLoading.value = true
            try {
                // 1. Fetch Assessments for this class
                val assessmentsSnapshot = firestore.collection("assessments")
                    .whereEqualTo("classId", classId)
                    .get().await()
                
                val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                val assessmentsList = assessmentsSnapshot.documents.map { doc ->
                    val data = doc.data ?: emptyMap<String, Any>()
                    val studentMarks = data["studentMarks"] as? Map<String, String> ?: emptyMap()
                    val totalMarks = (data["totalScore"] as? Number)?.toInt() ?: 100
                    
                    val marks = studentMarks.values.mapNotNull { it.toDoubleOrNull() }
                    val avg = if (marks.isNotEmpty()) marks.average().toInt() else 0

                    AssessmentData(
                        id = doc.id,
                        title = data["title"] as? String ?: "Unnamed Assessment",
                        type = data["type"] as? String ?: "CAT",
                        totalMarks = totalMarks,
                        average = (avg.toDouble() / totalMarks * 100).toInt(),
                        datePosted = dateFormatter.format(Date(data["timestamp"] as? Long ?: 0))
                    )
                }
                _assessments.value = assessmentsList

                // 2. Fetch Students for this course and calculate their average across assessments
                val students = FirebaseManager.getStudentsByCourses(listOf(targetCourse))
                val performanceList = students.map { student ->
                    val studentMarks = assessmentsSnapshot.documents.mapNotNull { doc ->
                        val marks = doc.get("studentMarks") as? Map<String, String> ?: emptyMap()
                        val total = (doc.get("totalScore") as? Number)?.toDouble() ?: 100.0
                        marks[student.uid]?.toDoubleOrNull()?.let { (it / total) * 100 }
                    }
                    
                    val avg = if (studentMarks.isNotEmpty()) studentMarks.average().toInt() else 0
                    
                    StudentPerformanceData(
                        id = student.uid,
                        name = student.fullName,
                        regNo = student.registrationNumber ?: "",
                        average = avg
                    )
                }.sortedByDescending { it.average }
                
                _studentPerformances.value = performanceList

            } catch (e: Exception) {
                // Handle error
            } finally {
                _isDataLoading.value = false
            }
        }
    }
}

data class CourseOptionData(val id: String, val code: String, val name: String, val targetCourse: String)
data class AssessmentData(val id: String, val title: String, val type: String, val totalMarks: Int, val average: Int, val datePosted: String)
data class StudentPerformanceData(val id: String, val name: String, val regNo: String, val average: Int)
