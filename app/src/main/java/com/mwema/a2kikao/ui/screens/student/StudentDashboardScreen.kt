package com.mwema.a2kikao.ui.screens.student


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.StudentDashboardViewModel

private data class TodayClass(
    val time: String,
    val title: String,
    val code: String,
    val location: String,
    val status: ClassStatus
)

private enum class ClassStatus {
    PRESENT,
    NEXT,
    LATER
}

@Composable
fun StudentDashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: StudentDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onViewAllClasses: () -> Unit = {},
    onClassClick: (String) -> Unit = {},
    onFinanceClick: () -> Unit = {},
    onExamSlipClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val attendancePercentage by viewModel.attendancePercentage.collectAsState()
    val todayClasses by viewModel.todayClasses.collectAsState()
    
    val studentName = userProfile?.fullName?.substringBefore(" ") ?: "Student"
    
    val mappedClasses = todayClasses.mapIndexed { index, course ->
        TodayClass(
            time = course.time,
            title = course.name,
            code = course.code,
            location = course.room,
            status = when (index) {
                0 -> ClassStatus.PRESENT
                1 -> ClassStatus.NEXT
                else -> ClassStatus.LATER
            }
        )
    }

    // Fallback classes if none are in DB
    val classesToShow = if (mappedClasses.isNotEmpty()) mappedClasses else listOf(
        TodayClass(
            time = "09:00",
            title = "Data Structures",
            code = "CSC 210",
            location = "Hall B",
            status = ClassStatus.PRESENT
        ),
        TodayClass(
            time = "14:00",
            title = "Database Systems",
            code = "CSC 221",
            location = "Lab 3",
            status = ClassStatus.NEXT
        ),
        TodayClass(
            time = "16:00",
            title = "Discrete Mathematics",
            code = "MAT 204",
            location = "Room 12",
            status = ClassStatus.LATER
        )
    )

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.HOME,
        screenTitle = "Good morning, $studentName",
        screenSubtitle = "Tuesday, 18 August",
        studentName = studentName,
        onNotificationClick = onNotificationClick,
        onScanClick = onScanClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 100.dp)
        ) {
            VerificationBanner()

            Spacer(modifier = Modifier.height(18.dp))

            AttendanceOverview(
                attendancePercentage = attendancePercentage,
                attendedSessions = if (attendancePercentage > 0) (45 * (attendancePercentage / 100f)).toInt() else 39,
                totalSessions = 45
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Today's classes",
                actionText = "View all",
                onActionClick = onViewAllClasses
            )

            Spacer(modifier = Modifier.height(12.dp))

            classesToShow.forEach { classItem ->
                TodayClassCard(
                    classItem = classItem,
                    onClick = { onClassClick(classItem.code) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Fees",
                    subtitle = "Financials",
                    icon = "KES",
                    backgroundColor = KikaoColors.TealLight,
                    accentColor = KikaoColors.Teal,
                    onClick = onFinanceClick,
                    modifier = Modifier.weight(1f)
                )

                QuickActionCard(
                    title = "Exams",
                    subtitle = "Slip & Seat",
                    icon = "EX",
                    backgroundColor = Color(0xFFFFF2CC),
                    accentColor = Color(0xFF9A6700),
                    onClick = onExamSlipClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ScanReminderCard(onClick = onScanClick)

            Spacer(modifier = Modifier.height(18.dp))

            InsightCard()
        }
    }
}

@Composable
private fun VerificationBanner() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = KikaoColors.Teal,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                Text(
                    text = "University verified",
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your student account is active.",
                    color = KikaoColors.MutedText,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AttendanceOverview(
    attendancePercentage: Int,
    attendedSessions: Int,
    totalSessions: Int
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Teal
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AttendanceRing(
                percentage = attendancePercentage,
                modifier = Modifier.size(118.dp)
            )

            Spacer(modifier = Modifier.size(18.dp))

            Column {
                Text(
                    text = "Semester attendance",
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "You are on track",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$attendedSessions of $totalSessions sessions attended",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(KikaoColors.Gold)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Healthy attendance",
                        color = KikaoColors.DeepIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceRing(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(118.dp)) {
            drawCircle(
                color = Color.White.copy(alpha = 0.22f),
                style = Stroke(width = 11.dp.toPx())
            )

            drawArc(
                color = KikaoColors.Gold,
                startAngle = -90f,
                sweepAngle = (percentage / 100f) * 360f,
                useCenter = false,
                style = Stroke(
                    width = 11.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "present",
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = KikaoColors.Ink,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            color = KikaoColors.Teal,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
private fun TodayClassCard(
    classItem: TodayClass,
    onClick: () -> Unit
) {
    val statusColor = when (classItem.status) {
        ClassStatus.PRESENT -> KikaoColors.Teal
        ClassStatus.NEXT -> KikaoColors.Gold
        ClassStatus.LATER -> KikaoColors.MutedText
    }

    val statusBackground = when (classItem.status) {
        ClassStatus.PRESENT -> KikaoColors.TealLight
        ClassStatus.NEXT -> Color(0xFFFFF2CC)
        ClassStatus.LATER -> Color(0xFFF1F5F9)
    }

    val statusText = when (classItem.status) {
        ClassStatus.PRESENT -> "PRESENT"
        ClassStatus.NEXT -> "UP NEXT"
        ClassStatus.LATER -> "LATER"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = classItem.time,
                    color = KikaoColors.Indigo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(7.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusColor)
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = classItem.title,
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${classItem.code} · ${classItem.location}",
                    color = KikaoColors.MutedText,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusBackground)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ScanReminderCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(KikaoColors.Gold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▣",
                    color = KikaoColors.DeepIndigo,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(13.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ready to check in?",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Database Systems starts in 2h 14m.",
                    color = Color.White.copy(alpha = 0.76f),
                    fontSize = 12.sp
                )
            }

            Text(
                text = "›",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun InsightCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(
                text = "Kikao insight",
                color = KikaoColors.Teal,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your attendance improved by 8% this month.",
                color = KikaoColors.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Keep attending your afternoon sessions to maintain your progress.",
                color = KikaoColors.MutedText,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: String,
    backgroundColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(text = icon, color = accentColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 10.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StudentDashboardScreenPreview() {
    MaterialTheme {
        StudentDashboardScreen()
    }
}