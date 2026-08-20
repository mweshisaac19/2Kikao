package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LecturerResultsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _courseOptions = MutableStateFlow<List<CourseOptionData>>(emptyList())
    val courseOptions: StateFlow<List<CourseOptionData>> = _courseOptions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
                    _courseOptions.value = classes.map { CourseOptionData(it.id, it.code, it.name) }
                } catch (e: Exception) {
                    // Handle error
                }
            }
            _isLoading.value = false
        }
    }
}

data class CourseOptionData(val id: String, val code: String, val name: String)
