package com.mwema.a2kikao.ui.screens.lecturer


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * KIKAO
 * Session Cancellation Logger
 *
 * Lecturer quick-action screen for cancelling a scheduled class/session.
 *
 * Intended workflow:
 *
 * Lecturer selects session
 *       ↓
 * Selects cancellation reason
 *       ↓
 * Adds optional message
 *       ↓
 * Confirms cancellation
 *       ↓
 * Students are notified
 *       ↓
 * Timetable is updated
 *
 * Replace mock callbacks/data with Firebase/Firestore logic later.
 */

private object KikaoCancellationColors {
    val Background = Color(0xFFF5F7FB)
    val Indigo = Color(0xFF172554)
    val DeepIndigo = Color(0xFF0F172A)
    val Teal = Color(0xFF0F9D8A)
    val TealLight = Color(0xFFE5F7F3)
    val Gold = Color(0xFFF4C95D)
    val Ink = Color(0xFF172033)
    val Muted = Color(0xFF718096)
    val Border = Color(0xFFE3E8EF)
    val Red = Color(0xFFB42318)
    val RedLight = Color(0xFFFFECEB)
    val Blue = Color(0xFF2563EB)
    val BlueLight = Color(0xFFEAF1FF)
    val Green = Color(0xFF16855B)
    val GreenLight = Color(0xFFE8F7EF)
    val Amber = Color(0xFFB7791F)
    val AmberLight = Color(0xFFFFF6DD)
}

private data class ScheduledSession(
    val id: String,
    val courseCode: String,
    val courseName: String,
    val topic: String,
    val date: String,
    val time: String,
    val room: String,
    val students: Int
)

private enum class CancellationReason(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    ILLNESS(
        "Lecturer unavailable",
        "Illness or personal emergency",
        Icons.Default.Info
    ),
    UNIVERSITY_EVENT(
        "University event",
        "Official university activity or event",
        Icons.Default.CalendarMonth
    ),
    ROOM_UNAVAILABLE(
        "Room unavailable",
        "Room or campus facility issue",
        Icons.Default.EventBusy
    ),
    WEATHER(
        "Weather / travel disruption",
        "Unsafe or disrupted travel conditions",
        Icons.Default.Warning
    ),
    OTHER(
        "Other reason",
        "Another reason not listed above",
        Icons.Default.MoreHorizIcon()
    )
}

