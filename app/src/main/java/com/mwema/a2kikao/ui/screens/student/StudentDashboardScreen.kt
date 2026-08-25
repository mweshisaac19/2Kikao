package com.mwema.a2kikao.ui.screens.student


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.StudentDashboardClass
import com.mwema.a2kikao.ui.viewmodels.StudentDashboardViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

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
    LATER,
    MISSED
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
    val dashboardClasses by viewModel.dashboardClasses.collectAsState()
    
    val studentName = userProfile?.fullName?.substringBefore(" ") ?: "Student"
    val currentDate = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }
    
    val mappedClasses = dashboardClasses.mapIndexed { index, dashClass ->
        TodayClass(
            time = dashClass.course.time.substringBefore("-").trim(),
            title = dashClass.course.name,
            code = dashClass.course.code,
            location = dashClass.course.room,
            status = when {
                dashClass.isAttended -> ClassStatus.PRESENT
                dashClass.isMissed -> ClassStatus.MISSED
                else -> ClassStatus.LATER
            }
        )
    }

    val classesToShow = mappedClasses

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.HOME,
        screenTitle = "Good morning, $studentName",
        screenSubtitle = currentDate,
        studentName = studentName,
        onNotificationClick = onNotificationClick,
        onScanClick = onScanClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 100.dp)
        ) {
            VerificationBanner()

            Spacer(modifier = Modifier.height(18.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically()
            ) {
                AttendanceOverview(
                    attendancePercentage = attendancePercentage,
                    attendedSessions = if (attendancePercentage > 0) (45 * (attendancePercentage / 100f)).toInt() else 39,
                    totalSessions = 45
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Today's classes",
                actionText = "View all",
                onActionClick = onViewAllClasses
            )

            Spacer(modifier = Modifier.height(12.dp))

            dashboardClasses.forEachIndexed { index, dashClass ->
                val classItem = TodayClass(
                    time = dashClass.course.time.substringBefore("-").trim(),
                    title = dashClass.course.name,
                    code = dashClass.course.code,
                    location = dashClass.course.room,
                    status = when {
                        dashClass.isAttended -> ClassStatus.PRESENT
                        dashClass.isMissed -> ClassStatus.MISSED
                        else -> ClassStatus.LATER
                    }
                )
                
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(index * 100L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally { it } + fadeIn()
                ) {
                    TodayClassCard(
                        classItem = classItem,
                        onClick = { onClassClick(classItem.code) }
                    )
                }
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
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                    .background(KikaoColors.Teal.copy(alpha = 0.2f)),
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
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your student account is active.",
                    color = Color.White.copy(alpha = 0.7f),
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
                    color = Color.White,
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
                    color = Color.White.copy(alpha = 0.95f),
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
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "Attendance sweep"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(118.dp)) {
            // Background track
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                style = Stroke(width = 11.dp.toPx())
            )

            // Dynamic progress with gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(KikaoColors.Gold, KikaoColors.TealLight, KikaoColors.Gold)
                ),
                startAngle = -90f,
                sweepAngle = animatedPercentage * 360f,
                useCenter = false,
                style = Stroke(
                    width = 11.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
            
            // Outer glow effect
            drawCircle(
                color = KikaoColors.Gold.copy(alpha = 0.08f),
                radius = size.width / 2 + 8.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedPercentage * 100).toInt()}%",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "present",
                color = Color.White.copy(alpha = 0.85f),
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
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            color = KikaoColors.TealLight,
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
        ClassStatus.LATER -> Color.White.copy(alpha = 0.5f)
        ClassStatus.MISSED -> Color(0xFFDC3545)
    }

    val statusBackground = when (classItem.status) {
        ClassStatus.PRESENT -> KikaoColors.Teal.copy(alpha = 0.15f)
        ClassStatus.NEXT -> KikaoColors.Gold.copy(alpha = 0.15f)
        ClassStatus.LATER -> Color.White.copy(alpha = 0.08f)
        ClassStatus.MISSED -> Color(0xFFDC3545).copy(alpha = 0.15f)
    }

    val statusText = when (classItem.status) {
        ClassStatus.PRESENT -> "PRESENT"
        ClassStatus.NEXT -> "UP NEXT"
        ClassStatus.LATER -> "LATER"
        ClassStatus.MISSED -> "MISSED"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.10f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
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
                    color = Color.White,
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
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${classItem.code} · ${classItem.location}",
                    color = Color.White.copy(alpha = 0.65f),
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
                    color = Color.White.copy(alpha = 0.90f),
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
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(
                text = "Kikao insight",
                color = KikaoColors.TealLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your attendance improved by 8% this month.",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Keep attending your afternoon sessions to maintain your progress.",
                color = Color.White.copy(alpha = 0.75f),
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