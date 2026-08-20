package com.mwema.a2kikao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.screens.admin.AdminTab
import com.mwema.a2kikao.ui.screens.admin.KikaoAdminScaffold
import com.mwema.a2kikao.ui.theme.KikaoColors

enum class AdminInviteRole {
    SUPER_ADMIN,
    ADMIN,
    ACADEMIC_ADMIN,
    FINANCE_ADMIN
}

enum class InvitationStatus {
    PENDING,
    VERIFIED,
    EXPIRED,
    REVOKED
}

data class AdminInvitation(
    val name: String,
    val email: String,
    val role: AdminInviteRole,
    val invitedOn: String,
    val status: InvitationStatus
)

@Composable
fun AdminOnboardingScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onInviteSent: (String, String, AdminInviteRole) -> Unit = { _, _, _ -> },
    onVerifyAdmin: (AdminInvitation) -> Unit = {},
    onResendInvitation: (AdminInvitation) -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(AdminInviteRole.ADMIN) }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Admin Onboarding",
        screenSubtitle = "Invite institutional managers",
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
            
            TextButton(onClick = onBackClick) {
                Text("‹ Back to profile", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }

            OnboardingSummaryCard()
            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("New invitation")
            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Institutional email") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = { if(fullName.isNotBlank() && email.isNotBlank()) onInviteSent(fullName, email, selectedRole) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                Text("Send Secure Invitation", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OnboardingSummaryCard() {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "ADMIN TEAM", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Manage your institution by inviting trusted administrative staff.", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminOnboardingPreview() {
    MaterialTheme {
        AdminOnboardingScreen()
    }
}
