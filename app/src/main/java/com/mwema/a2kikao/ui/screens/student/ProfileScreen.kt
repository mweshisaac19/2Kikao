package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.ProfileViewModel
import com.mwema.a2kikao.ui.viewmodels.ResetUiState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onManageDevices: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onDigitalIDClick: () -> Unit = {},
    onFinanceClick: () -> Unit = {},
    onFacilityClick: () -> Unit = {},
    onRegistrationClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var biometricEnabled by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val resetState by viewModel.resetState.collectAsState()

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { 
                showResetDialog = false 
                viewModel.clearResetState()
            },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("We will send a password reset link to your registered email: ${userProfile?.email ?: ""}", fontSize = 14.sp)
                    if (resetState is ResetUiState.Success) {
                        Text("Reset email sent! Check your inbox.", color = KikaoColors.Teal, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    val state = resetState
                    if (state is ResetUiState.Error) {
                        Text(state.message, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.sendPasswordReset(userProfile?.email ?: "") },
                    enabled = resetState !is ResetUiState.Loading && resetState !is ResetUiState.Success,
                    colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
                ) {
                    if (resetState is ResetUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Send Link")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    KikaoStudentScaffold(
        modifier = modifier,
        selectedTab = StudentTab.PROFILE,
        screenTitle = "My profile",
        screenSubtitle = "Account, privacy and preferences",
        showScanButton = false,
        onBackClick = onBackClick,
        onNotificationClick = onNotificationClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 32.dp)
        ) {
            ProfileHero(
                name = userProfile?.fullName ?: "Amani Mwangi",
                programme = "${userProfile?.course ?: "BSc Computer Science"} · Year ${userProfile?.yearOfStudy ?: "2"}",
                onEditProfile = onEditProfile
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("University identity")

            Spacer(modifier = Modifier.height(10.dp))

            ActionCard(
                icon = "◎",
                title = "Digital student ID",
                subtitle = "Your verified institutional identity",
                onClick = onDigitalIDClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            UniversityIdentityCard(
                regNo = userProfile?.registrationNumber ?: "SC211/1234/2025",
                email = userProfile?.email ?: "amani.mwangi@university.ac.ke",
                department = userProfile?.department ?: "Computer Science",
                campus = userProfile?.campus ?: "Main Campus"
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Academic services")

            Spacer(modifier = Modifier.height(10.dp))

            ActionCard(
                icon = "KES",
                title = "Finance and fees",
                subtitle = "Manage your student account and balance",
                onClick = onFinanceClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActionCard(
                icon = "⌖",
                title = "Campus facilities",
                subtitle = "Find lecture halls, labs and maps",
                onClick = onFacilityClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActionCard(
                icon = "▦",
                title = "Semester registration",
                subtitle = "Register units and verify enrollment",
                onClick = onRegistrationClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Preferences")

            Spacer(modifier = Modifier.height(10.dp))

            PreferenceCard(
                title = "Notifications",
                subtitle = "Class updates, grades and attendance reminders",
                icon = "●",
                isEnabled = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            PreferenceCard(
                title = "Biometric unlock",
                subtitle = "Use your fingerprint or face to open Kikao",
                icon = "◉",
                isEnabled = biometricEnabled,
                onCheckedChange = { biometricEnabled = it }
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Account and security")

            Spacer(modifier = Modifier.height(10.dp))

            ActionCard(
                icon = "⌁",
                title = "Change password",
                subtitle = "Keep your Kikao account secure",
                onClick = { showResetDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ActionCard(
                icon = "?",
                title = "Help and support",
                subtitle = "Get help with your account or attendance",
                onClick = onSupportClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            SignOutCard(onClick = {
                com.mwema.a2kikao.data.FirebaseManager.signOut()
                onSignOut()
            })

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Kikao v1.0.0 · Verified learning",
                color = KikaoColors.MutedText,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileHero(
    name: String,
    programme: String,
    onEditProfile: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString("").take(2).uppercase(),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = programme,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = "✓ UNIVERSITY VERIFIED",
                        color = KikaoColors.DeepIndigo,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KikaoColors.Gold)
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable(onClick = onEditProfile)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Keep your academic details current",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp
                )

                Text(
                    text = "Edit profile  ›",
                    color = KikaoColors.Gold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UniversityIdentityCard(
    regNo: String,
    email: String,
    department: String,
    campus: String
) {
    Card(
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            ProfileDetail("Registration number", regNo)
            ProfileDetail("University email", email)
            ProfileDetail("Department", department)
            ProfileDetail("Campus", campus, showDivider = false)
        }
    }
}

@Composable
private fun ProfileDetail(
    label: String,
    value: String,
    showDivider: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = KikaoColors.MutedText,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = KikaoColors.Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        if (showDivider) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEDF1F6))
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PreferenceCard(
    title: String,
    subtitle: String,
    icon: String,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = KikaoColors.Teal,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = KikaoColors.Teal,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }
    }
}

@Composable
private fun ActionCard(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0xFFEAF0F8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = KikaoColors.Indigo,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )
            }

            Text(
                text = "›",
                color = KikaoColors.MutedText,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
private fun SignOutCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEAEC)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "↪",
                color = Color(0xFFB42318),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Sign out of Kikao",
                color = Color(0xFFB42318),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionLabel(
    text: String
) {
    Text(
        text = text.uppercase(),
        color = KikaoColors.MutedText,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.8.sp
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}