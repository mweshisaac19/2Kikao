package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
// DATA MODELS
// ------------------------------------------------------------

private data class LecturerSession(
    val id: String,
    val courseCode: String,
    val courseName: String,
    val topic: String,
    val dateLabel: String,
    val time: String,
    val duration: String,
    val room: String,
    val studentCount: Int,
    val attendanceCount: Int,
    val status: SessionStatus
)

private enum class SessionStatus {
    LIVE,
    UPCOMING,
    COMPLETED
}

private enum class SessionsFilter {
    ALL,
    TODAY,
    UPCOMING,
    RECENT
}

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun SessionsScreen(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onSessionClick: (String) -> Unit = {},
    onCreateSession: () -> Unit = {},
    onCancelSession: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(SessionsFilter.ALL) }
    val sessions = remember { demoSessions() }
    
    val filteredSessions = when (selectedFilter) {
        SessionsFilter.ALL -> sessions
        SessionsFilter.TODAY -> sessions.filter { it.dateLabel == "Today" }
        SessionsFilter.UPCOMING -> sessions.filter { it.status == SessionStatus.UPCOMING }
        SessionsFilter.RECENT -> sessions.filter { it.status == SessionStatus.COMPLETED }
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.SESSIONS,
        screenTitle = "Your sessions",
        screenSubtitle = "Track and manage your teaching schedule",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                
                // Welcome/Summary Card
                SessionsSummaryHeader(
                    todayCount = sessions.count { it.dateLabel == "Today" }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Filter chips
                SessionsFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Sessions list
                Text(
                    text = when (selectedFilter) {
                        SessionsFilter.ALL -> "All sessions"
                        SessionsFilter.TODAY -> "Today's schedule"
                        SessionsFilter.UPCOMING -> "Upcoming classes"
                        SessionsFilter.RECENT -> "Recent sessions"
                    },
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                if (filteredSessions.isEmpty()) {
                    EmptySessionsState()
                } else {
                    filteredSessions.forEach { session ->
                        SessionCard(
                            session = session,
                            onClick = { onSessionClick(session.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
            
            // Floating Action Button
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = onCancelSession,
                    containerColor = Color(0xFFDC3545),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.EventBusy, contentDescription = "Cancel session")
                }

                FloatingActionButton(
                    onClick = onCreateSession,
                    containerColor = KikaoColors.Teal,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create session")
                }
            }
        }
    }
}

// ------------------------------------------------------------
// COMPONENTS
// ------------------------------------------------------------

@Composable
private fun SessionsSummaryHeader(
    todayCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
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
                        text = "SESSION OVERVIEW",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(7.dp))
                    
                    Text(
                        text = "Stay on schedule",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = KikaoColors.Gold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "You have $todayCount classes today",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp
                )
                
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = KikaoColors.Gold)
                ) {
                    Text(
                        text = "Next in 45m",
                        color = KikaoColors.DeepIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionsFilterRow(
    selectedFilter: SessionsFilter,
    onFilterSelected: (SessionsFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SessionsFilter.entries.forEach { filter ->
            val selected = filter == selectedFilter
            
            Surface(
                onClick = { onFilterSelected(filter) },
                shape = RoundedCornerShape(12.dp),
                color = if (selected) KikaoColors.Indigo else Color.White,
                border = if (selected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = if (selected) Color.White else KikaoColors.MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: LecturerSession,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.courseCode,
                        color = KikaoColors.Teal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = session.topic,
                        color = KikaoColors.Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = session.courseName,
                        color = KikaoColors.MutedText,
                        fontSize = 12.sp
                    )
                }
                
                SessionStatusBadge(session.status)
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SessionIconInfo(icon = Icons.Default.Event, text = session.dateLabel)
                    Spacer(modifier = Modifier.width(12.dp))
                    SessionIconInfo(icon = Icons.Default.Schedule, text = session.time)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SessionIconInfo(icon = Icons.Default.Timer, text = session.duration)
                    Spacer(modifier = Modifier.width(12.dp))
                    SessionIconInfo(icon = Icons.Default.Room, text = session.room)
                }
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            HorizontalDivider(color = Color(0xFFF1F5F9))
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Attendance Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = KikaoColors.MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Attendance",
                        color = KikaoColors.MutedText,
                        fontSize = 12.sp
                    )
                }
                
                Text(
                    text = "${session.attendanceCount} / ${session.studentCount} students",
                    color = KikaoColors.Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (session.status == SessionStatus.LIVE) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Live Attendance", fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View details",
                        color = KikaoColors.Indigo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = KikaoColors.Indigo,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionIconInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = KikaoColors.MutedText, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = KikaoColors.MutedText, fontSize = 11.sp)
    }
}

@Composable
private fun SessionStatusBadge(status: SessionStatus) {
    val (bg, fg, label) = when (status) {
        SessionStatus.LIVE -> Triple(KikaoColors.TealLight, KikaoColors.Teal, "LIVE NOW")
        SessionStatus.UPCOMING -> Triple(Color(0xFFEAF0F8), KikaoColors.Indigo, "UPCOMING")
        SessionStatus.COMPLETED -> Triple(Color(0xFFF1F5F9), KikaoColors.MutedText, "COMPLETED")
    }
    
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EmptySessionsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(KikaoColors.TealLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Event, contentDescription = null, tint = KikaoColors.Teal, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No sessions found", fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
        Text("Try changing your filter settings.", color = KikaoColors.MutedText, fontSize = 13.sp)
    }
}

// ------------------------------------------------------------
// DEMO DATA
// ------------------------------------------------------------

private fun demoSessions() = listOf(
    LecturerSession("s1", "CSC 221", "Database Systems", "Indexing & Query Optimization", "Today", "10:00", "2 hrs", "Lab 3", 120, 98, SessionStatus.LIVE),
    LecturerSession("s2", "CSC 210", "Data Structures", "Trees and Graph Theory", "Today", "14:00", "1.5 hrs", "Room B14", 96, 0, SessionStatus.UPCOMING),
    LecturerSession("s3", "CSC 305", "Software Engineering", "Agile Methodologies", "Yesterday", "16:00", "2 hrs", "LH 2", 84, 82, SessionStatus.COMPLETED),
    LecturerSession("s4", "MAT 204", "Discrete Math", "Predicate Logic", "Mon 17 Aug", "08:00", "2 hrs", "Room A07", 110, 105, SessionStatus.COMPLETED)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SessionsScreenPreview() {
    MaterialTheme {
        SessionsScreen()
    }
}
