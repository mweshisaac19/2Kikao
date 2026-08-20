package com.mwema.a2kikao.data

import com.mwema.a2kikao.ui.screens.auth.UserRole

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT,
    val registrationNumber: String? = null,
    val employeeNumber: String? = null,
    val school: String? = null,
    val department: String? = null,
    val course: String? = null,
    val className: String? = null,
    val yearOfStudy: String? = null,
    val campus: String? = null,
    val academicTitle: String? = null,
    val classesTaught: String? = null,
    val roleTitle: String? = null,
    val administrativeUnit: String? = null,
    val officePhone: String? = null
)
