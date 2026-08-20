package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mwema.a2kikao.ui.screens.student.Facility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FacilityViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _facilities = MutableStateFlow<List<Facility>>(emptyList())
    val facilities: StateFlow<List<Facility>> = _facilities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchFacilities()
    }

    private fun fetchFacilities() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("facilities").get().await()
                _facilities.value = snapshot.toObjects(Facility::class.java)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
