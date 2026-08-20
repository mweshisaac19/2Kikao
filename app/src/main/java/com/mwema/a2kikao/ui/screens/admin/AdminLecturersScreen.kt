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
import androidx.compose.runtime.setValue
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

// ------------------------------------------------------------
// DATA
// ------------------------------------------------------------

private data class AdminLecturer(
    val id: String,
    val name: String,
    val initials: String,
    val email: String,
    val department: String,
    val faculty: String,
    val courses: Int,
    val students: Int,
    val attendance: Int,
    val status: LecturerStatus
)

private enum class LecturerStatus {
    ACTIVE,
    PENDING,
    INACTIVE
}

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun AdminLecturersScreen(
    modifier: Modifier = Modifier,
    onLecturerSelected: (String) -> Unit = {},
    onAddLecturer: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val lecturers = remember { demoAdminLecturers() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredLecturers = lecturers.filter { lecturer ->
        val matchesSearch =
            lecturer.name.contains(searchQuery, ignoreCase = true) ||
                    lecturer.email.contains(searchQuery, ignoreCase = true) ||
                    lecturer.department.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Active" -> lecturer.status == LecturerStatus.ACTIVE
            "Pending" -> lecturer.status == LecturerStatus.PENDING
            "Inactive" -> lecturer.status == LecturerStatus.INACTIVE
            else -> true
        }

        matchesSearch && matchesFilter
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.USERS,
        screenTitle = "Lecturers",
        screenSubtitle = "Faculty and teaching staff",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(18.dp))

                LecturerOverviewCard(lecturers)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Teaching staff",
                            color = KikaoColors.Ink,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${lecturers.size} lecturers in the university",
                            color = KikaoColors.MutedText,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = onAddLecturer,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(KikaoColors.Indigo)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add lecturer",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = KikaoColors.MutedText
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search name, email or department",
                            color = KikaoColors.MutedText,
                            fontSize = 12.sp
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                LecturerFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (filteredLecturers.isEmpty()) {
                    EmptyLecturerState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            bottom = 110.dp
                        )
                    ) {
                        items(
                            items = filteredLecturers,
                            key = { it.id }
                        ) { lecturer ->

                            LecturerCard(
                                lecturer = lecturer,
                                onClick = {
                                    onLecturerSelected(lecturer.id)
                                }
                            )
                        }
                    }
                }
            }

            AddLecturerFloatingButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 20.dp,
                        bottom = 92.dp
                    ),
                onClick = onAddLecturer
            )
        }
    }
}

