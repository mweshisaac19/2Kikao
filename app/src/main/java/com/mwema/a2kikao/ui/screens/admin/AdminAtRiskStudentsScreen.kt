package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class AtRiskStudent(
    val id: String,
    val name: String,
    val registration: String,
    val department: String,
    val year: String,
    val attendance: Int,
    val average: Int,
    val missedSessions: Int,
    val riskLevel: RiskLevel,
    val riskReason: String,
    val lastActive: String
)

private enum class RiskLevel {
    CRITICAL,
    HIGH,
    MODERATE
}

private enum class RiskFilter {
    ALL,
    CRITICAL,
    HIGH,
    MODERATE
}

@Composable
fun AdminAtRiskStudentsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onStudentClick: (String) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onInterventionClick: (String) -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    val students = remember { demoAtRiskStudents() }
    var selectedFilter by remember { mutableStateOf(RiskFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredStudents = students.filter { student ->
        val matchesFilter = when (selectedFilter) {
            RiskFilter.ALL -> true
            RiskFilter.CRITICAL -> student.riskLevel == RiskLevel.CRITICAL
            RiskFilter.HIGH -> student.riskLevel == RiskLevel.HIGH
            RiskFilter.MODERATE -> student.riskLevel == RiskLevel.MODERATE
        }
        val matchesSearch = searchQuery.isBlank() || student.name.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ANALYTICS,
        screenTitle = "At-risk students",
        screenSubtitle = "Early warning analysis",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 110.dp)
        ) {
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color.White)) {
                    Text("‹", color = KikaoColors.Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Risk Monitor", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            RiskOverviewCard(students.size, 12, 28, 45) // Demo counts
            
            Spacer(modifier = Modifier.height(24.dp))
            filteredStudents.forEach { student ->
                AtRiskStudentCard(student, { onStudentClick(student.id) }, { onInterventionClick(student.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.clickable(onClick = onClick), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun RiskOverviewCard(total: Int, critical: Int, high: Int, moderate: Int) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "$total STUDENTS FLAGGED", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                RiskMetric("Critical", critical, Color(0xFFFF6B6B))
                RiskMetric("High", high, KikaoColors.Gold)
                RiskMetric("Moderate", moderate, Color(0xFF8B5CF6))
            }
        }
    }
}

@Composable
private fun RiskMetric(label: String, value: Int, color: Color) {
    Column {
        Text(text = "$value", color = color, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
private fun AtRiskStudentCard(student: AtRiskStudent, onClick: () -> Unit, onIntervention: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(KikaoColors.Indigo.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Text(text = student.name.take(1), color = KikaoColors.Indigo, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = student.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = student.registration, color = KikaoColors.MutedText, fontSize = 11.sp)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(student.riskLevel == RiskLevel.CRITICAL) Color(0xFFFFEAEC) else Color(0xFFFFF5D6)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = student.riskLevel.name, color = if(student.riskLevel == RiskLevel.CRITICAL) Color(0xFFB42318) else Color(0xFF9A6700), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            androidx.compose.material3.TextButton(
                onClick = onIntervention,
                modifier = Modifier.align(Alignment.End),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(text = "Intervene ›", color = KikaoColors.Teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun demoAtRiskStudents() = listOf(
    AtRiskStudent("s1", "Brian Otieno", "SC211/1042/2024", "Comp Science", "Year 3", 48, 42, 9, RiskLevel.CRITICAL, "Low attendance", "3d ago"),
    AtRiskStudent("s2", "Mercy Wanjiku", "SC212/0876/2024", "IT", "Year 3", 57, 49, 7, RiskLevel.HIGH, "Poor performance", "1d ago")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminAtRiskStudentsPreview() {
    MaterialTheme {
        AdminAtRiskStudentsScreen()
    }
}
