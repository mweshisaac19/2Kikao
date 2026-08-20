package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LecturerMarkEntryViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun saveMarks(assessmentId: String, marks: List<Pair<String, String>>) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val marksMap = marks.associate { it.first to it.second }
                firestore.collection("assessments").document(assessmentId)
                    .update("studentMarks", marksMap).await()
                _saveSuccess.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetSuccess() {
        _saveSuccess.value = false
    }
}
