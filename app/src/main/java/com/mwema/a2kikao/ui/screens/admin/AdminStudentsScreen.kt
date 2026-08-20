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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

// ------------------------------------------------------------
// DATA
// ------------------------------------------------------------

private data class AdminStudent(
    val id: String,
    val name: String,
    val registrationNumber: String,
    val department: String,
    val year: String,
    val attendance: Int,
    val average: Int,
    val status: StudentAdminStatus
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun AdminStudentsScreen(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onAddStudent: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val students = remember {
        demoAdminStudents()
    }

    val filteredStudents = students.filter { student ->

        val matchesSearch =
            student.name.contains(searchQuery, ignoreCase = true) ||
                    student.registrationNumber.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                    student.department.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        val matchesFilter = when (selectedFilter) {
            "All" -> true
            "At risk" -> student.status == StudentAdminStatus.AT_RISK
            "Low attendance" -> student.attendance < 75
            "High performers" -> student.average >= 75
            else -> true
        }

        matchesSearch && matchesFilter
    }

    val totalStudents = students.size

    val activeStudents = students.count {
        it.status == StudentAdminStatus.ACTIVE
    }

    val atRiskStudents = students.count {
        it.status == StudentAdminStatus.AT_RISK
    }

    val universityAverage = if (students.isEmpty()) 0 else students.map { it.average }.average().toInt()

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.USERS,
        screenTitle = "Students",
        screenSubtitle = "Manage institutional users",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            SearchStudentsField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            StudentFilters(
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    selectedFilter = it
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            StudentOverviewStats(
                totalStudents = totalStudents,
                activeStudents = activeStudents,
                atRiskStudents = atRiskStudents,
                universityAverage = universityAverage
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Student Directory",
                        color = KikaoColors.Ink,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${filteredStudents.size} students found",
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(KikaoColors.Indigo)
                        .clickable(onClick = onAddStudent)
                        .padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+",
                            color = KikaoColors.Gold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "Add",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(11.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    bottom = 110.dp
                )
            ) {

                items(
                    items = filteredStudents,
                    key = { it.id }
                ) { student ->

                    AdminStudentCard(
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

@Composable
private fun SearchStudentsField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 15.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "⌕",
                color = KikaoColors.Teal,
                fontSize = 24.sp
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
                            text = "Search name, registration no. or department",
                            color = KikaoColors.MutedText,
                            fontSize = 12.sp
                        )
                    }

                    innerTextField()
                }
            )

            if (value.isNotEmpty()) {
                Text(
                    text = "×",
                    color = KikaoColors.MutedText,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable {
                        onValueChange("")
                    }
                )
            }
        }
    }
}

