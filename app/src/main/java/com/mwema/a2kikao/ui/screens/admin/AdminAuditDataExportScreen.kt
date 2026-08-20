package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

private enum class ExportFormat {
    CSV,
    PDF
}

private enum class ExportRange {
    TODAY,
    SEVEN_DAYS,
    THIRTY_DAYS,
    CUSTOM
}

private data class AuditExportEvent(
    val title: String,
    val description: String,
    val count: Int,
    val color: Color
)

@Composable
fun AdminAuditDataExportScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    onExportPdf: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedFormat by remember { mutableStateOf(ExportFormat.CSV) }
    var selectedRange by remember { mutableStateOf(ExportRange.THIRTY_DAYS) }
    var selectedCategory by remember { mutableStateOf("All events") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val auditEvents = listOf(
        AuditExportEvent("Authentication", "Logins and failed attempts", 428, KikaoColors.Teal),
        AuditExportEvent("Administrative", "Institutional admin changes", 186, KikaoColors.Indigo),
        AuditExportEvent("Academic", "Grades and student record changes", 312, KikaoColors.Gold),
        AuditExportEvent("Security", "Permission updates", 74, Color(0xFFE05A5A))
    )

    val totalRecords = auditEvents.sumOf { it.count }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Audit Export",
        screenSubtitle = "Compliance records",
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
            
            TextButton(onClick = onBackClick) {
                Text("‹ Back to security", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            ExportSummaryHeader(totalRecords)
            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Export format")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormatChip("CSV", selectedFormat == ExportFormat.CSV, Modifier.weight(1f)) { selectedFormat = ExportFormat.CSV }
                FormatChip("PDF", selectedFormat == ExportFormat.PDF, Modifier.weight(1f)) { selectedFormat = ExportFormat.PDF }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Included activity")
            auditEvents.forEach { event ->
                AuditEventRow(event)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { if (selectedFormat == ExportFormat.CSV) onExportCsv() else onExportPdf() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                Text("Export ${selectedFormat.name} Report", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ExportSummaryHeader(count: Int) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "AUDIT TRAIL", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "$count Events Available", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun FormatChip(label: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) KikaoColors.Indigo else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(text = label, color = if (isSelected) Color.White else KikaoColors.MutedText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AuditEventRow(event: AuditExportEvent) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(event.color))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = event.description, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Text(text = event.count.toString(), color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminAuditDataExportPreview() {
    MaterialTheme {
        AdminAuditDataExportScreen()
    }
}
