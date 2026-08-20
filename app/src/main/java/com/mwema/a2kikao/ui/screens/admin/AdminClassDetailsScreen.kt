package com.mwema.a2kikao.ui.screens.admin

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private enum class AdminClassSection {
    OVERVIEW,
    STUDENTS,
    ATTENDANCE,
    SESSIONS
}

private data class AdminClassStudent(
    val name: String,
    val registration: String,
    val attendance: Int,
    val average: Int,
    val status: String
)

private data class AdminClassSession(
    val title: String,
    val date: String,
    val time: String,
    val room: String,
    val attendance: String,
    val status: String
)

@Composable
fun AdminClassDetailsScreen(
    classId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onEditClass: () -> Unit = {},
    onManageLecturer: () -> Unit = {},
    onAddSession: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedSection by remember { mutableStateOf(AdminClassSection.OVERVIEW) }
    val students = remember { demoClassStudents() }
    val sessions = remember { demoClassSessions() }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Class details",
        screenSubtitle = classId,
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
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)) {
                    Text("‹", color = KikaoColors.Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Data Structures", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = classId, color = KikaoColors.MutedText, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(text = "INSTITUTIONAL METRICS", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ClassMetric("Presence", "87%")
                        ClassMetric("Avg Grade", "74%")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFEAF0F8)).padding(4.dp)) {
                AdminClassTabItem("Overview", selectedSection == AdminClassSection.OVERVIEW, { selectedSection = AdminClassSection.OVERVIEW }, Modifier.weight(1f))
                AdminClassTabItem("Students", selectedSection == AdminClassSection.STUDENTS, { selectedSection = AdminClassSection.STUDENTS }, Modifier.weight(1f))
                AdminClassTabItem("Sessions", selectedSection == AdminClassSection.SESSIONS, { selectedSection = AdminClassSection.SESSIONS }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedSection) {
                AdminClassSection.OVERVIEW -> ClassOverviewContent(onEditClass, onManageLecturer)
                AdminClassSection.STUDENTS -> ClassStudentsContent(students, onStudentClick)
                AdminClassSection.SESSIONS -> ClassSessionsContent(sessions, onAddSession)
                else -> {}
            }
        }
    }
}

@Composable
private fun IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.clickable(onClick = onClick), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun ClassMetric(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
        Text(text = value, color = KikaoColors.Gold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AdminClassTabItem(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(if (isSelected) KikaoColors.Indigo else Color.Transparent).clickable(onClick = onClick).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text(text = text, color = if (isSelected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ClassOverviewContent(onEdit: () -> Unit, onLecturer: () -> Unit) {
    Column {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Primary Lecturer", color = KikaoColors.MutedText, fontSize = 10.sp)
                Text(text = "Dr. James Kamau", color = KikaoColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    androidx.compose.material3.Button(onClick = onEdit, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Edit Class") }
                    androidx.compose.material3.OutlinedButton(onClick = onLecturer, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Lecturer") }
                }
            }
        }
    }
}

@Composable
private fun ClassStudentsContent(students: List<AdminClassStudent>, onStudentClick: (String) -> Unit) {
    Column {
        students.forEach { student ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onStudentClick(student.registration) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = student.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = student.registration, color = KikaoColors.MutedText, fontSize = 11.sp)
                    }
                    Text(text = "${student.attendance}%", color = KikaoColors.Teal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ClassSessionsContent(sessions: List<AdminClassSession>, onAdd: () -> Unit) {
    Column {
        sessions.forEach { session ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = session.title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${session.date} · ${session.room}", color = KikaoColors.MutedText, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private fun demoClassStudents() = listOf(
    AdminClassStudent("Amani Mwangi", "SC211/1234/2025", 94, 82, "Excellent"),
    AdminClassStudent("Brian Otieno", "SC211/1187/2025", 88, 76, "Good")
)

private fun demoClassSessions() = listOf(
    AdminClassSession("Intro to Trees", "19 Aug", "10:00 AM", "Lab 2", "108/120", "Completed")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminClassDetailsPreview() {
    MaterialTheme {
        AdminClassDetailsScreen(classId = "CSC210")
    }
}
