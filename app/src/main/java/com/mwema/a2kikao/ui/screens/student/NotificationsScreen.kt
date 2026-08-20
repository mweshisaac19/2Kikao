package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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
import com.mwema.a2kikao.ui.viewmodels.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class NotificationFilter {
    ALL,
    LECTURERS,
    ADMIN
}

enum class NotificationType {
    ANNOUNCEMENT,
    GRADE,
    ATTENDANCE,
    SESSION,
    ADMINISTRATION
}

data class StudentNotification(
    val id: String,
    val title: String,
    val message: String,
    val sourceName: String,
    val sourceRole: String,
    val courseCode: String?,
    val time: String,
    val type: NotificationType,
    val isUnread: Boolean
)

@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNotificationClick: (StudentNotification) -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    var selectedFilter by remember {
        mutableStateOf(NotificationFilter.ALL)
    }

    val realNotifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    val mappedNotifications = realNotifications.map { notif ->
        StudentNotification(
            id = notif.id,
            title = notif.title,
            message = notif.message,
            sourceName = notif.sender,
            sourceRole = if (notif.classId != null) "Lecturer" else "System",
            courseCode = notif.classId,
            time = dateFormatter.format(Date(notif.timestamp)),
            type = when (notif.type) {
                "grade" -> NotificationType.GRADE
                "alert" -> NotificationType.ADMINISTRATION
                "attendance" -> NotificationType.ATTENDANCE
                else -> NotificationType.ANNOUNCEMENT
            },
            isUnread = true
        )
    }

    val notificationsToDisplay = if (mappedNotifications.isNotEmpty()) mappedNotifications else listOf(
        StudentNotification(
            id = "n1",
            title = "CAT 1 results have been posted",
            message = "Your CAT 1 result for Database Systems is now available. Open Academic Analytics to view your score and class comparison.",
            sourceName = "Dr. Kamau",
            sourceRole = "Lecturer",
            courseCode = "CSC 221",
            time = "18 min ago",
            type = NotificationType.GRADE,
            isUnread = true
        ),
        StudentNotification(
            id = "n2",
            title = "Tomorrow's class has moved",
            message = "The CSC 210 session scheduled for 10:00 AM tomorrow will now take place in Lab 3 instead of Room B14.",
            sourceName = "Prof. Wanjiku",
            sourceRole = "Lecturer",
            courseCode = "CSC 210",
            time = "1 hr ago",
            type = NotificationType.SESSION,
            isUnread = true
        ),
        StudentNotification(
            id = "n3",
            title = "Semester examination timetable",
            message = "The university has released the provisional end-of-semester examination timetable. Please review your examination dates and venues.",
            sourceName = "Academic Registry",
            sourceRole = "Administration",
            courseCode = null,
            time = "3 hrs ago",
            type = NotificationType.ADMINISTRATION,
            isUnread = true
        )
    )

    val filteredNotifications = when (selectedFilter) {
        NotificationFilter.ALL -> notificationsToDisplay
        NotificationFilter.LECTURERS ->
            notificationsToDisplay.filter { it.sourceRole == "Lecturer" }

        NotificationFilter.ADMIN ->
            notificationsToDisplay.filter {
                it.sourceRole == "Administration" ||
                        it.sourceRole == "System"
            }
    }

    val unreadCount = notificationsToDisplay.count { it.isUnread }

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.PROFILE,
        screenTitle = "Notifications",
        screenSubtitle = "Updates from your academic community",
        showScanButton = false,
        onBackClick = onBackClick,
        onNotificationClick = {},
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

            NotificationHeader(
                unreadCount = unreadCount,
                onMarkAllRead = {
                    // Logic to mark all as read in DB
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            NotificationFilters(
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    selectedFilter = it
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (filteredNotifications.isEmpty()) {
                EmptyNotificationsState()
            } else {

                Text(
                    text = if (unreadCount > 0) {
                        "Recent updates"
                    } else {
                        "Your notifications"
                    },
                    color = KikaoColors.Ink,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                filteredNotifications.forEach { notification ->

                    NotificationCard(
                        notification = notification,
                        onClick = {
                            onNotificationClick(notification)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NotificationFooter()
        }
    }
}

@Composable
private fun NotificationHeader(
    unreadCount: Int,
    onMarkAllRead: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Color.White.copy(alpha = 0.13f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "●",
                        color = KikaoColors.Gold,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Stay in the loop",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = if (unreadCount > 0) {
                            "$unreadCount new updates waiting for you"
                        } else {
                            "You're all caught up"
                        },
                        color = Color.White.copy(alpha = 0.70f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(17.dp))

            if (unreadCount > 0) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Color.White.copy(alpha = 0.10f)
                        )
                        .clickable(
                            onClick = onMarkAllRead
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 11.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "You have $unreadCount unread notifications",
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 11.sp
                    )

                    Text(
                        text = "Mark all read",
                        color = KikaoColors.Gold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationFilters(
    selectedFilter: NotificationFilter,
    onFilterSelected: (NotificationFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        NotificationFilterChip(
            label = "All",
            selected = selectedFilter == NotificationFilter.ALL,
            onClick = {
                onFilterSelected(NotificationFilter.ALL)
            }
        )

        NotificationFilterChip(
            label = "Lecturers",
            selected = selectedFilter == NotificationFilter.LECTURERS,
            onClick = {
                onFilterSelected(NotificationFilter.LECTURERS)
            }
        )

        NotificationFilterChip(
            label = "Administration",
            selected = selectedFilter == NotificationFilter.ADMIN,
            onClick = {
                onFilterSelected(NotificationFilter.ADMIN)
            }
        )
    }
}

@Composable
private fun NotificationFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    KikaoColors.Indigo
                } else {
                    Color.White
                }
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {

        Text(
            text = label,
            color = if (selected) {
                Color.White
            } else {
                KikaoColors.MutedText
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NotificationCard(
    notification: StudentNotification,
    onClick: () -> Unit
) {
    val accent = notificationAccent(notification.type)
    val icon = notificationIcon(notification.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isUnread) {
                Color.White
            } else {
                Color(0xFFFAFBFD)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isUnread) 3.dp else 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = icon,
                        color = accent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = notification.title,
                                color = KikaoColors.Ink,
                                fontSize = 14.sp,
                                fontWeight = if (notification.isUnread) {
                                    FontWeight.ExtraBold
                                } else {
                                    FontWeight.Bold
                                }
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = notification.sourceName,
                                    color = accent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "·",
                                    color = KikaoColors.MutedText,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = notification.sourceRole,
                                    color = KikaoColors.MutedText,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (notification.isUnread) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 3.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(KikaoColors.Teal)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = notification.message,
                color = KikaoColors.MutedText,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (notification.courseCode != null) {

                        Text(
                            text = notification.courseCode,
                            color = accent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    accent.copy(alpha = 0.10f)
                                )
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 5.dp
                                )
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = notification.time,
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "View ›",
                    color = KikaoColors.Indigo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun notificationAccent(
    type: NotificationType
): Color {
    return when (type) {
        NotificationType.GRADE ->
            KikaoColors.Teal

        NotificationType.ATTENDANCE ->
            Color(0xFF8B5CF6)

        NotificationType.SESSION ->
            KikaoColors.Gold

        NotificationType.ADMINISTRATION ->
            KikaoColors.Indigo

        NotificationType.ANNOUNCEMENT ->
            Color(0xFF2563EB)
    }
}

private fun notificationIcon(
    type: NotificationType
): String {
    return when (type) {
        NotificationType.GRADE -> "✓"
        NotificationType.ATTENDANCE -> "◉"
        NotificationType.SESSION -> "◷"
        NotificationType.ADMINISTRATION -> "!"
        NotificationType.ANNOUNCEMENT -> "i"
    }
}

@Composable
private fun EmptyNotificationsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✓",
                    color = KikaoColors.Teal,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Nothing here yet",
                color = KikaoColors.Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "New updates from your lecturers and university administration will appear here.",
                color = KikaoColors.MutedText,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun NotificationFooter() {
    Card(
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.TealLight
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KikaoColors.Teal),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column {

                Text(
                    text = "Official Kikao updates",
                    color = KikaoColors.Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Notifications are delivered from verified lecturers and university administrators.",
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun NotificationsScreenPreview() {
    MaterialTheme {
        NotificationsScreen()
    }
}
