package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminHomeViewModel : ViewModel() {
    private val _studentCount = MutableStateFlow(0)
    val studentCount: StateFlow<Int> = _studentCount

    private val _lecturerCount = MutableStateFlow(0)
    val lecturerCount: StateFlow<Int> = _lecturerCount

    private val _courseCount = MutableStateFlow(0)
    val courseCount: StateFlow<Int> = _courseCount

    init {
        fetchStats()
    }

    private fun fetchStats() {
        // In a real app, you'd use a repository that listens to Firestore updates
        // For now, we'll just simulate fetching from FirebaseManager
        viewModelScope.launch {
            // Simulated counts
            _studentCount.value = 4286
            _lecturerCount.value = 186
            _courseCount.value = 94
        }
    }
}
