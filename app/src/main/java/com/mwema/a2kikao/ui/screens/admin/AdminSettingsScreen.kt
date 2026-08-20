package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun AdminSettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAcademicCalendarClick: () -> Unit = {},
    onAuditExportClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var maintenanceMode by rememberSaveable { mutableStateOf(false) }
    var attendanceAlerts by rememberSaveable { mutableStateOf(true) }
    var performanceAlerts by rememberSaveable { mutableStateOf(true) }
    var adminNotifications by rememberSaveable { mutableStateOf(true) }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Institutional settings",
        screenSubtitle = "Global system configuration",
        onNotificationClick = {},
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
            
            SectionHeader("System Status")
            
            SettingToggleCard(
                title = "Institutional Maintenance Mode",
                subtitle = "Suspend all non-administrative activities across the platform.",
                icon = "⚙",
                checked = maintenanceMode,
                onCheckedChange = { maintenanceMode = it },
                accent = KikaoColors.Indigo
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionHeader("Global Notifications")
            
            SettingToggleCard(
                title = "Critical Attendance Alerts",
                subtitle = "Notify administrators when class attendance falls below thresholds.",
                icon = "✓",
                checked = attendanceAlerts,
                onCheckedChange = { attendanceAlerts = it },
                accent = KikaoColors.Teal
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            SettingToggleCard(
                title = "Performance Thresholds",
                subtitle = "Alert administration of significant declines in assessment averages.",
                icon = "↗",
                checked = performanceAlerts,
                onCheckedChange = { performanceAlerts = it },
                accent = KikaoColors.Indigo
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SectionHeader("Institutional Data")
            
            ActionSettingCard(
                title = "Academic Term Configuration",
                subtitle = "Manage semesters, start dates and academic calendars.",
                icon = "▦",
                onClick = onAcademicCalendarClick
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            ActionSettingCard(
                title = "Backup & Data Management",
                subtitle = "Export institutional records and configure automated backups.",
                icon = "▥",
                onClick = onAuditExportClick
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                Text("Save Configuration", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = KikaoColors.MutedText,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
private fun SettingToggleCard(
    title: String,
    subtitle: String,
    icon: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accent: Color
) {
    Card(
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(15.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp, lineHeight = 16.sp)
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = KikaoColors.Teal,
                    uncheckedTrackColor = Color(0xFFE2E8F0)
                )
            )
        }
    }
}

@Composable
private fun ActionSettingCard(
    title: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = KikaoColors.Indigo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(15.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp, lineHeight = 16.sp)
            }
            
            Text(text = "›", color = KikaoColors.MutedText, fontSize = 24.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminSettingsPreview() {
    MaterialTheme {
        AdminSettingsScreen()
    }
}
