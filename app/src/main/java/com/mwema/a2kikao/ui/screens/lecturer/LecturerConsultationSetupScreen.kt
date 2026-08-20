package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

@Composable
fun LecturerConsultationSetupScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var mondayEnabled by remember { mutableStateOf(true) }
    var wednesdayEnabled by remember { mutableStateOf(true) }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.PROFILE,
        screenTitle = "Consultation Hours",
        screenSubtitle = "Manage your availability for students",
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 100.dp)
        ) {
            
            TextButton(onClick = onBack, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp), tint = KikaoColors.Teal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to profile", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            Text("Set availability", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
            Text("Students will see these times when booking appointments.", fontSize = 12.sp, color = KikaoColors.MutedText)

            Spacer(modifier = Modifier.height(24.dp))

            AvailabilityToggle(day = "Monday", isEnabled = mondayEnabled, time = "14:00 - 15:30") { mondayEnabled = it }
            Spacer(modifier = Modifier.height(12.dp))
            AvailabilityToggle(day = "Tuesday", isEnabled = false, time = "Not available") { }
            Spacer(modifier = Modifier.height(12.dp))
            AvailabilityToggle(day = "Wednesday", isEnabled = wednesdayEnabled, time = "10:00 - 12:00") { wednesdayEnabled = it }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                Text("Save Availability", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AvailabilityToggle(day: String, isEnabled: Boolean, time: String, onToggle: (Boolean) -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = day, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = KikaoColors.Ink)
                Text(text = time, fontSize = 12.sp, color = if(isEnabled) KikaoColors.Teal else KikaoColors.MutedText)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedTrackColor = KikaoColors.Teal)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerConsultationSetupPreview() {
    MaterialTheme {
        LecturerConsultationSetupScreen()
    }
}
