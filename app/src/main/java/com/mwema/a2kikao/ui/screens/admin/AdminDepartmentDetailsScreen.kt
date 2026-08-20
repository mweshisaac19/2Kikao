package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
// DATA MODELS
// ------------------------------------------------------------

private data class DepartmentCourse(
    val code: String,
    val name: String,
    val students: Int,
    val attendance: Int,
    val average: Int,
    val lecturer: String
)

private data class DepartmentLecturer(
    val name: String,
    val title: String,
    val courses: Int,
    val students: Int,
    val attendance: Int
)

private data class DepartmentActivity(
    val title: String,
    val description: String,
    val time: String,
    val icon: String,
    val iconBackground: Color
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun AdminDepartmentDetailsScreen(
    departmentId: String,
    modifier: Modifier = Modifier,
    departmentName: String = "Computer Science",
    departmentCode: String = "CIT",
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCourseClick: (String) -> Unit = {},
    onLecturerClick: (String) -> Unit = {},
    onViewStudents: () -> Unit = {},
    onViewCourses: () -> Unit = {},
    onViewLecturers: () -> Unit = {},
    onViewClasses: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val courses = listOf(
        DepartmentCourse("CSC 210", "Data Structures", 118, 91, 74, "Dr. Peter Kamau"),
        DepartmentCourse("CSC 221", "Database Systems", 120, 87, 71, "Prof. Sarah Wanjiku"),
        DepartmentCourse("CSC 230", "Computer Networks", 96, 84, 68, "Dr. Brian Otieno"),
        DepartmentCourse("CSC 240", "Software Engineering", 105, 89, 76, "Dr. Grace Njeri")
    )

    val lecturers = listOf(
        DepartmentLecturer("Dr. Peter Kamau", "Senior Lecturer", 3, 246, 92),
        DepartmentLecturer("Prof. Sarah Wanjiku", "Associate Professor", 2, 198, 89),
        DepartmentLecturer("Dr. Brian Otieno", "Lecturer", 3, 221, 84)
    )

    val activities = listOf(
        DepartmentActivity("Results posted", "CSC 221 results published", "18m ago", "✓", KikaoColors.TealLight),
        DepartmentActivity("Attendance alert", "CSC 230 below 85%", "1h ago", "!", Color(0xFFFFF2CC))
    )

    val departmentAverage = if (courses.isEmpty()) 0 else courses.map { it.average }.average().toInt()
    val departmentAttendance = if (courses.isEmpty()) 0 else courses.map { it.attendance }.average().toInt()

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Department details",
        screenSubtitle = departmentName,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 110.dp)
        ) {
            
            // Sub-header with back action
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color.White)) {
                    Text("‹", color = KikaoColors.Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = departmentName, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Code: $departmentCode", color = KikaoColors.MutedText, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            DepartmentOverviewCard(departmentAverage, departmentAttendance)

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Academic Units", "View all", onViewCourses)
            Spacer(modifier = Modifier.height(10.dp))
            courses.forEach { course ->
                DepartmentCourseCard(course) { onCourseClick(course.code) }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Faculty Members", "View all", onViewLecturers)
            Spacer(modifier = Modifier.height(10.dp))
            lecturers.forEach { lecturer ->
                DepartmentLecturerCard(lecturer) { onLecturerClick(lecturer.name) }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun DepartmentOverviewCard(departmentAverage: Int, attendance: Int) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "DEPARTMENT PERFORMANCE", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BigMetric("$departmentAverage%", "Academic Average")
                BigMetric("$attendance%", "Presence Rate")
            }
        }
    }
}

@Composable
private fun BigMetric(value: String, label: String) {
    Column {
        Text(text = value, color = KikaoColors.Gold, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(text = action, color = KikaoColors.Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onAction))
    }
}

@Composable
private fun DepartmentCourseCard(course: DepartmentCourse, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.name, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "${course.code} · ${course.lecturer}", color = KikaoColors.MutedText, fontSize = 10.sp)
            }
            Text(text = "${course.average}%", color = KikaoColors.Teal, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun DepartmentLecturerCard(lecturer: DepartmentLecturer, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lecturer.name, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = lecturer.title, color = KikaoColors.MutedText, fontSize = 10.sp)
            }
            Text(text = "${lecturer.attendance}%", color = KikaoColors.Indigo, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminDepartmentDetailsPreview() {
    MaterialTheme {
        AdminDepartmentDetailsScreen(departmentId = "CIT")
    }
}
