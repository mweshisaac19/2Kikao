package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import kotlin.math.roundToInt

private data class PerformanceDepartment(
    val name: String,
    val students: Int,
    val average: Int,
    val passRate: Int,
    val distinctionRate: Int,
    val atRisk: Int,
    val assessments: Int,
    val accent: Color
)

private data class PerformanceTrend(
    val label: String,
    val average: Int
)

private data class AssessmentOverview(
    val title: String,
    val course: String,
    val type: String,
    val average: Int,
    val highest: Int,
    val lowest: Int,
    val submissions: Int,
    val totalStudents: Int,
    val accent: Color
)

@Composable
fun AdminPerformanceAnalyticsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onDepartmentClick: (String) -> Unit = {},
    onAtRiskStudentsClick: () -> Unit = {},
    onAssessmentClick: (String) -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val departments = remember { demoPerformanceDepartments() }
    val assessments = remember { demoAssessments() }
    var selectedPeriod by remember { mutableStateOf("This Semester") }
    var selectedDepartmentIndex by remember { mutableIntStateOf(0) }

    val overallAverage = departments.map { it.average }.average().roundToInt()
    val overallPassRate = departments.map { it.passRate }.average().roundToInt()
    val distinctionRate = departments.map { it.distinctionRate }.average().roundToInt()
    val studentsAtRisk = departments.sumOf { it.atRisk }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "Performance analytics",
        screenSubtitle = "Academic outcomes intelligence",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .padding(bottom = 110.dp)
        ) {
            
            TextButton(onClick = onBack, modifier = Modifier.padding(bottom = 8.dp)) {
                Text("‹ Back to command center", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            PerformancePeriodSelector(selectedPeriod, onPeriodSelected = { selectedPeriod = it })
            Spacer(modifier = Modifier.height(18.dp))

            PerformanceSnapshotCard(overallAverage, overallPassRate, distinctionRate, studentsAtRisk)
            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Performance trend")
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceTrendCard()

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Department benchmarks")
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceDepartmentSelector(departments, selectedDepartmentIndex) { selectedDepartmentIndex = it }
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceDepartmentCard(departments[selectedDepartmentIndex]) { onDepartmentClick(departments[selectedDepartmentIndex].name) }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Recent assessments")
            Spacer(modifier = Modifier.height(12.dp))
            assessments.forEach { assessment ->
                AssessmentAnalyticsCard(assessment) { onAssessmentClick(assessment.title) }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TextButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = modifier) { content() }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun PerformancePeriodSelector(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    val periods = listOf("This Week", "This Month", "This Semester")
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        periods.forEach { period ->
            val selected = period == selectedPeriod
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (selected) KikaoColors.Indigo else Color.White).clickable { onPeriodSelected(period) }.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(text = period, color = if (selected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PerformanceSnapshotCard(average: Int, passRate: Int, distinctionRate: Int, studentsAtRisk: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(21.dp)) {
            Text(text = "ACADEMIC OUTCOMES", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PerformanceRing(average)
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(text = "$average%", color = KikaoColors.Gold, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "$passRate% overall pass rate", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Text(text = "$studentsAtRisk students below threshold", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun PerformanceRing(value: Int) {
    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color.White.copy(alpha = 0.1f), style = Stroke(width = 10.dp.toPx()))
            drawArc(color = KikaoColors.Gold, startAngle = -90f, sweepAngle = value * 3.6f, useCenter = false, style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round))
        }
        Text(text = "$value%", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun PerformanceTrendCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(text = "Average Grade Trends", color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFF8FAFF)), contentAlignment = Alignment.Center) {
                Text("Trend graph visual goes here", color = KikaoColors.MutedText, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun PerformanceDepartmentSelector(departments: List<PerformanceDepartment>, selectedIndex: Int, onSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        departments.forEachIndexed { index, dept ->
            val selected = index == selectedIndex
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (selected) KikaoColors.Indigo else Color.White).clickable { onSelected(index) }.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(text = dept.name, color = if (selected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PerformanceDepartmentCard(department: PerformanceDepartment, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "DEPARTMENT", color = department.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = department.name, color = KikaoColors.Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "${department.average}%", color = department.accent, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MiniMetric("Assessments", "${department.assessments}")
                MiniMetric("Pass rate", "${department.passRate}%")
                MiniMetric("At risk", "${department.atRisk}")
            }
        }
    }
}

@Composable
private fun MiniMetric(label: String, value: String) {
    Column {
        Text(text = value, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = KikaoColors.MutedText, fontSize = 9.sp)
    }
}

@Composable
private fun AssessmentAnalyticsCard(assessment: AssessmentOverview, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = assessment.type, color = assessment.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = assessment.title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = assessment.course, color = KikaoColors.MutedText, fontSize = 10.sp)
                }
                Text(text = "${assessment.average}%", color = assessment.accent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun AcademicRiskCard(title: String, subtitle: String, value: String, severity: String, accent: Color, background: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = background)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(accent), contentAlignment = Alignment.Center) {
                Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Text(text = severity, color = accent, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

private fun demoPerformanceDepartments() = listOf(
    PerformanceDepartment("Computer Science", 420, 74, 88, 19, 31, 42, KikaoColors.Teal),
    PerformanceDepartment("Business", 610, 68, 81, 13, 67, 57, KikaoColors.Gold),
    PerformanceDepartment("Engineering", 530, 71, 84, 16, 52, 51, KikaoColors.Indigo)
)

private fun demoAssessments() = listOf(
    AssessmentOverview("CAT 1", "CSC 210 · Data Structures", "CAT", 76, 98, 31, 112, 120, KikaoColors.Teal),
    AssessmentOverview("Assignment 1", "CSC 221 · Database Systems", "Assignment", 72, 100, 28, 108, 120, KikaoColors.Gold)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminPerformanceAnalyticsPreview() {
    MaterialTheme {
        AdminPerformanceAnalyticsScreen()
    }
}
