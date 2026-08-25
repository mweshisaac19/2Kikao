package com.mwema.a2kikao.ui.screens.lecturer


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.mwema.a2kikao.ui.viewmodels.LecturerStudentsViewModel

private data class OverallStudent(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val initials: String,
    val course: String,
    val year: String,
    val attendance: Int,
    val average: Int,
    val coursesTaken: Int
)

private enum class StudentFilter {
    ALL,
    AT_RISK,
    EXCELLENT,
    LOW_ATTENDANCE
}

@Composable
fun StudentsScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerStudentsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val realStudents by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf(StudentFilter.ALL)
    }

    val students = realStudents.map { profile ->
        OverallStudent(
            id = profile.uid,
            name = profile.fullName,
            registrationNumber = profile.registrationNumber ?: "",
            initials = profile.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
            course = profile.course ?: "N/A",
            year = "Year ${profile.yearOfStudy ?: "?"}",
            attendance = profile.overallAttendance ?: 0,
            average = profile.academicAverage ?: 0,
            coursesTaken = 1
        )
    }

    val filteredStudents = remember(
        searchQuery,
        selectedFilter,
        students
    ) {
        students.filter { student ->

            val matchesSearch =
                searchQuery.isBlank() ||
                        student.name.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ||
                        student.registrationNumber.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ||
                        student.course.contains(
                            searchQuery,
                            ignoreCase = true
                        )

            val matchesFilter = when (selectedFilter) {
                StudentFilter.ALL -> true

                StudentFilter.AT_RISK ->
                    student.average < 50 ||
                            student.attendance < 70

                StudentFilter.EXCELLENT ->
                    student.average >= 75 &&
                            student.attendance >= 85

                StudentFilter.LOW_ATTENDANCE ->
                    student.attendance < 70
            }

            matchesSearch && matchesFilter
        }
    }

    val averageAttendance = if (students.isNotEmpty()) students.map { it.attendance }.average().toInt() else 0
    val averagePerformance = if (students.isNotEmpty()) students.map { it.average }.average().toInt() else 0
    val atRiskCount = students.count { it.average < 50 || it.attendance < 70 }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.STUDENTS,
        screenTitle = "Students",
        screenSubtitle = "Academic overview across your classes",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    StudentsAnalyticsHeader(
                        totalStudents = students.size,
                        averageAttendance = averageAttendance,
                        averagePerformance = averagePerformance,
                        atRiskCount = atRiskCount
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    StudentSearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(2.dp))

                    StudentFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = {
                            selectedFilter = it
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when (selectedFilter) {
                                    StudentFilter.ALL ->
                                        "All students"

                                    StudentFilter.AT_RISK ->
                                        "Students needing attention"

                                    StudentFilter.EXCELLENT ->
                                        "High performers"

                                    StudentFilter.LOW_ATTENDANCE ->
                                        "Low attendance"
                                },
                                color = KikaoColors.Ink,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${filteredStudents.size} students",
                                color = KikaoColors.MutedText,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "ACADEMIC DIRECTORY",
                            color = KikaoColors.Teal,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = KikaoColors.Teal)
                        }
                    }
                } else if (filteredStudents.isEmpty()) {
                    item {
                        EmptyStudentsState()
                    }
                } else {
                    items(
                        items = filteredStudents,
                        key = { it.id }
                    ) { student ->

                        OverallStudentCard(
                            student = student,
                            onClick = {
                                onStudentClick(student.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentsAnalyticsHeader(
    totalStudents: Int,
    averageAttendance: Int,
    averagePerformance: Int,
    atRiskCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Column(
            modifier = Modifier.padding(19.dp)
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
                        text = "STUDENT ANALYTICS",
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Know your students",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "A consolidated view of academic progress and verified attendance.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
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
                        text = "◎",
                        color = KikaoColors.Gold,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                AnalyticsMiniMetric(
                    label = "Students",
                    value = totalStudents.toString(),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsMiniMetric(
                    label = "Attendance",
                    value = "$averageAttendance%",
                    modifier = Modifier.weight(1f)
                )

                AnalyticsMiniMetric(
                    label = "Average",
                    value = "$averagePerformance%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (atRiskCount > 0) {
                            Color.White.copy(alpha = 0.10f)
                        } else {
                            KikaoColors.Teal.copy(alpha = 0.22f)
                        }
                    )
                    .padding(
                        horizontal = 11.dp,
                        vertical = 9.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (atRiskCount > 0) {
                                KikaoColors.Gold
                            } else {
                                KikaoColors.Teal
                            }
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (atRiskCount > 0) {
                        "$atRiskCount students may need academic attention"
                    } else {
                        "No students currently flagged as at risk"
                    },
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AnalyticsMiniMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(
                Color.White.copy(alpha = 0.10f)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 9.dp
            )
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            color = KikaoColors.Gold,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun StudentSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        KikaoColors.TealLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    color = KikaoColors.Teal,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = KikaoColors.Ink,
                    fontSize = 13.sp
                ),
                decorationBox = { innerTextField ->

                    if (query.isEmpty()) {
                        Text(
                            text = "Search name, registration no. or course",
                            color = KikaoColors.MutedText,
                            fontSize = 12.sp
                        )
                    }

                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                Text(
                    text = "×",
                    color = KikaoColors.MutedText,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable {
                        onQueryChange("")
                    }
                )
            }
        }
    }
}

@Composable
private fun StudentFilterRow(
    selectedFilter: StudentFilter,
    onFilterSelected: (StudentFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        StudentFilterChip(
            label = "All",
            selected = selectedFilter == StudentFilter.ALL,
            onClick = {
                onFilterSelected(StudentFilter.ALL)
            }
        )

        StudentFilterChip(
            label = "At risk",
            selected = selectedFilter == StudentFilter.AT_RISK,
            onClick = {
                onFilterSelected(StudentFilter.AT_RISK)
            }
        )

        StudentFilterChip(
            label = "Excellent",
            selected = selectedFilter == StudentFilter.EXCELLENT,
            onClick = {
                onFilterSelected(StudentFilter.EXCELLENT)
            }
        )

        StudentFilterChip(
            label = "Low attendance",
            selected = selectedFilter == StudentFilter.LOW_ATTENDANCE,
            onClick = {
                onFilterSelected(StudentFilter.LOW_ATTENDANCE)
            }
        )
    }
}

@Composable
private fun StudentFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(11.dp))
            .background(
                if (selected) {
                    KikaoColors.Indigo
                } else {
                    Color.White
                }
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 14.dp,
                vertical = 9.dp
            )
    ) {
        Text(
            text = label,
            color = if (selected) {
                Color.White
            } else {
                KikaoColors.MutedText
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun OverallStudentCard(
    student: OverallStudent,
    onClick: () -> Unit
) {
    val status = getStudentStatus(student)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(status.avatarBackground.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.initials,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = student.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = student.registrationNumber,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${student.course} · ${student.year}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(status.background.copy(alpha = 0.25f))
                            .padding(
                                horizontal = 8.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Text(
                            text = status.label,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "View ›",
                        color = KikaoColors.TealLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            StudentPerformanceRow(
                attendance = student.attendance,
                average = student.average
            )
        }
    }
}

@Composable
private fun StudentPerformanceRow(
    attendance: Int,
    average: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        StudentMetric(
            label = "Attendance",
            value = "$attendance%",
            progress = attendance / 100f,
            progressColor = if (attendance >= 80) {
                KikaoColors.Teal
            } else {
                Color(0xFFD97706)
            },
            modifier = Modifier.weight(1f)
        )

        StudentMetric(
            label = "Academic average",
            value = "$average%",
            progress = average / 100f,
            progressColor = when {
                average >= 75 -> KikaoColors.Teal
                average >= 50 -> KikaoColors.Gold
                else -> Color(0xFFDC4A4A)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StudentMetric(
    label: String,
    value: String,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = KikaoColors.MutedText,
                fontSize = 10.sp
            )

            Text(
                text = value,
                color = KikaoColors.Ink,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Color(0xFFECEFF4)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(progressColor)
            )
        }
    }
}

private data class StudentStatus(
    val label: String,
    val background: Color,
    val textColor: Color,
    val avatarBackground: Color,
    val avatarText: Color
)

private fun getStudentStatus(
    student: OverallStudent
): StudentStatus {

    return when {
        student.average < 50 || student.attendance < 70 -> {
            StudentStatus(
                label = "AT RISK",
                background = Color(0xFFFFE7E8),
                textColor = Color(0xFFB42318),
                avatarBackground = Color(0xFFFFE7E8),
                avatarText = Color(0xFFB42318)
            )
        }

        student.average >= 75 &&
                student.attendance >= 85 -> {
            StudentStatus(
                label = "EXCELLENT",
                background = KikaoColors.TealLight,
                textColor = KikaoColors.Teal,
                avatarBackground = KikaoColors.TealLight,
                avatarText = KikaoColors.Teal
            )
        }

        else -> {
            StudentStatus(
                label = "ON TRACK",
                background = Color(0xFFEAF0F8),
                textColor = KikaoColors.Indigo,
                avatarBackground = Color(0xFFEAF0F8),
                avatarText = KikaoColors.Indigo
            )
        }
    }
}

@Composable
private fun EmptyStudentsState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No students found",
                color = KikaoColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Try changing your search or filter.",
                color = KikaoColors.MutedText,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun StudentsScreenPreview() {
    MaterialTheme {
        StudentsScreen()
    }
}