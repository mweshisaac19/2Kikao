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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * KIKAO
 * Lecturer Mark Appeals Inbox
 *
 * Dedicated workflow for student grade/mark change requests.
 *
 * Later Firebase integration can replace the mock data without changing
 * the overall UI structure.
 */

private object KikaoAppealColors {
    val Background = Color(0xFFF5F7FB)
    val Indigo = Color(0xFF172554)
    val DeepIndigo = Color(0xFF0F172A)
    val Teal = Color(0xFF0F9D8A)
    val TealLight = Color(0xFFE6F7F3)
    val Gold = Color(0xFFF4C95D)
    val Ink = Color(0xFF172033)
    val Muted = Color(0xFF718096)
    val Border = Color(0xFFE4E9F0)
    val Blue = Color(0xFF2563EB)
    val BlueLight = Color(0xFFEAF1FF)
    val Green = Color(0xFF16855B)
    val GreenLight = Color(0xFFE7F7EF)
    val Amber = Color(0xFFB7791F)
    val AmberLight = Color(0xFFFFF5D8)
    val Red = Color(0xFFB42318)
    val RedLight = Color(0xFFFFECEB)
}

private enum class AppealStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    REJECTED
}

private enum class AppealFilter {
    ALL,
    PENDING,
    REVIEWING,
    RESOLVED
}

private data class MarkAppeal(
    val id: String,
    val studentName: String,
    val admissionNo: String,
    val course: String,
    val assessment: String,
    val currentMark: String,
    val requestedMark: String,
    val reason: String,
    val submitted: String,
    val status: AppealStatus,
    val hasAttachment: Boolean
)

