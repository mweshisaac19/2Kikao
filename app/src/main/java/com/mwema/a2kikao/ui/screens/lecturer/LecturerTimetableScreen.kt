package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerTimetableViewModel

@Composable
fun LecturerTimetableScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerTimetableViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onStartAttendance: (CourseClass) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val schedule by viewModel.schedule.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.SESSIONS,
        screenTitle = "Teaching Schedule",
        screenSubtitle = "Your weekly academic commitments",
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
                    .padding(bottom = 32.dp)
            ) {
                
                TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White.copy(alpha = 0.90f))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back to sessions", color = Color.White.copy(alpha = 0.90f), fontWeight = FontWeight.Bold)
                }

                val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                days.forEach { day ->
                    DaySection(
                        day = day,
                        classes = schedule
                            .filter { it.day.equals(day, ignoreCase = true) || it.days.any { d -> d.equals(day, ignoreCase = true) } }
                            .sortedBy { it.time.substringBefore("-").trim() },
                        onStartAttendance = onStartAttendance
                    )
                }
            }
        }
    }
}

@Composable
private fun DaySection(day: String, classes: List<CourseClass>, onStartAttendance: (CourseClass) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = day.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.90f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (classes.isEmpty()) {
            Text("No classes scheduled", color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
        } else {
            classes.forEach { course ->
                ClassScheduleCard(course, onStartAttendance)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun ClassScheduleCard(course: CourseClass, onStartAttendance: (CourseClass) -> Unit) {
    val accent = when(course.code.take(3)) {
        "CSC" -> KikaoColors.Teal
        "MAT" -> Color(0xFF8B5CF6)
        else -> KikaoColors.Indigo
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(4.dp).height(40.dp).clip(RoundedCornerShape(4.dp)).background(accent))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${course.code}: ${course.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KikaoColors.Ink)
                    Text(text = "${course.time} · ${course.room}", fontSize = 12.sp, color = KikaoColors.MutedText)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { onStartAttendance(course) },
                modifier = Modifier.fillMaxWidth().height(38.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo.copy(alpha = 0.08f), contentColor = KikaoColors.Indigo),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Start Live Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerTimetablePreview() {
    MaterialTheme {
        LecturerTimetableScreen()
    }
}