@Composable
private fun StudentFilters(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "All",
        "At risk",
        "Low attendance",
        "High performers"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        filters.forEach { filter ->

            val selected =
                selectedFilter == filter

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
                    .clickable {
                        onFilterSelected(filter)
                    }
                    .padding(
                        horizontal = 13.dp,
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StudentOverviewStats(
    totalStudents: Int,
    activeStudents: Int,
    atRiskStudents: Int,
    universityAverage: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        OverviewStat(
            title = "Total",
            value = totalStudents.toString(),
            subtitle = "Registered",
            accent = KikaoColors.Indigo
        )

        OverviewStat(
            title = "Active",
            value = activeStudents.toString(),
            subtitle = "Currently enrolled",
            accent = KikaoColors.Teal
        )

        OverviewStat(
            title = "At risk",
            value = atRiskStudents.toString(),
            subtitle = "Needs attention",
            accent = Color(0xFFC77700)
        )

        OverviewStat(
            title = "Average",
            value = "$universityAverage%",
            subtitle = "Academic",
            accent = KikaoColors.Gold
        )
    }
}

@Composable
private fun OverviewStat(
    title: String,
    value: String,
    subtitle: String,
    accent: Color
) {
    Card(
        modifier = Modifier.width(105.dp),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(13.dp)
        ) {

            Text(
                text = title.uppercase(),
                color = KikaoColors.MutedText,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = value,
                color = accent,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = KikaoColors.MutedText,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
private fun AdminStudentCard(
    student: AdminStudent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                StudentAvatar(
                    name = student.name,
                    status = student.status
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = student.name,
                        color = KikaoColors.Ink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = student.registrationNumber,
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${student.department} · ${student.year}",
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp
                    )
                }

                StudentStatusBadge(student.status)

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "›",
                    color = KikaoColors.MutedText,
                    fontSize = 25.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                StudentMetric(
                    label = "Attendance",
                    value = "${student.attendance}%",
                    progress = student.attendance / 100f,
                    color = when {
                        student.attendance >= 85 -> KikaoColors.Teal
                        student.attendance >= 75 -> KikaoColors.Gold
                        else -> Color(0xFFC2413A)
                    },
                    modifier = Modifier.weight(1f)
                )

                StudentMetric(
                    label = "Academic average",
                    value = "${student.average}%",
                    progress = student.average / 100f,
                    color = when {
                        student.average >= 75 -> KikaoColors.Indigo
                        student.average >= 60 -> KikaoColors.Gold
                        else -> Color(0xFFC2413A)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StudentAvatar(
    name: String,
    status: StudentAdminStatus
) {
    val initials = name
        .split(" ")
        .take(2)
        .joinToString("") {
            it.firstOrNull()?.toString() ?: ""
        }

    val background = when (status) {
        StudentAdminStatus.ACTIVE -> KikaoColors.Indigo
        StudentAdminStatus.AT_RISK -> Color(0xFF9A6700)
        StudentAdminStatus.PROBATION -> Color(0xFFC53030)
        StudentAdminStatus.SUSPENDED -> Color(0xFF9B2C2C)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = initials.uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun StudentStatusBadge(
    status: StudentAdminStatus
) {
    val text: String
    val foreground: Color
    val background: Color

    when (status) {

        StudentAdminStatus.ACTIVE -> {
            text = "ACTIVE"
            foreground = KikaoColors.Teal
            background = KikaoColors.TealLight
        }

        StudentAdminStatus.AT_RISK -> {
            text = "AT RISK"
            foreground = Color(0xFF9A6700)
            background = Color(0xFFFFF2CC)
        }

        StudentAdminStatus.PROBATION -> {
            text = "PROBATION"
            foreground = Color(0xFFC53030)
            background = Color(0xFFFFF5F5)
        }

        StudentAdminStatus.SUSPENDED -> {
            text = "SUSPENDED"
            foreground = Color(0xFF9B2C2C)
            background = Color(0xFFFFE5E5)
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(
                horizontal = 7.dp,
                vertical = 5.dp
            )
    ) {

        Text(
            text = text,
            color = foreground,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun StudentMetric(
    label: String,
    value: String,
    progress: Float,
    color: Color,
    modifier: Modifier
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
                fontSize = 9.sp
            )

            Text(
                text = value,
                color = KikaoColors.Ink,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEFF2F6))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        progress.coerceIn(0f, 1f)
                    )
                    .height(6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color)
            )
        }
    }
}

private fun demoAdminStudents(): List<AdminStudent> {
    return listOf(

        AdminStudent(
            id = "STU001",
            name = "Amani Mwangi",
            registrationNumber = "SC211/1234/2025",
            department = "Computer Science",
            year = "Year 2",
            attendance = 94,
            average = 82,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU002",
            name = "Brian Otieno",
            registrationNumber = "SC211/1288/2025",
            department = "Computer Science",
            year = "Year 2",
            attendance = 88,
            average = 76,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU003",
            name = "Faith Wanjiru",
            registrationNumber = "BA203/0876/2025",
            department = "Business Administration",
            year = "Year 3",
            attendance = 91,
            average = 79,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU004",
            name = "Kevin Kamau",
            registrationNumber = "SC211/0912/2025",
            department = "Computer Science",
            year = "Year 2",
            attendance = 68,
            average = 57,
            status = StudentAdminStatus.AT_RISK
        ),

        AdminStudent(
            id = "STU005",
            name = "Mary Njeri",
            registrationNumber = "BIT204/1134/2024",
            department = "Information Technology",
            year = "Year 3",
            attendance = 83,
            average = 88,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU006",
            name = "Daniel Kiptoo",
            registrationNumber = "ENG201/0678/2025",
            department = "Engineering",
            year = "Year 2",
            attendance = 72,
            average = 63,
            status = StudentAdminStatus.AT_RISK
        ),

        AdminStudent(
            id = "STU007",
            name = "Sharon Akinyi",
            registrationNumber = "CSC210/1456/2025",
            department = "Computer Science",
            year = "Year 1",
            attendance = 96,
            average = 91,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU008",
            name = "Samuel Kariuki",
            registrationNumber = "BIT201/0321/2024",
            department = "Information Technology",
            year = "Year 3",
            attendance = 61,
            average = 49,
            status = StudentAdminStatus.AT_RISK
        ),

        AdminStudent(
            id = "STU009",
            name = "Lucy Wambui",
            registrationNumber = "COM205/0991/2025",
            department = "Communication",
            year = "Year 2",
            attendance = 89,
            average = 84,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU010",
            name = "Victor Ochieng",
            registrationNumber = "CSC210/0554/2024",
            department = "Computer Science",
            year = "Year 3",
            attendance = 77,
            average = 72,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU011",
            name = "Mercy Atieno",
            registrationNumber = "BA202/1245/2025",
            department = "Business Administration",
            year = "Year 2",
            attendance = 93,
            average = 86,
            status = StudentAdminStatus.ACTIVE
        ),

        AdminStudent(
            id = "STU012",
            name = "John Kamau",
            registrationNumber = "ENG202/0812/2024",
            department = "Engineering",
            year = "Year 3",
            attendance = 58,
            average = 51,
            status = StudentAdminStatus.SUSPENDED
        )
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminStudentsScreenPreview() {
    MaterialTheme {
        AdminStudentsScreen()
    }
}
