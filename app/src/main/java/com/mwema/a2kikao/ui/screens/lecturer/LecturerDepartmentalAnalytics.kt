package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private object DepartmentAnalyticsColors {
    val DeepIndigo = Color(0xFF0F172A)
    val Teal = Color(0xFF0F9D8A)
    val Blue = Color(0xFF2563EB)
    val Purple = Color(0xFF6D4AFF)
}

private data class CourseBenchmark(
    val code: String,
    val name: String,
    val students: Int,
    val attendance: Float,
    val universityAttendance: Float,
    val averageMark: Float,
    val universityAverageMark: Float,
    val passRate: Float,
    val universityPassRate: Float,
    val atRisk: Int
)

private data class BenchmarkMetric(
    val title: String,
    val lecturerValue: Float,
    val universityValue: Float,
    val suffix: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun LecturerDepartmentalAnalyticsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCourseClick: (String) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedPeriod by remember { mutableStateOf("Semester 2 · 2026") }
    var selectedTab by remember { mutableStateOf("Overview") }

    val courses = remember {
        listOf(
            CourseBenchmark("CSC 301", "Algorithms", 84, 86f, 78f, 71f, 65f, 89f, 81f, 7),
            CourseBenchmark("CSC 210", "Data Structures", 96, 82f, 78f, 68f, 65f, 84f, 81f, 11),
            CourseBenchmark("CSC 221", "Database Systems", 71, 75f, 78f, 62f, 65f, 76f, 81f, 14),
            CourseBenchmark("BIT 314", "Information Systems", 68, 91f, 78f, 74f, 65f, 93f, 81f, 4)
        )
    }

    val averageAttendance = courses.map { it.attendance }.average().toFloat()
    val universityAttendance = courses.map { it.universityAttendance }.average().toFloat()
    val averageMark = courses.map { it.averageMark }.average().toFloat()
    val universityAverageMark = courses.map { it.universityAverageMark }.average().toFloat()
    val passRate = courses.map { it.passRate }.average().toFloat()
    val universityPassRate = courses.map { it.universityPassRate }.average().toFloat()
    val totalStudents = courses.sumOf { it.students }

    val metrics = listOf(
        BenchmarkMetric("Attendance", averageAttendance, universityAttendance, "%", Icons.Default.Groups, DepartmentAnalyticsColors.Teal),
        BenchmarkMetric("Average mark", averageMark, universityAverageMark, "%", Icons.Default.School, DepartmentAnalyticsColors.Blue),
        BenchmarkMetric("Pass rate", passRate, universityPassRate, "%", Icons.Default.ShowChart, DepartmentAnalyticsColors.Purple)
    )

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.RESULTS,
        screenTitle = "Department Analytics",
        screenSubtitle = "Performance Benchmarking",
        onBackClick = onBack,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AnalyticsHero(selectedPeriod) { selectedPeriod = it }
            }

            item {
                AnalyticsSummary(courses.size, totalStudents, averageAttendance, passRate)
            }

            item {
                AnalyticsTabs(selectedTab) { selectedTab = it }
            }

            if (selectedTab == "Overview") {
                item {
                    SectionHeader("Department Performance", "Vs university-wide benchmarks")
                }

                items(metrics, key = { it.title }) { metric ->
                    BenchmarkMetricCard(metric)
                }

                item {
                    BenchmarkComparisonCard(averageAttendance, universityAttendance, averageMark, universityAverageMark, passRate, universityPassRate)
                }

                item {
                    SectionHeader("Course Performance", "Select course to explore")
                }

                items(courses, key = { it.code }) { course ->
                    CourseBenchmarkCard(course) { onCourseClick(course.code) }
                }
            } else {
                item {
                    SectionHeader("Course Leaderboard", "Ranked by overall performance")
                }

                items(courses.sortedByDescending { (it.attendance + it.averageMark + it.passRate) / 3 }) { course ->
                    CourseRankingCard(
                        rank = courses.sortedByDescending { (it.attendance + it.averageMark + it.passRate) / 3 }.indexOf(course) + 1,
                        course = course,
                        onClick = { onCourseClick(course.code) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(110.dp)) }
        }
    }
}

@Composable
private fun AnalyticsHero(period: String, onPeriodChange: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(25.dp), colors = CardDefaults.cardColors(containerColor = DepartmentAnalyticsColors.DeepIndigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(DepartmentAnalyticsColors.Teal.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Insights, contentDescription = null, tint = DepartmentAnalyticsColors.Teal)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Comparative Analysis", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Reporting: $period", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
                IconButton(onClick = { onPeriodChange(period) }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = KikaoColors.Gold)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSummary(courses: Int, students: Int, att: Float, pass: Float) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        SummaryCard(courses.toString(), "Courses", Icons.Default.Book, Modifier.weight(1f))
        SummaryCard(students.toString(), "Students", Icons.Default.Groups, Modifier.weight(1f))
        SummaryCard("${att.toInt()}%", "Attendance", Icons.Default.CheckCircle, Modifier.weight(1f))
        SummaryCard("${pass.toInt()}%", "Pass Rate", Icons.Default.ShowChart, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(value: String, label: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = KikaoColors.Teal, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = KikaoColors.Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, color = KikaoColors.MutedText, fontSize = 9.sp)
        }
    }
}

@Composable
private fun AnalyticsTabs(selected: String, onSelected: (String) -> Unit) {
    Row(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(4.dp)) {
        listOf("Overview", "Leaderboard").forEach { tab ->
            val isSelected = tab == selected
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (isSelected) KikaoColors.Indigo else Color.Transparent).clickable { onSelected(tab) }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                Text(tab, color = if (isSelected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BenchmarkMetricCard(metric: BenchmarkMetric) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(metric.title, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text("${metric.lecturerValue.toInt()}${metric.suffix}", color = metric.color, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(progress = { metric.lecturerValue / 100f }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)), color = metric.color, trackColor = Color(0xFFF1F5F9))
            Text("University Avg: ${metric.universityValue.toInt()}%", fontSize = 10.sp, color = KikaoColors.MutedText, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun BenchmarkComparisonCard(att: Float, uAtt: Float, mark: Float, uMark: Float, pass: Float, uPass: Float) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = DepartmentAnalyticsColors.DeepIndigo)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Benchmarking Results", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            ComparisonRow("Attendance", att, uAtt)
            ComparisonRow("Avg Mark", mark, uMark)
            ComparisonRow("Pass Rate", pass, uPass)
        }
    }
}

@Composable
private fun ComparisonRow(label: String, val1: Float, val2: Float) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        Text("${val1.toInt()}% vs ${val2.toInt()}%", color = KikaoColors.Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CourseBenchmarkCard(course: CourseBenchmark, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(course.name, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(course.code, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KikaoColors.MutedText)
        }
    }
}

@Composable
private fun CourseRankingCard(rank: Int, course: CourseBenchmark, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#$rank", fontWeight = FontWeight.Bold, color = KikaoColors.Teal, modifier = Modifier.width(32.dp))
            Column {
                Text(course.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(course.code, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 15.sp)
        Text(subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerDepartmentalAnalyticsPreview() {
    MaterialTheme {
        LecturerDepartmentalAnalyticsScreen()
    }
}
