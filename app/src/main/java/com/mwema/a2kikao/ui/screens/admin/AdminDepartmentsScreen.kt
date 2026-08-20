package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

private data class AdminDepartment(
    val id: String,
    val name: String,
    val faculty: String,
    val head: String,
    val courses: Int,
    val students: Int,
    val lecturers: Int,
    val attendance: Int
)

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@Composable
fun AdminDepartmentsScreen(
    modifier: Modifier = Modifier,
    onDepartmentClick: (String) -> Unit = {},
    onAddDepartment: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val departments = remember { demoAdminDepartments() }
    var searchQuery by remember { mutableStateOf("") }

    val filteredDepartments = departments.filter { dept ->
        searchQuery.isBlank() ||
                dept.name.contains(searchQuery, ignoreCase = true) ||
                dept.faculty.contains(searchQuery, ignoreCase = true) ||
                dept.head.contains(searchQuery, ignoreCase = true)
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Departments",
        screenSubtitle = "University organizational structure",
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

            DepartmentOverviewCard(departments)

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Academic units",
                        color = KikaoColors.Ink,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${departments.size} departments total",
                        color = KikaoColors.MutedText,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onAddDepartment,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(KikaoColors.Indigo)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add department",
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
                        text = "Search department or faculty",
                        color = KikaoColors.MutedText,
                        fontSize = 13.sp
                    )
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                items(
                    items = filteredDepartments,
                    key = { it.id }
                ) { dept ->
                    DepartmentCard(
                        department = dept,
                        onClick = { onDepartmentClick(dept.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DepartmentOverviewCard(
    departments: List<AdminDepartment>
) {
    val totalStudents = departments.sumOf { it.students }
    val avgAttendance = if (departments.isNotEmpty()) departments.map { it.attendance }.average().toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "INSTITUTION STRUCTURE",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = "Departmental ecosystem",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = null,
                        tint = KikaoColors.Gold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewStat("${departments.size}", "Departments")
                OverviewStat("${totalStudents / 1000}.${(totalStudents % 1000) / 100}k", "Students")
                OverviewStat("$avgAttendance%", "Attendance")
            }
        }
    }
}

@Composable
private fun OverviewStat(value: String, label: String) {
    Column {
        Text(text = value, color = KikaoColors.Gold, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.68f), fontSize = 10.sp)
    }
}

@Composable
private fun DepartmentCard(
    department: AdminDepartment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(departmentAccent(department.id).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = department.name.take(1),
                        color = departmentAccent(department.id),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = department.faculty,
                        color = departmentAccent(department.id),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = department.name,
                        color = KikaoColors.Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "HOD: ${department.head}",
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = KikaoColors.MutedText,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DeptStat("▦", "${department.courses} courses")
                DeptStat("♙", "${department.students} students")
                DeptStat("◉", "${department.lecturers} faculty")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEDF1F6)))

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (department.attendance >= 85) KikaoColors.Teal else KikaoColors.Gold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${department.attendance}% avg. attendance",
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "View details ›",
                    color = KikaoColors.Indigo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DeptStat(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, color = KikaoColors.Teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = text, color = KikaoColors.MutedText, fontSize = 10.sp)
    }
}

private fun departmentAccent(id: String): Color {
    return when (kotlin.math.abs(id.hashCode()) % 4) {
        0 -> KikaoColors.Teal
        1 -> KikaoColors.Indigo
        2 -> KikaoColors.Gold
        else -> Color(0xFF8B5CF6)
    }
}

private fun demoAdminDepartments() = listOf(
    AdminDepartment("dept_001", "Computer Science", "Faculty of Computing", "Dr. Alice Njeri", 18, 542, 14, 88),
    AdminDepartment("dept_002", "Information Technology", "Faculty of Computing", "Dr. Brian Otieno", 12, 428, 9, 84),
    AdminDepartment("dept_003", "Mathematics", "Faculty of Science", "Prof. James Kariuki", 24, 312, 11, 79),
    AdminDepartment("dept_004", "Business Administration", "Faculty of Business", "Dr. Mary Atieno", 15, 618, 12, 91),
    AdminDepartment("dept_005", "Accounting & Finance", "Faculty of Business", "Mr. David Mwangi", 11, 486, 8, 86)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminDepartmentsScreenPreview() {
    MaterialTheme {
        AdminDepartmentsScreen()
    }
}
