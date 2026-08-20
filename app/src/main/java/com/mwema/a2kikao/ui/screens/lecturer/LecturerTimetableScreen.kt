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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class TimetableEvent(
    val day: String,
    val time: String,
    val course: String,
    val room: String,
    val type: String // "Lecture", "Lab", "Office"
)

@Composable
fun LecturerTimetableScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val schedule = listOf(
        TimetableEvent("Monday", "10:00 - 12:00", "CSC 221: Database Systems", "Lab 3", "Lab"),
        TimetableEvent("Monday", "14:00 - 15:30", "Office Hours", "Office B12", "Office"),
        TimetableEvent("Tuesday", "08:00 - 10:00", "MAT 204: Discrete Math", "LH 2", "Lecture"),
        TimetableEvent("Wednesday", "11:00 - 13:00", "CSC 210: Data Structures", "Room B04", "Lecture")
    )

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.SESSIONS,
        screenTitle = "Teaching Schedule",
        screenSubtitle = "Your weekly academic commitments",
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            
            TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp), tint = KikaoColors.Teal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to sessions", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            // Simple Day-based view
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday").forEach { day ->
                DaySection(
                    day = day,
                    events = schedule.filter { it.day == day }
                )
            }
        }
    }
}

@Composable
private fun DaySection(day: String, events: List<TimetableEvent>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = day.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = KikaoColors.MutedText, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (events.isEmpty()) {
            Text("No classes scheduled", color = Color.LightGray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
        } else {
            events.forEach { event ->
                EventCard(event)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun EventCard(event: TimetableEvent) {
    val accent = when(event.type) {
        "Lab" -> KikaoColors.Teal
        "Office" -> KikaoColors.Gold
        else -> KikaoColors.Indigo
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(40.dp).clip(RoundedCornerShape(4.dp)).background(accent))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.course, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KikaoColors.Ink)
                Text(text = "${event.time} · ${event.room}", fontSize = 12.sp, color = KikaoColors.MutedText)
            }
            Badge(containerColor = accent.copy(alpha = 0.1f), contentColor = accent) {
                Text(event.type, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
