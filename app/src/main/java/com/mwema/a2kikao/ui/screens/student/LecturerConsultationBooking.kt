package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.ConsultationViewModel

data class ConsultationSlot(
    val day: String,
    val time: String,
    val isAvailable: Boolean
)

@Composable
fun LecturerConsultationBooking(
    lecturerId: String,
    modifier: Modifier = Modifier,
    viewModel: ConsultationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val realSlots by viewModel.slots.collectAsState()
    val lecturerName by viewModel.lecturerName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(lecturerId) {
        viewModel.fetchConsultationData(lecturerId)
    }

    val slots = if (realSlots.isNotEmpty()) realSlots else listOf(
        ConsultationSlot("Monday", "14:00 - 15:30", true),
        ConsultationSlot("Wednesday", "10:00 - 12:00", true)
    )

    KikaoStudentScaffold(
        selectedTab = StudentTab.CLASSES,
        screenTitle = "Consultation",
        screenSubtitle = "Book academic support",
        onBackClick = onBackClick,
        onTabSelected = onTabSelected,
        showScanButton = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            LecturerInfoCard(name = if (lecturerName.isNotEmpty()) lecturerName else "Mr. Kevin Otieno")
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Available Slots")
            
            if (isLoading) {
                CircularProgressIndicator(color = KikaoColors.Teal)
            } else {
                slots.forEach { slot ->
                    ConsultationSlotCard(slot)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun LecturerInfoCard(name: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(KikaoColors.Teal), contentAlignment = Alignment.Center) {
                Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Lecturer", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ConsultationSlotCard(slot: ConsultationSlot) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(slot.day, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(slot.time, fontSize = 12.sp, color = KikaoColors.Teal)
            }
            Button(onClick = {}, shape = RoundedCornerShape(10.dp)) {
                Text("Book", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LecturerConsultationBookingPreview() {
    MaterialTheme {
        LecturerConsultationBooking(lecturerId = "1")
    }
}
