package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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
import com.mwema.a2kikao.ui.viewmodels.TimetableViewModel
import java.text.SimpleDateFormat
import java.util.*

data class TimetableClass(
    val courseCode: String,
    val courseName: String,
    val lecturer: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val type: String,
    val day: Int,
    val accent: Color
)

private data class DayInfo(
    val name: String,
    val date: String,
    val dayOfMonth: String,
    val fullDate: String
)

@Composable
fun TimetableScreen(
    modifier: Modifier = Modifier,
    viewModel: TimetableViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onClassClick: (TimetableClass) -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val weekDays = remember { getCurrentWeekDays() }
    val currentDayIndex = remember { 
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        // Calendar.MONDAY is 2, Kikao indices are 0-4
        (day - Calendar.MONDAY).coerceIn(0, 4)
    }
    
    var selectedDay by remember { mutableIntStateOf(currentDayIndex) }
    val realClasses by viewModel.classes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val mappedClasses = realClasses.flatMap { course ->
        val classDays = if (course.days.isNotEmpty()) course.days else listOf(course.day)
        classDays.map { dayStr ->
            TimetableClass(
                courseCode = course.code,
                courseName = course.name,
                lecturer = course.lecturer,
                room = course.room,
                startTime = course.time.substringBefore("-").trim(),
                endTime = course.time.substringAfter("-").trim(),
                type = "Session",
                day = when (dayStr.lowercase()) {
                    "monday" -> 0
                    "tuesday" -> 1
                    "wednesday" -> 2
                    "thursday" -> 3
                    "friday" -> 4
                    "saturday" -> 5
                    "sunday" -> 6
                    else -> 0
                },
                accent = when (course.code.take(3)) {
                    "CSC" -> KikaoColors.Teal
                    "MAT" -> Color(0xFF8B5CF6)
                    "BIT" -> KikaoColors.Gold
                    else -> KikaoColors.Indigo
                }
            )
        }
    }

    val displayClasses = if (mappedClasses.isNotEmpty()) mappedClasses else demoTimetableClasses()
    val selectedDayClasses = displayClasses
        .filter { it.day == selectedDay }
        .sortedBy { it.startTime }

    val todayClassCount = selectedDayClasses.size

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.HOME,
        screenTitle = "My timetable",
        screenSubtitle = "Semester 1 · Week 3",
        showScanButton = false,
        onBackClick = onBackClick,
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .padding(bottom = 40.dp)
        ) {

            TimetableSummaryCard(classCount = todayClassCount)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "This week",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(11.dp))

            DaySelector(
                selectedDay = selectedDay,
                days = weekDays,
                onDaySelected = { selectedDay = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    val selectedDateInfo = weekDays[selectedDay]
                    Text(
                        text = selectedDateInfo.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedDateInfo.fullDate,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "$todayClassCount ${if (todayClassCount == 1) "class" else "classes"}",
                    color = KikaoColors.Teal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(KikaoColors.TealLight)
                        .padding(horizontal = 9.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            if (selectedDayClasses.isEmpty()) {
                EmptyDayCard()
            } else {
                selectedDayClasses.forEachIndexed { index, timetableClass ->
                    TimetableClassCard(
                        timetableClass = timetableClass,
                        isNext = index == 0 && selectedDay == 1, // Simple logic for demo
                        onClick = { onClassClick(timetableClass) }
                    )
                    if (index < selectedDayClasses.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            TimetableLegend()
            Spacer(modifier = Modifier.height(14.dp))
            AcademicTimetableInsight()
        }
    }
}

@Composable
private fun TimetableSummaryCard(classCount: Int) {
    Card(
        shape = RoundedCornerShape(25.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("YOUR ACADEMIC WEEK", color = Color.White.copy(alpha = 0.95f), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(7.dp))
                    Text("Stay ahead of your classes", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Your verified classes, rooms and lecturers in one place.", color = Color.White.copy(alpha = 0.95f), fontSize = 11.sp, lineHeight = 16.sp)
                }
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Text("▦", color = KikaoColors.Gold, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(19.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Today's schedule", color = Color.White, fontSize = 11.sp)
                Text("$classCount ${if (classCount == 1) "class" else "classes"}", color = KikaoColors.Gold, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun DaySelector(selectedDay: Int, days: List<DayInfo>, onDaySelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        days.forEachIndexed { index, dayInfo ->
            val selected = index == selectedDay
            Column(
                modifier = Modifier
                    .width(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selected) KikaoColors.Indigo else Color.White)
                    .clickable { onDaySelected(index) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = dayInfo.date, color = if (selected) Color.White.copy(alpha = 0.72f) else KikaoColors.MutedText, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dayInfo.dayOfMonth, color = if (selected) KikaoColors.Gold else KikaoColors.Ink, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                if (selected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.size(5.dp).clip(RoundedCornerShape(5.dp)).background(KikaoColors.Gold))
                }
            }
        }
    }
}

@Composable
private fun TimetableClassCard(timetableClass: TimetableClass, isNext: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.width(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = timetableClass.startTime, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = timetableClass.endTime, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.width(3.dp).height(34.dp).clip(RoundedCornerShape(5.dp)).background(timetableClass.accent))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = timetableClass.courseCode, color = timetableClass.accent, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = timetableClass.courseName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (isNext) {
                        Text("NEXT", color = KikaoColors.Teal, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.clip(RoundedCornerShape(7.dp)).background(KikaoColors.Teal.copy(alpha = 0.2f)).padding(horizontal = 7.dp, vertical = 5.dp))
                    }
                }
                Spacer(modifier = Modifier.height(11.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⌂", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = timetableClass.room, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("•", color = timetableClass.accent, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = timetableClass.type, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = timetableClass.lecturer, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            Text("›", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp, modifier = Modifier.padding(top = 18.dp))
        }
    }
}

@Composable
private fun EmptyDayCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(58.dp).clip(RoundedCornerShape(18.dp)).background(KikaoColors.TealLight), contentAlignment = Alignment.Center) {
                Text("✓", color = KikaoColors.Teal, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(13.dp))
            Text("No classes scheduled", color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(5.dp))
            Text("Enjoy the lighter day. Use the time to review your coursework.", color = KikaoColors.MutedText, fontSize = 11.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun TimetableLegend() {
    Card(
        shape = RoundedCornerShape(19.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text("Class types", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LegendItem("Lecture", KikaoColors.Teal)
                LegendItem("Practical", KikaoColors.Gold)
                LegendItem("Tutorial", Color(0xFF8B5CF6))
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(8.dp)).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
private fun AcademicTimetableInsight() {
    Card(
        shape = RoundedCornerShape(20.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(KikaoColors.Gold.copy(alpha = 0.25f)), contentAlignment = Alignment.Center) {
                Text("✦", color = KikaoColors.Gold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Kikao insight", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text("Your timetable connects directly with attendance. Missing a scheduled session can affect your academic analytics.", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

private fun getCurrentWeekDays(): List<DayInfo> {
    val days = mutableListOf<DayInfo>()
    val calendar = Calendar.getInstance()
    
    // Set to Monday of the current week
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dayNumberFormat = SimpleDateFormat("d", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    
    val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    for (i in 0..4) {
        days.add(
            DayInfo(
                name = dayNames[i],
                date = dayFormat.format(calendar.time).uppercase(),
                dayOfMonth = dayNumberFormat.format(calendar.time),
                fullDate = fullDateFormat.format(calendar.time)
            )
        )
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

private fun demoTimetableClasses() = listOf(
    TimetableClass("CSC 210", "Data Structures", "Prof. Wanjiku", "Lab 3", "08:00", "10:00", "Lecture", 0, KikaoColors.Gold),
    TimetableClass("CSC 221", "Database Systems", "Dr. Kamau", "Room B14", "10:30", "12:30", "Lecture", 0, KikaoColors.Teal)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TimetableScreenPreview() {
    MaterialTheme {
        TimetableScreen()
    }
}
