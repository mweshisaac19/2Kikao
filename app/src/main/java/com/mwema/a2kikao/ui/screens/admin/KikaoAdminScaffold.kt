package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

// ------------------------------------------------------------
// ADMIN NAVIGATION
// ------------------------------------------------------------

enum class AdminTab {
    HOME,
    ACADEMICS,
    USERS,
    ANALYTICS,
    PROFILE
}

// ------------------------------------------------------------
// ADMIN SCAFFOLD
// ------------------------------------------------------------

@Composable
fun KikaoAdminScaffold(
    modifier: Modifier = Modifier,
    selectedTab: AdminTab = AdminTab.HOME,
    screenTitle: String = "Admin command center",
    screenSubtitle: String = "Institution overview",
    adminName: String = "Isaac",
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {},
    content: @Composable (innerPadding: androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            KikaoColors.DeepIndigo,
                            KikaoColors.Indigo,
                            Color(0xFF31539A)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawKikaoBackground()
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                // ------------------------------------------------
                // HEADER
                // ------------------------------------------------

                AdminHeader(
                    screenTitle = screenTitle,
                    screenSubtitle = screenSubtitle,
                    adminName = adminName,
                    onNotificationClick = onNotificationClick,
                    onProfileClick = onProfileClick
                )

                // ------------------------------------------------
                // CONTENT
                // ------------------------------------------------

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    content(
                        androidx.compose.foundation.layout.PaddingValues(
                            bottom = 12.dp
                        )
                    )
                }

                // ------------------------------------------------
                // BOTTOM NAVIGATION
                // ------------------------------------------------

                AdminBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected
                )
            }
        }
    }
}

// ------------------------------------------------------------
// HEADER
// ------------------------------------------------------------

@Composable
private fun AdminHeader(
    screenTitle: String,
    screenSubtitle: String,
    adminName: String,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 20.dp
                )
        ) {

            // ----------------------------------------------
            // TOP ROW
            // ----------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "KIKAO ADMIN",
                        color = KikaoColors.Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    Text(
                        text = screenTitle,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.size(3.dp))

                    Text(
                        text = screenSubtitle,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                // ------------------------------------------
                // NOTIFICATION BUTTON
                // ------------------------------------------

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Color.White.copy(alpha = 0.14f)
                        )
                        .clickable(
                            onClick = onNotificationClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "●",
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    // Notification badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(7.dp)
                            .size(8.dp)
                            .background(KikaoColors.Gold, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                // ------------------------------------------
                // PROFILE AVATAR
                // ------------------------------------------

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(KikaoColors.Teal)
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = adminName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.size(15.dp))

            // ----------------------------------------------
            // ADMIN STATUS STRIP
            // ----------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Color.White.copy(alpha = 0.09f)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 9.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(KikaoColors.Teal)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Institution systems operational",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "LIVE",
                    color = KikaoColors.Teal,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.7.sp
                )
            }
        }
    }
}

// ------------------------------------------------------------
// BOTTOM NAVIGATION
// ------------------------------------------------------------

@Composable
private fun AdminBottomNavigation(
    selectedTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color.White.copy(alpha = 0.08f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            AdminNavItem(
                tab = AdminTab.HOME,
                selected = selectedTab == AdminTab.HOME,
                icon = "⌂",
                label = "Home",
                onClick = {
                    onTabSelected(AdminTab.HOME)
                }
            )

            AdminNavItem(
                tab = AdminTab.ACADEMICS,
                selected = selectedTab == AdminTab.ACADEMICS,
                icon = "▦",
                label = "Academics",
                onClick = {
                    onTabSelected(AdminTab.ACADEMICS)
                }
            )

            AdminNavItem(
                tab = AdminTab.USERS,
                selected = selectedTab == AdminTab.USERS,
                icon = "♙",
                label = "Users",
                onClick = {
                    onTabSelected(AdminTab.USERS)
                }
            )

            AdminNavItem(
                tab = AdminTab.ANALYTICS,
                selected = selectedTab == AdminTab.ANALYTICS,
                icon = "◔",
                label = "Analytics",
                onClick = {
                    onTabSelected(AdminTab.ANALYTICS)
                }
            )

            AdminNavItem(
                tab = AdminTab.PROFILE,
                selected = selectedTab == AdminTab.PROFILE,
                icon = "●",
                label = "Me",
                onClick = {
                    onTabSelected(AdminTab.PROFILE)
                }
            )
        }
    }
}

// ------------------------------------------------------------
// NAV ITEM
// ------------------------------------------------------------

@Composable
private fun AdminNavItem(
    tab: AdminTab,
    selected: Boolean,
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(
                    if (selected) {
                        KikaoColors.Teal
                    } else {
                        Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = if (selected) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.5f)
                },
                fontSize = 17.sp,
                fontWeight = if (selected) {
                    FontWeight.ExtraBold
                } else {
                    FontWeight.Medium
                }
            )
        }

        Spacer(modifier = Modifier.size(3.dp))

        Text(
            text = label,
            color = if (selected) {
                KikaoColors.Teal
            } else {
                Color.White.copy(alpha = 0.5f)
            },
            fontSize = 10.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )

        if (selected) {
            Spacer(modifier = Modifier.size(3.dp))

            Box(
                modifier = Modifier
                    .size(
                        width = 16.dp,
                        height = 3.dp
                    )
                    .clip(RoundedCornerShape(3.dp))
                    .background(KikaoColors.Gold)
            )
        }
    }
}

private fun DrawScope.drawKikaoBackground() {
    drawCircle(
        color = KikaoColors.Teal.copy(alpha = 0.22f),
        radius = size.width * 0.55f,
        center = Offset(size.width * 1.05f, size.height * 0.12f)
    )

    drawCircle(
        color = KikaoColors.Gold.copy(alpha = 0.14f),
        radius = size.width * 0.48f,
        center = Offset(size.width * -0.12f, size.height * 0.90f)
    )
}

// ------------------------------------------------------------
// PREVIEW
// ------------------------------------------------------------

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun KikaoAdminScaffoldPreview() {
    MaterialTheme {
        KikaoAdminScaffold(
            selectedTab = AdminTab.HOME,
            screenTitle = "Admin command center",
            screenSubtitle = "Institution overview"
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Dashboard content goes here",
                    color = KikaoColors.Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}