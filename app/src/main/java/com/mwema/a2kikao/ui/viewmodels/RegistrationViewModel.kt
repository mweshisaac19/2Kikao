package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.CourseClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegistrationViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _availableUnits = MutableStateFlow<List<CourseClass>>(emptyList())
    val availableUnits: StateFlow<List<CourseClass>> = _availableUnits

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess

    init {
        fetchAvailableUnits()
    }

    private fun fetchAvailableUnits() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // In a real app, you'd filter by student's current year/semester
                val snapshot = firestore.collection("available_units").get().await()
                _availableUnits.value = snapshot.toObjects(CourseClass::class.java)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitRegistration(studentId: String, selectedUnitIds: List<String>) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val data = mapOf(
                    "studentId" to studentId,
                    "unitIds" to selectedUnitIds,
                    "timestamp" to System.currentTimeMillis(),
                    "status" to "PENDING"
                )
                firestore.collection("registrations").add(data).await()
                _registrationSuccess.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
