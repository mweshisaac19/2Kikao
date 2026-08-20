package com.mwema.a2kikao.ui.screens.student


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.MyClassesViewModel

private data class EnrolledClass(
    val id: String,
    val title: String,
    val code: String,
    val lecturer: String,
    val nextClass: String,
    val attendancePercent: Int,
    val attendedSessions: Int,
    val totalSessions: Int,
    val accentColor: Color
)

@Composable
fun MyClassesScreen(
    modifier: Modifier = Modifier,
    viewModel: MyClassesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onClassClick: (String) -> Unit = {},
    onTimetableClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val realClasses by viewModel.classes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val mappedClasses = realClasses.mapIndexed { index, course ->
        EnrolledClass(
            id = course.id,
            title = course.name,
            code = course.code,
            lecturer = course.lecturer,
            nextClass = "${course.day} · ${course.time} · ${course.room}",
            attendancePercent = 85 + (index * 2), // Simulated per class
            attendedSessions = 12,
            totalSessions = 14,
            accentColor = when (index % 4) {
                0 -> KikaoColors.Teal
                1 -> KikaoColors.Gold
                2 -> Color(0xFF8B5CF6)
                else -> KikaoColors.Indigo
            }
        )
    }

    val classesToDisplay = if (mappedClasses.isNotEmpty()) mappedClasses else listOf(
        EnrolledClass(
            id = "csc_210",
            title = "Data Structures",
            code = "CSC 210",
            lecturer = "Dr. Mercy Wanjiku",
            nextClass = "Tomorrow · 09:00 · Hall B",
            attendancePercent = 92,
            attendedSessions = 12,
            totalSessions = 13,
            accentColor = KikaoColors.Teal
        ),
        EnrolledClass(
            id = "csc_221",
            title = "Database Systems",
            code = "CSC 221",
            lecturer = "Mr. Kevin Otieno",
            nextClass = "Today · 14:00 · Lab 3",
            attendancePercent = 87,
            attendedSessions = 13,
            totalSessions = 15,
            accentColor = KikaoColors.Gold
        )
    )

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.CLASSES,
        screenTitle = "My classes",
        screenSubtitle = "Semester 1 · 2026/2027",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 100.dp)
            ) {
                AttendanceSummary(classesToDisplay)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Enrolled classes",
                    color = KikaoColors.Ink,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                classesToDisplay.forEach { classItem ->
                    ClassCard(
                        classItem = classItem,
                        onClick = { onClassClick(classItem.id) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            FloatingActionButton(
                onClick = onTimetableClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        bottom = innerPadding.calculateBottomPadding() + 20.dp,
                        end = 20.dp
                    ),
                shape = RoundedCornerShape(18.dp),
                containerColor = KikaoColors.Indigo,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp
                )
            ) {
                Text(
                    text = "📅",
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun AttendanceSummary(
    classes: List<EnrolledClass>
) {
    val averageAttendance = classes
        .map { it.attendancePercent }
        .average()
        .toInt()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(19.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Overall attendance",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "$averageAttendance%",
                    color = Color.White,
                    fontSize = 33.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Across ${classes.size} active classes",
                    color = Color.White.copy(alpha = 0.76f),
                    fontSize = 12.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "On track",
                    color = KikaoColors.DeepIndigo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(KikaoColors.Gold)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Keep it up!",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ClassCard(
    classItem: EnrolledClass,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .width(6.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(classItem.accentColor)
                ) {}

                Spacer(modifier = Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = classItem.code,
                        color = classItem.accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = classItem.title,
                        color = KikaoColors.Ink,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = classItem.lecturer,
                        color = KikaoColors.MutedText,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "›",
                    color = KikaoColors.MutedText,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light
                )
            }

            Spacer(modifier = Modifier.height(17.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Attendance",
                    color = KikaoColors.MutedText,
                    fontSize = 12.sp
                )

                Text(
                    text = "${classItem.attendedSessions}/${classItem.totalSessions} sessions",
                    color = KikaoColors.MutedText,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F3F8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(classItem.attendancePercent / 100f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(classItem.accentColor)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${classItem.attendancePercent}% present",
                    color = classItem.accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = classItem.nextClass,
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyClassesScreenPreview() {
    MaterialTheme {
        MyClassesScreen()
    }
}