@Composable
private fun LecturerOverviewCard(
    lecturers: List<AdminLecturer>
) {
    val active = lecturers.count {
        it.status == LecturerStatus.ACTIVE
    }

    val pending = lecturers.count {
        it.status == LecturerStatus.PENDING
    }

    val averageAttendance =
        if (lecturers.isNotEmpty()) {
            lecturers.map { it.attendance }.average().toInt()
        } else {
            0
        }

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
                        text = "LECTURER OVERVIEW",
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Teaching ecosystem",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Monitor faculty activity and academic delivery.",
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = KikaoColors.Gold,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewMetric(
                    value = "${lecturers.size}",
                    label = "Total"
                )

                OverviewMetric(
                    value = "$active",
                    label = "Active"
                )

                OverviewMetric(
                    value = "$pending",
                    label = "Pending"
                )

                OverviewMetric(
                    value = "$averageAttendance%",
                    label = "Attendance"
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    value: String,
    label: String
) {
    Column {
        Text(
            text = value,
            color = KikaoColors.Gold,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.66f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun LecturerFilterRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "All",
        "Active",
        "Pending",
        "Inactive"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->

            val selected = filter == selectedFilter

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
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
                        horizontal = 15.dp,
                        vertical = 9.dp
                    )
            ) {
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

@Composable
private fun LecturerCard(
    lecturer: AdminLecturer,
    onClick: () -> Unit
) {
    val statusColor = when (lecturer.status) {
        LecturerStatus.ACTIVE -> KikaoColors.Teal
        LecturerStatus.PENDING -> Color(0xFFB7791F)
        LecturerStatus.INACTIVE -> Color(0xFFB42318)
    }

    val statusBackground = when (lecturer.status) {
        LecturerStatus.ACTIVE -> KikaoColors.TealLight
        LecturerStatus.PENDING -> Color(0xFFFFF3D6)
        LecturerStatus.INACTIVE -> Color(0xFFFFEAEC)
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
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(
                            lecturerAccent(lecturer.id)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lecturer.initials,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(13.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = lecturer.name,
                        color = KikaoColors.Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = lecturer.department,
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = lecturer.email,
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View lecturer",
                        tint = KikaoColors.MutedText,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LecturerInfoPill(
                    icon = Icons.Default.School,
                    value = "${lecturer.courses} courses",
                    modifier = Modifier.weight(1f)
                )

                LecturerInfoPill(
                    icon = Icons.Default.Person,
                    value = "${lecturer.students} students",
                    modifier = Modifier.weight(1f)
                )

                AttendancePill(
                    attendance = lecturer.attendance,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(statusBackground)
                        .padding(
                            horizontal = 9.dp,
                            vertical = 6.dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (lecturer.status) {
                                LecturerStatus.ACTIVE ->
                                    Icons.Default.CheckCircle

                                LecturerStatus.PENDING ->
                                    Icons.Default.Pending

                                LecturerStatus.INACTIVE ->
                                    Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(13.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = when (lecturer.status) {
                                LecturerStatus.ACTIVE -> "ACTIVE"
                                LecturerStatus.PENDING -> "PENDING APPROVAL"
                                LecturerStatus.INACTIVE -> "INACTIVE"
                            },
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Text(
                    text = "View profile  ›",
                    color = KikaoColors.Indigo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LecturerInfoPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xFFF5F7FA))
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KikaoColors.Indigo,
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = value,
            color = KikaoColors.MutedText,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AttendancePill(
    attendance: Int,
    modifier: Modifier = Modifier
) {
    val color = when {
        attendance >= 85 -> KikaoColors.Teal
        attendance >= 70 -> Color(0xFFB7791F)
        else -> Color(0xFFB42318)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xFFF5F7FA))
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = "$attendance% attendance",
            color = KikaoColors.MutedText,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun AddLecturerFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(KikaoColors.Indigo)
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp,
                vertical = 13.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = "Add lecturer",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyLecturerState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF0F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = KikaoColors.Indigo,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "No lecturers found",
            color = KikaoColors.Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Try a different search or filter.",
            color = KikaoColors.MutedText,
            fontSize = 12.sp
        )
    }
}

private fun lecturerAccent(id: String): Color {
    return when (id.hashCode().absoluteValue % 4) {
        0 -> KikaoColors.Teal
        1 -> KikaoColors.Indigo
        2 -> KikaoColors.Gold
        else -> Color(0xFF8B5CF6)
    }
}

private fun demoAdminLecturers(): List<AdminLecturer> {
    return listOf(
        AdminLecturer(
            id = "lec_001",
            name = "Dr. James Kamau",
            initials = "JK",
            email = "j.kamau@university.ac.ke",
            department = "Computer Science",
            faculty = "Faculty of Computing",
            courses = 4,
            students = 286,
            attendance = 91,
            status = LecturerStatus.ACTIVE
        ),
        AdminLecturer(
            id = "lec_002",
            name = "Dr. Sarah Wanjiku",
            initials = "SW",
            email = "s.wanjiku@university.ac.ke",
            department = "Information Technology",
            faculty = "Faculty of Computing",
            courses = 3,
            students = 214,
            attendance = 88,
            status = LecturerStatus.ACTIVE
        ),
        AdminLecturer(
            id = "lec_003",
            name = "Prof. David Mwangi",
            initials = "DM",
            email = "d.mwangi@university.ac.ke",
            department = "Business Administration",
            faculty = "Faculty of Business",
            courses = 5,
            students = 341,
            attendance = 86,
            status = LecturerStatus.ACTIVE
        ),
        AdminLecturer(
            id = "lec_004",
            name = "Ms. Faith Njeri",
            initials = "FN",
            email = "f.njeri@university.ac.ke",
            department = "Mathematics",
            faculty = "Faculty of Science",
            courses = 2,
            students = 168,
            attendance = 79,
            status = LecturerStatus.ACTIVE
        ),
        AdminLecturer(
            id = "lec_005",
            name = "Mr. Brian Otieno",
            initials = "BO",
            email = "b.otieno@university.ac.ke",
            department = "Computer Science",
            faculty = "Faculty of Computing",
            courses = 2,
            students = 132,
            attendance = 0,
            status = LecturerStatus.PENDING
        ),
        AdminLecturer(
            id = "lec_006",
            name = "Dr. Mary Atieno",
            initials = "MA",
            email = "m.atieno@university.ac.ke",
            department = "Statistics",
            faculty = "Faculty of Science",
            courses = 1,
            students = 74,
            attendance = 0,
            status = LecturerStatus.INACTIVE
        )
    )
}

private val Int.absoluteValue: Int
    get() = if (this == Int.MIN_VALUE) Int.MAX_VALUE else kotlin.math.abs(this)

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminLecturersScreenPreview() {
    MaterialTheme {
        AdminLecturersScreen()
    }
}
