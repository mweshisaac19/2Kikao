package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import kotlin.math.roundToInt

// ------------------------------------------------------------
// DATA MODELS
// ------------------------------------------------------------

private data class AttendanceStudent(
    val name: String,
    val registration: String,
    val attendance: Int,
    val present: Int,
    val late: Int,
    val absent: Int
)

private data class AttendanceSession(
    val date: String,
    val topic: String,
    val present: Int,
    val total: Int
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun AttendanceAnalyticsScreen(
    modifier: Modifier = Modifier,
    className: String = "Database Systems",
    classCode: String = "CSC 221",
    onBackClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {}
) {
    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.CLASSES,
        screenTitle = "Attendance analytics",
        screenSubtitle = "$classCode · $className",
        onTabSelected = {}
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            
            // Header stats
            AttendanceOverviewHeader()
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // At risk section
            Text(
                text = "Students needing attention",
                color = KikaoColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Attendance below 75%",
                color = KikaoColors.MutedText,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            demoAtRiskStudents().forEach { student ->
                AtRiskStudentCard(student)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Session history analytics
            Text(
                text = "Session trends",
                color = KikaoColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            demoAttendanceSessions().forEach { session ->
                SessionAttendanceCard(session)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// ------------------------------------------------------------
// COMPONENTS
// ------------------------------------------------------------

@Composable
private fun AttendanceOverviewHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "OVERALL ATTENDANCE",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color.White.copy(alpha = 0.15f), style = Stroke(width = 8.dp.toPx()))
                        drawArc(
                            color = KikaoColors.Gold,
                            startAngle = -90f,
                            sweepAngle = 87 * 3.6f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text("87%", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column {
                    Text("Class health is strong", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("3 sessions remaining this month", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun AtRiskStudentCard(student: AttendanceStudent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = student.name.take(1), color = Color(0xFFDC3545), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = student.registration, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${student.attendance}%", color = Color(0xFFDC3545), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "${student.absent} missed", color = KikaoColors.MutedText, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun SessionAttendanceCard(session: AttendanceSession) {
    val percent = ((session.present.toFloat() / session.total) * 100).toInt()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = session.date, color = KikaoColors.MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = session.topic, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "$percent%", color = if (percent >= 85) KikaoColors.Teal else KikaoColors.Gold, fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Small visual bar
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFF1F5F9))) {
                Box(modifier = Modifier.fillMaxWidth(percent/100f).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(if (percent >= 85) KikaoColors.Teal else KikaoColors.Gold))
            }
        }
    }
}

// ------------------------------------------------------------
// DEMO DATA
// ------------------------------------------------------------

private fun demoAtRiskStudents() = listOf(
    AttendanceStudent("Kevin Otieno", "SC211/3456/2025", 68, 10, 1, 4),
    AttendanceStudent("John Doe", "SC211/5678/2025", 72, 11, 0, 4)
)

private fun demoAttendanceSessions() = listOf(
    AttendanceSession("18 Aug", "Indexing & Optimization", 98, 120),
    AttendanceSession("11 Aug", "SQL Subqueries", 104, 120),
    AttendanceSession("04 Aug", "Relational Algebra", 82, 120)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AttendanceAnalyticsPreview() {
    MaterialTheme {
        AttendanceAnalyticsScreen()
    }
}
