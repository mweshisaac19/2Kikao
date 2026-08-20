package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.ExamSlipViewModel

@Composable
fun ExamSlipScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ExamSlipViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val examClasses by viewModel.examClasses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    KikaoStudentScaffold(
        selectedTab = StudentTab.HOME,
        screenTitle = "Examination Slip",
        screenSubtitle = "Semester 1 · Final Exams",
        onBackClick = onBackClick,
        onTabSelected = onTabSelected,
        showScanButton = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {
            ExamSummaryHeader(examClasses.size)
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Exam Timetable")
            
            if (examClasses.isEmpty()) {
                repeat(3) { index ->
                    ExamItem(
                        code = if (index == 0) "CSC 221" else if (index == 1) "MAT 204" else "BIT 301",
                        name = if (index == 0) "Databases" else if (index == 1) "Discrete Math" else "Project Management",
                        time = "18 Aug · 09:00 AM",
                        seat = "Seat A-${10 + index}"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                examClasses.forEachIndexed { index, course ->
                    ExamItem(
                        code = course.code,
                        name = course.name,
                        time = "${course.day.take(3)} · ${course.time.substringBefore("-")}",
                        seat = "Seat B-${20 + index}"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ExamSummaryHeader(unitCount: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("EXAMINATION CENTRE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("Main Campus Hall A", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric("Total Units", if (unitCount > 0) unitCount.toString() else "6")
                Metric("Seat Range", "A1-B200")
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        Text(value, color = KikaoColors.Gold, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun ExamItem(code: String, name: String, time: String, seat: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(KikaoColors.TealLight), contentAlignment = Alignment.Center) {
                Text("EX", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("$code: $name", fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(time, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Text(seat, fontWeight = FontWeight.Bold, color = KikaoColors.Indigo, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExamSlipSchedulePreview() {
    MaterialTheme {
        ExamSlipScheduleScreen()
    }
}
