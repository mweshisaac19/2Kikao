package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private enum class SecurityTab {
    AUDIT_LOGS,
    PERMISSIONS
}

private enum class AuditFilter {
    ALL,
    SECURITY,
    USERS,
    ACADEMIC,
    SYSTEM
}

private data class AuditLog(
    val action: String,
    val description: String,
    val administrator: String,
    val role: String,
    val time: String,
    val category: AuditFilter,
    val severity: String,
    val iconType: AuditIcon
)

private enum class AuditIcon {
    LOGIN,
    EDIT,
    ADD,
    DELETE,
    SECURITY,
    WARNING
}

private data class AdminPermission(
    val name: String,
    val description: String,
    val enabled: Boolean,
    val level: String
)

@Composable
fun AdminSecurityAuditScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(SecurityTab.AUDIT_LOGS) }
    var selectedFilter by rememberSaveable { mutableStateOf(AuditFilter.ALL) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var selectedPermission by remember { mutableStateOf<AdminPermission?>(null) }

    val auditLogs = remember { demoAuditLogs() }
    val permissions = remember { demoPermissions() }

    val filteredLogs = auditLogs.filter { log ->
        val matchesFilter = selectedFilter == AuditFilter.ALL || log.category == selectedFilter
        val query = searchQuery.trim()
        val matchesSearch = query.isBlank() || log.action.contains(query, ignoreCase = true) || log.administrator.contains(query, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Security & Audit",
        screenSubtitle = "Institutional integrity control",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .padding(bottom = 90.dp)
        ) {
            
            SecurityOverviewCard()
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFEAF0F8)).padding(4.dp)) {
                SecurityTabItem("Audit Logs", selectedTab == SecurityTab.AUDIT_LOGS, { selectedTab = SecurityTab.AUDIT_LOGS }, Modifier.weight(1f))
                SecurityTabItem("Permissions", selectedTab == SecurityTab.PERMISSIONS, { selectedTab = SecurityTab.PERMISSIONS }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                SecurityTab.AUDIT_LOGS -> AuditLogsSection(filteredLogs, searchQuery, selectedFilter, { searchQuery = it }, { showFilterDialog = true })
                SecurityTab.PERMISSIONS -> PermissionsSection(permissions) {
                    selectedPermission = it
                    showPermissionDialog = true
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Activity", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    AuditFilter.entries.forEach { filter ->
                        Row(modifier = Modifier.fillMaxWidth().clickable { selectedFilter = filter; showFilterDialog = false }.padding(12.dp)) {
                            Text(text = filter.name.lowercase().replaceFirstChar { it.uppercase() }, color = if (selectedFilter == filter) KikaoColors.Teal else KikaoColors.Ink, fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFilterDialog = false }) { Text("Close") } }
        )
    }

    selectedPermission?.let { permission ->
        if (showPermissionDialog) {
            PermissionDialog(permission) { showPermissionDialog = false; selectedPermission = null }
        }
    }
}

@Composable
private fun SecurityTabItem(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(if (isSelected) KikaoColors.Indigo else Color.Transparent).clickable(onClick = onClick).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text(text = text, color = if (isSelected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SecurityOverviewCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "SYSTEM HEALTH", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Security center", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Security, contentDescription = null, tint = KikaoColors.Gold, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OverviewMetric("99.8%", "Uptime")
                OverviewMetric("0", "Critical Alerts")
                OverviewMetric("8", "Administrators")
            }
        }
    }
}

@Composable
private fun OverviewMetric(value: String, label: String) {
    Column {
        Text(text = value, color = KikaoColors.Gold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
private fun AuditLogsSection(logs: List<AuditLog>, query: String, filter: AuditFilter, onQueryChange: (String) -> Unit, onFilterClick: () -> Unit) {
    Column {
        OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search logs...") }, shape = RoundedCornerShape(14.dp), trailingIcon = { IconButton(onClick = onFilterClick) { Icon(Icons.Default.Tune, contentDescription = null) } })
        Spacer(modifier = Modifier.height(15.dp))
        logs.forEach { log ->
            AuditLogCard(log)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun AuditLogCard(log: AuditLog) {
    val color = when (log.severity) {
        "High" -> Color(0xFFD32F2F)
        "Medium" -> Color(0xFFF57C00)
        else -> KikaoColors.Teal
    }
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(auditIcon(log.iconType), contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = log.action, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = log.time, color = KikaoColors.MutedText, fontSize = 10.sp)
                }
                Text(text = log.description, color = KikaoColors.MutedText, fontSize = 11.sp, lineHeight = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "${log.administrator} · ${log.role}", color = KikaoColors.Ink, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun PermissionsSection(permissions: List<AdminPermission>, onPermissionClick: (AdminPermission) -> Unit) {
    Column {
        permissions.forEach { permission ->
            PermissionCard(permission) { onPermissionClick(permission) }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PermissionCard(permission: AdminPermission, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(KikaoColors.Indigo.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = KikaoColors.Indigo, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = permission.name, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = permission.description, color = KikaoColors.MutedText, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KikaoColors.MutedText)
        }
    }
}

@Composable
private fun PermissionDialog(permission: AdminPermission, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Permission", fontWeight = FontWeight.Bold) },
        text = { Text("Configuration for ${permission.name} access level.") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Save", color = KikaoColors.Teal, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun auditIcon(type: AuditIcon) = when (type) {
    AuditIcon.LOGIN -> Icons.AutoMirrored.Filled.Login
    AuditIcon.EDIT -> Icons.Default.Edit
    AuditIcon.ADD -> Icons.Default.PersonAdd
    AuditIcon.DELETE -> Icons.Default.Delete
    AuditIcon.SECURITY -> Icons.Default.Security
    AuditIcon.WARNING -> Icons.Default.Warning
}

private fun demoAuditLogs() = listOf(
    AuditLog("Admin login", "Sign-in from portal.", "Isaac Mwema", "Super Admin", "08:42 AM", AuditFilter.SECURITY, "Normal", AuditIcon.LOGIN),
    AuditLog("Record deleted", "Test record removed.", "Mary Njeri", "Academic Admin", "11:05 AM", AuditFilter.USERS, "Medium", AuditIcon.DELETE)
)

private fun demoPermissions() = listOf(
    AdminPermission("Manage Students", "Modify academic records.", true, "Full"),
    AdminPermission("System Config", "Change global settings.", true, "Elevated")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminSecurityAuditPreview() {
    MaterialTheme {
        AdminSecurityAuditScreen()
    }
}
