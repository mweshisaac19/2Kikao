package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class InterventionHistory(
    val title: String,
    val date: String,
    val status: String,
    val description: String
)

@Composable
fun AdminInterventionCreatorScreen(
    modifier: Modifier = Modifier,
    studentName: String = "Amani Mwangi",
    registrationNumber: String = "SC211/1234/2025",
    course: String = "BSc Computer Science",
    year: String = "Year 2",
    attendance: Int = 68,
    performance: Int = 54,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSave: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var interventionType by remember { mutableStateOf("Academic Support") }
    var priority by remember { mutableStateOf("High") }
    var notes by remember { mutableStateOf("") }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "Create intervention",
        screenSubtitle = "At-risk support",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 110.dp)
        ) {
            
            TextButton(onClick = onBack) {
                Text("‹ Back to risk monitor", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            StudentRiskSummaryCard(studentName, registrationNumber, attendance, performance)
            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Intervention details")
            OutlinedTextField(value = interventionType, onValueChange = { interventionType = it }, label = { Text("Intervention type") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Priority") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Administrator notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                Text("Log Intervention", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StudentRiskSummaryCard(name: String, reg: String, att: Int, perf: Int) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(KikaoColors.Teal), contentAlignment = Alignment.Center) {
                    Text(text = name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = reg, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricItem("Attendance", "$att%")
                MetricItem("Grade Avg", "$perf%")
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        Text(text = value, color = KikaoColors.Gold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminInterventionCreatorPreview() {
    MaterialTheme {
        AdminInterventionCreatorScreen()
    }
}
