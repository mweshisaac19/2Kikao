package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

// ------------------------------------------------------------
// DATA
// ------------------------------------------------------------

private data class AdminCourseDetails(
    val id: String,
    val code: String,
    val name: String,
    val department: String,
    val faculty: String,
    val lecturer: String,
    val lecturerInitials: String,
    val creditHours: Int,
    val semester: String,
    val year: Int,
    val description: String,
    val totalStudents: Int,
    val avgAttendance: Int,
    val passingRate: Int
)

private data class AdminStudentSummary(
    val id: String,
    val name: String,
    val regNo: String,
    val attendance: Int,
    val grade: Int,
    val status: StudentAdminStatus
)

private enum class CourseDetailTab {
    OVERVIEW,
    STUDENTS,
    SESSIONS
}

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCourseDetailsScreen(
    courseId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onEditCourse: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onLecturerClick: (String) -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(CourseDetailTab.OVERVIEW) }
    val course = remember { demoCourseDetails(courseId) }
    val students = remember { demoCourseStudents() }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Course details",
        screenSubtitle = "${course.code} · ${course.name}",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            
            // Back navigation
            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 12.dp, top = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp), tint = KikaoColors.Teal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back to courses", color = KikaoColors.Teal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Header Card
            CourseHeaderCard(course, onEditCourse)

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Navigation
            CourseDetailTabs(selectedTab) { selectedTab = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTab) {
                CourseDetailTab.OVERVIEW -> CourseOverviewContent(course, onLecturerClick)
                CourseDetailTab.STUDENTS -> CourseStudentsContent(students, onStudentClick)
                CourseDetailTab.SESSIONS -> CourseSessionsContent()
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun CourseHeaderCard(course: AdminCourseDetails, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.code, color = KikaoColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = course.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${course.department} · ${course.faculty}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                
                IconButton(onClick = onEdit, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.12f))) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(22.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HeaderMetric(label = "Students", value = "${course.totalStudents}")
                HeaderMetric(label = "Attendance", value = "${course.avgAttendance}%")
                HeaderMetric(label = "Passing", value = "${course.passingRate}%")
            }
        }
    }
}

@Composable
private fun HeaderMetric(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        Text(text = value, color = KikaoColors.Gold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun CourseDetailTabs(selected: CourseDetailTab, onSelected: (CourseDetailTab) -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(Color(0xFFEAF0F8)).padding(4.dp)
    ) {
        CourseDetailTabItem("Overview", selected == CourseDetailTab.OVERVIEW, { onSelected(CourseDetailTab.OVERVIEW) }, Modifier.weight(1f))
        CourseDetailTabItem("Students", selected == CourseDetailTab.STUDENTS, { onSelected(CourseDetailTab.STUDENTS) }, Modifier.weight(1f))
        CourseDetailTabItem("Sessions", selected == CourseDetailTab.SESSIONS, { onSelected(CourseDetailTab.SESSIONS) }, Modifier.weight(1f))
    }
}

@Composable
private fun CourseDetailTabItem(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(if (isSelected) KikaoColors.Indigo else Color.Transparent).clickable(onClick = onClick).padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = if (isSelected) Color.White else KikaoColors.MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CourseOverviewContent(course: AdminCourseDetails, onLecturerClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        
        SectionTitle("Academic Delivery")
        LecturerSmallCard(course.lecturer, course.lecturerInitials, "Primary Lecturer", onLecturerClick)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionTitle("Course Description")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Text(
                text = course.description,
                color = KikaoColors.MutedText,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SectionTitle("Quick Stats")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatBox("Credit Hours", "${course.creditHours}", KikaoColors.Teal, Modifier.weight(1f))
            StatBox("Term", "Sem ${course.semester}", KikaoColors.Indigo, Modifier.weight(1f))
            StatBox("Year", "${course.year}", KikaoColors.Gold, Modifier.weight(1f))
        }
    }
}

@Composable
private fun LecturerSmallCard(name: String, initials: String, role: String, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick("lec_1") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(KikaoColors.TealLight), contentAlignment = Alignment.Center) {
                Text(text = initials, color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = role, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KikaoColors.MutedText)
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, color = KikaoColors.MutedText, fontSize = 9.sp)
            Text(text = value, color = accent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CourseStudentsContent(students: List<AdminStudentSummary>, onStudentClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SectionTitle("Enrolled Students")
        students.forEach { student ->
            StudentSummaryCard(student, onStudentClick)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun StudentSummaryCard(student: AdminStudentSummary, onClick: (String) -> Unit) {
    val statusColor = when (student.status) {
        StudentAdminStatus.ACTIVE -> KikaoColors.Teal
        StudentAdminStatus.AT_RISK -> KikaoColors.Gold
        StudentAdminStatus.PROBATION -> Color(0xFFDC3545)
        StudentAdminStatus.SUSPENDED -> Color.Gray
    }

    Card(
        onClick = { onClick(student.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = student.regNo, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${student.attendance}%", color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "Attendance", color = KikaoColors.MutedText, fontSize = 9.sp)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KikaoColors.MutedText, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun CourseSessionsContent() {
    Column(modifier = Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp))
        Icon(Icons.Default.EventNote, contentDescription = null, tint = KikaoColors.Indigo.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Session history is available for this course.", color = KikaoColors.Ink, fontWeight = FontWeight.Bold)
        Text("16 sessions have been conducted this semester.", color = KikaoColors.MutedText, fontSize = 12.sp)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = KikaoColors.Indigo,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

private fun demoCourseDetails(id: String) = AdminCourseDetails(
    id = id,
    code = "CSC 221",
    name = "Database Systems",
    department = "Computer Science",
    faculty = "Faculty of Science & IT",
    lecturer = "Prof. Amani Mwangi",
    lecturerInitials = "AM",
    creditHours = 3,
    semester = "1",
    year = 2026,
    description = "Study of fundamental concepts of database systems including the relational model, SQL, normalization, and database design. The course includes practical lab sessions for building database applications.",
    totalStudents = 118,
    avgAttendance = 88,
    passingRate = 92
)

private fun demoCourseStudents() = listOf(
    AdminStudentSummary("s1", "Brian Otieno", "SC211/1288/2025", 94, 82, StudentAdminStatus.ACTIVE),
    AdminStudentSummary("s2", "Faith Wanjiru", "BA203/0876/2025", 81, 74, StudentAdminStatus.ACTIVE),
    AdminStudentSummary("s3", "Kevin Kamau", "SC211/0912/2025", 68, 55, StudentAdminStatus.AT_RISK)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminCourseDetailsPreview() {
    MaterialTheme {
        AdminCourseDetailsScreen(courseId = "csc221")
    }
}
