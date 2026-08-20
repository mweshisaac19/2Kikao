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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private object KikaoExamColors {
    val DeepIndigo = Color(0xFF0F172A)
    val Teal = Color(0xFF0F9D8A)
    val TealLight = Color(0xFFE5F7F3)
    val Green = Color(0xFF16855B)
    val GreenLight = Color(0xFFE8F7EF)
    val Amber = Color(0xFFB7791F)
    val AmberLight = Color(0xFFFFF6DD)
    val Red = Color(0xFFB42318)
    val RedLight = Color(0xFFFFECEB)
}

enum class ExamStatus {
    UPCOMING,
    ACTIVE,
    COMPLETED
}

private enum class StudentAttendance {
    PRESENT,
    ABSENT,
    LATE,
    NOT_VERIFIED
}

data class ExamSession(
    val id: String,
    val courseCode: String,
    val courseName: String,
    val paper: String,
    val date: String,
    val time: String,
    val duration: String,
    val venue: String,
    val room: String,
    val totalStudents: Int,
    val present: Int,
    val status: ExamStatus
)

private data class ExamStudent(
    val name: String,
    val admissionNo: String,
    val seat: String,
    val attendance: StudentAttendance
)

private data class Incident(
    val title: String,
    val description: String,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerExamInvigilationDashboardScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onOpenSeatingPlan: (ExamSession) -> Unit = {},
    onStartInvigilation: (ExamSession) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedExam by remember { mutableStateOf<ExamSession?>(null) }
    var showEndDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showIncidentDialog by remember { mutableStateOf(false) }

    val exams = remember {
        listOf(
            ExamSession("EX-001", "CSC 301", "Algorithms", "End Semester Exam", "Today · 9:00 AM", "9 AM – 12 PM", "3h", "Main Hall", "Hall A", 84, 0, ExamStatus.UPCOMING),
            ExamSession("EX-002", "CSC 210", "Data Structures", "End Semester Exam", "Today · 2:00 PM", "2 PM – 5 PM", "3h", "Main Hall", "Hall C", 96, 0, ExamStatus.UPCOMING)
        )
    }

    val students = remember {
        listOf(
            ExamStudent("Brian Otieno", "SCI/2024/0182", "A-01", StudentAttendance.PRESENT),
            ExamStudent("Faith Wanjiku", "BIT/2024/0074", "A-02", StudentAttendance.PRESENT),
            ExamStudent("Daniel Mwangi", "CS/2023/0451", "A-03", StudentAttendance.NOT_VERIFIED)
        )
    }

    val incidents = remember {
        listOf(Incident("Late arrival", "Student arrived at 9:17 AM.", "9:17 AM"))
    }

    val activeExam = selectedExam ?: exams.first()

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.HOME,
        screenTitle = "Exam Invigilation",
        screenSubtitle = "Manage exam papers and candidates",
        onBackClick = onBack,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ExamHeroCard(activeExam)
            }

            item {
                ExamStats(activeExam)
            }

            item {
                SectionHeader("Invigilation Workspace", "Manage seats and live attendance")
            }

            item {
                QuickActionGrid(
                    onSeatingPlan = { onOpenSeatingPlan(activeExam) },
                    onStart = { onStartInvigilation(activeExam) },
                    onIncident = { showIncidentDialog = true }
                )
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    placeholder = { Text("Search candidate or seat", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KikaoColors.Teal) },
                    shape = RoundedCornerShape(15.dp),
                    singleLine = true
                )
            }

            items(students) { student ->
                ExamStudentCard(student, Modifier.padding(horizontal = 20.dp))
            }

            item { Spacer(modifier = Modifier.height(110.dp)) }
        }
    }
}

@Composable
private fun ExamHeroCard(exam: ExamSession) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), shape = RoundedCornerShape(25.dp), colors = CardDefaults.cardColors(containerColor = KikaoExamColors.DeepIndigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(KikaoExamColors.Teal.copy(alpha = 0.17f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = KikaoExamColors.Teal)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${exam.courseCode} — ${exam.courseName}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = exam.paper, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ExamStats(exam: ExamSession) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        StatCard("${exam.totalStudents}", "Candidates", Icons.Default.Groups, Modifier.weight(1f))
        StatCard("${exam.present}", "Verified", Icons.Default.CheckCircle, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = KikaoColors.Teal, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = KikaoColors.MutedText, fontSize = 9.sp)
        }
    }
}

@Composable
private fun QuickActionGrid(onSeatingPlan: () -> Unit, onStart: () -> Unit, onIncident: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickAction("Seats", Icons.Default.EventSeat, Modifier.weight(1f), onSeatingPlan)
        QuickAction("Start", Icons.Default.PlayArrow, Modifier.weight(1f), onStart)
        QuickAction("Incident", Icons.Default.Flag, Modifier.weight(1f), onIncident)
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = KikaoColors.Indigo)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ExamStudentCard(student: ExamStudent, modifier: Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Seat: ${student.seat} · ${student.admissionNo}", fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            StatusPill(student.attendance)
        }
    }
}

@Composable
private fun StatusPill(status: StudentAttendance) {
    val color = when(status) {
        StudentAttendance.PRESENT -> KikaoExamColors.Green
        else -> KikaoExamColors.Amber
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 3.dp)) {
        Text(status.name, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
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
private fun LecturerExamInvigilationDashboardPreview() {
    MaterialTheme {
        LecturerExamInvigilationDashboardScreen()
    }
}
