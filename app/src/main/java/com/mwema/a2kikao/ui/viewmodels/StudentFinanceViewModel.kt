package com.mwema.a2kikao.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mwema.a2kikao.data.FinanceRecord
import com.mwema.a2kikao.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentFinanceViewModel : ViewModel() {
    private val _financeRecord = MutableStateFlow<FinanceRecord?>(null)
    val financeRecord: StateFlow<FinanceRecord?> = _financeRecord

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchFinanceData()
    }

    private fun fetchFinanceData() {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseManager.currentUserUId
            if (uid != null) {
                _financeRecord.value = FirebaseManager.getFinanceRecord(uid)
            }
            _isLoading.value = false
        }
    }
}
