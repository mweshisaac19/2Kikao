package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class AdminLecturerCourse(
    val code: String,
    val name: String,
    val students: Int,
    val attendance: Int,
    val status: String
)

data class AdminLecturerActivity(
    val title: String,
    val subtitle: String,
    val time: String,
    val iconType: String
)

private data class AdminLecturerDetailsData(
    val id: String,
    val name: String,
    val initials: String,
    val title: String,
    val department: String,
    val faculty: String,
    val staffNumber: String,
    val email: String,
    val phone: String,
    val campus: String,
    val office: String,
    val status: String,
    val verified: Boolean,
    val courses: Int,
    val students: Int,
    val attendance: Int,
    val sessions: Int,
    val assessments: Int
)

@Composable
fun AdminLecturerDetailsScreen(
    lecturerId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEditLecturer: () -> Unit = {},
    onViewClasses: () -> Unit = {},
    onViewResults: () -> Unit = {},
    onViewAttendance: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onDeactivateLecturer: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val lecturer = remember {
        AdminLecturerDetailsData(
            id = lecturerId,
            name = "Dr. James Mwangi",
            initials = "JM",
            title = "Senior Lecturer",
            department = "Department of Computer Science",
            faculty = "Faculty of Computing & Information Sciences",
            staffNumber = "STAFF/CS/0148",
            email = "j.mwangi@university.ac.ke",
            phone = "+254 712 345 678",
            campus = "Main Campus",
            office = "Block B · Room 214",
            status = "Active",
            verified = true,
            courses = 4,
            students = 286,
            attendance = 89,
            sessions = 42,
            assessments = 18
        )
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    val courses = remember {
        listOf(
            AdminLecturerCourse("CSC 210", "Data Structures", 96, 92, "Active"),
            AdminLecturerCourse("CSC 221", "Database Systems", 82, 87, "Active"),
            AdminLecturerCourse("CSC 315", "Software Engineering", 64, 90, "Active"),
            AdminLecturerCourse("CSC 401", "Advanced Algorithms", 44, 86, "Active")
        )
    }

    val activities = remember {
        listOf(
            AdminLecturerActivity("Posted CAT 1 results", "CSC 221 · Database Systems", "Today · 10:42 AM", "results"),
            AdminLecturerActivity("Completed attendance session", "CSC 210 · Data Structures", "Yesterday · 2:18 PM", "attendance"),
            AdminLecturerActivity("Created a new session", "CSC 315 · Software Engineering", "18 Aug · 8:05 AM", "session")
        )
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.USERS,
        screenTitle = "Lecturer details",
        screenSubtitle = lecturer.name,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF6F8FC))
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 110.dp)
        ) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color.White)) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = KikaoColors.Ink)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = lecturer.name, color = KikaoColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Staff profile & activity", color = KikaoColors.MutedText, fontSize = 11.sp)
                }
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color.White)) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = KikaoColors.Ink)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Edit lecturer") }, onClick = { showMenu = false; onEditLecturer() })
                        DropdownMenuItem(text = { Text("Message staff") }, onClick = { showMenu = false; showMessageDialog = true })
                        DropdownMenuItem(text = { Text("Deactivate", color = Color(0xFFB42318)) }, onClick = { showMenu = false; showDeactivateDialog = true })
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            LecturerHeroCard(lecturer = lecturer, onEditClick = onEditLecturer)
            Spacer(modifier = Modifier.height(20.dp))
            LecturerStatsGrid(lecturer)
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Professional information")
            Spacer(modifier = Modifier.height(10.dp))
            ProfessionalInformationCard(lecturer)
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionTitle(text = "Courses", modifier = Modifier.weight(1f))
                TextButton(onClick = onViewClasses) {
                    Text(text = "View all", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                }
            }
            courses.forEach { course ->
                LecturerCourseCard(course)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Account Status")
            Spacer(modifier = Modifier.height(10.dp))
            AccountStatusCard(lecturer = lecturer, onDeactivate = { showDeactivateDialog = true })
        }
    }

    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text(text = "Deactivate lecturer?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Account will lose access to all Kikao institutional functions.") },
            confirmButton = {
                TextButton(onClick = { showDeactivateDialog = false; onDeactivateLecturer() }) {
                    Text(text = "Deactivate", color = Color(0xFFB42318), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showDeactivateDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun LecturerHeroCard(lecturer: AdminLecturerDetailsData, onEditClick: () -> Unit) {
    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(KikaoColors.Teal), contentAlignment = Alignment.Center) {
                    Text(text = lecturer.initials, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(15.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = lecturer.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = lecturer.title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(onClick = onEditClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                Text("Edit Profile Details", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LecturerStatsGrid(lecturer: AdminLecturerDetailsData) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        LecturerStatCard("Courses", "${lecturer.courses}", KikaoColors.Teal, Modifier.weight(1f))
        LecturerStatCard("Students", "${lecturer.students}", KikaoColors.Gold, Modifier.weight(1f))
        LecturerStatCard("Presence", "${lecturer.attendance}%", KikaoColors.Indigo, Modifier.weight(1f))
    }
}

@Composable
private fun LecturerStatCard(label: String, value: String, accent: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = label, color = KikaoColors.MutedText, fontSize = 9.sp)
            Text(text = value, color = accent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ProfessionalInformationCard(lecturer: AdminLecturerDetailsData) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow("Department", lecturer.department)
            InfoRow("Faculty", lecturer.faculty)
            InfoRow("Staff ID", lecturer.staffNumber, false)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, divider: Boolean = true) {
    Column {
        Text(text = label, color = KikaoColors.MutedText, fontSize = 9.sp)
        Text(text = value, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        if (divider) Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun LecturerCourseCard(course: AdminLecturerCourse) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.name, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = "${course.code} · ${course.students} Students", color = KikaoColors.MutedText, fontSize = 10.sp)
            }
            Text(text = "${course.attendance}%", color = KikaoColors.Teal, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun AccountStatusCard(lecturer: AdminLecturerDetailsData, onDeactivate: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp).clickable(onClick = onDeactivate), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Manage Access", color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Suspend or deactivate this account", color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KikaoColors.MutedText)
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = modifier)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminLecturerDetailsPreview() {
    MaterialTheme {
        AdminLecturerDetailsScreen(lecturerId = "lec_001")
    }
}
