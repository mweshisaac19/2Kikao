package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
// MODELS
// ------------------------------------------------------------

private enum class ClassDetailTab {
    STUDENTS,
    ATTENDANCE,
    SESSIONS
}

private data class ClassStudent(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val attendance: Int,
    val performance: Int,
    val sessionsAttended: Int,
    val totalSessions: Int,
    val latestAssessment: String,
    val latestScore: String,
    val status: String,
    val initials: String
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun ClassDetailsScreen(
    courseId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onViewAnalytics: (String, String) -> Unit = { _, _ -> },
    onStudentClick: (String) -> Unit = {},
    onSessionClick: (String) -> Unit = {},
    onViewContent: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(ClassDetailTab.STUDENTS) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    val students = remember { demoClassStudents() }
    val filteredStudents = students.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.registrationNumber.contains(searchQuery, ignoreCase = true)
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.CLASSES,
        screenTitle = "Database Systems",
        screenSubtitle = "CSC 221 · 120 Students",
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
            
            // Back Action
            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            ) {
                Text("‹ Back to all classes", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            
            // Summary Card
            ClassSummaryHeader(onViewContent = { onViewContent(courseId) })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tab Switcher
            ClassTabSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Content based on tab
            when (selectedTab) {
                ClassDetailTab.STUDENTS -> {
                    StudentsListSection(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        students = filteredStudents,
                        onStudentClick = onStudentClick
                    )
                }
                ClassDetailTab.ATTENDANCE -> {
                    AttendanceAnalyticsSection(
                        onViewFullAnalytics = { onViewAnalytics("CSC 221", "Database Systems") }
                    )
                }
                ClassDetailTab.SESSIONS -> {
                    SessionsHistorySection(
                        onSessionClick = onSessionClick
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------
// COMPONENTS
// ------------------------------------------------------------

@Composable
private fun ClassSummaryHeader(onViewContent: () -> Unit) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CSC 221",
                        color = KikaoColors.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Database Systems",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onViewContent,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.Default.FolderCopy, contentDescription = "Content", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryMetric(label = "Attendance", value = "87%", modifier = Modifier.weight(1f))
                SummaryMetric(label = "Students", value = "120", modifier = Modifier.weight(1f))
                SummaryMetric(label = "Performance", value = "72%", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(12.dp)
    ) {
        Text(text = value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
    }
}

@Composable
private fun ClassTabSwitcher(
    selectedTab: ClassDetailTab,
    onTabSelected: (ClassDetailTab) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEAF0F8))
            .padding(4.dp)
    ) {
        ClassDetailTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) KikaoColors.Indigo else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = if (selected) Color.White else KikaoColors.MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StudentsListSection(
    query: String,
    onQueryChange: (String) -> Unit,
    students: List<ClassStudent>,
    onStudentClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // Search
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by name or reg number...", fontSize = 14.sp) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KikaoColors.Teal,
                unfocusedBorderColor = Color(0xFFE2E8F0),
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // List
        students.forEach { student ->
            StudentListItem(student, onClick = { onStudentClick(student.id) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StudentListItem(student: ClassStudent, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
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
                Text(text = "${student.attendance}%", color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Attendance", color = KikaoColors.MutedText, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun AttendanceAnalyticsSection(
    onViewFullAnalytics: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            "Overall attendance is 87%. Most students are consistent, but 8 students are below the 75% threshold.",
            color = KikaoColors.MutedText,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = onViewFullAnalytics,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)
        ) {
            Text("View Full Attendance Analytics", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SessionsHistorySection(onSessionClick: (String) -> Unit) {
    val sessions = listOf(
        Pair("18 Aug", "Indexing & Optimization"),
        Pair("11 Aug", "SQL Subqueries"),
        Pair("04 Aug", "Relational Algebra")
    )
    
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        sessions.forEach { (date, topic) ->
            Card(
                onClick = { onSessionClick("s1") }, // demo id
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(date, color = KikaoColors.MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(topic, color = KikaoColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("View ›", color = KikaoColors.Teal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ------------------------------------------------------------
// DEMO DATA
// ------------------------------------------------------------

private fun demoClassStudents() = listOf(
    ClassStudent("s1", "Amani Mwangi", "SC211/1234/2025", 87, 84, 13, 15, "CAT 1", "18/20", "Normal", "AM"),
    ClassStudent("s2", "John Doe", "SC211/5678/2025", 72, 65, 11, 15, "CAT 1", "12/20", "At Risk", "JD")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClassDetailsPreview() {
    MaterialTheme {
        ClassDetailsScreen(courseId = "csc221")
    }
}
