package com.mwema.a2kikao.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object SplashScreen : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object SignUp : Screen

    @Serializable
    data object Dashboard : Screen

    @Serializable
    data object MyClasses : Screen

    @Serializable
    data object PerformanceInsights : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Notifications : Screen

    @Serializable
    data object Timetable : Screen

    @Serializable
    data class ClassDetails(val classId: String) : Screen

    @Serializable
    data object QrScanner : Screen

    @Serializable
    data class LocationVerification(
        val classId: String,
        val className: String,
        val roomName: String,
        val latitude: Double,
        val longitude: Double,
        val radius: Float
    ) : Screen

    @Serializable
    data class SelfieVerification(val sessionId: String, val isRequired: Boolean = true) : Screen

    @Serializable
    data class VerificationConfirmed(
        val sessionId: String,
        val className: String,
        val classCode: String,
        val roomName: String
    ) : Screen

    @Serializable
    data class VerificationFailed(val reason: String) : Screen

    @Serializable
    data class AttendanceSupport(val classId: String? = null) : Screen

    @Serializable
    data object StudentFinanceOverview : Screen

    @Serializable
    data object DigitalStudentID : Screen

    @Serializable
    data object ExamSlipSchedule : Screen

    @Serializable
    data object CampusFacilityNavigator : Screen

    @Serializable
    data class LecturerConsultationBooking(val lecturerId: String) : Screen

    @Serializable
    data class AssessmentFeedbackDetail(val assessmentId: String) : Screen

    @Serializable
    data object SemesterRegistrationFlow : Screen

    // --- LECTURER SCREENS ---
    @Serializable
    data object LecturerHome : Screen

    @Serializable
    data object LecturerMyClasses : Screen

    @Serializable
    data object LecturerSessions : Screen

    @Serializable
    data object LecturerResults : Screen

    @Serializable
    data object LecturerProfile : Screen

    @Serializable
    data class LecturerClassDetails(val courseId: String) : Screen

    @Serializable
    data class LecturerSessionDetails(val sessionId: String) : Screen

    @Serializable
    data class LecturerLiveAttendance(
        val sessionId: String,
        val courseCode: String,
        val courseName: String,
        val sessionTopic: String,
        val room: String,
        val totalStudents: Int
    ) : Screen

    @Serializable
    data class LecturerAttendanceAnalytics(
        val classCode: String,
        val className: String
    ) : Screen

    @Serializable
    data object LecturerNotificationPosting : Screen

    @Serializable
    data class LecturerStudentAssessment(val studentId: String) : Screen

    @Serializable
    data object LecturerNotifications : Screen

    @Serializable
    data object LecturerStudentsList : Screen

    @Serializable
    data class LecturerAssessmentDetails(val assessmentId: String) : Screen

    @Serializable
    data class LecturerMarkEntry(val assessmentId: String) : Screen

    @Serializable
    data class LecturerCourseContent(val courseId: String) : Screen

    @Serializable
    data object LecturerDisputeWorkspace : Screen

    @Serializable
    data object LecturerTimetable : Screen

    @Serializable
    data object LecturerConsultationSetup : Screen

    @Serializable
    data object LecturerMarkAppealsInbox : Screen

    @Serializable
    data object LecturerSessionCancellationLogger : Screen

    @Serializable
    data object LecturerExamInvigilationDashboard : Screen

    @Serializable
    data object LecturerDepartmentalAnalytics : Screen

    @Serializable
    data object LecturerEditProfile : Screen

    @Serializable
    data class LecturerCreateSession(val initialCourseCode: String? = null) : Screen

    @Serializable
    data class LecturerAddClass(val classId: String? = null) : Screen

    // --- ADMIN SCREENS ---
    @Serializable
    data object AdminHome : Screen

    @Serializable
    data object AdminClasses : Screen

    @Serializable
    data class AdminClassDetails(val classId: String) : Screen

    @Serializable
    data object AdminCourses : Screen

    @Serializable
    data class AdminCourseDetails(val courseId: String) : Screen

    @Serializable
    data object AdminDepartments : Screen

    @Serializable
    data class AdminDepartmentDetails(val departmentId: String) : Screen

    @Serializable
    data object AdminStudents : Screen

    @Serializable
    data class AdminStudentDetails(val studentId: String) : Screen

    @Serializable
    data object AdminLecturers : Screen

    @Serializable
    data class AdminLecturerDetails(val lecturerId: String) : Screen

    @Serializable
    data object AdminAttendanceAnalytics : Screen

    @Serializable
    data object AdminPerformanceAnalytics : Screen

    @Serializable
    data object AdminAttendancePerformance : Screen

    @Serializable
    data object AdminAtRiskStudents : Screen

    @Serializable
    data object AdminNotifications : Screen

    @Serializable
    data object AdminReports : Screen

    @Serializable
    data class AdminDetailedReport(val reportTitle: String, val reportType: String) : Screen

    @Serializable
    data object AdminProfile : Screen

    @Serializable
    data object AdminSettings : Screen

    @Serializable
    data object AdminManagementForms : Screen

    @Serializable
    data object AdminUserManagement : Screen

    @Serializable
    data object AdminSecurityAudit : Screen

    @Serializable
    data object AdminAcademicCalendar : Screen

    @Serializable
    data object AdminAuditDataExport : Screen

    @Serializable
    data class AdminInterventionCreator(val studentId: String) : Screen

    @Serializable
    data object AdminOnboarding : Screen

    @Serializable
    data object AdminBulkImportWorkspace : Screen

    @Serializable
    data object AdminFinanceFeesOverview : Screen

    @Serializable
    data object AdminCampusMapAssetRegistry : Screen

    @Serializable
    data object AdminSystemHealthDashboard : Screen
}
