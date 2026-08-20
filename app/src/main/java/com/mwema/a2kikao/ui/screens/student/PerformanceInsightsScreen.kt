package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.mwema.a2kikao.ui.viewmodels.PerformanceInsightsViewModel
import com.mwema.a2kikao.ui.viewmodels.CourseClassAnalytics
import kotlin.math.roundToInt

data class PostedAssessment(
    val title: String,
    val type: String,
    val score: Double,
    val totalScore: Double,
    val classAverage: Double,
    val datePosted: String
)

private enum class AnalyticsSection {
    OVERVIEW,
    RESULTS,
    ATTENDANCE
}

@Composable
fun PerformanceInsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: PerformanceInsightsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onFeedbackClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val realCourses by viewModel.courses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val courses = realCourses
    var selectedCourseIndex by remember { mutableIntStateOf(0) }
    var selectedSection by rememberSaveable {
        mutableStateOf(AnalyticsSection.OVERVIEW)
    }

    if (selectedCourseIndex >= courses.size && courses.isNotEmpty()) {
        selectedCourseIndex = 0
    }

    val selectedCourse = if (courses.isNotEmpty()) courses[selectedCourseIndex] else null
    val overallAverage = if (courses.isNotEmpty()) courses.map { courseAverage(it) }.average().roundToInt() else 0
    val overallAttendance = if (courses.isNotEmpty()) courses.map { it.attendancePercent }.average().roundToInt() else 0

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.INSIGHTS,
        screenTitle = "Academic analytics",
        screenSubtitle = "Semester 1 · Lecturer-posted results",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KikaoColors.Teal)
                }
            } else if (courses.isEmpty()) {
                EmptyAnalyticsState()
            } else {
                AnalyticsGrid(
                    average = overallAverage,
                    attendance = overallAttendance,
                    rankedCourses = courses.count { courseAverage(it) >= 75 }
                )

                Spacer(modifier = Modifier.height(26.dp))

                CourseSelector(
                    courses = courses,
                    selectedIndex = selectedCourseIndex,
                    onSelect = { selectedCourseIndex = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                AnalyticsNavigation(
                    selectedSection = selectedSection,
                    onSelect = { selectedSection = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (selectedCourse != null) {
                    when (selectedSection) {
                        AnalyticsSection.OVERVIEW -> OverviewSection(selectedCourse)
                        AnalyticsSection.RESULTS -> ResultsSection(selectedCourse, onFeedbackClick)
                        AnalyticsSection.ATTENDANCE -> AttendanceSection(selectedCourse)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsGrid(average: Int, attendance: Int, rankedCourses: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnalyticsCard("Average", "$average%", KikaoColors.Teal, Modifier.weight(1f))
        AnalyticsCard("Attendance", "$attendance%", KikaoColors.Gold, Modifier.weight(1f))
        AnalyticsCard("Ranked", "$rankedCourses", Color(0xFF8B5CF6), Modifier.weight(1f))
    }
}

@Composable
private fun AnalyticsCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 10.sp, color = KikaoColors.MutedText, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 20.sp, color = color, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CourseSelector(courses: List<CourseClassAnalytics>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        courses.forEachIndexed { index, course ->
            val selected = index == selectedIndex
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (selected) KikaoColors.Indigo else Color.White).clickable { onSelect(index) }.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(course.code, color = if (selected) Color.White else KikaoColors.MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnalyticsNavigation(selectedSection: AnalyticsSection, onSelect: (AnalyticsSection) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        AnalyticsTab("Overview", selectedSection == AnalyticsSection.OVERVIEW) { onSelect(AnalyticsSection.OVERVIEW) }
        AnalyticsTab("Results", selectedSection == AnalyticsSection.RESULTS) { onSelect(AnalyticsSection.RESULTS) }
        AnalyticsTab("Attendance", selectedSection == AnalyticsSection.ATTENDANCE) { onSelect(AnalyticsSection.ATTENDANCE) }
    }
}

@Composable
private fun AnalyticsTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = if (isSelected) KikaoColors.Ink else KikaoColors.MutedText, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp)
        if (isSelected) Box(modifier = Modifier.padding(top = 4.dp).size(width = 20.dp, height = 2.dp).background(KikaoColors.Teal))
    }
}

@Composable
private fun OverviewSection(course: CourseClassAnalytics) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        PerformanceStandingCard(course)
    }
}

@Composable
private fun PerformanceStandingCard(course: CourseClassAnalytics) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ACADEMIC STANDING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KikaoColors.MutedText)
                Text("Rank ${course.classPosition}/${course.classSize}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = course.accentColor)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text("Your performance is consistently in the top 10% of this class. Great job!", fontSize = 14.sp, color = KikaoColors.Ink)
        }
    }
}

@Composable
private fun ResultsSection(course: CourseClassAnalytics, onFeedbackClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        course.assessments.forEach { assessment ->
            AssessmentCard(assessment, course.accentColor) { onFeedbackClick(assessment.title) }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AssessmentCard(assessment: PostedAssessment, accent: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(assessment.title, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text("${assessment.type} · ${assessment.datePosted}", fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Text("${assessment.score.toInt()}/${assessment.totalScore.toInt()}", fontWeight = FontWeight.ExtraBold, color = accent, fontSize = 18.sp)
        }
    }
}

@Composable
private fun AttendanceSection(course: CourseClassAnalytics) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = course.accentColor.copy(alpha = 0.08f))) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(course.accentColor), contentAlignment = Alignment.Center) {
                    Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text("${course.attendancePercent}% Attendance", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Institutional minimum: 75%", fontSize = 11.sp, color = KikaoColors.MutedText)
                }
            }
        }
    }
}

private fun courseAverage(course: CourseClassAnalytics): Int {
    if (course.assessments.isEmpty()) return 0
    return (course.assessments.map { (it.score / it.totalScore) * 100 }.average()).roundToInt()
}

@Composable
private fun EmptyAnalyticsState() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text("No academic data found.", color = KikaoColors.MutedText)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerformanceInsightsPreview() {
    MaterialTheme {
        PerformanceInsightsScreen()
    }
}
