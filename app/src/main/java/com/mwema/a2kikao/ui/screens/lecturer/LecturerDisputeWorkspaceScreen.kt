package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class AttendanceDispute(
    val id: String,
    val studentName: String,
    val regNo: String,
    val courseCode: String,
    val sessionDate: String,
    val reason: String,
    val status: String // "Pending", "Approved", "Denied"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerDisputeWorkspaceScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val disputes = remember {
        mutableStateListOf(
            AttendanceDispute("d1", "Kevin Kiptoo", "SC211/1028/2025", "CSC 221", "12 Aug", "App crashed during QR scan.", "Pending"),
            AttendanceDispute("d2", "Brian Otieno", "SC211/1187/2025", "CSC 210", "15 Aug", "Was present but forgot to scan.", "Pending")
        )
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.HOME, // Arbitrary, could be a new tab
        screenTitle = "Attendance Disputes",
        screenSubtitle = "Review and resolve student requests",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp), tint = KikaoColors.Teal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to home", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            
            if (disputes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pending disputes", color = KikaoColors.MutedText)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(disputes) { dispute ->
                        DisputeCard(
                            dispute = dispute,
                            onApprove = { disputes.remove(dispute) },
                            onDeny = { disputes.remove(dispute) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisputeCard(dispute: AttendanceDispute, onApprove: () -> Unit, onDeny: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(KikaoColors.Indigo.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Text(text = dispute.studentName.take(1), color = KikaoColors.Indigo, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = dispute.studentName, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = dispute.regNo, color = KikaoColors.MutedText, fontSize = 11.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(text = "Course: ${dispute.courseCode} · Date: ${dispute.sessionDate}", fontSize = 12.sp, color = KikaoColors.Ink, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = dispute.reason, fontSize = 12.sp, color = KikaoColors.MutedText, lineHeight = 18.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Approve", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onDeny,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Deny", color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerDisputeWorkspacePreview() {
    MaterialTheme {
        LecturerDisputeWorkspaceScreen()
    }
}
