package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AssessmentFeedbackViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _score = MutableStateFlow(0.0)
    val score: StateFlow<Double> = _score

    private val _totalScore = MutableStateFlow(20.0)
    val totalScore: StateFlow<Double> = _totalScore

    private val _feedback = MutableStateFlow("")
    val feedback: StateFlow<String> = _feedback

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchFeedback(assessmentId: String, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("assessments").document(assessmentId).get().await()
                val data = doc.data
                if (data != null) {
                    val studentMarks = data["studentMarks"] as? Map<String, String> ?: emptyMap()
                    val scoreStr = studentMarks[studentId]
                    _score.value = scoreStr?.toDoubleOrNull() ?: 0.0
                    _totalScore.value = (data["totalScore"] as? Number)?.toDouble() ?: 20.0
                    _feedback.value = data["lecturerFeedback"] as? String ?: "No feedback provided yet."
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
