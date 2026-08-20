package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import kotlin.math.roundToInt

private enum class ReportFormat {
    PDF,
    HTML
}

private data class ReportMetric(
    val label: String,
    val value: String,
    val change: String,
    val positive: Boolean
)

private data class DepartmentPerformance(
    val department: String,
    val students: Int,
    val attendance: Int,
    val performance: Int,
    val atRisk: Int
)

@Composable
fun AdminDetailedReportScreen(
    modifier: Modifier = Modifier,
    reportTitle: String = "Institutional Performance Report",
    reportType: String = "Academic Performance",
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {},
    onDownload: () -> Unit = {},
    onShare: () -> Unit = {},
    onPrint: () -> Unit = {}
) {
    var selectedFormat by rememberSaveable { mutableStateOf(ReportFormat.PDF) }
    var selectedPeriod by rememberSaveable { mutableStateOf("Semester 1 · 2026") }

    val metrics = remember {
        listOf(
            ReportMetric("Average performance", "68.4%", "+4.8%", true),
            ReportMetric("Verified attendance", "84.7%", "+3.2%", true),
            ReportMetric("At-risk students", "126", "-18", true)
        )
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "Report Details",
        screenSubtitle = reportType,
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
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = KikaoColors.Ink)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = reportTitle, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = selectedPeriod, color = KikaoColors.MutedText, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(text = "EXECUTIVE SUMMARY", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(13.dp))
                    Text(text = reportTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(13.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ReportFormatItem("Format", selectedFormat.name)
                        ReportFormatItem("Status", "Finalised")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                metrics.forEach { metric ->
                    ReportKpiCard(metric, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader("Insights", "Based on verified activity")
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Text(
                    text = "Performance across departments is currently stable with a 4.2% increase in attendance. Critical attention required for students below 60% threshold.",
                    modifier = Modifier.padding(16.dp), color = KikaoColors.MutedText, fontSize = 13.sp, lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onDownload, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download")
                }
            }
        }
    }
}

@Composable
private fun ReportFormatItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
        Text(text = value, color = KikaoColors.Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReportKpiCard(metric: ReportMetric, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = metric.label, color = KikaoColors.MutedText, fontSize = 9.sp)
            Text(text = metric.value, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = metric.change, color = KikaoColors.Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(text = title, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminDetailedReportPreview() {
    MaterialTheme {
        AdminDetailedReportScreen()
    }
}
