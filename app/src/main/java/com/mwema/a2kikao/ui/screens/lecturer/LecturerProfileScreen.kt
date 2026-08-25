package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.AttendanceRequestData
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerProfileViewModel

@Composable
fun LecturerProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onConsultationSetup: () -> Unit = {},
    onExamDashboardClick: () -> Unit = {},
    onSystemHealthClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedRequest by remember { mutableStateOf<AttendanceRequestData?>(null) }

    val userProfile by viewModel.userProfile.collectAsState()
    val leaveRequests by viewModel.leaveRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val lecturerName = userProfile?.fullName ?: "Prof. Mwema"
    val school = userProfile?.school ?: "Faculty of Science & IT"

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.PROFILE,
        screenTitle = "Your profile",
        screenSubtitle = "Account, preferences and requests",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            
            // Hero Card
            LecturerHeroCard(lecturerName, school, onEditProfile)
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Preferences Section
            SectionLabel("Preferences")
            Spacer(modifier = Modifier.height(12.dp))
            PreferenceCard(
                title = "Push Notifications",
                subtitle = "Alerts for student check-ins and messages",
                enabled = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // New Action: Consultation Hours
            ActionCard(
                title = "Consultation Hours",
                subtitle = "Set your weekly availability for students",
                icon = Icons.Default.Schedule,
                onClick = onConsultationSetup
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "Exam Invigilation",
                subtitle = "Manage your assigned exam sessions",
                icon = Icons.Default.Assignment,
                onClick = onExamDashboardClick
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            SectionLabel("Support & Health")
            Spacer(modifier = Modifier.height(12.dp))
            ActionCard(
                title = "System Health",
                subtitle = "View infrastructure status",
                icon = Icons.Default.CloudDone,
                onClick = onSystemHealthClick
            )

            Spacer(modifier = Modifier.height(28.dp))
            
            // Leave Requests Section
            SectionLabel("Requests & Disputes")
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KikaoColors.Teal)
                }
            } else {
                LeaveManagementSection(
                    requests = leaveRequests,
                    onRequestClick = { selectedRequest = it }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Actions
            SignOutButton(onSignOut = {
                com.mwema.a2kikao.data.FirebaseManager.signOut()
                onSignOut()
            })
        }
    }

    if (selectedRequest != null) {
        LeaveRequestDialog(
            request = selectedRequest!!,
            onApprove = {
                viewModel.updateRequestStatus(selectedRequest!!.id, "APPROVED")
                selectedRequest = null
            },
            onReject = {
                viewModel.updateRequestStatus(selectedRequest!!.id, "REJECTED")
                selectedRequest = null
            },
            onDismiss = { selectedRequest = null }
        )
    }
}

@Composable
private fun LecturerHeroCard(name: String, school: String, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(70.dp).clip(CircleShape).background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
                        color = Color.White, 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.width(18.dp))
                Column {
                    Text(text = name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = school, color = Color.White.copy(alpha = 0.90f), fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Text("Edit Profile Details", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PreferenceCard(title: String, subtitle: String, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Switch(
                checked = enabled, 
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = KikaoColors.Teal)
            )
        }
    }
}

@Composable
private fun ActionCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(KikaoColors.Indigo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = KikaoColors.Indigo, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
private fun LeaveManagementSection(requests: List<AttendanceRequestData>, onRequestClick: (AttendanceRequestData) -> Unit) {
    if (requests.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Text(
                "No pending requests or disputes", 
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (request in requests) {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onRequestClick(request) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = request.studentName, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                text = "${if (request.type == "LEAVE") "Leave" else "Dispute"} · Class: ${request.classId}", 
                                color = Color.White.copy(alpha = 0.7f), 
                                fontSize = 11.sp
                            )
                        }
                        Text(text = "Review ›", color = KikaoColors.TealLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaveRequestDialog(request: AttendanceRequestData, onApprove: () -> Unit, onReject: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (request.type == "LEAVE") "Leave Request" else "Attendance Dispute", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(request.studentName, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(request.studentId, fontSize = 12.sp, color = KikaoColors.MutedText)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Reason / Issue:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(request.reason, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (request.type == "LEAVE") {
                    Text("Duration: ${request.startDate} to ${request.endDate}", fontSize = 12.sp)
                } else {
                    Text("Affected Session: ${request.affectedSessionDate}", fontSize = 12.sp)
                }
                if (request.details.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Additional Details:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(request.details, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)) {
                Text("Approve")
            }
        },
        dismissButton = {
            TextButton(onClick = onReject) {
                Text("Reject", color = Color(0xFFDC3545))
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
private fun SignOutButton(onSignOut: () -> Unit) {
    Button(
        onClick = onSignOut,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Text("Sign Out of Kikao", color = Color(0xFFDC3545), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label.uppercase(), 
        color = Color.White.copy(alpha = 0.90f), 
        fontSize = 11.sp, 
        fontWeight = FontWeight.ExtraBold, 
        letterSpacing = 1.sp
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerProfilePreview() {
    MaterialTheme {
        LecturerProfileScreen()
    }
}
