package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mwema.a2kikao.ui.viewmodels.LecturerNotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class LecturerNotificationFilter {
    ALL,
    UNREAD,
    ACADEMIC,
    SYSTEM
}

data class LecturerNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val category: String,
    val icon: String,
    val iconBackground: Color,
    val iconColor: Color,
    val isUnread: Boolean
)

@Composable
fun LecturerNotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerNotificationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNotificationSelected: (LecturerNotification) -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(LecturerNotificationFilter.ALL) }
    val realNotifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    val mappedNotifications = realNotifications.map { notif ->
        val category = when (notif.type) {
            "grade", "assignment" -> "Academic"
            else -> "System"
        }
        val icon = when (category) {
            "Academic" -> "A"
            else -> "S"
        }
        val iconColor = if (category == "Academic") Color(0xFF0284C7) else Color(0xFF475569)
        val iconBg = if (category == "Academic") Color(0xFFE0F2FE) else Color(0xFFF1F5F9)

        LecturerNotification(
            id = notif.id,
            title = notif.title,
            message = notif.message,
            time = dateFormatter.format(Date(notif.timestamp)),
            category = category,
            icon = icon,
            iconBackground = iconBg,
            iconColor = iconColor,
            isUnread = true
        )
    }

    val notifications = if (mappedNotifications.isNotEmpty()) mappedNotifications else demoLecturerNotifications()

    val filteredNotifications = when (selectedFilter) {
        LecturerNotificationFilter.ALL -> notifications
        LecturerNotificationFilter.UNREAD -> notifications.filter { it.isUnread }
        LecturerNotificationFilter.ACADEMIC -> notifications.filter { it.category == "Academic" }
        LecturerNotificationFilter.SYSTEM -> notifications.filter { it.category == "System" }
    }

    val unreadCount = notifications.count { it.isUnread }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.HOME,
        screenTitle = "Notifications",
        screenSubtitle = "Stay updated on your classes",
        onTabSelected = onTabSelected
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Back Action
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            ) {
                Text("‹ Back", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                NotificationSummaryCard(
                    unreadCount = unreadCount,
                    totalCount = notifications.size
                )

                Spacer(modifier = Modifier.height(20.dp))

                NotificationFilters(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KikaoColors.Teal)
                    }
                } else if (filteredNotifications.isEmpty()) {
                    EmptyNotificationsState()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(filteredNotifications) { notification ->
                            LecturerNotificationCard(
                                notification = notification,
                                onClick = { 
                                    onNotificationSelected(notification)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSummaryCard(unreadCount: Int, totalCount: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 0) unreadCount.toString() else "✓",
                    color = KikaoColors.Gold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (unreadCount > 0) "New updates waiting" else "All caught up",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$totalCount total notifications recorded",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun NotificationFilters(
    selectedFilter: LecturerNotificationFilter,
    onFilterSelected: (LecturerNotificationFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LecturerNotificationFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                onClick = { onFilterSelected(filter) },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) KikaoColors.Indigo else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Text(
                    text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = if (isSelected) Color.White else KikaoColors.MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LecturerNotificationCard(notification: LecturerNotification, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(notification.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(notification.icon, color = notification.iconColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(notification.category.uppercase(), color = notification.iconColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    Text(notification.time, color = KikaoColors.MutedText, fontSize = 10.sp)
                }
                Text(notification.title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(notification.message, color = KikaoColors.MutedText, fontSize = 12.sp, lineHeight = 18.sp)
            }
            if (notification.isUnread) {
                Box(modifier = Modifier.padding(start = 8.dp).size(8.dp).clip(CircleShape).background(KikaoColors.Teal))
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No notifications found", color = KikaoColors.MutedText, fontWeight = FontWeight.Bold)
    }
}

private fun demoLecturerNotifications() = listOf(
    LecturerNotification("1", "New submission: CAT 1", "Amani Mwangi submitted CAT 1 for Database Systems.", "12m ago", "Academic", "A", Color(0xFFE0F2FE), Color(0xFF0284C7), true),
    LecturerNotification("2", "System Maintenance", "Kikao will be offline for 30 minutes tonight at 11:00 PM.", "1h ago", "System", "S", Color(0xFFF1F5F9), Color(0xFF475569), true)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerNotificationsPreview() {
    MaterialTheme {
        LecturerNotificationsScreen()
    }
}
