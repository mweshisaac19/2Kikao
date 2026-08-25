package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextAlign
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerHomeClass
import com.mwema.a2kikao.ui.viewmodels.LecturerHomeViewModel
import kotlinx.coroutines.delay

private data class UpcomingClass(
    val code: String,
    val name: String,
    val time: String,
    val room: String,
    val students: Int,
    val duration: String,
    val accent: Color,
    val statusLabel: String? = null
)

@Composable
fun LecturerHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStartAttendance: (courseCode: String) -> Unit = {},
    onViewClass: (courseCode: String) -> Unit = {},
    onViewAllClasses: () -> Unit = {},
    onViewStudents: () -> Unit = {},
    onTeachingPulseClick: () -> Unit = {},
    onTimetableClick: () -> Unit = {},
    onDisputeClick: () -> Unit = {},
    onExamInvigilationClick: () -> Unit = {},
    onCancellationClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val homeClasses by viewModel.homeClasses.collectAsState()
    val nextScheduledClass by viewModel.nextClass.collectAsState()
    val pendingRequestsCount by viewModel.pendingRequestsCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val lecturerName = userProfile?.fullName ?: "Lecturer"
    
    val upcomingClasses = homeClasses.mapIndexed { index, homeClass ->
        UpcomingClass(
            code = homeClass.course.code,
            name = homeClass.course.name,
            time = homeClass.course.time.substringBefore("-").trim(),
            room = homeClass.course.room,
            students = homeClass.course.studentsEnrolled.size,
            duration = "2 hrs",
            accent = when {
                homeClass.isDone -> Color.Gray
                homeClass.isMissed -> Color(0xFFDC3545)
                index % 3 == 0 -> KikaoColors.Teal
                index % 3 == 1 -> KikaoColors.Gold
                else -> Color(0xFF8B5CF6)
            },
            statusLabel = when {
                homeClass.isDone -> "COMPLETED"
                homeClass.isMissed -> "CLASS NOT DONE"
                else -> null
            }
        )
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.HOME,
        screenTitle = "Command center",
        screenSubtitle = "Your teaching day at a glance",
        lecturerName = lecturerName,
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KikaoColors.Teal)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .padding(bottom = 30.dp)
            ) {
                WelcomeCard(lecturerName = lecturerName, classCount = upcomingClasses.size)
                
                Spacer(modifier = Modifier.height(18.dp))

                if (nextScheduledClass != null) {
                    ActiveSessionSuggest(
                        course = nextScheduledClass!!,
                        onStart = { onStartAttendance(it.code) }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                DailyStats(classCount = upcomingClasses.size, studentCount = upcomingClasses.sumOf { it.students })
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Today's schedule", action = "View all", onActionClick = onViewAllClasses)
                Spacer(modifier = Modifier.height(12.dp))
                
                if (upcomingClasses.isEmpty()) {
                    NoClassesTodayCard()
                } else {
                    upcomingClasses.forEachIndexed { index, course ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(index * 120L)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically { it } + fadeIn()
                        ) {
                            UpcomingClassCard(
                                course = course,
                                isNext = index == 0,
                                onStartAttendance = { onStartAttendance(course.code) },
                                onViewClass = { onViewClass(course.code) }
                            )
                        }
                        if (index != upcomingClasses.lastIndex) Spacer(modifier = Modifier.height(13.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Teaching pulse", action = "View analytics", onActionClick = onTeachingPulseClick)
                Spacer(modifier = Modifier.height(12.dp))
                TeachingPulseCard(onClick = onTeachingPulseClick)
                Spacer(modifier = Modifier.height(14.dp))
                AttentionCard(
                    count = pendingRequestsCount,
                    onViewRequests = { onTabSelected(LecturerTab.PROFILE) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                QuickActions(onTimetableClick, onViewStudents, onDisputeClick, onExamInvigilationClick, onCancellationClick)
            }
        }
    }
}

@Composable
private fun ActiveSessionSuggest(
    course: CourseClass,
    onStart: (CourseClass) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.TealLight.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, KikaoColors.Teal.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(KikaoColors.Teal),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White)
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text("Scheduled now", color = KikaoColors.Teal, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                Text("${course.code}: ${course.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${course.time} · ${course.room}", fontSize = 12.sp, color = KikaoColors.MutedText)
            }
            
            Button(
                onClick = { onStart(course) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                Text("Start", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun NoClassesTodayCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {
                Text("📅", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("No classes scheduled for today", fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
            Text("Use this time for research or preparation.", color = KikaoColors.MutedText, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun WelcomeCard(lecturerName: String, classCount: Int) {
    val currentDate = remember { SimpleDateFormat("EEEE • d MMMM", Locale.getDefault()).format(Date()).uppercase() }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(currentDate, color = KikaoColors.Gold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(7.dp))
            Text("Good afternoon, ${lecturerName.substringBefore(" ")}", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(5.dp))
            Text("You have $classCount classes scheduled today.", color = Color.White.copy(alpha = 0.95f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun DailyStats(classCount: Int, studentCount: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DailyStatCard(value = classCount.toString(), label = "Classes", modifier = Modifier.weight(1f))
        DailyStatCard(value = studentCount.toString(), label = "Students", modifier = Modifier.weight(1f))
        DailyStatCard(value = "87%", label = "Attendance", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DailyStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier, 
        shape = RoundedCornerShape(18.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Text(value, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, action: String?, onActionClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action != null) Text(action, color = KikaoColors.TealLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onActionClick))
    }
}

@Composable
private fun UpcomingClassCard(course: UpcomingClass, isNext: Boolean, onStartAttendance: () -> Unit, onViewClass: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onViewClass), 
        shape = RoundedCornerShape(22.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(course.accent.copy(alpha = 0.20f)), contentAlignment = Alignment.Center) {
                    Text("▦", color = course.accent, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(course.code, color = course.accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    Text(course.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                if (isNext) {
                    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "Scale"
                    )
                    Box(modifier = Modifier.scale(pulseScale).clip(RoundedCornerShape(6.dp)).background(KikaoColors.Gold).padding(horizontal = 7.dp, vertical = 4.dp)) {
                        Text("NEXT", color = KikaoColors.DeepIndigo, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                    }
                } else if (course.statusLabel != null) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(course.accent.copy(alpha = 0.25f)).padding(horizontal = 7.dp, vertical = 4.dp)) {
                        Text(course.statusLabel, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(13.dp))
            Button(
                onClick = onStartAttendance, 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(12.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo),
                enabled = course.statusLabel != "COMPLETED"
            ) {
                Text(if (course.statusLabel == "COMPLETED") "Attendance Recorded" else "Start Attendance", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TeachingPulseCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), 
        shape = RoundedCornerShape(21.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text("Teaching pulse trend looks strong.", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Engagement is up 12% this week.", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun AttentionCard(count: Int, onViewRequests: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(), 
        shape = RoundedCornerShape(21.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(KikaoColors.Gold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("!", color = KikaoColors.Gold, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (count > 0) "$count pending requests" else "No pending items", 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White
                )
                Text(
                    text = if (count > 0) "Students are awaiting your response" else "Everything is up to date", 
                    color = Color.White.copy(alpha = 0.7f), 
                    fontSize = 11.sp
                )
            }
            Text(
                text = "View ›", 
                modifier = Modifier.clickable(onClick = onViewRequests), 
                color = KikaoColors.TealLight,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun QuickActions(onTimetable: () -> Unit, onViewStudents: () -> Unit, onDisputes: () -> Unit, onExams: () -> Unit, onCancel: () -> Unit) {
    Column {
        Text("Quick actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onTimetable, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) { Text("Timetable", fontSize = 10.sp, color = Color.White) }
            Button(onClick = onViewStudents, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) { Text("Students", fontSize = 10.sp, color = Color.White) }
            Button(onClick = onDisputes, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) { Text("Disputes", fontSize = 10.sp, color = Color.White) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onExams, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) { Text("Exams", fontSize = 10.sp, color = Color.White) }
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))) { Text("Cancel", fontSize = 10.sp, color = Color.White) }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerHomeScreenPreview() {
    MaterialTheme {
        LecturerHomeScreen()
    }
}
