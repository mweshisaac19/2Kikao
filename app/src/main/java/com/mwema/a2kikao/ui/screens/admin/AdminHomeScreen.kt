package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.AdminHomeViewModel

@Composable
fun AdminHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {},
    onStudentsClick: () -> Unit = {},
    onLecturersClick: () -> Unit = {},
    onCoursesClick: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onPerformanceClick: () -> Unit = {},
    onAtRiskStudentsClick: () -> Unit = {},
    onReportsClick: () -> Unit = {},
    onBulkImportClick: () -> Unit = {},
    onFinanceClick: () -> Unit = {},
    onInfrastructureClick: () -> Unit = {}
) {
    val studentCount by viewModel.studentCount.collectAsState()
    val lecturerCount by viewModel.lecturerCount.collectAsState()
    val courseCount by viewModel.courseCount.collectAsState()

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.HOME,
        screenTitle = "Command center",
        screenSubtitle = "Institution overview · Wednesday, 19 August",
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
                .padding(bottom = 110.dp)
        ) {

            Text(
                text = "Good morning, Administrator 👋",
                color = KikaoColors.Ink,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Here's what is happening across your institution.",
                color = KikaoColors.MutedText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            InstitutionStatusCard()

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(
                title = "Institution snapshot",
                subtitle = "Live academic activity"
            )

            Spacer(modifier = Modifier.height(11.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("Students", "$studentCount", "+128 term", "♙", KikaoColors.Indigo, Modifier.weight(1f), onStudentsClick)
                AdminMetricCard("Lecturers", "$lecturerCount", "174 active", "◉", KikaoColors.Teal, Modifier.weight(1f), onLecturersClick)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("Courses", "$courseCount", "8 schools", "▦", KikaoColors.Gold, Modifier.weight(1f), onCoursesClick)
                AdminMetricCard("Attendance", "86.4%", "+2.8% month", "✓", Color(0xFF8B5CF6), Modifier.weight(1f), onAttendanceClick)
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Quick actions", "Institutional management")

            Spacer(modifier = Modifier.height(11.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAction("♙", "Students", "Manage", KikaoColors.Indigo, Modifier.weight(1f), onStudentsClick)
                QuickAction("▦", "Courses", "Manage", KikaoColors.Gold, Modifier.weight(1f), onCoursesClick)
                QuickAction("◔", "Analytics", "Explore", KikaoColors.Teal, Modifier.weight(1f), onAnalyticsClick)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAction("▤", "Reports", "View", Color(0xFF8B5CF6), Modifier.weight(1f), onReportsClick)
                QuickAction("↥", "Import", "Bulk", KikaoColors.Teal, Modifier.weight(1f), onBulkImportClick)
                QuickAction("KES", "Finance", "Fees", Color(0xFF16855B), Modifier.weight(1f), onFinanceClick)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickAction("⌂", "Campus", "Facilities", Color(0xFFD97706), Modifier.weight(1f), onInfrastructureClick)
                QuickAction("!", "At-risk", "Support", Color(0xFFB45309), Modifier.weight(1f), onAtRiskStudentsClick)
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader("Live academic activity", "Active sessions")

            Spacer(modifier = Modifier.height(11.dp))

            LiveSessionCard("CSC 221", "Database Systems", "Dr. Brian Otieno", "Lab 3", "87 students", 91)

            Spacer(modifier = Modifier.height(10.dp))

            LiveSessionCard("BIT 214", "Systems Analysis", "Ms. Jane Wambui", "Room B12", "64 students", 84)
        }
    }
}

@Composable
private fun InstitutionStatusCard() {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(11.dp).clip(CircleShape).background(KikaoColors.Teal))
                Spacer(modifier = Modifier.width(9.dp))
                Text(text = "INSTITUTION LIVE", color = KikaoColors.Gold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(11.dp))
            Text(text = "Systems are operating normally.", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(text = "42 classes scheduled today across campus.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(text = title, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
    }
}

@Composable
private fun AdminMetricCard(title: String, value: String, detail: String, icon: String, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(11.dp)).background(accent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Text(text = icon, color = accent, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, color = KikaoColors.MutedText, fontSize = 10.sp)
            Text(text = value, color = KikaoColors.Ink, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = detail, color = accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AttendancePulseCard(onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick), shape = RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "VERIFIED ATTENDANCE", color = KikaoColors.Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = "86.4%", color = KikaoColors.Ink, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "institution average", color = KikaoColors.MutedText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun PerformanceOverviewCard(onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick), shape = RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "ACADEMIC PERFORMANCE", color = KikaoColors.Indigo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = "68.7%", color = KikaoColors.Ink, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "assessment average", color = KikaoColors.MutedText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun QuickAction(icon: String, title: String, subtitle: String, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(modifier = Modifier.size(37.dp).clip(RoundedCornerShape(11.dp)).background(accent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Text(text = icon, color = accent, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(9.dp))
            Text(text = title, color = KikaoColors.Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 9.sp)
        }
    }
}

@Composable
private fun LiveSessionCard(code: String, name: String, lec: String, room: String, students: String, att: Int) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "LIVE NOW", color = KikaoColors.Teal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(text = room, color = KikaoColors.MutedText, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(9.dp))
            Text(text = code, color = KikaoColors.Indigo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = name, color = KikaoColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = lec, color = KikaoColors.MutedText, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(13.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = students, color = KikaoColors.MutedText, fontSize = 10.sp)
                Text(text = "$att% attendance", color = KikaoColors.Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminHomeScreenPreview() {
    MaterialTheme {
        AdminHomeScreen()
    }
}
