package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.FirebaseManager
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.AssessmentFeedbackViewModel

@Composable
fun AssessmentFeedbackDetail(
    assessmentId: String,
    modifier: Modifier = Modifier,
    viewModel: AssessmentFeedbackViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {}
) {
    val score by viewModel.score.collectAsState()
    val totalScore by viewModel.totalScore.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(assessmentId) {
        val uid = FirebaseManager.currentUserUId
        if (uid != null) {
            viewModel.fetchFeedback(assessmentId, uid)
        }
    }

    val displayScore = if (score > 0) "${score.toInt()} / ${totalScore.toInt()}" else "18 / 20"
    val displayFeedback = if (feedback.isNotEmpty()) feedback else "Great work on the normalization questions. Your ER diagram was clear and followed all best practices. Minor point: watch the foreign key constraints in your SQL script."

    KikaoStudentScaffold(
        selectedTab = StudentTab.INSIGHTS,
        screenTitle = "Feedback",
        screenSubtitle = "Assessment Results",
        onBackClick = onBackClick,
        showScanButton = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = KikaoColors.Teal)
                }
            } else {
                ScoreHeader(displayScore)
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle("Lecturer's Feedback")
                FeedbackCard(displayFeedback)
            }
        }
    }
}

@Composable
private fun ScoreHeader(score: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("YOUR SCORE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(score, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = KikaoColors.Teal, shape = RoundedCornerShape(8.dp)) {
                Text("VERIFIED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
            }
        }
    }
}

@Composable
private fun FeedbackCard(feedback: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(feedback, color = KikaoColors.Ink, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AssessmentFeedbackDetailPreview() {
    MaterialTheme {
        AssessmentFeedbackDetail(assessmentId = "demo")
    }
}
