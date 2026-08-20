package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

// ---------------------------------------------------------
// DATA
// ---------------------------------------------------------

private data class AdminCourse(
    val id: String,
    val code: String,
    val name: String,
    val department: String,
    val lecturer: String,
    val students: Int,
    val attendance: Int,
    val status: String,
    val semester: String,
    val accent: Color
)

// ---------------------------------------------------------
// MAIN SCREEN
// ---------------------------------------------------------

@Composable
fun AdminCoursesScreen(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAddCourse: () -> Unit = {},
    onCourseClick: (String) -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val courses = remember {
        demoAdminCourses()
    }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedFilter by rememberSaveable {
        mutableStateOf("All")
    }

    val filteredCourses = courses.filter { course ->

        val matchesSearch =
            searchQuery.isBlank() ||
                    course.code.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                    course.name.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                    course.lecturer.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        val matchesFilter =
            when (selectedFilter) {
                "Active" -> course.status == "Active"
                "High attendance" -> course.attendance >= 85
                "Needs attention" -> course.attendance < 80
                else -> true
            }

        matchesSearch && matchesFilter
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Courses",
        screenSubtitle = "Manage academic units",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 110.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // -------------------------------------------------
                // OVERVIEW
                // -------------------------------------------------

                item {
                    CourseOverviewCard(
                        totalCourses = courses.size,
                        activeCourses = courses.count {
                            it.status == "Active"
                        },
                        totalStudents = courses.sumOf {
                            it.students
                        }
                    )
                }

                // -------------------------------------------------
                // SEARCH
                // -------------------------------------------------

                item {
                    CourseSearchBar(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                        },
                        onClear = {
                            searchQuery = ""
                        }
                    )
                }

                // -------------------------------------------------
                // FILTERS
                // -------------------------------------------------

                item {
                    CourseFilters(
                        selectedFilter = selectedFilter,
                        onFilterSelected = {
                            selectedFilter = it
                        }
                    )
                }

                // -------------------------------------------------
                // SECTION HEADER
                // -------------------------------------------------

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text(
                                text = "Academic units",
                                color = KikaoColors.Ink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text = "${filteredCourses.size} courses shown",
                                color = KikaoColors.MutedText,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = onAddCourse,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(
                                    RoundedCornerShape(13.dp)
                                )
                                .background(
                                    KikaoColors.Indigo
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add course",
                                tint = Color.White
                            )
                        }
                    }
                }

                // -------------------------------------------------
                // COURSE LIST
                // -------------------------------------------------

                if (filteredCourses.isEmpty()) {

                    item {
                        EmptyCoursesCard()
                    }

                } else {

                    items(
                        items = filteredCourses,
                        key = {
                            it.id
                        }
                    ) { course ->

                        AdminCourseCard(
                            course = course,
                            onClick = {
                                onCourseClick(course.id)
                            }
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // FLOATING ADD BUTTON
            // -----------------------------------------------------

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 22.dp,
                        bottom = 22.dp
                    )
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(KikaoColors.Indigo)
                    .clickable(
                        onClick = onAddCourse
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create course",
                    tint = Color.White,
                    modifier = Modifier.size(27.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------
// OVERVIEW CARD
// ---------------------------------------------------------

@Composable
private fun CourseOverviewCard(
    totalCourses: Int,
    activeCourses: Int,
    totalStudents: Int
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
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

                Column {

                    Text(
                        text = "ACADEMIC OVERVIEW",
                        color = Color.White.copy(
                            alpha = 0.68f
                        ),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )

                    Text(
                        text = "Course ecosystem",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(
                            RoundedCornerShape(13.dp)
                        )
                        .background(
                            Color.White.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = KikaoColors.Gold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                AdminSummaryMetric(
                    value = totalCourses.toString(),
                    label = "Total courses"
                )

                AdminSummaryMetric(
                    value = activeCourses.toString(),
                    label = "Active"
                )

                AdminSummaryMetric(
                    value = totalStudents.toString(),
                    label = "Students"
                )
            }
        }
    }
}

// ---------------------------------------------------------
// SUMMARY METRIC
// ---------------------------------------------------------

@Composable
private fun AdminSummaryMetric(
    value: String,
    label: String
) {

    Column {

        Text(
            text = value,
            color = KikaoColors.Gold,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = label,
            color = Color.White.copy(
                alpha = 0.68f
            ),
            fontSize = 10.sp
        )
    }
}

// ---------------------------------------------------------
// SEARCH BAR
// ---------------------------------------------------------

@Composable
private fun CourseSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(17.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = KikaoColors.MutedText
            )
        },
        trailingIcon = {

            if (value.isNotEmpty()) {

                IconButton(
                    onClick = onClear
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = "Search course, code or lecturer...",
                color = KikaoColors.MutedText,
                fontSize = 13.sp
            )
        }
    )
}

// ---------------------------------------------------------
// FILTERS
// ---------------------------------------------------------

@Composable
private fun CourseFilters(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {

    val filters = listOf(
        "All",
        "Active",
        "High attendance",
        "Needs attention"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        filters.forEach { filter ->

            val selected =
                filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        if (selected) {
                            KikaoColors.Indigo
                        } else {
                            Color.White
                        }
                    )
                    .clickable {
                        onFilterSelected(filter)
                    }
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (filter == "Needs attention") {

                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = if (selected) {
                                Color.White
                            } else {
                                Color(0xFFB42318)
                            },
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )
                    }

                    Text(
                        text = filter,
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
        }
    }
}

// ---------------------------------------------------------
// COURSE CARD
// ---------------------------------------------------------

@Composable
private fun AdminCourseCard(
    course: AdminCourse,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                        .size(48.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(
                            course.accent.copy(
                                alpha = 0.12f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = course.accent,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = course.code,
                        color = course.accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.7.sp
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = course.name,
                        color = KikaoColors.Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = course.department,
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(9.dp)
                        )
                        .background(
                            if (course.status == "Active") {
                                KikaoColors.TealLight
                            } else {
                                Color(0xFFF0F3F8)
                            }
                        )
                        .padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        )
                ) {

                    Text(
                        text = course.status,
                        color = if (
                            course.status == "Active"
                        ) {
                            KikaoColors.Teal
                        } else {
                            KikaoColors.MutedText
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(17.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Color(0xFFEDF1F6)
                    )
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                CourseInfoItem(
                    icon = "◉",
                    label = "Lecturer",
                    value = course.lecturer,
                    modifier = Modifier.weight(1.2f)
                )

                CourseInfoItem(
                    icon = "●",
                    label = "Students",
                    value = course.students.toString(),
                    modifier = Modifier.weight(0.7f)
                )

                CourseInfoItem(
                    icon = "↗",
                    label = "Attendance",
                    value = "${course.attendance}%",
                    valueColor = when {
                        course.attendance >= 85 ->
                            KikaoColors.Teal

                        course.attendance >= 80 ->
                            Color(0xFF9A6700)

                        else ->
                            Color(0xFFB42318)
                    },
                    modifier = Modifier.weight(0.8f)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = course.semester,
                    color = KikaoColors.MutedText,
                    fontSize = 10.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "View course",
                        color = KikaoColors.Indigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = KikaoColors.Indigo,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------
// COURSE INFO ITEM
// ---------------------------------------------------------

@Composable
private fun CourseInfoItem(
    icon: String,
    label: String,
    value: String,
    valueColor: Color = KikaoColors.Ink,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = icon,
                color = KikaoColors.Teal,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = label,
                color = KikaoColors.MutedText,
                fontSize = 9.sp
            )
        }

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = value,
            color = valueColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ---------------------------------------------------------
// EMPTY STATE
// ---------------------------------------------------------

@Composable
private fun EmptyCoursesCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
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
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFEAF0F8)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = KikaoColors.Indigo,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "No courses found",
                color = KikaoColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "Try another search or change the filters.",
                color = KikaoColors.MutedText,
                fontSize = 12.sp
            )
        }
    }
}

// ---------------------------------------------------------
// DEMO DATA
// ---------------------------------------------------------

private fun demoAdminCourses(): List<AdminCourse> {

    return listOf(

        AdminCourse(
            id = "csc210",
            code = "CSC 210",
            name = "Data Structures",
            department = "Computer Science",
            lecturer = "Dr. James Kariuki",
            students = 120,
            attendance = 91,
            status = "Active",
            semester = "Semester 1 · 2026",
            accent = KikaoColors.Teal
        ),

        AdminCourse(
            id = "csc221",
            code = "CSC 221",
            name = "Database Systems",
            department = "Computer Science",
            lecturer = "Prof. Sarah Wanjiku",
            students = 115,
            attendance = 87,
            status = "Active",
            semester = "Semester 1 · 2026",
            accent = KikaoColors.Gold
        ),

        AdminCourse(
            id = "mat204",
            code = "MAT 204",
            name = "Discrete Mathematics",
            department = "Mathematics",
            lecturer = "Dr. Peter Otieno",
            students = 108,
            attendance = 76,
            status = "Active",
            semester = "Semester 1 · 2026",
            accent = Color(0xFF8B5CF6)
        ),

        AdminCourse(
            id = "sta201",
            code = "STA 201",
            name = "Probability & Statistics",
            department = "Statistics",
            lecturer = "Dr. Mary Njeri",
            students = 96,
            attendance = 83,
            status = "Active",
            semester = "Semester 1 · 2026",
            accent = Color(0xFF0EA5E9)
        ),

        AdminCourse(
            id = "ics230",
            code = "ICS 230",
            name = "Software Engineering",
            department = "Information Technology",
            lecturer = "Mr. Brian Mwangi",
            students = 88,
            attendance = 89,
            status = "Active",
            semester = "Semester 1 · 2026",
            accent = Color(0xFF10B981)
        ),

        AdminCourse(
            id = "csc315",
            code = "CSC 315",
            name = "Artificial Intelligence",
            department = "Computer Science",
            lecturer = "Prof. David Kamau",
            students = 74,
            attendance = 79,
            status = "Active",
            semester = "Semester 1 · 2026",
            accent = Color(0xFFF97316)
        )
    )
}

// ---------------------------------------------------------
// PREVIEW
// ---------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminCoursesScreenPreview() {

    MaterialTheme {

        AdminCoursesScreen()
    }
}
