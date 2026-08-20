package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminManagementViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun addStudent(fullName: String, regNo: String, email: String) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val student = mapOf(
                    "fullName" to fullName,
                    "regNo" to regNo,
                    "email" to email,
                    "role" to "STUDENT"
                )
                firestore.collection("students").add(student).await()
                _saveSuccess.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun addDepartment(name: String, head: String) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val department = mapOf(
                    "name" to name,
                    "head" to head
                )
                firestore.collection("departments").add(department).await()
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
