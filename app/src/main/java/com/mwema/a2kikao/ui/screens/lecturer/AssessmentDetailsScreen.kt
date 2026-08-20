package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlin.math.roundToInt

// -----------------------------------------------------------------------------
// DATA MODELS
// -----------------------------------------------------------------------------

private data class AssessmentSubmission(
    val id: String,
    val studentName: String,
    val registrationNumber: String,
    val initials: String,
    val submitted: Boolean,
    val score: Double?,
    val totalMarks: Double,
    val submittedAt: String,
    val status: SubmissionStatus
)

private enum class SubmissionStatus {
    MARKED,
    UNMARKED,
    LATE,
    MISSING
}

private enum class AssessmentView {
    OVERVIEW,
    SUBMISSIONS
}

// -----------------------------------------------------------------------------
// MAIN SCREEN
// -----------------------------------------------------------------------------

@Composable
fun AssessmentDetailsScreen(
    assessmentId: String,
    modifier: Modifier = Modifier,
    assessmentTitle: String = "CAT 1",
    assessmentType: String = "CAT",
    courseCode: String = "CSC 221",
    courseName: String = "Database Systems",
    totalMarks: Int = 100,
    assessmentDate: String = "15 Aug 2026",
    dueDate: String = "15 Aug 2026 · 5:00 PM",
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onEditAssessment: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var selectedView by remember { mutableStateOf(AssessmentView.OVERVIEW) }
    val submissions = remember { demoAssessmentSubmissions() }
    
    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.RESULTS,
        screenTitle = "Assessment details",
        screenSubtitle = "$courseCode · $assessmentTitle",
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            
            // Back Action
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            ) {
                Text("‹ Back to results", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            
            // Header Card
            AssessmentHeaderCard(
                title = assessmentTitle,
                type = assessmentType,
                courseCode = courseCode,
                courseName = courseName,
                marks = totalMarks,
                date = assessmentDate,
                due = dueDate,
                onEdit = onEditAssessment
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats
            AssessmentStatsCard(
                submissions = submissions.count { it.submitted },
                total = submissions.size,
                marked = submissions.count { it.status == SubmissionStatus.MARKED },
                average = 72,
                highest = 98
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // View Toggle
            AssessmentNavigation(
                selected = selectedView,
                onSelected = { selectedView = it }
            )
            
            Spacer(modifier = Modifier.height(18.dp))
            
            // Content
            when (selectedView) {
                AssessmentView.OVERVIEW -> {
                    AssessmentOverviewContent(submissions, totalMarks)
                }
                AssessmentView.SUBMISSIONS -> {
                    SubmissionsListContent(submissions, onStudentClick)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// COMPONENTS
// -----------------------------------------------------------------------------

@Composable
private fun AssessmentHeaderCard(
    title: String,
    type: String,
    courseCode: String,
    courseName: String,
    marks: Int,
    date: String,
    due: String,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = type.uppercase(), color = KikaoColors.Gold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$courseCode · $courseName", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssessmentMeta(icon = Icons.Default.Event, text = date)
                AssessmentMeta(icon = Icons.Default.Schedule, text = "Due $due")
                AssessmentMeta(icon = Icons.Default.Assessment, text = "$marks Marks")
            }
        }
    }
}

@Composable
private fun AssessmentMeta(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun AssessmentStatsCard(submissions: Int, total: Int, marked: Int, average: Int, highest: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssessmentStat(label = "Submissions", value = "$submissions/$total", accent = KikaoColors.Indigo)
            AssessmentStat(label = "Marked", value = "$marked", accent = KikaoColors.Teal)
            AssessmentStat(label = "Average", value = "$average%", accent = KikaoColors.Gold)
        }
    }
}

@Composable
private fun AssessmentStat(label: String, value: String, accent: Color) {
    Column {
        Text(text = label, color = KikaoColors.MutedText, fontSize = 10.sp)
        Text(text = value, color = accent, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AssessmentNavigation(selected: AssessmentView, onSelected: (AssessmentView) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFEAF0F8))
            .padding(4.dp)
    ) {
        AssessmentNavigationItem(
            label = "Overview",
            isSelected = selected == AssessmentView.OVERVIEW,
            onClick = { onSelected(AssessmentView.OVERVIEW) },
            modifier = Modifier.weight(1f)
        )
        AssessmentNavigationItem(
            label = "Submissions",
            isSelected = selected == AssessmentView.SUBMISSIONS,
            onClick = { onSelected(AssessmentView.SUBMISSIONS) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AssessmentNavigationItem(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) KikaoColors.Indigo else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else KikaoColors.MutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AssessmentOverviewContent(submissions: List<AssessmentSubmission>, totalMarks: Int) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        PerformanceDistributionCard(submissions, totalMarks)
        Spacer(modifier = Modifier.height(20.dp))
        AssessmentInsightsCard(submissions.size, 118, 2, 5)
    }
}

@Composable
private fun PerformanceDistributionCard(submissions: List<AssessmentSubmission>, totalMarks: Int) {
    val marked = submissions.filter { it.score != null }
    if (marked.isEmpty()) return
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Grade Distribution", color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            DistributionRow(label = "80-100%", value = "12 students", percent = 0.15f, color = KikaoColors.Teal)
            DistributionRow(label = "60-79%", value = "45 students", percent = 0.48f, color = KikaoColors.Indigo)
            DistributionRow(label = "40-59%", value = "32 students", percent = 0.28f, color = KikaoColors.Gold)
            DistributionRow(label = "Below 40%", value = "9 students", percent = 0.09f, color = Color(0xFFDC3545))
        }
    }
}

@Composable
private fun DistributionRow(label: String, value: String, percent: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, fontSize = 11.sp, color = KikaoColors.MutedText)
            Text(text = value, fontSize = 11.sp, color = KikaoColors.Ink, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFF1F5F9))) {
            Box(modifier = Modifier.fillMaxWidth(percent).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(color))
        }
    }
}

@Composable
private fun AssessmentInsightsCard(total: Int, attended: Int, flagged: Int, improved: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E8))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = KikaoColors.Gold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Kikao Insight", color = KikaoColors.Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Performance is strongly correlated with attendance. Students who attended >90% of sessions scored 12% higher on average.",
                color = KikaoColors.MutedText,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun SubmissionsListContent(submissions: List<AssessmentSubmission>, onStudentClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        submissions.forEach { submission ->
            SubmissionCard(submission, onClick = { onStudentClick(submission.id) })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SubmissionCard(submission: AssessmentSubmission, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = submission.initials, color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = submission.studentName, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = submission.registrationNumber, color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                if (submission.score != null) {
                    Text(text = "${submission.score}/${submission.totalMarks.toInt()}", color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Marked", color = KikaoColors.Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(text = "Pending", color = KikaoColors.Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Not marked", color = KikaoColors.MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// DEMO DATA
// -----------------------------------------------------------------------------

private fun demoAssessmentSubmissions() = listOf(
    AssessmentSubmission("s1", "Amani Mwangi", "SC211/1234/2025", "AM", true, 18.0, 20.0, "Today", SubmissionStatus.MARKED),
    AssessmentSubmission("s2", "John Doe", "SC211/5678/2025", "JD", true, null, 20.0, "Yesterday", SubmissionStatus.UNMARKED),
    AssessmentSubmission("s3", "Sarah Wanjiku", "SC211/9012/2025", "SW", true, 15.0, 20.0, "16 Aug", SubmissionStatus.MARKED)
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AssessmentDetailsPreview() {
    MaterialTheme {
        AssessmentDetailsScreen(assessmentId = "a1")
    }
}
