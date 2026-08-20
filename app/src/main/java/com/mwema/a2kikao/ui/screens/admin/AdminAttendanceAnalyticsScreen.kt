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
import androidx.compose.material3.TextButton
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

// ---------------------------------------------------------
// DATA MODELS
// ---------------------------------------------------------

private data class AttendanceDepartment(
    val name: String,
    val students: Int,
    val attendance: Int,
    val sessions: Int,
    val riskStudents: Int,
    val accent: Color
)

private data class AttendanceTrend(
    val label: String,
    val percentage: Int
)

private data class AttendanceRisk(
    val title: String,
    val subtitle: String,
    val value: String,
    val severity: String,
    val accent: Color,
    val background: Color
)

// ---------------------------------------------------------
// MAIN SCREEN
// ---------------------------------------------------------

@Composable
fun AdminAttendanceAnalyticsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onDepartmentClick: (String) -> Unit = {},
    onStudentRiskClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val departments = remember { demoAttendanceDepartments() }
    var selectedPeriod by remember { mutableStateOf("This Semester") }
    var selectedDepartmentIndex by remember { mutableIntStateOf(0) }

    val overallAttendance = departments.map { it.attendance }.average().roundToInt()
    val totalStudents = departments.sumOf { it.students }
    val studentsAtRisk = departments.sumOf { it.riskStudents }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "Attendance analytics",
        screenSubtitle = "Institution-wide intelligence",
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
            
            TextButton(onClick = onBack) {
                Text("‹ Back to command center", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            PeriodSelector(selectedPeriod, onPeriodSelected = { selectedPeriod = it })
            Spacer(modifier = Modifier.height(18.dp))

            AttendanceSnapshotCard(overallAttendance, totalStudents, studentsAtRisk)
            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Institution snapshot")
            Spacer(modifier = Modifier.height(11.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsKpi("$totalStudents", "Students", "●", KikaoColors.Teal, Modifier.weight(1f))
                AnalyticsKpi("${departments.sumOf { it.sessions }}", "Sessions", "◷", KikaoColors.Indigo, Modifier.weight(1f))
                AnalyticsKpi("$studentsAtRisk", "At risk", "!", Color(0xFFD97706), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Attendance trend")
            Text("Verified presence across teaching weeks", color = KikaoColors.MutedText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            AttendanceTrendCard()

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Department analytics")
            Spacer(modifier = Modifier.height(12.dp))
            DepartmentSelector(departments, selectedDepartmentIndex) { selectedDepartmentIndex = it }
            Spacer(modifier = Modifier.height(12.dp))
            DepartmentAnalyticsCard(departments[selectedDepartmentIndex]) { onDepartmentClick(departments[selectedDepartmentIndex].name) }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun PeriodSelector(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
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
private fun AttendanceSnapshotCard(attendance: Int, totalStudents: Int, studentsAtRisk: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(21.dp)) {
            Text(text = "INSTITUTIONAL HEALTH", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AttendanceRing(attendance)
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(text = "$attendance%", color = KikaoColors.Gold, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "$totalStudents students tracked", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(text = "$studentsAtRisk students need attention", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun AttendanceRing(attendance: Int) {
    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color.White.copy(alpha = 0.1f), style = Stroke(width = 10.dp.toPx()))
            drawArc(color = KikaoColors.Gold, startAngle = -90f, sweepAngle = attendance * 3.6f, useCenter = false, style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round))
        }
        Text(text = "$attendance%", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AnalyticsKpi(value: String, label: String, icon: String, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(13.dp)) {
            Box(modifier = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(accent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Text(text = icon, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, color = KikaoColors.Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = label, color = KikaoColors.MutedText, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AttendanceTrendCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(text = "Weekly Analytics", color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFF8FAFF)), contentAlignment = Alignment.Center) {
                Text("Trend graph visual goes here", color = KikaoColors.MutedText, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun DepartmentSelector(departments: List<AttendanceDepartment>, selectedIndex: Int, onSelected: (Int) -> Unit) {
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
private fun DepartmentAnalyticsCard(department: AttendanceDepartment, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "DEPARTMENT", color = department.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = department.name, color = KikaoColors.Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "${department.attendance}%", color = department.accent, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MiniMetric("Students", "${department.students}")
                MiniMetric("Sessions", "${department.sessions}")
                MiniMetric("At risk", "${department.riskStudents}")
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

private fun demoAttendanceDepartments() = listOf(
    AttendanceDepartment("Computer Science", 420, 91, 86, 27, KikaoColors.Teal),
    AttendanceDepartment("Business", 610, 87, 94, 41, KikaoColors.Gold),
    AttendanceDepartment("Engineering", 530, 84, 88, 53, KikaoColors.Indigo)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminAttendanceAnalyticsPreview() {
    MaterialTheme {
        AdminAttendanceAnalyticsScreen()
    }
}
