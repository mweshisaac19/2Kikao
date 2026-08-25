package com.mwema.a2kikao.data

data class CourseClass(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val lecturer: String = "",
    val lecturerId: String = "",
    val time: String = "",
    val room: String = "",
    val day: String = "", // Legacy field for single day support
    val days: List<String> = emptyList(), // e.g., ["Monday", "Wednesday"] or ["Daily"]
    val targetCourse: String = "", // e.g., "BSc Computer Science"
    val studentsEnrolled: List<String> = emptyList()
)
