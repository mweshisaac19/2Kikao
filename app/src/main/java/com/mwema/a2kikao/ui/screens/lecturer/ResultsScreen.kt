package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerResultsViewModel
import com.mwema.a2kikao.ui.viewmodels.CourseOptionData
import kotlin.math.roundToInt

// -----------------------------------------------------------------------------
// DATA MODELS
// -----------------------------------------------------------------------------

private data class Assessment(
    val id: String,
    val title: String,
    val type: String,
    val totalMarks: Int,
    val average: Int,
    val highest: Int,
    val lowest: Int,
    val submissions: Int,
    val totalStudents: Int,
    val datePosted: String,
    val status: AssessmentStatus
)

private enum class AssessmentStatus {
    PUBLISHED,
    DRAFT,
    SCHEDULED
}

private data class StudentPerformance(
    val id: String,
    val name: String,
    val regNo: String,
    val average: Int,
    val trend: String, // "up", "down", "steady"
    val lastScore: Int?
)

@Composable
fun ResultsScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerResultsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onAddAssessment: (courseId: String) -> Unit = {},
    onViewAnalytics: (courseCode: String, courseName: String) -> Unit = { _, _ -> },
    onViewDepartmentalAnalytics: () -> Unit = {},
    onViewAppeals: () -> Unit = {},
    onAssessmentClick: (assessmentId: String) -> Unit = {},
    onEnterMarks: (assessmentId: String) -> Unit = {},
    onStudentClick: (studentId: String) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val realCourses by viewModel.courseOptions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val courses = if (realCourses.isNotEmpty()) realCourses else listOf(
        CourseOptionData("csc210", "CSC 210", "Data Structures"),
        CourseOptionData("csc221", "CSC 221", "Database Systems")
    )
    
    var selectedCourseIndex by rememberSaveable { mutableIntStateOf(0) }
    
    if (selectedCourseIndex >= courses.size) {
        selectedCourseIndex = 0
    }
    
    val selectedCourse = courses[selectedCourseIndex]
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.RESULTS,
        screenTitle = "Results & analytics",
        screenSubtitle = "Track student performance across your courses",
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
                    .padding(bottom = 32.dp)
            ) {
                
                CoursePicker(
                    courses = courses,
                    selectedIndex = selectedCourseIndex,
                    onCourseSelected = { selectedCourseIndex = it }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                PerformanceOverview(
                    courseName = selectedCourse.name,
                    onAddAssessment = { onAddAssessment(selectedCourse.id) },
                    onViewDepartmentalAnalytics = onViewDepartmentalAnalytics
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Assessments", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onViewAppeals) {
                            Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp), tint = KikaoColors.Teal)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark appeals", color = KikaoColors.Teal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = { onViewAnalytics(selectedCourse.code, selectedCourse.name) }) {
                            Icon(Icons.Default.Assessment, null, modifier = Modifier.size(16.dp), tint = KikaoColors.Teal)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Full analytics", color = KikaoColors.Teal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                RecentAssessmentsList(
                    assessments = demoAssessments(selectedCourse.id),
                    onAssessmentClick = onEnterMarks
                )
                
                Spacer(modifier = Modifier.height(28.dp))
                
                Text(text = "Student performance", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                
                Spacer(modifier = Modifier.height(14.dp))
                
                SearchField(query = searchQuery, onQueryChange = { searchQuery = it })
                
                Spacer(modifier = Modifier.height(16.dp))
                
                StudentRanking(
                    students = demoStudents(selectedCourse.id).filter { 
                        it.name.contains(searchQuery, ignoreCase = true) || 
                        it.regNo.contains(searchQuery, ignoreCase = true)
                    },
                    onStudentClick = onStudentClick
                )
            }
        }
    }
}

@Composable
private fun CoursePicker(courses: List<CourseOptionData>, selectedIndex: Int, onCourseSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCourse = if (courses.isNotEmpty()) courses[selectedIndex] else CourseOptionData("", "", "")
    
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Card(onClick = { expanded = true }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(KikaoColors.TealLight), contentAlignment = Alignment.Center) {
                        Text("▦", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(selectedCourse.code, fontWeight = FontWeight.Bold)
                        Text(selectedCourse.name, fontSize = 11.sp, color = KikaoColors.MutedText, maxLines = 1)
                    }
                }
                Icon(Icons.Default.KeyboardArrowDown, null, tint = KikaoColors.MutedText)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            courses.forEachIndexed { index, course ->
                DropdownMenuItem(text = { Text("${course.code} ${course.name}") }, onClick = { onCourseSelected(index); expanded = false })
            }
        }
    }
}

@Composable
private fun PerformanceOverview(courseName: String, onAddAssessment: () -> Unit, onViewDepartmentalAnalytics: () -> Unit) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo), modifier = Modifier.padding(horizontal = 20.dp)) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("COURSE HEALTH", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = onViewDepartmentalAnalytics) { Icon(Icons.Default.Insights, null, tint = KikaoColors.Gold) }
                    IconButton(onClick = onAddAssessment) { Icon(Icons.Default.Add, null, tint = KikaoColors.Gold) }
                }
            }
            Text("82% Class average", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecentAssessmentsList(assessments: List<Assessment>, onAssessmentClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        assessments.forEach { assessment ->
            AssessmentCard(assessment) { onAssessmentClick(assessment.id) }
        }
    }
}

@Composable
private fun AssessmentCard(assessment: Assessment, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(assessment.title, fontWeight = FontWeight.Bold)
                Text("${assessment.type} · ${assessment.datePosted}", fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Text("${assessment.average}%", fontWeight = FontWeight.Bold, color = KikaoColors.Teal)
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query, onValueChange = onQueryChange, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        placeholder = { Text("Search students...") }, shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun StudentRanking(students: List<StudentPerformance>, onStudentClick: (String) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.padding(horizontal = 20.dp)) {
        Column {
            students.forEach { student ->
                Row(modifier = Modifier.fillMaxWidth().clickable { onStudentClick(student.id) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(student.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("${student.average}%", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun demoAssessments(courseId: String) = listOf(
    Assessment("a1", "Assignment 1", "Assignment", 20, 72, 95, 40, 118, 120, "16 Aug", AssessmentStatus.PUBLISHED),
    Assessment("a2", "CAT 1", "CAT", 100, 64, 98, 22, 115, 120, "08 Aug", AssessmentStatus.PUBLISHED)
)

private fun demoStudents(courseId: String) = listOf(
    StudentPerformance("s1", "Amani Mwangi", "SC211/1234/2025", 87, "up", 92),
    StudentPerformance("s2", "John Doe", "SC211/5678/2025", 74, "steady", 72)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResultsScreenPreview() {
    MaterialTheme {
        ResultsScreen()
    }
}
