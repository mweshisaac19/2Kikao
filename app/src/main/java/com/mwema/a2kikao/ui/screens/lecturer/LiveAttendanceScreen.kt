package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LiveAttendanceViewModel
import kotlinx.coroutines.delay

// ------------------------------------------------------------
// DATA MODELS
// ------------------------------------------------------------

data class VerifiedStudent(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val verificationTime: String,
    val initials: String
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun LiveAttendanceScreen(
    sessionId: String,
    modifier: Modifier = Modifier,
    viewModel: LiveAttendanceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    courseCode: String = "CSC 221",
    courseName: String = "Database Systems",
    sessionTopic: String = "Introduction to SQL",
    room: String = "Lab 3",
    totalStudents: Int = 48,
    onEndSession: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val qrToken by viewModel.currentQrToken.collectAsState()
    var presentCount by remember { mutableIntStateOf(0) }
    val verifiedStudents = remember { mutableStateListOf<VerifiedStudent>() }
    var showEndConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.startQrRotation(sessionId)
    }
    
    // Simulate incoming students
    LaunchedEffect(Unit) {
        delay(2000)
        verifiedStudents.add(VerifiedStudent("s1", "Amani Mwangi", "SC211/1234/2025", "10:04 AM", "AM"))
        presentCount++
        delay(3000)
        verifiedStudents.add(VerifiedStudent("s2", "John Doe", "SC211/5678/2025", "10:12 AM", "JD"))
        presentCount++
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            LiveAttendanceHeader(
                courseCode = courseCode,
                presentCount = presentCount,
                totalStudents = totalStudents,
                onBack = onBack
            )
        },
        bottomBar = {
            EndSessionBar(onEndClick = { showEndConfirmation = true })
        }
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // QR Section
            QrRotationSection(token = "$sessionId|$qrToken")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // List Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live attendance list",
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = KikaoColors.TealLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = KikaoColors.Teal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Verified Students List
            if (verifiedStudents.isEmpty()) {
                EmptyLiveState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(verifiedStudents) { student ->
                        VerifiedStudentRow(student)
                    }
                }
            }
        }
    }
    
    if (showEndConfirmation) {
        EndSessionDialog(
            present = presentCount,
            onConfirm = onEndSession,
            onDismiss = { showEndConfirmation = false }
        )
    }
}

// ------------------------------------------------------------
// COMPONENTS
// ------------------------------------------------------------

@Composable
private fun LiveAttendanceHeader(
    courseCode: String,
    presentCount: Int,
    totalStudents: Int,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "LIVE SESSION", color = KikaoColors.Gold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                    Text(text = courseCode, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Currently present", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(text = "$presentCount / $totalStudents", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                }
                
                CircularProgressIndicator(
                    progress = { presentCount / totalStudents.toFloat() },
                    modifier = Modifier.size(56.dp),
                    color = KikaoColors.Gold,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    strokeWidth = 6.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun QrRotationSection(token: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(2.dp, KikaoColors.TealLight, RoundedCornerShape(24.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Simulated QR - In a real app, this would use a QR generator lib
            // For now, we display the token to show it's working
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = "Session QR",
                    modifier = Modifier.size(120.dp),
                    tint = KikaoColors.Indigo
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = token.takeLast(8), // Show last 8 chars for debug/demo
                    fontSize = 10.sp,
                    color = KikaoColors.Indigo,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "QR rotates every 15 seconds for security",
            color = KikaoColors.MutedText,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun VerifiedStudentRow(student: VerifiedStudent) {
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
            
            Text(text = student.verificationTime, color = KikaoColors.MutedText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun EndSessionBar(onEndClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Button(
            onClick = onEndClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
        ) {
            Text("End and Save Session", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyLiveState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = KikaoColors.Teal, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Waiting for students...", color = KikaoColors.MutedText, fontSize = 14.sp)
    }
}

@Composable
private fun EndSessionDialog(present: Int, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("End Session?", fontWeight = FontWeight.Bold) },
        text = { Text("You have $present students verified. Ending the session will finalize the attendance list for this class.") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = KikaoColors.MutedText)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LiveAttendancePreview() {
    MaterialTheme {
        LiveAttendanceScreen(sessionId = "demo_session")
    }
}
