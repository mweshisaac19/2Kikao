package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.data.UserProfile
import com.mwema.a2kikao.ui.screens.lecturer.StudentAcademicProfile
import com.mwema.a2kikao.ui.screens.lecturer.StudentAssessmentResult
import com.mwema.a2kikao.ui.screens.lecturer.StudentCoursePerformance
import com.mwema.a2kikao.ui.theme.KikaoColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.Color

class LecturerStudentDetailViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _studentProfile = MutableStateFlow<StudentAcademicProfile?>(null)
    val studentProfile: StateFlow<StudentAcademicProfile?> = _studentProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchStudentDetail(studentId: String, lecturerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Fetch Student Profile
                val profile = FirebaseManager.getUserProfile(studentId) ?: return@launch
                
                // 2. Get classes taught by this lecturer
                val lecturerClasses = FirebaseManager.getLecturerClasses(lecturerId)
                val classIds = lecturerClasses.map { it.id }
                
                // 3. Fetch assessments for these classes where this student has a mark
                val assessments = mutableListOf<StudentAssessmentResult>()
                val coursePerformances = mutableListOf<StudentCoursePerformance>()
                
                if (classIds.isNotEmpty()) {
                    val assessmentsSnapshot = firestore.collection("assessments")
                        .whereIn("classId", classIds)
                        .get().await()
                    
                    assessmentsSnapshot.documents.forEach { doc ->
                        val data = doc.data ?: return@forEach
                        val studentMarks = data["studentMarks"] as? Map<String, String> ?: emptyMap()
                        val scoreStr = studentMarks[studentId] ?: return@forEach
                        
                        assessments.add(
                            StudentAssessmentResult(
                                title = data["title"] as? String ?: "Assessment",
                                type = data["type"] as? String ?: "CAT",
                                score = scoreStr.toDoubleOrNull() ?: 0.0,
                                total = (data["totalScore"] as? Number)?.toDouble() ?: 20.0,
                                classAverage = 15.0, // Should be calculated
                                date = "Recent"
                            )
                        )
                    }

                    // 4. Group by class to get course performance
                    lecturerClasses.forEachIndexed { index, cls ->
                        val classAssessments = assessmentsSnapshot.documents.filter { it.getString("classId") == cls.id }
                        val studentClassMarks = classAssessments.mapNotNull { doc ->
                            (doc.data?.get("studentMarks") as? Map<String, String>)?.get(studentId)?.toDoubleOrNull()
                        }
                        
                        if (studentClassMarks.isNotEmpty()) {
                            val avg = (studentClassMarks.average() / 20.0 * 100).toInt() // Assuming 20 is total for demo
                            coursePerformances.add(
                                StudentCoursePerformance(
                                    code = cls.code,
                                    name = cls.name,
                                    average = avg,
                                    attendance = 85 + (index * 2), // Simulated
                                    position = 5, // Simulated
                                    accent = when (index % 4) {
                                        0 -> KikaoColors.Teal
                                        1 -> KikaoColors.Gold
                                        2 -> Color(0xFF8B5CF6)
                                        else -> KikaoColors.Indigo
                                    }
                                )
                            )
                        }
                    }
                }

                _studentProfile.value = StudentAcademicProfile(
                    name = profile.fullName,
                    registrationNumber = profile.registrationNumber ?: "",
                    program = profile.course ?: "N/A",
                    year = "Year ${profile.yearOfStudy ?: "?"}",
                    email = profile.email,
                    photo = profile.profilePictureUrl,
                    overallAverage = if (coursePerformances.isNotEmpty()) coursePerformances.map { it.average }.average().toInt() else 0,
                    rankedCourses = coursePerformances.size,
                    assessments = assessments.take(5),
                    coursePerformance = coursePerformances
                )
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
