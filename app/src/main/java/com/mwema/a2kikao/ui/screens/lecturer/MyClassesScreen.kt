package com.mwema.a2kikao.ui.screens.lecturer


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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.mwema.a2kikao.ui.viewmodels.LecturerClassesViewModel

data class LecturerClass(
    val id: String,
    val code: String,
    val name: String,
    val students: Int,
    val sessions: Int,
    val attendance: Int,
    val averagePerformance: Int,
    val room: String,
    val schedule: String,
    val accentColor: Color
)

@Composable
fun MyClassesScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerClassesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onClassClick: (String) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val realClasses by viewModel.classes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember {
        mutableStateOf("")
    }

    val mappedClasses = realClasses.mapIndexed { index, course ->
        LecturerClass(
            id = course.id,
            code = course.code,
            name = course.name,
            students = course.studentsEnrolled.size,
            sessions = 12, // Simulated or fetch from sessions collection
            attendance = 85, // Simulated
            averagePerformance = 72, // Simulated
            room = course.room,
            schedule = "${course.day} · ${course.time}",
            accentColor = when (index % 4) {
                0 -> KikaoColors.Teal
                1 -> KikaoColors.Gold
                2 -> Color(0xFF8B5CF6)
                else -> KikaoColors.Indigo
            }
        )
    }

    val classes = if (mappedClasses.isNotEmpty()) mappedClasses else demoLecturerClasses()

    val filteredClasses = classes.filter { course ->
        course.name.contains(searchQuery, ignoreCase = true) ||
                course.code.contains(searchQuery, ignoreCase = true)
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.CLASSES,
        screenTitle = "My classes",
        screenSubtitle = "Manage your courses and students",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
                .padding(bottom = 35.dp)
        ) {

            ClassesOverviewHeader(
                totalClasses = classes.size,
                totalStudents = classes.sumOf { it.students }
            )

            Spacer(modifier = Modifier.height(20.dp))

            SearchClassesBar(
                value = searchQuery,
                onValueChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active classes",
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${filteredClasses.size} courses",
                    color = KikaoColors.Teal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredClasses.isEmpty()) {
                EmptyClassesState()
            } else {
                filteredClasses.forEach { course ->

                    LecturerClassCard(
                        course = course,
                        onClick = {
                            onClassClick(course.id)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun ClassesOverviewHeader(
    totalClasses: Int,
    totalStudents: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "TEACHING OVERVIEW",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Your teaching portfolio",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Everything you teach, in one place.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Color.White.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "▦",
                        color = KikaoColors.Gold,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                PortfolioMetric(
                    value = totalClasses.toString(),
                    label = "Classes",
                    modifier = Modifier.weight(1f)
                )

                PortfolioMetric(
                    value = totalStudents.toString(),
                    label = "Students",
                    modifier = Modifier.weight(1f)
                )

                PortfolioMetric(
                    value = "Active",
                    label = "Semester",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PortfolioMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {
        Text(
            text = value,
            color = KikaoColors.Gold,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun SearchClassesBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(Color.White)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "⌕",
                color = KikaoColors.MutedText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = KikaoColors.Ink,
                    fontSize = 13.sp
                ),
                decorationBox = { innerTextField ->

                    if (value.isEmpty()) {
                        Text(
                            text = "Search classes...",
                            color = KikaoColors.MutedText,
                            fontSize = 13.sp
                        )
                    }

                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun LecturerClassCard(
    course: LecturerClass,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            course.accentColor.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = course.code.take(3),
                        color = course.accentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(13.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = course.code,
                        color = course.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.7.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = course.name,
                        color = KikaoColors.Ink,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${course.room} · ${course.schedule}",
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            course.accentColor.copy(alpha = 0.10f)
                        )
                        .padding(
                            horizontal = 9.dp,
                            vertical = 7.dp
                        )
                ) {
                    Text(
                        text = "›",
                        color = course.accentColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEEF1F5))
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ClassMetric(
                    value = course.students.toString(),
                    label = "Students",
                    modifier = Modifier.weight(1f)
                )

                ClassMetric(
                    value = course.sessions.toString(),
                    label = "Sessions",
                    modifier = Modifier.weight(1f)
                )

                ClassMetric(
                    value = "${course.attendance}%",
                    label = "Attendance",
                    modifier = Modifier.weight(1f),
                    valueColor = attendanceColor(course.attendance)
                )

                ClassMetric(
                    value = "${course.averagePerformance}%",
                    label = "Average",
                    modifier = Modifier.weight(1f),
                    valueColor = performanceColor(course.averagePerformance)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF5F7FA))
                    .padding(
                        horizontal = 13.dp,
                        vertical = 11.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Class performance",
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = performanceMessage(
                            course.averagePerformance
                        ),
                        color = KikaoColors.Ink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "View class ›",
                    color = course.accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ClassMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = KikaoColors.Ink
) {
    Column(
        modifier = modifier
    ) {

        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = KikaoColors.MutedText,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun EmptyClassesState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    color = KikaoColors.Teal,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "No classes found",
                color = KikaoColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Try searching with another course name or code.",
                color = KikaoColors.MutedText,
                fontSize = 12.sp
            )
        }
    }
}

private fun attendanceColor(
    attendance: Int
): Color {
    return when {
        attendance >= 85 -> KikaoColors.Teal
        attendance >= 75 -> KikaoColors.Gold
        else -> Color(0xFFB42318)
    }
}

private fun performanceColor(
    performance: Int
): Color {
    return when {
        performance >= 70 -> KikaoColors.Teal
        performance >= 55 -> KikaoColors.Gold
        else -> Color(0xFFB42318)
    }
}

private fun performanceMessage(
    performance: Int
): String {
    return when {
        performance >= 75 -> "Strong class performance"
        performance >= 65 -> "Class is progressing well"
        performance >= 55 -> "Monitor performance closely"
        else -> "Intervention may be needed"
    }
}

private fun demoLecturerClasses(): List<LecturerClass> {
    return listOf(

        LecturerClass(
            id = "csc_210",
            code = "CSC 210",
            name = "Data Structures",
            students = 84,
            sessions = 18,
            attendance = 92,
            averagePerformance = 78,
            room = "Lab 3",
            schedule = "Mon · 10:00 AM",
            accentColor = KikaoColors.Teal
        ),

        LecturerClass(
            id = "csc_221",
            code = "CSC 221",
            name = "Database Systems",
            students = 120,
            sessions = 16,
            attendance = 87,
            averagePerformance = 72,
            room = "LH 2",
            schedule = "Tue · 2:00 PM",
            accentColor = KikaoColors.Gold
        ),

        LecturerClass(
            id = "mat_204",
            code = "MAT 204",
            name = "Discrete Mathematics",
            students = 96,
            sessions = 14,
            attendance = 78,
            averagePerformance = 61,
            room = "Room B14",
            schedule = "Wed · 8:00 AM",
            accentColor = Color(0xFF8B5CF6)
        ),

        LecturerClass(
            id = "csc_230",
            code = "CSC 230",
            name = "Computer Networks",
            students = 73,
            sessions = 12,
            attendance = 89,
            averagePerformance = 74,
            room = "Lab 1",
            schedule = "Thu · 11:00 AM",
            accentColor = Color(0xFF0EA5A4)
        )
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MyClassesScreenPreview() {
    MaterialTheme {
        MyClassesScreen()
    }
}