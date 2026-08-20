package com.mwema.a2kikao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mwema.a2kikao.ui.screens.student.*
import com.mwema.a2kikao.ui.screens.lecturer.*
import com.mwema.a2kikao.ui.screens.admin.*
import com.mwema.a2kikao.ui.screens.AdminOnboardingScreen
import com.mwema.a2kikao.ui.screens.auth.*
import com.mwema.a2kikao.ui.viewmodels.SplashUiState
import com.mwema.a2kikao.ui.viewmodels.SplashViewModel

@Composable
fun KikaoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen,
        modifier = modifier
    ) {
        // --- AUTH ROUTES ---
        composable<Screen.SplashScreen> {
            val viewModel: SplashViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val uiState by viewModel.uiState.collectAsState()

            KikaoSplashScreen()

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is SplashUiState.Authenticated -> {
                        val route = when (state.profile.role) {
                            UserRole.STUDENT -> Screen.Dashboard
                            UserRole.LECTURER -> Screen.LecturerHome
                            UserRole.ADMIN -> Screen.AdminHome
                        }
                        navController.navigate(route) {
                            popUpTo(Screen.SplashScreen) { inclusive = true }
                        }
                    }
                    is SplashUiState.Unauthenticated -> {
                        navController.navigate(Screen.Login) {
                            popUpTo(Screen.SplashScreen) { inclusive = true }
                        }
                    }
                    else -> {}
                }
            }
        }

        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = { profile ->
                    val route = when (profile.role) {
                        UserRole.STUDENT -> Screen.Dashboard
                        UserRole.LECTURER -> Screen.LecturerHome
                        UserRole.ADMIN -> Screen.AdminHome
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onCreateAccount = { navController.navigate(Screen.SignUp) }
            )
        }

        composable<Screen.SignUp> {
            SignUpScreen(
                onSignUpSuccess = { role ->
                    val route = when (role) {
                        UserRole.STUDENT -> Screen.Dashboard
                        UserRole.LECTURER -> Screen.LecturerHome
                        UserRole.ADMIN -> Screen.AdminHome
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.SignUp) { inclusive = true }
                    }
                }
            )
        }

        // --- STUDENT ROUTES ---
        composable<Screen.Dashboard> {
            StudentDashboardScreen(
                onNotificationClick = { navController.navigate(Screen.Notifications) },
                onScanClick = { navController.navigate(Screen.QrScanner) },
                onViewAllClasses = { navController.navigate(Screen.MyClasses) },
                onClassClick = { classId -> navController.navigate(Screen.ClassDetails(classId)) },
                onFinanceClick = { navController.navigate(Screen.StudentFinanceOverview) },
                onExamSlipClick = { navController.navigate(Screen.ExamSlipSchedule) },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.MyClasses> {
            MyClassesScreen(
                onClassClick = { classId -> navController.navigate(Screen.ClassDetails(classId)) },
                onTimetableClick = { navController.navigate(Screen.Timetable) },
                onNotificationClick = { navController.navigate(Screen.Notifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.PerformanceInsights> {
            PerformanceInsightsScreen(
                onNotificationClick = { navController.navigate(Screen.Notifications) },
                onFeedbackClick = { id -> navController.navigate(Screen.AssessmentFeedbackDetail(id)) },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.Profile> {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.Notifications) },
                onDigitalIDClick = { navController.navigate(Screen.DigitalStudentID) },
                onFinanceClick = { navController.navigate(Screen.StudentFinanceOverview) },
                onFacilityClick = { navController.navigate(Screen.CampusFacilityNavigator) },
                onRegistrationClick = { navController.navigate(Screen.SemesterRegistrationFlow) },
                onSignOut = {
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.Notifications> {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.Timetable> {
            TimetableScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.ClassDetails> { backStackEntry ->
            val route: Screen.ClassDetails = backStackEntry.toRoute()
            com.mwema.a2kikao.ui.screens.student.ClassDetailsScreen(
                classId = route.classId,
                onBackClick = { navController.popBackStack() },
                onLeaveRequest = { navController.navigate(Screen.AttendanceSupport(route.classId)) },
                onAttendanceDispute = { navController.navigate(Screen.AttendanceSupport(route.classId)) },
                onConsultationClick = { id -> navController.navigate(Screen.LecturerConsultationBooking(id)) },
                onNotificationClick = { navController.navigate(Screen.Notifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.QrScanner> {
            QrAttendanceScannerScreen(
                onBackClick = { navController.popBackStack() },
                onQrVerified = { location ->
                    navController.navigate(
                        Screen.LocationVerification(
                            classId = "session_id", // This would be the actual session ID
                            className = location.className,
                            roomName = location.roomName,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            radius = location.allowedRadiusMeters
                        )
                    )
                }
            )
        }

        composable<Screen.LocationVerification> { backStackEntry ->
            val route: Screen.LocationVerification = backStackEntry.toRoute()
            LocationVerificationScreen(
                classLocation = com.mwema.a2kikao.ui.screens.student.ClassLocation(
                    className = route.className,
                    roomName = route.roomName,
                    latitude = route.latitude,
                    longitude = route.longitude,
                    allowedRadiusMeters = route.radius
                ),
                onBackClick = { navController.popBackStack() },
                onLocationVerified = { navController.navigate(Screen.SelfieVerification) }
            )
        }

        composable<Screen.SelfieVerification> {
            SelfieVerificationScreen(
                isSelfieRequired = true,
                onBackClick = { navController.popBackStack() },
                onVerificationComplete = { success ->
                    if (success) {
                        navController.navigate(Screen.VerificationConfirmed)
                    } else {
                        navController.navigate(Screen.VerificationFailed("Face verification failed. Please try again in better lighting."))
                    }
                }
            )
        }

        composable<Screen.VerificationConfirmed> {
            VerificationConfirmedScreen(
                onFinish = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Dashboard) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.VerificationFailed> { backStackEntry ->
            val route: Screen.VerificationFailed = backStackEntry.toRoute()
            VerificationFailedScreen(
                reason = route.reason,
                onTryAgain = { navController.popBackStack() },
                onSupportClick = { navController.navigate(Screen.AttendanceSupport) }
            )
        }

        composable<Screen.AttendanceSupport> { backStackEntry ->
            val route: Screen.AttendanceSupport = backStackEntry.toRoute()
            AttendanceSupportScreen(
                classId = route.classId,
                onBackClick = { navController.popBackStack() },
                onSubmissionComplete = { navController.popBackStack() }
            )
        }

        composable<Screen.StudentFinanceOverview> {
            StudentFinanceOverviewScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.DigitalStudentID> {
            DigitalStudentIDScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.ExamSlipSchedule> {
            ExamSlipScheduleScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.CampusFacilityNavigator> {
            CampusFacilityNavigator(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.LecturerConsultationBooking> { backStackEntry ->
            val route: Screen.LecturerConsultationBooking = backStackEntry.toRoute()
            LecturerConsultationBooking(
                lecturerId = route.lecturerId,
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        StudentTab.HOME -> navController.navigate(Screen.Dashboard)
                        StudentTab.CLASSES -> navController.navigate(Screen.MyClasses)
                        StudentTab.INSIGHTS -> navController.navigate(Screen.PerformanceInsights)
                        StudentTab.PROFILE -> navController.navigate(Screen.Profile)
                    }
                }
            )
        }

        composable<Screen.AssessmentFeedbackDetail> { backStackEntry ->
            val route: Screen.AssessmentFeedbackDetail = backStackEntry.toRoute()
            AssessmentFeedbackDetail(
                assessmentId = route.assessmentId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.SemesterRegistrationFlow> {
            SemesterRegistrationFlow(
                onBackClick = { navController.popBackStack() },
                onRegistrationComplete = { navController.popBackStack() }
            )
        }

        // --- LECTURER ROUTES ---
        composable<Screen.LecturerHome> {
            LecturerHomeScreen(
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onProfileClick = { navController.navigate(Screen.LecturerProfile) },
                onStartAttendance = { code ->
                    navController.navigate(
                        Screen.LecturerLiveAttendance(
                            sessionId = "quick_start_$code",
                            courseCode = code,
                            courseName = "Database Systems",
                            sessionTopic = "Indexing & Optimization",
                            room = "Lab 3",
                            totalStudents = 120
                        )
                    )
                },
                onViewClass = { code -> navController.navigate(Screen.LecturerClassDetails(code)) },
                onViewAllClasses = { navController.navigate(Screen.LecturerMyClasses) },
                onViewStudents = { navController.navigate(Screen.LecturerStudentsList) },
                onTeachingPulseClick = { navController.navigate(Screen.LecturerDepartmentalAnalytics) },
                onTimetableClick = { navController.navigate(Screen.LecturerTimetable) },
                onDisputeClick = { navController.navigate(Screen.LecturerDisputeWorkspace) },
                onExamInvigilationClick = { navController.navigate(Screen.LecturerExamInvigilationDashboard) },
                onCancellationClick = { navController.navigate(Screen.LecturerSessionCancellationLogger) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerMyClasses> {
            com.mwema.a2kikao.ui.screens.lecturer.MyClassesScreen(
                onClassClick = { id -> navController.navigate(Screen.LecturerClassDetails(id)) },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerSessions> {
            SessionsScreen(
                onSessionClick = { id -> navController.navigate(Screen.LecturerSessionDetails(id)) },
                onCreateSession = { navController.navigate(Screen.LecturerCreateSession) },
                onCancelSession = { navController.navigate(Screen.LecturerSessionCancellationLogger) },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerResults> {
            ResultsScreen(
                onAddAssessment = { _ -> navController.navigate(Screen.LecturerNotificationPosting) },
                onViewAnalytics = { code, name ->
                    navController.navigate(Screen.LecturerAttendanceAnalytics(code, name))
                },
                onViewDepartmentalAnalytics = { navController.navigate(Screen.LecturerDepartmentalAnalytics) },
                onViewAppeals = { navController.navigate(Screen.LecturerMarkAppealsInbox) },
                onEnterMarks = { id -> navController.navigate(Screen.LecturerMarkEntry(id)) },
                onAssessmentClick = { id -> navController.navigate(Screen.LecturerAssessmentDetails(id)) },
                onStudentClick = { id -> navController.navigate(Screen.LecturerStudentAssessment(id)) },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerProfile> {
            LecturerProfileScreen(
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onEditProfile = { navController.navigate(Screen.LecturerEditProfile) },
                onConsultationSetup = { navController.navigate(Screen.LecturerConsultationSetup) },
                onExamDashboardClick = { navController.navigate(Screen.LecturerExamInvigilationDashboard) },
                onSystemHealthClick = { navController.navigate(Screen.AdminSystemHealthDashboard) },
                onSignOut = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerClassDetails> { backStackEntry ->
            val route: Screen.LecturerClassDetails = backStackEntry.toRoute()
            com.mwema.a2kikao.ui.screens.lecturer.ClassDetailsScreen(
                courseId = route.courseId,
                onBack = { navController.popBackStack() },
                onViewAnalytics = { code, name ->
                    navController.navigate(Screen.LecturerAttendanceAnalytics(code, name))
                },
                onStudentClick = { id -> navController.navigate(Screen.LecturerStudentAssessment(id)) },
                onSessionClick = { id -> navController.navigate(Screen.LecturerSessionDetails(id)) },
                onViewContent = { id -> navController.navigate(Screen.LecturerCourseContent(id)) },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerSessionDetails> { backStackEntry ->
            val route: Screen.LecturerSessionDetails = backStackEntry.toRoute()
            SessionDetailsScreen(
                sessionId = route.sessionId,
                onBack = { navController.popBackStack() },
                onManageAttendance = { _ ->
                    navController.navigate(
                        Screen.LecturerLiveAttendance(
                            sessionId = route.sessionId,
                            courseCode = "CSC 221",
                            courseName = "Database Systems",
                            sessionTopic = "Live Class",
                            room = "Lab 3",
                            totalStudents = 120
                        )
                    )
                },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerLiveAttendance> { backStackEntry ->
            val route: Screen.LecturerLiveAttendance = backStackEntry.toRoute()
            LiveAttendanceScreen(
                sessionId = route.sessionId,
                courseCode = route.courseCode,
                courseName = route.courseName,
                sessionTopic = route.sessionTopic,
                room = route.room,
                totalStudents = route.totalStudents,
                onEndSession = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.LecturerStudentAssessment> { backStackEntry ->
            val route: Screen.LecturerStudentAssessment = backStackEntry.toRoute()
            StudentAssessmentScreen(
                studentId = route.studentId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerNotificationPosting> {
            LecturerNotificationPostingScreen(
                onBack = { navController.popBackStack() },
                onPostComplete = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerAttendanceAnalytics> { backStackEntry ->
            val route: Screen.LecturerAttendanceAnalytics = backStackEntry.toRoute()
            AttendanceAnalyticsScreen(
                className = route.className,
                classCode = route.classCode,
                onBackClick = { navController.popBackStack() },
                onStudentClick = { id -> navController.navigate(Screen.LecturerStudentAssessment(id)) }
            )
        }

        composable<Screen.LecturerNotifications> {
            LecturerNotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerStudentsList> {
            StudentsScreen(
                onStudentClick = { id -> navController.navigate(Screen.LecturerStudentAssessment(id)) },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerAssessmentDetails> { backStackEntry ->
            val route: Screen.LecturerAssessmentDetails = backStackEntry.toRoute()
            AssessmentDetailsScreen(
                assessmentId = route.assessmentId,
                onBackClick = { navController.popBackStack() },
                onStudentClick = { id -> navController.navigate(Screen.LecturerStudentAssessment(id)) },
                onNotificationClick = { navController.navigate(Screen.LecturerNotifications) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerEditProfile> {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
            )
        }

        composable<Screen.LecturerMarkEntry> { backStackEntry ->
            val route: Screen.LecturerMarkEntry = backStackEntry.toRoute()
            LecturerMarkEntryScreen(
                assessmentId = route.assessmentId,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable<Screen.LecturerCourseContent> { backStackEntry ->
            val route: Screen.LecturerCourseContent = backStackEntry.toRoute()
            LecturerCourseContentScreen(
                courseId = route.courseId,
                onBack = { navController.popBackStack() },
                onUpload = { /* Logic */ }
            )
        }

        composable<Screen.LecturerDisputeWorkspace> {
            LecturerDisputeWorkspaceScreen(
                onBack = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerTimetable> {
            LecturerTimetableScreen(
                onBack = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerCreateSession> {
            CreateSessionScreen(
                onBack = { navController.popBackStack() },
                onSessionCreated = { sessionId, code, name, topic, room, students ->
                    navController.navigate(
                        Screen.LecturerLiveAttendance(
                            sessionId = sessionId,
                            courseCode = code,
                            courseName = name,
                            sessionTopic = topic,
                            room = room,
                            totalStudents = students
                        )
                    )
                }
            )
        }

        composable<Screen.LecturerConsultationSetup> {
            LecturerConsultationSetupScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerMarkAppealsInbox> {
            LecturerMarkAppealsInboxScreen(
                onBack = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerSessionCancellationLogger> {
            LecturerSessionCancellationLoggerScreen(
                onBack = { navController.popBackStack() },
                onCancellationComplete = { navController.popBackStack() },
                onViewTimetable = { navController.navigate(Screen.LecturerTimetable) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        composable<Screen.LecturerExamInvigilationDashboard> {
            LecturerExamInvigilationDashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.LecturerDepartmentalAnalytics> {
            LecturerDepartmentalAnalyticsScreen(
                onBack = { navController.popBackStack() },
                onCourseClick = { id -> navController.navigate(Screen.LecturerClassDetails(id)) },
                onTabSelected = { tab ->
                    when (tab) {
                        LecturerTab.HOME -> navController.navigate(Screen.LecturerHome)
                        LecturerTab.CLASSES -> navController.navigate(Screen.LecturerMyClasses)
                        LecturerTab.SESSIONS -> navController.navigate(Screen.LecturerSessions)
                        LecturerTab.RESULTS -> navController.navigate(Screen.LecturerResults)
                        LecturerTab.PROFILE -> navController.navigate(Screen.LecturerProfile)
                        LecturerTab.STUDENTS -> navController.navigate(Screen.LecturerStudentsList)
                    }
                }
            )
        }

        // --- ADMIN ROUTES ---
        composable<Screen.AdminHome> {
            AdminHomeScreen(
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onStudentsClick = { navController.navigate(Screen.AdminStudents) },
                onLecturersClick = { navController.navigate(Screen.AdminLecturers) },
                onCoursesClick = { navController.navigate(Screen.AdminCourses) },
                onAnalyticsClick = { navController.navigate(Screen.AdminReports) },
                onAttendanceClick = { navController.navigate(Screen.AdminAttendancePerformance) },
                onPerformanceClick = { navController.navigate(Screen.AdminPerformanceAnalytics) },
                onAtRiskStudentsClick = { navController.navigate(Screen.AdminAtRiskStudents) },
                onReportsClick = { navController.navigate(Screen.AdminReports) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminCourses> {
            AdminCoursesScreen(
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onCourseClick = { id -> navController.navigate(Screen.AdminCourseDetails(id)) },
                onAddCourse = { navController.navigate(Screen.AdminManagementForms) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminClasses> {
            AdminClassesScreen(
                onClassClick = { id -> navController.navigate(Screen.AdminClassDetails(id)) },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminStudents> {
            AdminStudentsScreen(
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onStudentClick = { id -> navController.navigate(Screen.AdminStudentDetails(id)) },
                onAddStudent = { navController.navigate(Screen.AdminManagementForms) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminLecturers> {
            AdminLecturersScreen(
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onLecturerSelected = { id -> navController.navigate(Screen.AdminLecturerDetails(id)) },
                onAddLecturer = { navController.navigate(Screen.AdminManagementForms) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminReports> {
            AdminReportsScreen(
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onGenerateReport = { title -> navController.navigate(Screen.AdminDetailedReport(title, "Generated Report")) },
                onOpenReport = { title -> navController.navigate(Screen.AdminDetailedReport(title, "Institutional Report")) },
                onBack = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminProfile> {
            AdminProfileScreen(
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onEditProfile = { navController.navigate(Screen.AdminManagementForms) },
                onSystemSettings = { navController.navigate(Screen.AdminSettings) },
                onManageUsers = { navController.navigate(Screen.AdminUserManagement) },
                onSecurity = { navController.navigate(Screen.AdminSecurityAudit) },
                onAuditLogs = { navController.navigate(Screen.AdminSecurityAudit) },
                onSystemHealthClick = { navController.navigate(Screen.AdminSystemHealthDashboard) },
                onInfrastructureClick = { navController.navigate(Screen.AdminCampusMapAssetRegistry) },
                onSignOut = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminSettings> {
            AdminSettingsScreen(
                onBack = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onAcademicCalendarClick = { navController.navigate(Screen.AdminAcademicCalendar) },
                onAuditExportClick = { navController.navigate(Screen.AdminAuditDataExport) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminNotifications> {
            AdminNotificationsScreen(
                onBack = { navController.popBackStack() },
                onNotificationSelected = { notification -> /* Logic for notification detail */ }
            )
        }

        composable<Screen.AdminCourseDetails> { backStackEntry ->
            val route: Screen.AdminCourseDetails = backStackEntry.toRoute()
            AdminCourseDetailsScreen(
                courseId = route.courseId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onEditCourse = { navController.navigate(Screen.AdminManagementForms) },
                onStudentClick = { id -> navController.navigate(Screen.AdminStudentDetails(id)) },
                onLecturerClick = { id -> navController.navigate(Screen.AdminLecturerDetails(id)) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminStudentDetails> { backStackEntry ->
            val route: Screen.AdminStudentDetails = backStackEntry.toRoute()
            AdminStudentDetailsScreen(
                studentId = route.studentId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onEditStudent = { navController.navigate(Screen.AdminManagementForms) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminLecturerDetails> { backStackEntry ->
            val route: Screen.AdminLecturerDetails = backStackEntry.toRoute()
            AdminLecturerDetailsScreen(
                lecturerId = route.lecturerId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onEditLecturer = { navController.navigate(Screen.AdminManagementForms) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminDepartmentDetails> { backStackEntry ->
            val route: Screen.AdminDepartmentDetails = backStackEntry.toRoute()
            AdminDepartmentDetailsScreen(
                departmentId = route.departmentId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onViewStudents = { navController.navigate(Screen.AdminStudents) },
                onViewCourses = { navController.navigate(Screen.AdminCourses) },
                onViewLecturers = { navController.navigate(Screen.AdminLecturers) },
                onViewClasses = { navController.navigate(Screen.AdminClasses) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminAttendanceAnalytics> {
            AdminAttendanceAnalyticsScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onDepartmentClick = { id -> navController.navigate(Screen.AdminDepartmentDetails(id)) },
                onStudentRiskClick = { navController.navigate(Screen.AdminAtRiskStudents) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminPerformanceAnalytics> {
            AdminPerformanceAnalyticsScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onDepartmentClick = { id -> navController.navigate(Screen.AdminDepartmentDetails(id)) },
                onAtRiskStudentsClick = { navController.navigate(Screen.AdminAtRiskStudents) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminAttendancePerformance> {
            AdminAttendancePerformanceScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminAtRiskStudents> {
            AdminAtRiskStudentsScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onStudentClick = { id -> navController.navigate(Screen.AdminStudentDetails(id)) },
                onInterventionClick = { id -> navController.navigate(Screen.AdminInterventionCreator(id)) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminDepartments> {
            AdminDepartmentsScreen(
                onDepartmentClick = { id -> navController.navigate(Screen.AdminDepartmentDetails(id)) },
                onAddDepartment = { navController.navigate(Screen.AdminManagementForms) },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminManagementForms> {
            AdminManagementFormsScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onStudentSaved = { navController.popBackStack() },
                onCourseSaved = { navController.popBackStack() },
                onDepartmentSaved = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminUserManagement> {
            AdminUserManagementScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onAddAdmin = { navController.navigate(Screen.AdminOnboarding) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminSecurityAudit> {
            AdminSecurityAuditScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminDetailedReport> { backStackEntry ->
            val route: Screen.AdminDetailedReport = backStackEntry.toRoute()
            AdminDetailedReportScreen(
                reportTitle = route.reportTitle,
                reportType = route.reportType,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminClassDetails> { backStackEntry ->
            val route: Screen.AdminClassDetails = backStackEntry.toRoute()
            AdminClassDetailsScreen(
                classId = route.classId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onStudentClick = { id -> navController.navigate(Screen.AdminStudentDetails(id)) },
                onEditClass = { navController.navigate(Screen.AdminManagementForms) },
                onManageLecturer = { navController.navigate(Screen.AdminLecturers) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminAcademicCalendar> {
            AdminAcademicCalendarScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminAuditDataExport> {
            AdminAuditDataExportScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminInterventionCreator> { backStackEntry ->
            val route: Screen.AdminInterventionCreator = backStackEntry.toRoute()
            AdminInterventionCreatorScreen(
                studentName = "Amani Mwangi", // Should be fetched from repo via route.studentId
                registrationNumber = route.studentId,
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onSave = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminOnboarding> {
            AdminOnboardingScreen(
                onBackClick = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminBulkImportWorkspace> {
            AdminBulkImportWorkspace(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onImportComplete = { navController.popBackStack() },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminFinanceFeesOverview> {
            AdminFinanceFeesOverviewScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminCampusMapAssetRegistry> {
            AdminCampusMapAssetRegistryScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }

        composable<Screen.AdminSystemHealthDashboard> {
            AdminSystemHealthDashboardScreen(
                onBack = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.AdminNotifications) },
                onProfileClick = { navController.navigate(Screen.AdminProfile) },
                onTabSelected = { tab ->
                    when (tab) {
                        AdminTab.HOME -> navController.navigate(Screen.AdminHome)
                        AdminTab.ACADEMICS -> navController.navigate(Screen.AdminCourses)
                        AdminTab.USERS -> navController.navigate(Screen.AdminStudents)
                        AdminTab.ANALYTICS -> navController.navigate(Screen.AdminReports)
                        AdminTab.PROFILE -> navController.navigate(Screen.AdminProfile)
                    }
                }
            )
        }
    }
}
