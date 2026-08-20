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

private data class AttendancePerformanceRecord(
    val department: String,
    val attendance: Int,
    val performance: Int,
    val students: Int,
    val atRisk: Int,
    val accent: Color
)

@Composable
fun AdminAttendancePerformanceScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val departments = rememberAttendancePerformanceData()

    val universityAttendance = if (departments.isEmpty()) 0 else departments.map { it.attendance }.average().roundToInt()
    val universityPerformance = if (departments.isEmpty()) 0 else departments.map { it.performance }.average().roundToInt()
    val totalStudents = departments.sumOf { it.students }
    val totalAtRisk = departments.sumOf { it.atRisk }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "Institutional pulse",
        screenSubtitle = "Attendance vs Performance",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 110.dp)
        ) {
            
            TextButton(onClick = onBack) {
                Text("‹ Back to command center", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            UniversityCorrelationHero(attendance = universityAttendance, performance = universityPerformance)

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("Correlation map", "How attendance shapes academic outcomes")
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.White, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Text("Correlation visual goes here", color = KikaoColors.MutedText, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("Department details", "Comparative outcomes by academic unit")
            Spacer(modifier = Modifier.height(12.dp))

            departments.forEach { department ->
                DepartmentPerformanceCard(department)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun UniversityCorrelationHero(attendance: Int, performance: Int) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(21.dp)) {
            Text(text = "CORRELATION INSIGHT", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricItem("Attendance", "$attendance%")
                MetricItem("Performance", "$performance%")
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
        Text(text = value, color = KikaoColors.Gold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(text = title, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
    }
}

@Composable
private fun DepartmentPerformanceCard(record: AttendancePerformanceRecord) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = record.department, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${record.students} students", color = KikaoColors.MutedText, fontSize = 10.sp)
                }
                Text(text = "${record.performance}%", color = record.accent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

private fun rememberAttendancePerformanceData() = listOf(
    AttendancePerformanceRecord("Computer Science", 88, 76, 420, 31, KikaoColors.Teal),
    AttendancePerformanceRecord("Business", 84, 73, 510, 38, KikaoColors.Indigo)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminAttendancePerformancePreview() {
    MaterialTheme {
        AdminAttendancePerformanceScreen()
    }
}
