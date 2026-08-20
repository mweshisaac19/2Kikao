package com.mwema.a2kikao.data

data class KikaoNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val sender: String = "",
    val timestamp: Long = 0,
    val type: String = "general", // "general", "assignment", "grade", "alert"
    val classId: String? = null
)
