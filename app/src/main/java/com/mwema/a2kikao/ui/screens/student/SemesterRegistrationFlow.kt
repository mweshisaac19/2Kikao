package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.RegistrationViewModel

@Composable
fun SemesterRegistrationFlow(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onRegistrationComplete: () -> Unit = {}
) {
    val availableUnits by viewModel.availableUnits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val success by viewModel.registrationSuccess.collectAsState()

    val selectedUnits = remember { mutableStateListOf<String>() }

    LaunchedEffect(success) {
        if (success) {
            onRegistrationComplete()
        }
    }

    KikaoStudentScaffold(
        selectedTab = StudentTab.PROFILE,
        screenTitle = "Enrollment",
        screenSubtitle = "Semester 1 · 2026",
        onBackClick = onBackClick,
        showScanButton = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {
            StepHeader()
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Unit Selection")
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KikaoColors.Teal)
                }
            } else if (availableUnits.isEmpty()) {
                repeat(4) {
                    UnitCard(
                        code = "CSC 301",
                        name = "Software Engineering",
                        isSelected = true,
                        onToggle = {}
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                availableUnits.forEach { unit ->
                    UnitCard(
                        code = unit.code,
                        name = unit.name,
                        isSelected = selectedUnits.contains(unit.id),
                        onToggle = {
                            if (selectedUnits.contains(unit.id)) {
                                selectedUnits.remove(unit.id)
                            } else {
                                selectedUnits.add(unit.id)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val uid = FirebaseManager.currentUserUId
                    if (uid != null) {
                        viewModel.submitRegistration(uid, selectedUnits.toList())
                    }
                },
                enabled = !isSubmitting && (selectedUnits.isNotEmpty() || availableUnits.isEmpty()),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirm Enrollment", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StepHeader() {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.TealLight)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(KikaoColors.Teal), contentAlignment = Alignment.Center) {
                Text("1", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Current Step", color = KikaoColors.MutedText, fontSize = 10.sp)
                Text("Review and Confirm Units", color = KikaoColors.Teal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun UnitCard(code: String, name: String, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("$code: $name", fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text("3 Credits · Core", fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Checkbox(checked = isSelected, onCheckedChange = onToggle)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SemesterRegistrationPreview() {
    MaterialTheme {
        SemesterRegistrationFlow()
    }
}
