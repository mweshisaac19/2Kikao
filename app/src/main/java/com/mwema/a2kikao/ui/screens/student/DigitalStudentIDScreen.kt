package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.ProfileViewModel

@Composable
fun DigitalStudentIDScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val name = userProfile?.fullName ?: "Student"
    val regNo = userProfile?.registrationNumber ?: "SC211/1234/2025"

    KikaoStudentScaffold(
        selectedTab = StudentTab.PROFILE,
        screenTitle = "Digital ID",
        screenSubtitle = "Your institutional identity",
        onBackClick = onBackClick,
        showScanButton = false,
        showBottomBar = false
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IDCard(name, regNo)
            Spacer(modifier = Modifier.height(40.dp))
            QRSection()
        }
    }
}

@Composable
private fun IDCard(name: String, regNo: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(KikaoColors.Teal), contentAlignment = Alignment.Center) {
                    Text(
                        text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(regNo, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("UNIVERSITY OF KIKAO", color = KikaoColors.Gold, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun QRSection() {
    Card(
        modifier = Modifier.size(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("QR CODE", color = KikaoColors.MutedText, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text("Dynamic Verification Code", color = KikaoColors.MutedText, fontSize = 12.sp)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DigitalStudentIDPreview() {
    MaterialTheme {
        DigitalStudentIDScreen()
    }
}
