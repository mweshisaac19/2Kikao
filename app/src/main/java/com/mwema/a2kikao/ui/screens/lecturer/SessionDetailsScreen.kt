package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
// MODELS
// ------------------------------------------------------------

enum class SessionDetailStatus {
    LIVE,
    UPCOMING,
    COMPLETED,
    CANCELLED
}

private data class SessionStudent(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val status: String, // "Present", "Absent", "Late", "Pending"
    val time: String?,
    val initials: String,
    val isVerified: Boolean
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun SessionDetailsScreen(
    sessionId: String,
    modifier: Modifier = Modifier,
    status: SessionDetailStatus = SessionDetailStatus.UPCOMING,
    onBack: () -> Unit = {},
    onManageAttendance: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var currentStatus by remember { mutableStateOf(status) }
    val students = remember { demoSessionStudents() }
    
    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.SESSIONS,
        screenTitle = "Session details",
        screenSubtitle = "CSC 221 · Database Systems",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            
            // Header
            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            ) {
                Text("‹ Back to all sessions", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            
            // Summary Card
            SessionSummaryCard(
                status = currentStatus,
                onStartAttendance = { 
                    currentStatus = SessionDetailStatus.LIVE
                    onManageAttendance(sessionId)
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Info Sections
            Text(
                text = "Session info",
                color = KikaoColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            SessionInfoSection(currentStatus)
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Attendance List
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Attendance",
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${students.count { it.status == "Present" }} / ${students.size} Present",
                    color = KikaoColors.Teal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            AttendanceList(students)
        }
    }
}

// ------------------------------------------------------------
// COMPONENTS
// ------------------------------------------------------------

@Composable
private fun SessionSummaryCard(
    status: SessionDetailStatus,
    onStartAttendance: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "TOPIC",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = "Indexing & Query Optimization",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                SessionStatusBadge(status)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (status == SessionDetailStatus.UPCOMING) {
                Button(
                    onClick = onStartAttendance,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Gold)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = KikaoColors.DeepIndigo)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Live Attendance", color = KikaoColors.DeepIndigo, fontWeight = FontWeight.Bold)
                }
            } else if (status == SessionDetailStatus.LIVE) {
                Button(
                    onClick = onStartAttendance,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Live Session", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SessionStatusBadge(status: SessionDetailStatus) {
    val (bg, fg, label) = when (status) {
        SessionDetailStatus.LIVE -> Triple(KikaoColors.TealLight, KikaoColors.Teal, "LIVE NOW")
        SessionDetailStatus.UPCOMING -> Triple(Color(0xFFEAF0F8), Color.White.copy(alpha = 0.8f), "UPCOMING")
        SessionDetailStatus.COMPLETED -> Triple(Color(0xFFF1F5F9), Color.White.copy(alpha = 0.6f), "COMPLETED")
        SessionDetailStatus.CANCELLED -> Triple(Color(0xFFFFEAEC), Color(0xFFDC3545), "CANCELLED")
    }
    
    Surface(
        color = bg.copy(alpha = if (status == SessionDetailStatus.UPCOMING || status == SessionDetailStatus.COMPLETED) 0.15f else 1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = if (status == SessionDetailStatus.UPCOMING || status == SessionDetailStatus.COMPLETED) Color.White else fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun SessionInfoSection(status: SessionDetailStatus) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            InfoRow(icon = Icons.Default.CalendarMonth, label = "Date", value = "Tuesday, 18 August 2026")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
            InfoRow(icon = Icons.Default.Schedule, label = "Time", value = "10:00 AM - 12:00 PM")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
            InfoRow(icon = Icons.Default.Room, label = "Venue", value = "Computer Lab 3")
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = KikaoColors.MutedText, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = KikaoColors.MutedText, fontSize = 10.sp)
            Text(text = value, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun AttendanceList(students: List<SessionStudent>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        students.forEach { student ->
            StudentAttendanceRow(student)
        }
    }
}

@Composable
private fun StudentAttendanceRow(student: SessionStudent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = student.initials, color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = student.registrationNumber, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = student.status, 
                    color = when (student.status) {
                        "Present" -> KikaoColors.Teal
                        "Absent" -> Color(0xFFDC3545)
                        else -> KikaoColors.Gold
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (student.time != null) {
                    Text(text = student.time, color = KikaoColors.MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

// ------------------------------------------------------------
// DEMO DATA
// ------------------------------------------------------------

private fun demoSessionStudents() = listOf(
    SessionStudent("s1", "Amani Mwangi", "SC211/1234/2025", "Present", "10:04 AM", "AM", true),
    SessionStudent("s2", "John Doe", "SC211/5678/2025", "Present", "10:12 AM", "JD", true),
    SessionStudent("s3", "Sarah Wanjiku", "SC211/9012/2025", "Absent", null, "SW", false),
    SessionStudent("s4", "Kevin Otieno", "SC211/3456/2025", "Late", "10:45 AM", "KO", true)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SessionDetailsPreview() {
    MaterialTheme {
        SessionDetailsScreen(sessionId = "s1")
    }
}
