package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LearningMaterial(
    val id: String = "",
    val title: String = "",
    val type: String = "pdf",
    val url: String = "",
    val size: String = "0 MB",
    val date: String = ""
)

data class ClassAnnouncement(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val date: String = "",
    val author: String = ""
)

class LearningMaterialsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _materials = MutableStateFlow<List<LearningMaterial>>(emptyList())
    val materials: StateFlow<List<LearningMaterial>> = _materials

    private val _announcements = MutableStateFlow<List<ClassAnnouncement>>(emptyList())
    val announcements: StateFlow<List<ClassAnnouncement>> = _announcements

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchClassContent(classId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch materials
                val matSnapshot = firestore.collection("classes").document(classId).collection("materials").get().await()
                _materials.value = matSnapshot.toObjects(LearningMaterial::class.java)

                // Fetch announcements
                val annSnapshot = firestore.collection("classes").document(classId).collection("announcements").get().await()
                _announcements.value = annSnapshot.toObjects(ClassAnnouncement::class.java)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
