package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.CourseClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClassDetailsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _courseClass = MutableStateFlow<CourseClass?>(null)
    val courseClass: StateFlow<CourseClass?> = _courseClass

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchClassDetails(classId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val document = firestore.collection("classes").document(classId).get().await()
                _courseClass.value = document.toObject(CourseClass::class.java)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
