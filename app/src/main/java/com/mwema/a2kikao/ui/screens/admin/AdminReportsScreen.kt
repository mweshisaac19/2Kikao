package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class ReportType(
    val title: String,
    val subtitle: String,
    val icon: String,
    val accent: Color
)

private data class RecentReport(
    val title: String,
    val period: String,
    val generated: String,
    val type: String
)

@Composable
fun AdminReportsScreen(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onGenerateReport: (String) -> Unit = {},
    onOpenReport: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedPeriod by remember { mutableStateOf("Aug 2026") }

    val reportTypes = listOf(
        ReportType("Attendance Analysis", "Deep dive into institution check-ins", "✓", KikaoColors.Teal),
        ReportType("Performance Trends", "Academic progress and risk metrics", "◔", KikaoColors.Indigo),
        ReportType("Resource Allocation", "Staff and facility utilization", "▦", KikaoColors.Gold),
        ReportType("Financial Summary", "Revenue, fees and scholarship data", "KES", Color(0xFF8B5CF6))
    )

    val recentReports = listOf(
        RecentReport(
            title = "Term 2 Attendance Overview",
            period = "May – Aug 2026",
            generated = "Today · 9:12 AM",
            type = "Attendance"
        ),
        RecentReport(
            title = "Mid-Term Academic Audit",
            period = "Jan – Aug 2026",
            generated = "16 Aug · 2:31 PM",
            type = "Performance"
        )
    )

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "Reports & insights",
        screenSubtitle = "Turn university data into decisions",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 40.dp)
        ) {

            ReportsHeroCard(
                selectedPeriod = selectedPeriod,
                onGenerateReport = {
                    onGenerateReport("Executive University Report")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Report Categories",
                color = KikaoColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            reportTypes.forEach { type ->
                ReportTypeCard(
                    reportType = type,
                    onClick = {
                        onGenerateReport(type.title)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recent Reports",
                color = KikaoColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            recentReports.forEach { report ->
                RecentReportCard(
                    report = report,
                    onClick = {
                        onOpenReport(report.title)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun ReportsHeroCard(
    selectedPeriod: String,
    onGenerateReport: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "INSTITUTIONAL INTELLIGENCE",
                color = KikaoColors.Gold,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Executive Summary",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Complete data for $selectedPeriod including all schools.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onGenerateReport,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate Report", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ReportTypeCard(
    reportType: ReportType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(reportType.accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reportType.icon,
                    color = reportType.accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reportType.title,
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reportType.subtitle,
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )
            }
            Text("→", color = KikaoColors.MutedText)
        }
    }
}

@Composable
private fun RecentReportCard(
    report: RecentReport,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.type.uppercase(),
                    color = KikaoColors.Teal,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = report.generated,
                    color = KikaoColors.MutedText,
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = report.title,
                color = KikaoColors.Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = report.period,
                color = KikaoColors.MutedText,
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminReportsScreenPreview() {
    MaterialTheme {
        AdminReportsScreen()
    }
}
