package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
    onClassClick: (LecturerClass) -> Unit = {},
    onEditClassClick: (String) -> Unit = {},
    onAddClassClick: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    val realClasses by viewModel.classes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val mappedClasses = realClasses.mapIndexed { index, course ->
        LecturerClass(
            id = course.id,
            code = course.code,
            name = course.name,
            students = course.studentsEnrolled.size,
            sessions = 12,
            attendance = 85,
            averagePerformance = 72,
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .padding(bottom = 100.dp)
            ) {
                ClassesOverviewHeader(
                    totalClasses = classes.size,
                    totalStudents = classes.sumOf { it.students }
                )

                Spacer(modifier = Modifier.height(20.dp))

                SearchClassesBar(value = searchQuery, onValueChange = { searchQuery = it })

                Spacer(modifier = Modifier.height(22.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Active classes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${filteredClasses.size} courses", color = KikaoColors.TealLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredClasses.isEmpty()) {
                    EmptyClassesState()
                } else {
                    filteredClasses.forEach { course ->
                        LecturerClassCard(
                            course = course,
                            onClick = { onClassClick(course) },
                            onEdit = { onEditClassClick(course.id) },
                            onDelete = { viewModel.deleteClass(course.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            FloatingActionButton(
                onClick = onAddClassClick,
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 90.dp, end = 20.dp),
                containerColor = KikaoColors.Teal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    }
}

@Composable
private fun ClassesOverviewHeader(totalClasses: Int, totalStudents: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("TEACHING OVERVIEW", color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(7.dp))
            Text("Your teaching portfolio", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PortfolioMetric(value = totalClasses.toString(), label = "Classes", modifier = Modifier.weight(1f))
                PortfolioMetric(value = totalStudents.toString(), label = "Students", modifier = Modifier.weight(1f))
                PortfolioMetric(value = "Active", label = "Semester", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PortfolioMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.10f)).padding(horizontal = 12.dp, vertical = 10.dp)) {
        Text(value, color = KikaoColors.Gold, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Color.White.copy(alpha = 0.90f), fontSize = 9.sp)
    }
}

@Composable
private fun SearchClassesBar(value: String, onValueChange: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(17.dp)).background(Color.White).padding(horizontal = 15.dp), contentAlignment = Alignment.CenterStart) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⌕", color = KikaoColors.MutedText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(value = value, onValueChange = onValueChange, modifier = Modifier.weight(1f), singleLine = true, textStyle = androidx.compose.ui.text.TextStyle(color = KikaoColors.Ink, fontSize = 13.sp), decorationBox = { innerTextField ->
                if (value.isEmpty()) Text("Search classes...", color = KikaoColors.MutedText, fontSize = 13.sp)
                innerTextField()
            })
        }
    }
}

@Composable
private fun LecturerClassCard(
    course: LecturerClass,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Class?") },
            text = { Text("Are you sure you want to remove ${course.code}? This will also delete the timetable entry for your students.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color(0xFFB42318))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(course.accentColor.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(course.code.take(3), color = course.accentColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.width(13.dp))
                    Column {
                        Text(course.code, color = course.accentColor, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        Text(course.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${course.room} · ${course.schedule}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("●", color = KikaoColors.Teal, fontSize = 8.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tap to start", color = KikaoColors.Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = KikaoColors.MutedText.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = KikaoColors.MutedText.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyClassesState() {
    Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("No classes found", color = KikaoColors.MutedText, fontWeight = FontWeight.Bold)
    }
}

private fun demoLecturerClasses() = listOf(
    LecturerClass("csc_210", "CSC 210", "Data Structures", 84, 18, 92, 78, "Lab 3", "Mon · 10:00 AM", KikaoColors.Teal)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyClassesScreenPreview() {
    MaterialTheme { MyClassesScreen() }
}
