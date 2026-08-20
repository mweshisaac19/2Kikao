package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private enum class AdminStudentSection {
    OVERVIEW,
    PERFORMANCE,
    ATTENDANCE,
    ACADEMIC
}

data class AdminStudentRecord(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val email: String,
    val phone: String,
    val programme: String,
    val department: String,
    val year: String,
    val campus: String,
    val status: String,
    val overallGrade: Int,
    val attendance: Int,
    val classPosition: Int,
    val classSize: Int
)

private data class StudentCoursePerformance(
    val code: String,
    val name: String,
    val grade: Int,
    val attendance: Int,
    val lecturer: String,
    val assessmentCount: Int,
    val trend: List<Int>,
    val accent: Color
)

private data class AttendanceMonth(
    val month: String,
    val percentage: Int
)

@Composable
fun AdminStudentDetailsScreen(
    studentId: String,
    modifier: Modifier = Modifier,
    student: AdminStudentRecord = demoAdminStudent(studentId),
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onEditStudent: () -> Unit = {},
    onContactStudent: () -> Unit = {},
    onViewAttendance: () -> Unit = {},
    onViewAcademicRecord: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedSection by remember { mutableStateOf(AdminStudentSection.OVERVIEW) }
    val courses = remember { demoStudentCoursePerformance() }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.USERS,
        screenTitle = "Student details",
        screenSubtitle = student.registrationNumber,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            AdminStudentSubHeader(
                student = student,
                onBack = onBack
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 15.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEAF0F8))
                    .padding(4.dp)
            ) {
                AdminStudentTab(
                    text = "Overview",
                    selected = selectedSection == AdminStudentSection.OVERVIEW,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedSection = AdminStudentSection.OVERVIEW }
                )
                AdminStudentTab(
                    text = "Grades",
                    selected = selectedSection == AdminStudentSection.PERFORMANCE,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedSection = AdminStudentSection.PERFORMANCE }
                )
                AdminStudentTab(
                    text = "Presence",
                    selected = selectedSection == AdminStudentSection.ATTENDANCE,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedSection = AdminStudentSection.ATTENDANCE }
                )
                AdminStudentTab(
                    text = "Record",
                    selected = selectedSection == AdminStudentSection.ACADEMIC,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedSection = AdminStudentSection.ACADEMIC }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 110.dp)
            ) {
                when (selectedSection) {
                    AdminStudentSection.OVERVIEW -> StudentOverviewSection(student, courses, onEditStudent, onContactStudent)
                    AdminStudentSection.PERFORMANCE -> StudentPerformanceSection(student, courses)
                    AdminStudentSection.ATTENDANCE -> StudentAttendanceSection(student, courses, onViewAttendance)
                    AdminStudentSection.ACADEMIC -> StudentAcademicSection(student, courses, onViewAcademicRecord)
                }
            }
        }
    }
}

@Composable
private fun AdminStudentSubHeader(
    student: AdminStudentRecord,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KikaoColors.Indigo)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.15f))
            ) {
                Text("‹", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(KikaoColors.Teal),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials(student.name), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = student.registrationNumber, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(KikaoColors.Gold).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = student.status, color = KikaoColors.DeepIndigo, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun AdminStudentTab(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) KikaoColors.Indigo else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = if (selected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StudentOverviewSection(
    student: AdminStudentRecord,
    courses: List<StudentCoursePerformance>,
    onEditStudent: () -> Unit,
    onContactStudent: () -> Unit
) {
    Column {
        StudentQuickStats(student)
        Spacer(modifier = Modifier.height(15.dp))
        StudentRiskCard(student)
        Spacer(modifier = Modifier.height(15.dp))
        StudentIdentityCard(student)
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onEditStudent, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)) {
                Text("Edit profile", fontSize = 12.sp)
            }
            OutlinedButton(onClick = onContactStudent, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) {
                Text("Contact", fontSize = 12.sp, color = KikaoColors.Indigo)
            }
        }
    }
}

