package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class AdminClass(
    val id: String,
    val name: String,
    val code: String,
    val course: String,
    val lecturer: String,
    val lecturerInitials: String,
    val students: Int,
    val sessions: Int,
    val attendance: Int,
    val status: String
)

private enum class ClassFilter {
    ALL,
    ACTIVE,
    ATTENTION
}

@Composable
fun AdminClassesScreen(
    modifier: Modifier = Modifier,
    onClassClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ClassFilter.ALL) }

    val classes = remember {
        listOf(
            AdminClass(
                id = "class_001",
                name = "BSc Computer Science · Year 2",
                code = "CSC 210",
                course = "Data Structures",
                lecturer = "Dr. James Mwangi",
                lecturerInitials = "JM",
                students = 118,
                sessions = 18,
                attendance = 91,
                status = "Healthy"
            ),
            AdminClass(
                id = "class_002",
                name = "BSc Computer Science · Year 2",
                code = "CSC 221",
                course = "Database Systems",
                lecturer = "Prof. Sarah Wanjiku",
                lecturerInitials = "SW",
                students = 124,
                sessions = 16,
                attendance = 86,
                status = "Healthy"
            ),
            AdminClass(
                id = "class_003",
                name = "BSc Information Technology · Year 3",
                code = "BIT 302",
                course = "Software Engineering",
                lecturer = "Dr. Peter Otieno",
                lecturerInitials = "PO",
                students = 96,
                sessions = 14,
                attendance = 73,
                status = "Attention"
            ),
            AdminClass(
                id = "class_004",
                name = "BSc Computer Science · Year 1",
                code = "MAT 104",
                course = "Discrete Mathematics",
                lecturer = "Dr. Alice Njeri",
                lecturerInitials = "AN",
                students = 142,
                sessions = 20,
                attendance = 68,
                status = "Attention"
            ),
            AdminClass(
                id = "class_005",
                name = "BSc Information Systems · Year 4",
                code = "IS 401",
                course = "Information Systems Audit",
                lecturer = "Mr. David Kamau",
                lecturerInitials = "DK",
                students = 82,
                sessions = 11,
                attendance = 88,
                status = "Healthy"
            )
        )
    }

    val filteredClasses = classes.filter { clazz ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    clazz.name.contains(searchQuery, ignoreCase = true) ||
                    clazz.code.contains(searchQuery, ignoreCase = true) ||
                    clazz.course.contains(searchQuery, ignoreCase = true) ||
                    clazz.lecturer.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            ClassFilter.ALL -> true
            ClassFilter.ACTIVE -> clazz.status == "Healthy"
            ClassFilter.ATTENTION -> clazz.status == "Attention"
        }

        matchesSearch && matchesFilter
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Classes",
        screenSubtitle = "Monitor university classes",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            ClassesOverviewCard(
                totalClasses = classes.size,
                healthyClasses = classes.count { it.status == "Healthy" },
                attentionClasses = classes.count { it.status == "Attention" }
            )

            Spacer(modifier = Modifier.height(18.dp))

            SearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(13.dp))

            FilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredClasses.isEmpty()) {
                EmptyClassesState(
                    searchQuery = searchQuery
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 110.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${filteredClasses.size} classes",
                                color = KikaoColors.Ink,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Updated just now",
                                color = KikaoColors.MutedText,
                                fontSize = 10.sp
                            )
                        }
                    }

                    items(
                        items = filteredClasses,
                        key = { it.id }
                    ) { clazz ->
                        AdminClassCard(
                            classItem = clazz,
                            onClick = {
                                onClassClick(clazz.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassesOverviewCard(
    totalClasses: Int,
    healthyClasses: Int,
    attentionClasses: Int
) {
    Card(
        modifier = Modifier.padding(horizontal = 20.dp),
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
                Column {
                    Text(
                        text = "CLASS OPERATIONS",
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Classes at a glance",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Monitor attendance and teaching activity.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(KikaoColors.Gold)
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "$totalClasses",
                        color = KikaoColors.DeepIndigo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OverviewMetric(
                    value = healthyClasses.toString(),
                    label = "Healthy",
                    modifier = Modifier.weight(1f)
                )

                OverviewMetric(
                    value = attentionClasses.toString(),
                    label = "Attention",
                    modifier = Modifier.weight(1f)
                )

                OverviewMetric(
                    value = "92%",
                    label = "Data sync",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 11.dp)
    ) {
        Text(
            text = value,
            color = KikaoColors.Gold,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⌕",
                color = KikaoColors.Teal,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = KikaoColors.Ink,
                    fontSize = 13.sp
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = "Search class, course or lecturer...",
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
private fun FilterRow(
    selectedFilter: ClassFilter,
    onFilterSelected: (ClassFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            label = "All classes",
            selected = selectedFilter == ClassFilter.ALL,
            onClick = {
                onFilterSelected(ClassFilter.ALL)
            }
        )

        FilterChip(
            label = "Healthy",
            selected = selectedFilter == ClassFilter.ACTIVE,
            onClick = {
                onFilterSelected(ClassFilter.ACTIVE)
            }
        )

        FilterChip(
            label = "Needs attention",
            selected = selectedFilter == ClassFilter.ATTENTION,
            onClick = {
                onFilterSelected(ClassFilter.ATTENTION)
            }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    KikaoColors.Teal
                } else {
                    Color.White
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
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
private fun AdminClassCard(
    classItem: AdminClass,
    onClick: () -> Unit
) {
    val attendanceColor = when {
        classItem.attendance >= 85 -> KikaoColors.Teal
        classItem.attendance >= 75 -> KikaoColors.Gold
        else -> Color(0xFFB42318)
    }

    val statusBackground = when (classItem.status) {
        "Healthy" -> KikaoColors.TealLight
        else -> Color(0xFFFFEAEC)
    }

    val statusColor = when (classItem.status) {
        "Healthy" -> KikaoColors.Teal
        else -> Color(0xFFB42318)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                        .size(49.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            KikaoColors.Indigo.copy(alpha = 0.10f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = classItem.code.take(3),
                        color = KikaoColors.Indigo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = classItem.code,
                        color = KikaoColors.Teal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = classItem.course,
                        color = KikaoColors.Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = classItem.name,
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(statusBackground)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 6.dp
                        )
                ) {
                    Text(
                        text = classItem.status,
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(29.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(KikaoColors.TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = classItem.lecturerInitials,
                        color = KikaoColors.Teal,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = classItem.lecturer,
                        color = KikaoColors.Ink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Class lecturer",
                        color = KikaoColors.MutedText,
                        fontSize = 9.sp
                    )
                }

                Text(
                    text = "View ›",
                    color = KikaoColors.Indigo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEDF1F6))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClassStat(
                    value = classItem.students.toString(),
                    label = "Students"
                )

                ClassStat(
                    value = classItem.sessions.toString(),
                    label = "Sessions"
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${classItem.attendance}%",
                        color = attendanceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Attendance",
                        color = KikaoColors.MutedText,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F3F8))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(classItem.attendance / 100f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(attendanceColor)
                )
            }
        }
    }
}

@Composable
private fun ClassStat(
    value: String,
    label: String
) {
    Column {
        Text(
            text = value,
            color = KikaoColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = label,
            color = KikaoColors.MutedText,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun EmptyClassesState(
    searchQuery: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 35.dp,
                vertical = 55.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(KikaoColors.TealLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⌕",
                color = KikaoColors.Teal,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = if (searchQuery.isBlank()) {
                "No classes found"
            } else {
                "No matching classes"
            },
            color = KikaoColors.Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (searchQuery.isBlank()) {
                "There are no classes matching the selected filter."
            } else {
                "Try searching using a course code, course name or lecturer."
            },
            color = KikaoColors.MutedText,
            fontSize = 12.sp
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminClassesScreenPreview() {
    MaterialTheme {
        AdminClassesScreen()
    }
}