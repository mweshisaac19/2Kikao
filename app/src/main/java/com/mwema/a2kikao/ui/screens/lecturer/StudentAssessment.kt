package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerStudentDetailViewModel
import kotlin.math.roundToInt

// ------------------------------------------------------------
// DATA MODELS
// ------------------------------------------------------------

data class StudentAssessmentResult(
    val title: String,
    val type: String,
    val score: Double,
    val total: Double,
    val classAverage: Double,
    val date: String
)

data class StudentCoursePerformance(
    val code: String,
    val name: String,
    val average: Int,
    val attendance: Int,
    val position: Int,
    val accent: Color
)

data class StudentAcademicProfile(
    val name: String,
    val registrationNumber: String,
    val program: String,
    val year: String,
    val email: String,
    val photo: String?,
    val overallAverage: Int,
    val rankedCourses: Int,
    val assessments: List<StudentAssessmentResult>,
    val coursePerformance: List<StudentCoursePerformance>
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun StudentAssessmentScreen(
    studentId: String,
    modifier: Modifier = Modifier,
    viewModel: LecturerStudentDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val studentProfile by viewModel.studentProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(studentId) {
        val lecturerId = FirebaseManager.currentUserUId
        if (lecturerId != null) {
            viewModel.fetchStudentDetail(studentId, lecturerId)
        }
    }

    val student = studentProfile ?: demoStudentProfile(studentId)

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.STUDENTS,
        screenTitle = "Student profile",
        screenSubtitle = student.registrationNumber,
        onNotificationClick = onNotificationClick,
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
                    .padding(bottom = 40.dp)
            ) {
                
                // Header Action
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text("‹ Back to student list", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                }
                
                // Profile Card
                StudentHeroCard(student)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Overall Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatMetricCard(
                        label = "Average",
                        value = "${student.overallAverage}%",
                        modifier = Modifier.weight(1f),
                        accent = KikaoColors.Indigo
                    )
                    StatMetricCard(
                        label = "Courses",
                        value = student.rankedCourses.toString(),
                        modifier = Modifier.weight(1f),
                        accent = KikaoColors.Teal
                    )
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Performance by Course
                Text(
                    text = "Course performance",
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                if (student.coursePerformance.isEmpty()) {
                    Text("No course data available.", color = KikaoColors.MutedText, fontSize = 14.sp)
                } else {
                    student.coursePerformance.forEach { course ->
                        CoursePerformanceRow(course)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Recent Assessments
                Text(
                    text = "Recent assessment results",
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                if (student.assessments.isEmpty()) {
                    Text("No assessments recorded.", color = KikaoColors.MutedText, fontSize = 14.sp)
                } else {
                    student.assessments.forEach { result ->
                        StudentAssessmentCard(result)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------
// COMPONENTS
// ------------------------------------------------------------

@Composable
private fun StudentHeroCard(student: StudentAcademicProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Spacer(modifier = Modifier.width(18.dp))
                
                Column {
                    Text(
                        text = student.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = student.registrationNumber,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 13.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = student.program,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(label: String, value: String, modifier: Modifier = Modifier, accent: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = label, color = KikaoColors.MutedText, fontSize = 11.sp)
            Text(text = value, color = accent, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CoursePerformanceRow(course: StudentCoursePerformance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(course.accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = course.code, color = course.accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Attendance: ${course.attendance}%", color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${course.average}%", color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Rank: ${course.position}", color = KikaoColors.MutedText, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun StudentAssessmentCard(result: StudentAssessmentResult) {
    val percentage = ((result.score / result.total) * 100).roundToInt()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = result.type.uppercase(), color = KikaoColors.Teal, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = result.title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "$percentage%", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Linear Progress Comparison
            Column {
                ComparisonBar(label = "Student", percentage = percentage, color = KikaoColors.Teal)
                Spacer(modifier = Modifier.height(6.dp))
                ComparisonBar(label = "Class Avg", percentage = result.classAverage.roundToInt(), color = KikaoColors.MutedText)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(text = "Score: ${result.score.toInt()}/${result.total.toInt()} · Posted ${result.date}", color = KikaoColors.MutedText, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ComparisonBar(label: String, percentage: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = KikaoColors.MutedText, fontSize = 9.sp, modifier = Modifier.width(60.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$percentage%", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// ------------------------------------------------------------
// DEMO DATA
// ------------------------------------------------------------

private fun demoStudentProfile(id: String) = StudentAcademicProfile(
    name = "Amani Mwangi",
    registrationNumber = "SC211/1234/2025",
    program = "BSc Computer Science",
    year = "Year 2",
    email = "amani.mwangi@university.ac.ke",
    photo = null,
    overallAverage = 84,
    rankedCourses = 4,
    assessments = emptyList(),
    coursePerformance = emptyList()
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StudentAssessmentPreview() {
    MaterialTheme {
        StudentAssessmentScreen(studentId = "s1")
    }
}
