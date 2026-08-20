package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyClassesViewModel : ViewModel() {
    private val _classes = MutableStateFlow<List<CourseClass>>(emptyList())
    val classes: StateFlow<List<CourseClass>> = _classes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchClasses()
    }

    private fun fetchClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                _classes.value = FirebaseManager.getStudentClasses(uid)
            }
            _isLoading.value = false
        }
    }
}
