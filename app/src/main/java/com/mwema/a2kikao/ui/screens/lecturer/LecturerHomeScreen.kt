package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerHomeViewModel

private data class UpcomingClass(
    val code: String,
    val name: String,
    val time: String,
    val room: String,
    val students: Int,
    val duration: String,
    val accent: Color
)

@Composable
fun LecturerHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStartAttendance: (courseCode: String) -> Unit = {},
    onViewClass: (courseCode: String) -> Unit = {},
    onViewAllClasses: () -> Unit = {},
    onViewStudents: () -> Unit = {},
    onTeachingPulseClick: () -> Unit = {},
    onTimetableClick: () -> Unit = {},
    onDisputeClick: () -> Unit = {},
    onExamInvigilationClick: () -> Unit = {},
    onCancellationClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val lecturerClasses by viewModel.lecturerClasses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lecturerName = userProfile?.fullName ?: "Lecturer"
    
    val mappedClasses = lecturerClasses.mapIndexed { index, course ->
        UpcomingClass(
            code = course.code,
            name = course.name,
            time = course.time.substringBefore("-").trim(),
            room = course.room,
            students = course.studentsEnrolled.size,
            duration = "2 hrs",
            accent = when (index % 3) {
                0 -> KikaoColors.Teal
                1 -> KikaoColors.Gold
                else -> Color(0xFF8B5CF6)
            }
        )
    }

    val upcomingClasses = if (mappedClasses.isNotEmpty()) mappedClasses else listOf(
        UpcomingClass("CSC 221", "Database Systems", "10:00 AM", "Lab 3", 120, "2 hrs", KikaoColors.Teal),
        UpcomingClass("CSC 210", "Data Structures", "1:00 PM", "Room B14", 96, "1.5 hrs", KikaoColors.Gold)
    )

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.HOME,
        screenTitle = "Command center",
        screenSubtitle = "Your teaching day at a glance",
        lecturerName = lecturerName,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KikaoColors.Teal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .padding(bottom = 30.dp)
            ) {
                WelcomeCard(lecturerName = lecturerName, classCount = upcomingClasses.size)
                Spacer(modifier = Modifier.height(18.dp))
                DailyStats(classCount = upcomingClasses.size, studentCount = upcomingClasses.sumOf { it.students })
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Upcoming classes", action = "View all", onActionClick = onViewAllClasses)
                Spacer(modifier = Modifier.height(12.dp))
                upcomingClasses.forEachIndexed { index, course ->
                    UpcomingClassCard(
                        course = course,
                        isNext = index == 0,
                        onStartAttendance = { onStartAttendance(course.code) },
                        onViewClass = { onViewClass(course.code) }
                    )
                    if (index != upcomingClasses.lastIndex) Spacer(modifier = Modifier.height(13.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Teaching pulse", action = "View analytics", onActionClick = onTeachingPulseClick)
                Spacer(modifier = Modifier.height(12.dp))
                TeachingPulseCard(onClick = onTeachingPulseClick)
                Spacer(modifier = Modifier.height(14.dp))
                AttentionCard(onViewStudents = onViewStudents)
                Spacer(modifier = Modifier.height(24.dp))
                QuickActions(onTimetableClick, onViewStudents, onDisputeClick, onExamInvigilationClick, onCancellationClick)
            }
        }
    }
}

@Composable
private fun WelcomeCard(lecturerName: String, classCount: Int) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("TUESDAY • 18 AUGUST", color = KikaoColors.Gold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(7.dp))
            Text("Good afternoon, ${lecturerName.substringBefore(" ")}", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(5.dp))
            Text("You have $classCount classes scheduled today.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun DailyStats(classCount: Int, studentCount: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DailyStatCard(value = classCount.toString(), label = "Classes", modifier = Modifier.weight(1f))
        DailyStatCard(value = studentCount.toString(), label = "Students", modifier = Modifier.weight(1f))
        DailyStatCard(value = "87%", label = "Attendance", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DailyStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(13.dp)) {
            Text(value, color = KikaoColors.Indigo, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = KikaoColors.MutedText, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onActionClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action != null) Text(action, color = KikaoColors.Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onActionClick))
    }
}

@Composable
private fun UpcomingClassCard(course: UpcomingClass, isNext: Boolean, onStartAttendance: () -> Unit, onViewClass: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onViewClass), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(course.accent.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Text("▦", color = course.accent, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(course.code, color = course.accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    Text(course.name, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                if (isNext) Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(KikaoColors.Gold).padding(horizontal = 7.dp, vertical = 4.dp)) {
                    Text("NEXT", color = KikaoColors.DeepIndigo, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(13.dp))
            Button(onClick = onStartAttendance, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)) {
                Text("Start Attendance", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TeachingPulseCard(onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text("Teaching pulse trend looks strong.", color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AttentionCard(onViewStudents: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E8))) {
        Row(modifier = Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Some students need attention", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("View ›", modifier = Modifier.clickable(onClick = onViewStudents), color = KikaoColors.Teal)
        }
    }
}

@Composable
private fun QuickActions(onTimetable: () -> Unit, onViewStudents: () -> Unit, onDisputes: () -> Unit, onExams: () -> Unit, onCancel: () -> Unit) {
    Column {
        Text("Quick actions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onTimetable, modifier = Modifier.weight(1f)) { Text("Timetable", fontSize = 10.sp) }
            Button(onClick = onViewStudents, modifier = Modifier.weight(1f)) { Text("Students", fontSize = 10.sp) }
            Button(onClick = onDisputes, modifier = Modifier.weight(1f)) { Text("Disputes", fontSize = 10.sp) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onExams, modifier = Modifier.weight(1f)) { Text("Exams", fontSize = 10.sp) }
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel", fontSize = 10.sp) }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerHomeScreenPreview() {
    MaterialTheme {
        LecturerHomeScreen()
    }
}
