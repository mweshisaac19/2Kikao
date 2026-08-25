package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val verifiedStudents by viewModel.verifiedStudents.collectAsState()
    val totalExpected by viewModel.totalExpectedStudents.collectAsState()
    val presentCount = verifiedStudents.size
    
    var showEndConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.startSession(sessionId, courseCode)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            LiveAttendanceHeader(
                courseCode = courseCode,
                presentCount = presentCount,
                totalStudents = if (totalExpected > 0) totalExpected else totalStudents,
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
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally { -it } + fadeIn()
                        ) {
                            VerifiedStudentRow(student)
                        }
                    }
                }
            }
        }
    }
    
    if (showEndConfirmation) {
        EndSessionDialog(
            present = presentCount,
            onConfirm = {
                viewModel.endSession(sessionId, onEndSession)
            },
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
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
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
                    Text(text = "Currently present", color = Color.White, fontSize = 12.sp)
                    Text(text = "$presentCount / $totalStudents", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                }
                
                CircularProgressIndicator(
                    progress = { presentCount / totalStudents.toFloat().coerceAtLeast(1f) },
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
    var progress by remember { mutableFloatStateOf(1f) }
    
    LaunchedEffect(token) {
        progress = 1f
        val startTime = System.currentTimeMillis()
        val duration = 15000L
        while (System.currentTimeMillis() - startTime < duration) {
            progress = 1f - (System.currentTimeMillis() - startTime).toFloat() / duration
            delay(100)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular progress background
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = KikaoColors.Teal,
                strokeWidth = 6.dp,
                trackColor = KikaoColors.TealLight
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(2.dp, KikaoColors.TealLight, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Session QR",
                        modifier = Modifier.size(130.dp),
                        tint = KikaoColors.Indigo
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = token.takeLast(8),
                        fontSize = 12.sp,
                        color = KikaoColors.Indigo,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "QR rotates every 15 seconds for security",
            color = KikaoColors.MutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
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
                .navigationBarsPadding()
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
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Radius"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(100.dp)) {
                drawCircle(
                    color = KikaoColors.Teal.copy(alpha = 1f - pulse),
                    radius = pulse * size.width / 2,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            Icon(
                imageVector = Icons.Default.Radar,
                contentDescription = null,
                tint = KikaoColors.Teal,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Broadcasting secure QR...", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text("Waiting for first student to scan", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
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
