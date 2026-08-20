package com.mwema.a2kikao.data

data class FinanceRecord(
    val studentId: String = "",
    val currentBalance: Double = 0.0,
    val totalBilled: Double = 0.0,
    val totalPaid: Double = 0.0,
    val lastPaymentDate: String = "",
    val payments: List<PaymentItem> = emptyList()
)

data class PaymentItem(
    val id: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val method: String = "",
    val description: String = ""
)