@Composable
private fun StudentQuickStats(student: AdminStudentRecord) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StudentStatCard("Average", "${student.overallGrade}%", "Current", KikaoColors.Teal, Modifier.weight(1f))
        StudentStatCard("Attendance", "${student.attendance}%", "Verified", KikaoColors.Gold, Modifier.weight(1f))
        StudentStatCard("Position", "#${student.classPosition}", "of ${student.classSize}", Color(0xFF8B5CF6), Modifier.weight(1f))
    }
}

@Composable
private fun StudentStatCard(title: String, value: String, subtitle: String, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(13.dp)) {
            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(accent))
            Spacer(modifier = Modifier.height(9.dp))
            Text(text = title, color = KikaoColors.MutedText, fontSize = 9.sp)
            Text(text = value, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 9.sp)
        }
    }
}

@Composable
private fun StudentRiskCard(student: AdminStudentRecord) {
    val risk = if (student.overallGrade < 60) "WATCH" else "ON TRACK"
    val riskColor = if (risk == "WATCH") Color(0xFF9A6700) else KikaoColors.Teal
    val background = if (risk == "WATCH") Color(0xFFFFF5D6) else KikaoColors.TealLight

    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = background)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(riskColor), contentAlignment = Alignment.Center) {
                Text(text = if (risk == "WATCH") "!" else "✓", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Institutional risk status", color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = if (risk == "WATCH") "Academic indicators require monitoring." else "Performance is currently healthy.", color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Text(text = risk, color = riskColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun StudentIdentityCard(student: AdminStudentRecord) {
    Card(shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(text = "Academic Information", color = KikaoColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            IdentityRow("Programme", student.programme)
            IdentityRow("Department", student.department)
            IdentityRow("Email", student.email, false)
        }
    }
}

@Composable
private fun IdentityRow(label: String, value: String, showDivider: Boolean = true) {
    Column {
        Text(text = label, color = KikaoColors.MutedText, fontSize = 9.sp)
        Text(text = value, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        if (showDivider) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StudentPerformanceSection(student: AdminStudentRecord, courses: List<StudentCoursePerformance>) {
    Column {
        Text(text = "Course Performance", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        courses.forEach { course ->
            CoursePerformanceRow(course)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CoursePerformanceRow(course: StudentCoursePerformance) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.name, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "${course.code} · ${course.lecturer}", color = KikaoColors.MutedText, fontSize = 10.sp)
            }
            Text(text = "${course.grade}%", color = course.accent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun StudentAttendanceSection(student: AdminStudentRecord, courses: List<StudentCoursePerformance>, onViewAttendance: () -> Unit) {
    Column {
        Text(text = "Attendance overview", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${student.attendance}%", color = KikaoColors.Teal, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Verified semester presence", color = KikaoColors.MutedText, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StudentAcademicSection(student: AdminStudentRecord, courses: List<StudentCoursePerformance>, onViewAcademicRecord: () -> Unit) {
    Column {
        Text(text = "Institutional Record", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Full academic history and official transcripts are available for download.", color = KikaoColors.MutedText, fontSize = 13.sp)
    }
}

private fun initials(name: String): String = name.split(" ").take(2).map { it.firstOrNull() ?: "" }.joinToString("").uppercase()

private fun demoAdminStudent(id: String) = AdminStudentRecord(
    id = id, name = "Amani Mwangi", registrationNumber = "SC211/1234/2025", email = "amani@university.ac.ke", phone = "0712345678",
    programme = "BSc Computer Science", department = "Computer Science", year = "Year 2", campus = "Main Campus",
    status = "Active", overallGrade = 74, attendance = 87, classPosition = 24, classSize = 120
)

private fun demoStudentCoursePerformance() = listOf(
    StudentCoursePerformance("CSC 210", "Data Structures", 78, 92, "Dr. Kamau", 3, listOf(65, 71, 78), KikaoColors.Teal),
    StudentCoursePerformance("CSC 221", "Database Systems", 72, 87, "Prof. Otieno", 4, listOf(58, 67, 72), KikaoColors.Gold)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminStudentDetailsPreview() {
    MaterialTheme {
        AdminStudentDetailsScreen(studentId = "STU001")
    }
}
