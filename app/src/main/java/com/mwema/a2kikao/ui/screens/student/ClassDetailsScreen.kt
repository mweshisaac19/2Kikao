package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.mwema.a2kikao.ui.viewmodels.ClassDetailsViewModel
import com.mwema.a2kikao.ui.viewmodels.LearningMaterialsViewModel
import com.mwema.a2kikao.ui.viewmodels.LearningMaterial
import com.mwema.a2kikao.ui.viewmodels.ClassAnnouncement

private enum class ClassDetailsTab {
    LEARNING,
    ANNOUNCEMENTS,
    REQUESTS
}

@Composable
fun ClassDetailsScreen(
    classId: String,
    modifier: Modifier = Modifier,
    viewModel: ClassDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    contentViewModel: LearningMaterialsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onMaterialClick: (String) -> Unit = {},
    onLeaveRequest: () -> Unit = {},
    onAttendanceDispute: () -> Unit = {},
    onConsultationClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    var selectedContent by rememberSaveable {
        mutableStateOf(ClassDetailsTab.LEARNING)
    }

    val courseClass by viewModel.courseClass.collectAsState()
    val materials by contentViewModel.materials.collectAsState()
    val announcements by contentViewModel.announcements.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(classId) {
        viewModel.fetchClassDetails(classId)
        contentViewModel.fetchClassContent(classId)
    }

    val screenTitle = courseClass?.code ?: "CSC 221"
    val screenSubtitle = courseClass?.name ?: "Database Systems"

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.CLASSES,
        screenTitle = screenTitle,
        screenSubtitle = screenSubtitle,
        onBackClick = onBackClick,
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            ClassHeader(
                onLeaveRequest = onLeaveRequest,
                onAttendanceDispute = onAttendanceDispute,
                onConsultationClick = { onConsultationClick(courseClass?.lecturer ?: "1") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ClassPulseCard()

            Spacer(modifier = Modifier.height(24.dp))

            ClassNavigation(
                selectedTab = selectedContent,
                onSelected = { selectedContent = it }
            )

            Spacer(modifier = Modifier.height(26.dp))

            when (selectedContent) {
                ClassDetailsTab.LEARNING -> {
                    LearningContent(materials, onMaterialClick)
                }

                ClassDetailsTab.ANNOUNCEMENTS -> {
                    AnnouncementsContent(announcements)
                }

                ClassDetailsTab.REQUESTS -> {
                    RequestsContent()
                }
            }
        }
    }
}

@Composable
private fun ClassHeader(
    onLeaveRequest: () -> Unit,
    onAttendanceDispute: () -> Unit,
    onConsultationClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Class Session Details",
            color = KikaoColors.Ink,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Course Administration",
                color = KikaoColors.MutedText,
                fontSize = 14.sp
            )

            Text(
                text = "Book talk  ›",
                color = KikaoColors.Teal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onConsultationClick)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionChip(
                label = "Leave request",
                icon = "✉",
                color = KikaoColors.Indigo,
                onClick = onLeaveRequest,
                modifier = Modifier.weight(1f)
            )

            ActionChip(
                label = "Dispute attendance",
                icon = "!",
                color = Color(0xFFC2413A),
                onClick = onAttendanceDispute,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ClassPulseCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "CLASS PERFORMANCE PULSE",
                color = KikaoColors.Gold,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "87% Attendance",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "You've missed 2 sessions this month.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "↗", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun ClassNavigation(
    selectedTab: ClassDetailsTab,
    onSelected: (ClassDetailsTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ClassTab("Learning", selectedTab == ClassDetailsTab.LEARNING) { onSelected(ClassDetailsTab.LEARNING) }
        ClassTab("Updates", selectedTab == ClassDetailsTab.ANNOUNCEMENTS) { onSelected(ClassDetailsTab.ANNOUNCEMENTS) }
        ClassTab("Your requests", selectedTab == ClassDetailsTab.REQUESTS) { onSelected(ClassDetailsTab.REQUESTS) }
    }
}

@Composable
private fun ClassTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = if (isSelected) KikaoColors.Ink else KikaoColors.MutedText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 20.dp, height = 2.dp)
                    .background(KikaoColors.Teal)
            )
        }
    }
}

@Composable
private fun LearningContent(materials: List<LearningMaterial>, onMaterialClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SectionTitle(title = "Resources", subtitle = "Official course materials.")
        Spacer(modifier = Modifier.height(15.dp))
        
        if (materials.isEmpty()) {
            repeat(3) {
                MaterialCard("Lecture ${it + 1}: Foundations", "PDF · 1.8 MB", "Recently", onMaterialClick)
                Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            materials.forEach { material ->
                MaterialCard(material.title, "${material.type.uppercase()} · ${material.size}", material.date, onMaterialClick)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun MaterialCard(title: String, detail: String, date: String, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(title) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(KikaoColors.TealLight), contentAlignment = Alignment.Center) {
                Text("📄", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 14.sp)
                Text(detail, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Text(date, fontSize = 10.sp, color = KikaoColors.MutedText)
        }
    }
}

@Composable
private fun AnnouncementsContent(announcements: List<ClassAnnouncement>) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SectionTitle(title = "Announcements", subtitle = "Latest updates from your lecturer.")
        Spacer(modifier = Modifier.height(15.dp))
        
        if (announcements.isEmpty()) {
            AnnouncementCard("No announcements yet", "Your lecturer hasn't posted any updates specifically for this class.", "System", "Now")
        } else {
            announcements.forEach { ann ->
                AnnouncementCard(ann.title, ann.message, ann.author, ann.date)
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

@Composable
private fun AnnouncementCard(title: String, message: String, author: String, date: String) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(message, fontSize = 13.sp, color = KikaoColors.MutedText, lineHeight = 19.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(author, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = KikaoColors.Teal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("•", color = KikaoColors.MutedText)
                Spacer(modifier = Modifier.width(6.dp))
                Text(date, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
        }
    }
}

@Composable
private fun RequestsContent() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        SectionTitle(title = "Your History", subtitle = "Track your leave and attendance disputes.")
        Spacer(modifier = Modifier.height(40.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("No active requests for this class.", color = KikaoColors.MutedText, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(text = title, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 13.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClassDetailsScreenPreview() {
    MaterialTheme {
        ClassDetailsScreen(classId = "demo")
    }
}
