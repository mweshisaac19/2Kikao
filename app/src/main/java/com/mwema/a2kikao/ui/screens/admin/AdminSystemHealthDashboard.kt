package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private object KikaoHealthColors {
    val Background = Color(0xFFF5F7FB)
    val Indigo = Color(0xFF172554)
    val DeepIndigo = Color(0xFF0F172A)
    val Teal = Color(0xFF0F9D8A)
    val TealLight = Color(0xFFE5F7F3)
    val Gold = Color(0xFFF4C95D)
    val Ink = Color(0xFF172033)
    val MutedText = Color(0xFF718096)
    val Green = Color(0xFF16855B)
    val GreenLight = Color(0xFFE8F7EF)
    val Amber = Color(0xFFB7791F)
    val AmberLight = Color(0xFFFFF6DD)
    val Red = Color(0xFFB42318)
    val RedLight = Color(0xFFFFECEB)
    val Blue = Color(0xFF2563EB)
}

private enum class HealthStatus {
    HEALTHY,
    DEGRADED,
    DOWN
}

private data class ServiceHealth(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val status: HealthStatus,
    val latency: String,
    val uptime: String
)

private data class Incident(
    val title: String,
    val description: String,
    val time: String,
    val status: String,
    val severity: String
)

@Composable
fun AdminSystemHealthDashboardScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAuditLogs: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val services = remember {
        listOf(
            ServiceHealth("Application Server", "API Gateway", Icons.Default.Dns, HealthStatus.HEALTHY, "86ms", "99.9%"),
            ServiceHealth("Realtime DB", "Live Sync", Icons.Default.DataObject, HealthStatus.HEALTHY, "74ms", "99.9%"),
            ServiceHealth("Storage", "Media assets", Icons.Default.Cloud, HealthStatus.HEALTHY, "148ms", "99.8%")
        )
    }

    val incidents = remember {
        listOf(
            Incident("Minor Latency", "Network traffic spikes.", "12m ago", "Monitoring", "Low")
        )
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "System Health",
        screenSubtitle = "Technical infrastructure status",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text("‹ Back to security", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                }
            }

            item {
                OverallHealthCard()
            }

            item {
                SectionTitle("Service availability")
            }

            items(services) { service ->
                ServiceHealthCard(service, Modifier.padding(horizontal = 20.dp))
            }

            item {
                SectionTitle("Recent incidents")
            }

            items(incidents) { incident ->
                IncidentCard(incident, Modifier.padding(horizontal = 20.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
}

@Composable
private fun OverallHealthCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoHealthColors.DeepIndigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(KikaoHealthColors.Teal.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = KikaoHealthColors.Teal)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("All Systems Normal", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("99.98% Uptime", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ServiceHealthCard(service: ServiceHealth, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(KikaoHealthColors.TealLight), contentAlignment = Alignment.Center) {
                Icon(service.icon, contentDescription = null, tint = KikaoHealthColors.Teal, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = service.latency, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            StatusBadge(service.status)
        }
    }
}

@Composable
private fun IncidentCard(incident: Incident, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = incident.title, color = KikaoColors.Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = incident.description, color = KikaoColors.MutedText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StatusBadge(status: HealthStatus) {
    val color = when(status) {
        HealthStatus.HEALTHY -> KikaoHealthColors.Green
        else -> KikaoHealthColors.Amber
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 3.dp)) {
        Text(text = status.name, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminSystemHealthDashboardPreview() {
    MaterialTheme {
        AdminSystemHealthDashboardScreen()
    }
}
