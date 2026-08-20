package com.mwema.a2kikao.data

data class AttendanceRequestData(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val type: String = "LEAVE",
    val reason: String = "",
    val details: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val affectedSessionDate: String = "",
    val classId: String = "",
    val status: String = "PENDING",
    val submittedAt: Long = 0
)
