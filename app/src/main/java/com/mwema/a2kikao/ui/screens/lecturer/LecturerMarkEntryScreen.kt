package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerMarkEntryViewModel

private data class MarkEntry(
    val studentId: String,
    val name: String,
    val regNo: String,
    var mark: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerMarkEntryScreen(
    assessmentId: String,
    modifier: Modifier = Modifier,
    viewModel: LecturerMarkEntryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val assessmentTitle = "CAT 1: Data Structures" // Demo title
    val maxMark = 20
    
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    val students = remember {
        mutableStateListOf(
            MarkEntry("s1", "Amani Mwangi", "SC211/1234/2025", "18"),
            MarkEntry("s2", "Brian Otieno", "SC211/1187/2025", "15"),
            MarkEntry("s3", "Faith Wanjiku", "SC211/1092/2025", ""),
            MarkEntry("s4", "Kevin Kiptoo", "SC211/1028/2025", ""),
            MarkEntry("s5", "Sharon Achieng", "SC211/1311/2025", "19")
        )
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onSaveSuccess()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Enter Marks", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = assessmentTitle, fontSize = 12.sp, color = KikaoColors.MutedText)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isSaving) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val marks = students.map { it.studentId to it.mark }
                            viewModel.saveMarks(assessmentId, marks)
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = KikaoColors.Teal, strokeWidth = 2.dp)
                        } else {
                            Text("Save", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                        }
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
        ) {
            // Header Info
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Max Score", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("$maxMark Marks", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pending", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("${students.count { it.mark.isBlank() }} Students", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Student List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(students) { index, student ->
                    StudentMarkRow(
                        name = student.name,
                        regNo = student.regNo,
                        mark = student.mark,
                        onMarkChange = { newMark ->
                            if (newMark.isEmpty() || (newMark.toIntOrNull() != null && newMark.toInt() <= maxMark)) {
                                students[index] = students[index].copy(mark = newMark)
                            }
                        }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun StudentMarkRow(
    name: String,
    regNo: String,
    mark: String,
    onMarkChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(text = regNo, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            
            OutlinedTextField(
                value = mark,
                onValueChange = onMarkChange,
                modifier = Modifier.width(60.dp),
                placeholder = { Text("0", color = Color.LightGray) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    color = KikaoColors.Indigo
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KikaoTeal,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}

private val KikaoTeal = Color(0xFF0F9D8A)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerMarkEntryPreview() {
    MaterialTheme {
        LecturerMarkEntryScreen(assessmentId = "a1")
    }
}