@Composable
fun LecturerSessionCancellationLoggerScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCancellationComplete: () -> Unit = {},
    onViewTimetable: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedSession by remember { mutableStateOf<ScheduledSession?>(null) }
    var selectedReason by remember { mutableStateOf<CancellationReason?>(null) }
    var additionalMessage by remember { mutableStateOf("") }
    var notifyStudents by remember { mutableStateOf(true) }
    var updateTimetable by remember { mutableStateOf(true) }
    var showConfirmation by remember { mutableStateOf(false) }

    val sessions = remember {
        listOf(
            ScheduledSession(
                id = "SES-210-22",
                courseCode = "CSC 210",
                courseName = "Data Structures",
                topic = "Trees & Graph Traversal",
                date = "Today, 2:00 PM",
                time = "2:00 PM – 4:00 PM",
                room = "Science Complex · LH-A01",
                students = 82
            ),
            ScheduledSession(
                id = "SES-301-24",
                courseCode = "CSC 301",
                courseName = "Algorithms",
                topic = "Dynamic Programming",
                date = "Tomorrow, 9:00 AM",
                time = "9:00 AM – 11:00 AM",
                room = "ICT Centre · LAB-C01",
                students = 64
            )
        )
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.SESSIONS,
        screenTitle = "Cancel Session",
        screenSubtitle = "Manage scheduling changes",
        onBackClick = onBack,
        onTabSelected = onTabSelected
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                CancellationHeader()
            }

            item {
                SectionLabel(
                    title = "1. Select session",
                    subtitle = "Choose the class you need to cancel"
                )
            }

            items(sessions.size) { index ->
                val session = sessions[index]

                SessionSelectionCard(
                    session = session,
                    selected = selectedSession?.id == session.id,
                    onClick = {
                        selectedSession = session
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                SectionLabel(
                    title = "2. Cancellation reason",
                    subtitle = "Help students understand why the class was cancelled"
                )
            }

            items(CancellationReason.entries.size) { index ->
                val reason = CancellationReason.entries[index]

                CancellationReasonCard(
                    reason = reason,
                    selected = selectedReason == reason,
                    onClick = {
                        selectedReason = reason
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                SectionLabel(
                    title = "3. Student notification",
                    subtitle = "The cancellation will appear in student notifications"
                )
            }

            item {
                NotificationPreview(
                    session = selectedSession,
                    reason = selectedReason,
                    message = additionalMessage,
                    notifyStudents = notifyStudents,
                    onNotifyChanged = {
                        notifyStudents = it
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = additionalMessage,
                    onValueChange = {
                        additionalMessage = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    label = {
                        Text(
                            text = "Additional message (optional)",
                            fontSize = 11.sp
                        )
                    },
                    placeholder = {
                        Text(
                            text = "e.g. We will communicate the replacement date shortly.",
                            fontSize = 10.sp
                        )
                    }
                )
            }

            item {
                TimetableUpdateCard(
                    updateTimetable = updateTimetable,
                    onUpdateChanged = {
                        updateTimetable = it
                    },
                    onViewTimetable = onViewTimetable,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                CancellationImpactCard(
                    session = selectedSession,
                    notifyStudents = notifyStudents,
                    updateTimetable = updateTimetable,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        if (
                            selectedSession != null &&
                            selectedReason != null
                        ) {
                            showConfirmation = true
                        }
                    },
                    enabled = selectedSession != null &&
                            selectedReason != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KikaoCancellationColors.Red,
                        disabledContainerColor = KikaoCancellationColors.Border,
                        disabledContentColor = KikaoCancellationColors.Muted
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Cancel Session",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showConfirmation) {
        CancellationConfirmationDialog(
            session = selectedSession,
            reason = selectedReason,
            notifyStudents = notifyStudents,
            updateTimetable = updateTimetable,
            onDismiss = {
                showConfirmation = false
            },
            onConfirm = {
                showConfirmation = false
                onCancellationComplete()
            }
        )
    }
}

@Composable
private fun CancellationHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoCancellationColors.DeepIndigo
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            KikaoCancellationColors.Red.copy(
                                alpha = 0.16f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = Color(0xFFFF8A80),
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Class cancellation",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cancel once. Kikao handles the communication.",
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepDot(
                    number = "1",
                    active = true
                )

                StepLine()

                StepDot(
                    number = "2",
                    active = true
                )

                StepLine()

                StepDot(
                    number = "3",
                    active = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Select → Explain → Notify",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun StepDot(
    number: String,
    active: Boolean
) {
    Box(
        modifier = Modifier
            .size(25.dp)
            .clip(CircleShape)
            .background(
                if (active) {
                    KikaoCancellationColors.Teal
                } else {
                    Color.White.copy(alpha = 0.15f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StepLine() {
    Box(
        modifier = Modifier
            .width(25.dp)
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.18f))
    )
}

@Composable
private fun SectionLabel(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.padding(
            horizontal = 20.dp
        )
    ) {
        Text(
            text = title,
            color = KikaoCancellationColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            color = KikaoCancellationColors.Muted,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun SessionSelectionCard(
    session: ScheduledSession,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                KikaoCancellationColors.Indigo
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (selected) {
                            Color.White.copy(alpha = 0.12f)
                        } else {
                            KikaoCancellationColors.BlueLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = if (selected) {
                        KikaoCancellationColors.Gold
                    } else {
                        KikaoCancellationColors.Blue
                    },
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${session.courseCode} · ${session.courseName}",
                    color = if (selected) {
                        Color.White
                    } else {
                        KikaoCancellationColors.Ink
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = session.topic,
                    color = if (selected) {
                        Color.White.copy(alpha = 0.68f)
                    } else {
                        KikaoCancellationColors.Muted
                    },
                    fontSize = 9.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (selected) {
                            Color.White.copy(alpha = 0.60f)
                        } else {
                            KikaoCancellationColors.Muted
                        },
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = session.date,
                        color = if (selected) {
                            Color.White.copy(alpha = 0.60f)
                        } else {
                            KikaoCancellationColors.Muted
                        },
                        fontSize = 8.sp
                    )

                    Spacer(modifier = Modifier.width(9.dp))

                    Text(
                        text = "• ${session.room}",
                        color = if (selected) {
                            Color.White.copy(alpha = 0.60f)
                        } else {
                            KikaoCancellationColors.Muted
                        },
                        fontSize = 8.sp
                    )
                }
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = KikaoCancellationColors.Teal,
                    modifier = Modifier.size(21.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Select",
                    tint = KikaoCancellationColors.Muted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CancellationReasonCard(
    reason: CancellationReason,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                KikaoCancellationColors.RedLight
            } else {
                Color.White
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(
                        if (selected) {
                            Color.White
                        } else {
                            KikaoCancellationColors.Background
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = reason.icon,
                    contentDescription = null,
                    tint = if (selected) {
                        KikaoCancellationColors.Red
                    } else {
                        KikaoCancellationColors.Indigo
                    },
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reason.title,
                    color = KikaoCancellationColors.Ink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = reason.description,
                    color = KikaoCancellationColors.Muted,
                    fontSize = 8.sp
                )
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = KikaoCancellationColors.Red,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationPreview(
    session: ScheduledSession?,
    reason: CancellationReason?,
    message: String,
    notifyStudents: Boolean,
    onNotifyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(KikaoCancellationColors.TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = KikaoCancellationColors.Teal,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Student notification",
                        color = KikaoCancellationColors.Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Preview what students will receive",
                        color = KikaoCancellationColors.Muted,
                        fontSize = 8.sp
                    )
                }

                TogglePill(
                    enabled = notifyStudents,
                    onClick = {
                        onNotifyChanged(!notifyStudents)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(KikaoCancellationColors.Background)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "CLASS CANCELLED",
                        color = KikaoCancellationColors.Red,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = session?.let {
                            "${it.courseCode} — ${it.courseName}"
                        } ?: "Select a session",
                        color = KikaoCancellationColors.Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = buildString {
                            append(session?.date ?: "Session date")
                            append(" · ")
                            append(session?.room ?: "Room")
                        },
                        color = KikaoCancellationColors.Muted,
                        fontSize = 9.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = reason?.let {
                            "Reason: ${it.title}"
                        } ?: "Select a cancellation reason",
                        color = KikaoCancellationColors.Muted,
                        fontSize = 9.sp
                    )

                    if (message.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = message,
                            color = KikaoCancellationColors.Ink,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TogglePill(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (enabled) {
            KikaoCancellationColors.TealLight
        } else {
            KikaoCancellationColors.Border
        }
    ) {
        Text(
            text = if (enabled) "ON" else "OFF",
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 6.dp
            ),
            color = if (enabled) {
                KikaoCancellationColors.Teal
            } else {
                KikaoCancellationColors.Muted
            },
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TimetableUpdateCard(
    updateTimetable: Boolean,
    onUpdateChanged: (Boolean) -> Unit,
    onViewTimetable: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(39.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(KikaoCancellationColors.BlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = KikaoCancellationColors.Blue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Update timetable",
                        color = KikaoCancellationColors.Ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Mark this session as cancelled on the timetable",
                        color = KikaoCancellationColors.Muted,
                        fontSize = 8.sp
                    )
                }

                TogglePill(
                    enabled = updateTimetable,
                    onClick = {
                        onUpdateChanged(!updateTimetable)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewTimetable,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(11.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "View lecturer timetable",
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun CancellationImpactCard(
    session: ScheduledSession?,
    notifyStudents: Boolean,
    updateTimetable: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoCancellationColors.Indigo
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "WHAT HAPPENS NEXT",
                color = KikaoCancellationColors.Gold,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            ImpactRow(
                icon = Icons.Default.Notifications,
                text = if (notifyStudents) {
                    "${session?.students ?: 0} students receive a cancellation notification"
                } else {
                    "Students will not receive an automatic notification"
                },
                enabled = notifyStudents
            )

            Spacer(modifier = Modifier.height(9.dp))

            ImpactRow(
                icon = Icons.Default.CalendarMonth,
                text = if (updateTimetable) {
                    "Session is marked cancelled on the timetable"
                } else {
                    "Timetable will remain unchanged"
                },
                enabled = updateTimetable
            )

            Spacer(modifier = Modifier.height(9.dp))

            ImpactRow(
                icon = Icons.Default.Send,
                text = "The cancellation is recorded in the session history",
                enabled = true
            )
        }
    }
}

@Composable
private fun ImpactRow(
    icon: ImageVector,
    text: String,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) {
                        KikaoCancellationColors.Teal.copy(alpha = 0.18f)
                    } else {
                        Color.White.copy(alpha = 0.08f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) {
                    KikaoCancellationColors.Teal
                } else {
                    Color.White.copy(alpha = 0.30f)
                },
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = text,
            color = Color.White.copy(
                alpha = if (enabled) 0.75f else 0.40f
            ),
            fontSize = 9.sp,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun CancellationConfirmationDialog(
    session: ScheduledSession?,
    reason: CancellationReason?,
    notifyStudents: Boolean,
    updateTimetable: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(KikaoCancellationColors.RedLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = KikaoCancellationColors.Red,
                    modifier = Modifier.size(27.dp)
                )
            }
        },
        title = {
            Text(
                text = "Confirm cancellation",
                color = KikaoCancellationColors.Ink,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = session?.let {
                        "${it.courseCode} — ${it.courseName}"
                    } ?: "Selected session",
                    color = KikaoCancellationColors.Ink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = session?.date ?: "",
                    color = KikaoCancellationColors.Muted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Reason",
                    color = KikaoCancellationColors.Muted,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = reason?.title ?: "Not specified",
                    color = KikaoCancellationColors.Ink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (notifyStudents) {
                    ConfirmationLine(
                        text = "Students will be notified"
                    )
                }

                if (updateTimetable) {
                    ConfirmationLine(
                        text = "Timetable will be updated"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KikaoCancellationColors.Red
                ),
                shape = RoundedCornerShape(11.dp)
            ) {
                Text(
                    text = "Confirm cancellation",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(11.dp)
            ) {
                Text(
                    text = "Go back",
                    fontSize = 10.sp
                )
            }
        }
    )
}

@Composable
private fun ConfirmationLine(
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = KikaoCancellationColors.Teal,
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = KikaoCancellationColors.Muted,
            fontSize = 9.sp
        )
    }
}

/*
 * Small helper so this single file remains self-contained.
 */
private fun Icons.Filled.MoreHorizIcon(): ImageVector {
    return Icons.Default.MoreVert
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LecturerSessionCancellationLoggerPreview() {
    MaterialTheme {
        LecturerSessionCancellationLoggerScreen()
    }
}