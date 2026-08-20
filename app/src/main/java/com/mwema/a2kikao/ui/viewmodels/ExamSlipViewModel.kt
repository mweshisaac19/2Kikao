package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExamSlipViewModel : ViewModel() {
    private val _examClasses = MutableStateFlow<List<CourseClass>>(emptyList())
    val examClasses: StateFlow<List<CourseClass>> = _examClasses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchExamSchedule()
    }

    private fun fetchExamSchedule() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                // In a real app, we'd fetch from an "exams" collection.
                // For now, we reuse the student classes.
                _examClasses.value = FirebaseManager.getStudentClasses(uid)
            }
            _isLoading.value = false
        }
    }
}
