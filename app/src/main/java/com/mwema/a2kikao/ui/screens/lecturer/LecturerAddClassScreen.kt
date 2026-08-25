package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerAddClassViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LecturerAddClassScreen(
    classId: String? = null,
    modifier: Modifier = Modifier,
    viewModel: LecturerAddClassViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    var code by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var targetCourse by rememberSaveable { mutableStateOf("") }
    var room by rememberSaveable { mutableStateOf("") }
    val selectedDays = remember { mutableStateListOf<String>() }
    var startTime by rememberSaveable { mutableStateOf("08:00") }
    var endTime by rememberSaveable { mutableStateOf("10:00") }
    
    val isSaving by viewModel.isSaving.collectAsState()
    val success by viewModel.success.collectAsState()
    val classToEdit by viewModel.classToEdit.collectAsState()

    LaunchedEffect(classId) {
        if (classId != null) {
            viewModel.fetchClassToEdit(classId)
        }
    }

    LaunchedEffect(classToEdit) {
        classToEdit?.let {
            code = it.code
            name = it.name
            targetCourse = it.targetCourse
            room = it.room
            selectedDays.clear()
            if (it.days.isNotEmpty()) {
                selectedDays.addAll(it.days)
            } else if (it.day.isNotEmpty()) {
                selectedDays.add(it.day)
            }
            startTime = it.time.substringBefore("-").trim()
            endTime = it.time.substringAfter("-").trim()
        }
    }

    LaunchedEffect(success) {
        if (success) onSuccess()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = if (classId == null) "Add Course Unit" else "Edit Course Unit", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Unit Information", color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Unit Code") },
                        placeholder = { Text("e.g. CSC 221") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Unit Name") },
                        placeholder = { Text("e.g. Database Systems") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = targetCourse,
                        onValueChange = { targetCourse = it },
                        label = { Text("Target Programme / Course") },
                        placeholder = { Text("e.g. BSc Computer Science") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        supportingText = { Text("Students in this course will see this on their timetable.", color = KikaoColors.MutedText) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Weekly Schedule", color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Days", color = KikaoColors.MutedText, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val allDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allDays.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedDays.remove(day) else selectedDays.add(day)
                                },
                                label = { Text(day, fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        
                        FilterChip(
                            selected = selectedDays.size == 7,
                            onClick = {
                                if (selectedDays.size == 7) selectedDays.clear()
                                else {
                                    selectedDays.clear()
                                    selectedDays.addAll(allDays)
                                }
                            },
                            label = { Text("Daily", fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KikaoColors.Teal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Room / Hall") },
                        placeholder = { Text("e.g. Lab 3") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("End") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Button(
                onClick = {
                    viewModel.saveClass(
                        id = classId,
                        code = code,
                        name = name,
                        targetCourse = targetCourse,
                        room = room,
                        days = selectedDays.toList(),
                        time = "$startTime - $endTime"
                    )
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = code.isNotBlank() && name.isNotBlank() && targetCourse.isNotBlank() && selectedDays.isNotEmpty() && !isSaving,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (classId == null) "Save to Timetable" else "Update Timetable", 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LecturerAddClassPreview() {
    MaterialTheme {
        LecturerAddClassScreen()
    }
}
