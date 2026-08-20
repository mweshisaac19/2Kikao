package com.mwema.a2kikao.ui.screens.student


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

enum class StudentTab(
    val label: String,
    val icon: String
) {
    HOME("Home", "⌂"),
    CLASSES("Classes", "▦"),
    INSIGHTS("Insights", "◔"),
    PROFILE("Profile", "◎")
}

@Composable
fun KikaoStudentScaffold(
    selectedTab: StudentTab,
    screenTitle: String,
    screenSubtitle: String,
    modifier: Modifier = Modifier,
    studentName: String = "Amani",
    showBottomBar: Boolean = true,
    showScanButton: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    onNotificationClick: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = KikaoColors.Background,
        topBar = {
            KikaoStudentHeader(
                title = screenTitle,
                subtitle = screenSubtitle,
                studentName = studentName,
                onBackClick = onBackClick,
                onNotificationClick = onNotificationClick
            )
        },
        bottomBar = {
            if (showBottomBar) {
                KikaoStudentBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected
                )
            }
        },
        floatingActionButton = {
            if (showScanButton) {
                KikaoScanButton(onClick = onScanClick)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KikaoColors.Background)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawKikaoBackground()
            }

            content(innerPadding)
        }
    }
}

@Composable
private fun KikaoStudentHeader(
    title: String,
    subtitle: String,
    studentName: String,
    onBackClick: (() -> Unit)? = null,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        KikaoColors.DeepIndigo,
                        KikaoColors.Indigo,
                        Color(0xFF31539A)
                    )
                )
            )
            .padding(horizontal = 22.dp, vertical = 18.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = KikaoColors.Teal.copy(alpha = 0.20f),
                radius = size.width * 0.34f,
                center = Offset(size.width * 0.98f, size.height * 0.05f)
            )
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBackClick != null) {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable(onClick = onBackClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "‹",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "KIKAO",
                            color = KikaoColors.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.8.sp
                        )

                        Text(
                            text = "STUDENT PORTAL",
                            color = KikaoColors.White.copy(alpha = 0.70f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(KikaoColors.White.copy(alpha = 0.14f))
                            .clickable(onClick = onNotificationClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "●",
                            color = KikaoColors.White,
                            fontSize = 18.sp
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(7.dp)
                                .size(8.dp)
                                .background(KikaoColors.Gold, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(KikaoColors.Teal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = studentName.take(1).uppercase(),
                            color = KikaoColors.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = subtitle,
                color = KikaoColors.White.copy(alpha = 0.76f),
                fontSize = 12.sp
            )

            Text(
                text = title,
                color = KikaoColors.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.7).sp
            )
        }
    }
}

@Composable
private fun KikaoStudentBottomBar(
    selectedTab: StudentTab,
    onTabSelected: (StudentTab) -> Unit
) {
    NavigationBar(
        containerColor = KikaoColors.White,
        tonalElevation = 6.dp
    ) {
        StudentTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Text(
                        text = tab.icon,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = KikaoColors.Teal,
                    selectedTextColor = KikaoColors.Teal,
                    indicatorColor = KikaoColors.TealLight,
                    unselectedIconColor = KikaoColors.MutedText,
                    unselectedTextColor = KikaoColors.MutedText
                )
            )
        }
    }
}

@Composable
private fun KikaoScanButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        containerColor = KikaoColors.Gold,
        contentColor = KikaoColors.DeepIndigo,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 10.dp
        )
    ) {
        Text(
            text = "▣",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

private fun DrawScope.drawKikaoBackground() {
    drawCircle(
        color = KikaoColors.Teal.copy(alpha = 0.07f),
        radius = size.width * 0.48f,
        center = Offset(size.width * 1.08f, size.height * 0.30f)
    )

    drawCircle(
        color = KikaoColors.Gold.copy(alpha = 0.06f),
        radius = size.width * 0.45f,
        center = Offset(size.width * -0.15f, size.height * 0.82f)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun KikaoStudentScaffoldPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = KikaoColors.Indigo,
            secondary = KikaoColors.Teal,
            background = KikaoColors.Background
        )
    ) {
        KikaoStudentScaffold(
            selectedTab = StudentTab.HOME,
            screenTitle = "Good morning, Amani",
            screenSubtitle = "Tuesday, 18 August"
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Your screen content goes here",
                    color = KikaoColors.Ink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}