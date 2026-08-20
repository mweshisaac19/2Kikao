package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.ui.screens.student.ConsultationSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ConsultationViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _slots = MutableStateFlow<List<ConsultationSlot>>(emptyList())
    val slots: StateFlow<List<ConsultationSlot>> = _slots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _lecturerName = MutableStateFlow("")
    val lecturerName: StateFlow<String> = _lecturerName

    fun fetchConsultationData(lecturerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch lecturer info
                val lecturerDoc = firestore.collection("users").document(lecturerId).get().await()
                _lecturerName.value = lecturerDoc.getString("fullName") ?: "Lecturer"

                // Fetch slots
                val snapshot = firestore.collection("consultations")
                    .whereEqualTo("lecturerId", lecturerId)
                    .get().await()
                _slots.value = snapshot.toObjects(ConsultationSlot::class.java)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
