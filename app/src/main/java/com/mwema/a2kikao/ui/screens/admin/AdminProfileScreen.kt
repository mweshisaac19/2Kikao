package com.mwema.a2kikao.ui.screens.admin

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

@Composable
fun AdminProfileScreen(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onManageUsers: () -> Unit = {},
    onSecurity: () -> Unit = {},
    onSystemSettings: () -> Unit = {},
    onAuditLogs: () -> Unit = {},
    onSupport: () -> Unit = {},
    onSystemHealthClick: () -> Unit = {},
    onInfrastructureClick: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var notificationsEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    var securityAlertsEnabled by rememberSaveable {
        mutableStateOf(true)
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Administrator profile",
        screenSubtitle = "Account, security and institution",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 40.dp)
        ) {

            AdminProfileHero(
                onEditProfile = onEditProfile
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Administrator identity")
            Spacer(modifier = Modifier.height(10.dp))
            AdministratorIdentityCard()

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Institution overview")
            Spacer(modifier = Modifier.height(10.dp))
            InstitutionOverviewCard()

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Administration")
            Spacer(modifier = Modifier.height(10.dp))
            AdminActionCard("◉", "Manage administrators", "Review users with administrative privileges", onManageUsers)
            Spacer(modifier = Modifier.height(10.dp))
            AdminActionCard("⌁", "Security & access", "Manage authentication and permissions", onSecurity)
            Spacer(modifier = Modifier.height(10.dp))
            AdminActionCard("⚙", "System settings", "Configure institution-wide Kikao preferences", onSystemSettings)
            Spacer(modifier = Modifier.height(10.dp))
            AdminActionCard("⌂", "Campus facilities", "Manage rooms, labs and assets", onInfrastructureClick)

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Preferences")
            Spacer(modifier = Modifier.height(10.dp))
            AdminPreferenceCard("System notifications", "Receive reports and important updates", "●", notificationsEnabled) { notificationsEnabled = it }
            Spacer(modifier = Modifier.height(10.dp))
            AdminPreferenceCard("Security alerts", "Get notified about unusual activity", "!", securityAlertsEnabled) { securityAlertsEnabled = it }

            Spacer(modifier = Modifier.height(22.dp))

            SectionLabel("Support & Health")
            Spacer(modifier = Modifier.height(10.dp))
            AdminActionCard("?", "Help & support", "Get assistance with Kikao administration", onSupport)
            Spacer(modifier = Modifier.height(10.dp))
            AdminActionCard("▤", "System health", "Monitor technical infrastructure status", onSystemHealthClick)

            Spacer(modifier = Modifier.height(22.dp))
            AdminSignOutCard(
                onClick = {
                    com.mwema.a2kikao.data.FirebaseManager.signOut()
                    onSignOut()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Kikao Admin v1.2.0", color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AdminProfileHero(
    onEditProfile: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "IM",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Isaac Mwema",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "System Administrator",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(KikaoColors.Gold)
                            .padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Text(
                            text = "✓ ADMIN VERIFIED",
                            color = KikaoColors.DeepIndigo,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        Color.White.copy(alpha = 0.11f)
                    )
                    .clickable(onClick = onEditProfile)
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = "Administrator account",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Keep your details current",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Edit profile  ›",
                    color = KikaoColors.Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdministratorIdentityCard() {
    Card(
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            AdminProfileDetail(
                label = "Administrator ID",
                value = "ADM-2026-001"
            )

            AdminProfileDetail(
                label = "Institutional email",
                value = "admin@university.ac.ke"
            )

            AdminProfileDetail(
                label = "Role",
                value = "System Administrator"
            )

            AdminProfileDetail(
                label = "Access level",
                value = "Full institutional access"
            )

            AdminProfileDetail(
                label = "Account status",
                value = "Active · Verified",
                valueColor = KikaoColors.Teal,
                showDivider = false
            )
        }
    }
}

@Composable
private fun AdminProfileDetail(
    label: String,
    value: String,
    valueColor: Color = KikaoColors.Ink,
    showDivider: Boolean = true
) {
    Column {

        Text(
            text = label,
            color = KikaoColors.MutedText,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
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
private fun InstitutionOverviewCard() {
    Card(
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "University of Kikao",
                color = KikaoColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Main Campus · Nairobi",
                color = KikaoColors.MutedText,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {

                InstitutionMetric(
                    value = "4,286",
                    label = "Students",
                    modifier = Modifier.weight(1f)
                )

                InstitutionMetric(
                    value = "186",
                    label = "Lecturers",
                    modifier = Modifier.weight(1f)
                )

                InstitutionMetric(
                    value = "14",
                    label = "Departments",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KikaoColors.TealLight)
                    .padding(11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(KikaoColors.Teal)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Institution systems are operating normally.",
                    color = KikaoColors.Teal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InstitutionMetric(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(Color(0xFFF4F6FA))
            .padding(
                horizontal = 8.dp,
                vertical = 11.dp
            )
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = value,
                color = KikaoColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = KikaoColors.MutedText,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun AdminPreferenceCard(
    title: String,
    subtitle: String,
    icon: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(43.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(KikaoColors.TealLight),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = icon,
                    color = KikaoColors.Teal,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

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
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }

            Switch(
                checked = checked,
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
private fun AdminActionCard(
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(43.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0xFFEAF0F8)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = icon,
                    color = KikaoColors.Indigo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

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
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }

            Text(
                text = "›",
                color = KikaoColors.MutedText,
                fontSize = 27.sp
            )
        }
    }
}

@Composable
private fun AdminSignOutCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEAEC)
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFD9DD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↪",
                    color = Color(0xFFB42318),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Sign out",
                    color = Color(0xFFB42318),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "End this administrator session",
                    color = Color(0xFFB42318).copy(alpha = 0.65f),
                    fontSize = 10.sp
                )
            }

            Text(
                text = "›",
                color = Color(0xFFB42318),
                fontSize = 25.sp
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
        color = Color.White.copy(alpha = 0.90f),
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.8.sp
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminProfileScreenPreview() {
    MaterialTheme {
        AdminProfileScreen()
    }
}
