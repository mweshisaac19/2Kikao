package com.mwema.a2kikao.data

data class CourseClass(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val lecturer: String = "",
    val lecturerId: String = "",
    val time: String = "",
    val room: String = "",
    val day: String = "", // e.g., "Monday"
    val studentsEnrolled: List<String> = emptyList()
)
