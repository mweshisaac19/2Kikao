package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.ui.screens.student.PostedAssessment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.Color

class PerformanceInsightsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _courses = MutableStateFlow<List<CourseClassAnalytics>>(emptyList())
    val courses: StateFlow<List<CourseClassAnalytics>> = _courses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchPerformanceData()
    }

    private fun fetchPerformanceData() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                try {
                    // Fetch enrolled classes first
                    val enrolledClasses = FirebaseManager.getStudentClasses(uid)
                    
                    val analyticsList = enrolledClasses.mapIndexed { index, course ->
                        // Fetch assessments for this class
                        val assessmentsSnapshot = firestore.collection("assessments")
                            .whereEqualTo("classId", course.id)
                            .get().await()
                        
                        val assessments = assessmentsSnapshot.documents.mapNotNull { doc ->
                            val data = doc.data ?: return@mapNotNull null
                            val studentMarks = data["studentMarks"] as? Map<String, String> ?: emptyMap()
                            val scoreStr = studentMarks[uid] ?: return@mapNotNull null
                            
                            PostedAssessment(
                                title = data["title"] as? String ?: "Assessment",
                                type = data["type"] as? String ?: "CAT",
                                score = scoreStr.toDoubleOrNull() ?: 0.0,
                                totalScore = (data["totalScore"] as? Number)?.toDouble() ?: 20.0,
                                classAverage = 15.0, // This should be calculated from all studentMarks
                                datePosted = "Recently"
                            )
                        }

                        CourseClassAnalytics(
                            id = course.id,
                            code = course.code,
                            name = course.name,
                            attendancePercent = 85 + (index * 2), // Simulated
                            classPosition = 5, // Simulated
                            classSize = course.studentsEnrolled.size,
                            assessments = assessments,
                            trendScores = listOf(65, 72, 68, 75, 80), // Simulated
                            accentColor = when (index % 4) {
                                0 -> Color(0xFF0F9D8A)
                                1 -> Color(0xFFF4B740)
                                2 -> Color(0xFF8B5CF6)
                                else -> Color(0xFF243B7A)
                            }
                        )
                    }
                    _courses.value = analyticsList
                } catch (e: Exception) {
                    // Handle error
                }
            }
            _isLoading.value = false
        }
    }
}

data class CourseClassAnalytics(
    val id: String,
    val code: String,
    val name: String,
    val attendancePercent: Int,
    val classPosition: Int,
    val classSize: Int,
    val assessments: List<PostedAssessment>,
    val trendScores: List<Int>,
    val accentColor: Color
)
