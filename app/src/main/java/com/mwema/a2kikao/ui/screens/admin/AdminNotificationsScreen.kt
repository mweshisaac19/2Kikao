package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// ------------------------------------------------------------
// DATA
// ------------------------------------------------------------

data class AdminNotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: AdminNotificationType,
    val isRead: Boolean = false
)

enum class AdminNotificationType {
    SYSTEM,
    ATTENDANCE,
    PERFORMANCE,
    REPORT
}

// ------------------------------------------------------------
// MAIN SCREEN
// ------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationSelected: (AdminNotificationItem) -> Unit = {}
) {
    val notifications = remember { demoAdminNotifications() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Mark all read */ }) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Mark all as read", tint = KikaoColors.Indigo)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            item {
                NotificationSectionHeader("Today")
            }

            items(notifications.filter { it.time.contains("m") || it.time.contains("h") }) { notification ->
                AdminNotificationCard(notification) { onNotificationSelected(notification) }
            }

            item {
                NotificationSectionHeader("Yesterday")
            }

            items(notifications.filter { it.time.contains("Yesterday") }) { notification ->
                AdminNotificationCard(notification) { onNotificationSelected(notification) }
            }
        }
    }
}

@Composable
private fun NotificationSectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = KikaoColors.MutedText,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun AdminNotificationCard(
    notification: AdminNotificationItem,
    onClick: () -> Unit
) {
    val (icon, accent) = when (notification.type) {
        AdminNotificationType.SYSTEM -> Icons.Default.SettingsSuggest to KikaoColors.Indigo
        AdminNotificationType.ATTENDANCE -> Icons.Default.FactCheck to KikaoColors.Teal
        AdminNotificationType.PERFORMANCE -> Icons.Default.TrendingUp to KikaoColors.Gold
        AdminNotificationType.REPORT -> Icons.Default.Summarize to Color(0xFF8B5CF6)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (notification.isRead) Color.White else Color(0xFFF8FAFF))
                .clickable(onClick = onClick)
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = notification.title,
                        color = KikaoColors.Ink,
                        fontSize = 14.sp,
                        fontWeight = if (notification.isRead) FontWeight.Bold else FontWeight.ExtraBold
                    )
                    Text(
                        text = notification.time,
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    color = if (notification.isRead) KikaoColors.MutedText else KikaoColors.Ink,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))
    }
}

private fun demoAdminNotifications() = listOf(
    AdminNotificationItem("1", "System Maintenance", "Scheduled maintenance tonight at 2:00 AM.", "10m ago", AdminNotificationType.SYSTEM),
    AdminNotificationItem("2", "Low Attendance Alert", "CSC 221 attendance dropped below 75% this week.", "2h ago", AdminNotificationType.ATTENDANCE),
    AdminNotificationItem("3", "Performance Report", "Monthly academic analytics for August are ready.", "Yesterday", AdminNotificationType.REPORT),
    AdminNotificationItem("4", "New Course Request", "Faculty of Science requested approval for MAT 302.", "Yesterday", AdminNotificationType.SYSTEM, true)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminNotificationsPreview() {
    MaterialTheme {
        AdminNotificationsScreen()
    }
}