@Composable
fun LecturerMarkAppealsInboxScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(AppealFilter.ALL) }
    var filterExpanded by remember { mutableStateOf(false) }
    var selectedAppeal by remember { mutableStateOf<MarkAppeal?>(null) }

    val appeals = remember {
        listOf(
            MarkAppeal(
                id = "APL-2401",
                studentName = "Brian Otieno",
                admissionNo = "SCI/2024/0182",
                course = "CSC 210 — Data Structures",
                assessment = "CAT 1",
                currentMark = "58",
                requestedMark = "68",
                reason = "I believe question 4 was marked incorrectly. I have attached my working for review.",
                submitted = "18 Aug, 10:42 AM",
                status = AppealStatus.PENDING,
                hasAttachment = true
            ),
            MarkAppeal(
                id = "APL-2398",
                studentName = "Faith Wanjiku",
                admissionNo = "BIT/2024/0074",
                course = "CSC 210 — Data Structures",
                assessment = "Assignment 2",
                currentMark = "72",
                requestedMark = "78",
                reason = "Part of my submission appears not to have been included during marking.",
                submitted = "17 Aug, 3:18 PM",
                status = AppealStatus.UNDER_REVIEW,
                hasAttachment = true
            )
        )
    }

    val filteredAppeals = appeals.filter { appeal ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    appeal.studentName.contains(searchQuery, true) ||
                    appeal.admissionNo.contains(searchQuery, true) ||
                    appeal.course.contains(searchQuery, true) ||
                    appeal.id.contains(searchQuery, true)

        val matchesFilter = when (selectedFilter) {
            AppealFilter.ALL -> true
            AppealFilter.PENDING -> appeal.status == AppealStatus.PENDING
            AppealFilter.REVIEWING -> appeal.status == AppealStatus.UNDER_REVIEW
            AppealFilter.RESOLVED ->
                appeal.status == AppealStatus.APPROVED ||
                        appeal.status == AppealStatus.REJECTED
        }

        matchesSearch && matchesFilter
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.RESULTS,
        screenTitle = "Mark Appeals",
        screenSubtitle = "Student grade requests",
        onBackClick = onBack,
        onTabSelected = onTabSelected
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                AppealsOverview(
                    appeals = appeals
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(15.dp),
                        placeholder = {
                            Text(
                                "Search student, course or appeal ID",
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = KikaoAppealColors.Teal
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(
                            onClick = { filterExpanded = true }
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = KikaoAppealColors.Indigo
                            )
                        }

                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = {
                                filterExpanded = false
                            }
                        ) {
                            AppealFilter.entries.forEach { filter ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (filter) {
                                                AppealFilter.ALL -> "All appeals"
                                                AppealFilter.PENDING -> "Pending"
                                                AppealFilter.REVIEWING -> "Under review"
                                                AppealFilter.RESOLVED -> "Resolved"
                                            }
                                        )
                                    },
                                    onClick = {
                                        selectedFilter = filter
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "${filteredAppeals.size} appeals",
                    modifier = Modifier.padding(
                        horizontal = 20.dp
                    ),
                    color = KikaoAppealColors.Muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            items(
                items = filteredAppeals,
                key = { it.id }
            ) { appeal ->
                MarkAppealCard(
                    appeal = appeal,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = {
                        selectedAppeal = appeal
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    selectedAppeal?.let { appeal ->
        AppealReviewDialog(
            appeal = appeal,
            onDismiss = {
                selectedAppeal = null
            },
            onApprove = {
                selectedAppeal = null
            },
            onReject = {
                selectedAppeal = null
            },
            onRequestReview = {
                selectedAppeal = null
            }
        )
    }
}

@Composable
private fun AppealsOverview(
    appeals: List<MarkAppeal>
) {
    val pending = appeals.count {
        it.status == AppealStatus.PENDING
    }

    val reviewing = appeals.count {
        it.status == AppealStatus.UNDER_REVIEW
    }

    val resolved = appeals.count {
        it.status == AppealStatus.APPROVED ||
                it.status == AppealStatus.REJECTED
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoAppealColors.DeepIndigo
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
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            KikaoAppealColors.Teal.copy(
                                alpha = 0.18f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = KikaoAppealColors.Teal,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Grade review centre",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Review, verify and resolve student mark requests.",
                        color = Color.White.copy(alpha = 0.62f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OverviewMetric(
                    value = pending.toString(),
                    label = "Pending",
                    highlight = KikaoAppealColors.Gold
                )

                OverviewMetric(
                    value = reviewing.toString(),
                    label = "Reviewing",
                    highlight = Color(0xFF7DD3FC)
                )

                OverviewMetric(
                    value = resolved.toString(),
                    label = "Resolved",
                    highlight = KikaoAppealColors.Teal
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    value: String,
    label: String,
    highlight: Color
) {
    Column {
        Text(
            text = value,
            color = highlight,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun MarkAppealCard(
    appeal: MarkAppeal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val statusColor = appealStatusColor(appeal.status)
    val statusBackground = appealStatusBackground(appeal.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudentAvatar(
                    name = appeal.studentName
                )

                Spacer(modifier = Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appeal.studentName,
                        color = KikaoAppealColors.Ink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = appeal.admissionNo,
                        color = KikaoAppealColors.Muted,
                        fontSize = 9.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBackground
                ) {
                    Text(
                        text = appealStatusLabel(appeal.status),
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        ),
                        color = statusColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KikaoAppealColors.Background)
                    .padding(11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appeal.course,
                        color = KikaoAppealColors.Ink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = appeal.assessment,
                        color = KikaoAppealColors.Muted,
                        fontSize = 9.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${appeal.currentMark} → ${appeal.requestedMark}",
                        color = KikaoAppealColors.Indigo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "mark requested",
                        color = KikaoAppealColors.Muted,
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = appeal.reason,
                color = KikaoAppealColors.Muted,
                fontSize = 10.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(11.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = KikaoAppealColors.Muted,
                    modifier = Modifier.size(13.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = appeal.submitted,
                    color = KikaoAppealColors.Muted,
                    fontSize = 8.sp
                )

                if (appeal.hasAttachment) {
                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = "Attachment",
                        tint = KikaoAppealColors.Teal,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = "Evidence attached",
                        color = KikaoAppealColors.Teal,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = KikaoAppealColors.Muted,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
private fun StudentAvatar(
    name: String
) {
    val initials = name
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString("")

    Box(
        modifier = Modifier
            .size(43.dp)
            .clip(CircleShape)
            .background(KikaoAppealColors.Indigo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AppealReviewDialog(
    appeal: MarkAppeal,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRequestReview: () -> Unit
) {
    var showRejectConfirmation by remember {
        mutableStateOf(false)
    }

    var lecturerComment by remember {
        mutableStateOf("")
    }

    if (showRejectConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showRejectConfirmation = false
            },
            title = {
                Text(
                    text = "Reject appeal?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "The student will be notified that this grade change request was not approved."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectConfirmation = false
                        onReject()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KikaoAppealColors.Red
                    )
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showRejectConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )

        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudentAvatar(
                        name = appeal.studentName
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = appeal.studentName,
                            color = KikaoAppealColors.Ink,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = appeal.id,
                            color = KikaoAppealColors.Teal,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(430.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AppealDetailSection(
                        title = "Assessment",
                        value = "${appeal.course}\n${appeal.assessment}"
                    )
                }

                item {
                    MarkComparison(
                        current = appeal.currentMark,
                        requested = appeal.requestedMark
                    )
                }

                item {
                    AppealDetailSection(
                        title = "Student's reason",
                        value = appeal.reason
                    )
                }

                item {
                    if (appeal.hasAttachment) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    KikaoAppealColors.TealLight
                                )
                                .padding(11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = KikaoAppealColors.Teal,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Supporting evidence",
                                    color = KikaoAppealColors.Ink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Student submitted an attachment",
                                    color = KikaoAppealColors.Muted,
                                    fontSize = 8.sp
                                )
                            }

                            Text(
                                text = "VIEW",
                                color = KikaoAppealColors.Teal,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = lecturerComment,
                        onValueChange = {
                            lecturerComment = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(13.dp),
                        label = {
                            Text(
                                "Lecturer decision note",
                                fontSize = 10.sp
                            )
                        },
                        placeholder = {
                            Text(
                                "Add a note for the student...",
                                fontSize = 10.sp
                            )
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onApprove,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KikaoAppealColors.Teal
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                "Approve",
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = {
                                showRejectConfirmation = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KikaoAppealColors.Red
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                "Reject",
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = onRequestReview,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            "Request moderation / second review",
                            fontSize = 10.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Text(
                text = "Close",
                modifier = Modifier.clickable(
                    onClick = onDismiss
                ),
                color = KikaoAppealColors.Muted,
                fontSize = 12.sp
            )
        }
    )
}

@Composable
private fun AppealDetailSection(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title.uppercase(),
            color = KikaoAppealColors.Muted,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = value,
            color = KikaoAppealColors.Ink,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun MarkComparison(
    current: String,
    requested: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MarkBox(
            title = "CURRENT MARK",
            mark = current,
            background = KikaoAppealColors.Background,
            modifier = Modifier.weight(1f)
        )

        MarkBox(
            title = "REQUESTED MARK",
            mark = requested,
            background = KikaoAppealColors.TealLight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MarkBox(
    title: String,
    mark: String,
    background: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                color = KikaoAppealColors.Muted,
                fontSize = 7.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = mark,
                color = KikaoAppealColors.Indigo,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private fun appealStatusLabel(
    status: AppealStatus
): String {
    return when (status) {
        AppealStatus.PENDING -> "PENDING"
        AppealStatus.UNDER_REVIEW -> "REVIEWING"
        AppealStatus.APPROVED -> "APPROVED"
        AppealStatus.REJECTED -> "REJECTED"
    }
}

private fun appealStatusColor(
    status: AppealStatus
): Color {
    return when (status) {
        AppealStatus.PENDING -> KikaoAppealColors.Amber
        AppealStatus.UNDER_REVIEW -> KikaoAppealColors.Blue
        AppealStatus.APPROVED -> KikaoAppealColors.Green
        AppealStatus.REJECTED -> KikaoAppealColors.Red
    }
}

private fun appealStatusBackground(
    status: AppealStatus
): Color {
    return when (status) {
        AppealStatus.PENDING -> KikaoAppealColors.AmberLight
        AppealStatus.UNDER_REVIEW -> KikaoAppealColors.BlueLight
        AppealStatus.APPROVED -> KikaoAppealColors.GreenLight
        AppealStatus.REJECTED -> KikaoAppealColors.RedLight
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LecturerMarkAppealsInboxPreview() {
    MaterialTheme {
        LecturerMarkAppealsInboxScreen()
    }
}