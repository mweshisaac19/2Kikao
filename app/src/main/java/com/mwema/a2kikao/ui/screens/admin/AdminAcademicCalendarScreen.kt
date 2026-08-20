package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class AcademicTerm(
    val name: String,
    val code: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val color: Color
)

private data class AcademicHoliday(
    val name: String,
    val date: String,
    val duration: String,
    val type: String
)

@Composable
fun AdminAcademicCalendarScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSaveCalendar: () -> Unit = {},
    onPublishCalendar: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedTermIndex by remember { mutableIntStateOf(0) }
    var showTermEditor by remember { mutableStateOf(false) }
    var showHolidayEditor by remember { mutableStateOf(false) }
    var showPublishDialog by remember { mutableStateOf(false) }

    val terms = remember {
        mutableStateListOf(
            AcademicTerm(
                name = "Semester 1",
                code = "SEM 1 · 2026",
                startDate = "05 Jan 2026",
                endDate = "24 Apr 2026",
                status = "Completed",
                color = KikaoColors.Teal
            ),
            AcademicTerm(
                name = "Semester 2",
                code = "SEM 2 · 2026",
                startDate = "11 May 2026",
                endDate = "28 Aug 2026",
                status = "Current",
                color = KikaoColors.Indigo
            ),
            AcademicTerm(
                name = "Semester 1",
                code = "SEM 1 · 2027",
                startDate = "04 Jan 2027",
                endDate = "23 Apr 2027",
                status = "Upcoming",
                color = KikaoColors.Gold
            )
        )
    }

    val holidays = remember {
        mutableStateListOf(
            AcademicHoliday(
                name = "Madaraka Day",
                date = "01 Jun 2026",
                duration = "1 day",
                type = "Public holiday"
            ),
            AcademicHoliday(
                name = "Mid-Semester Break",
                date = "06 Jul – 10 Jul 2026",
                duration = "5 days",
                type = "Academic break"
            ),
            AcademicHoliday(
                name = "Huduma Day",
                date = "10 Oct 2026",
                duration = "1 day",
                type = "Public holiday"
            )
        )
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = "Academic calendar",
        screenSubtitle = "Terms, holidays and transitions",
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
                .padding(top = 18.dp, bottom = 110.dp)
        ) {

            CalendarHero(
                currentTerm = terms.first { it.status == "Current" },
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            CalendarStatusCard(
                onPublishClick = {
                    showPublishDialog = true
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle(
                title = "Academic periods",
                subtitle = "Configure semester dates and transitions"
            )

            Spacer(modifier = Modifier.height(12.dp))

            TermSelector(
                terms = terms,
                selectedIndex = selectedTermIndex,
                onSelect = {
                    selectedTermIndex = it
                }
            )

            Spacer(modifier = Modifier.height(13.dp))

            TermDetailsCard(
                term = terms[selectedTermIndex],
                onEditClick = {
                    showTermEditor = true
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            SemesterTransitionCard(
                currentTerm = terms[selectedTermIndex],
                nextTerm = terms.getOrNull(selectedTermIndex + 1)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle(
                title = "Holidays & breaks",
                subtitle = "Days when academic activities are suspended"
            )

            Spacer(modifier = Modifier.height(12.dp))

            holidays.forEachIndexed { index, holiday ->

                HolidayCard(
                    holiday = holiday,
                    onDelete = {
                        holidays.removeAt(index)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            AddHolidayButton(
                onClick = {
                    showHolidayEditor = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            CalendarRulesCard()

            Spacer(modifier = Modifier.height(20.dp))

            SaveCalendarCard(
                onSave = onSaveCalendar,
                onPublish = {
                    showPublishDialog = true
                }
            )
        }
    }

    if (showTermEditor) {
        TermEditorDialog(
            term = terms[selectedTermIndex],
            onDismiss = {
                showTermEditor = false
            },
            onSave = {
                showTermEditor = false
                onSaveCalendar()
            }
        )
    }

    if (showHolidayEditor) {
        HolidayEditorDialog(
            onDismiss = {
                showHolidayEditor = false
            },
            onAdd = { name, date, duration, type ->
                holidays.add(
                    AcademicHoliday(
                        name = name,
                        date = date,
                        duration = duration,
                        type = type
                    )
                )
                showHolidayEditor = false
            }
        )
    }

    if (showPublishDialog) {
        AlertDialog(
            onDismissRequest = {
                showPublishDialog = false
            },
            title = {
                Text(
                    text = "Publish academic calendar?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Publishing will make the configured term dates and holidays visible across the institution."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPublishDialog = false
                        onPublishCalendar()
                    }
                ) {
                    Text(
                        text = "Publish",
                        color = KikaoColors.Indigo,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPublishDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CalendarHero(
    currentTerm: AcademicTerm,
    onBackClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(25.dp),
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(onClick = onBackClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        color = Color.White,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "ACADEMIC CONFIGURATION",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Academic Calendar",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(19.dp))

            Text(
                text = "Current academic period",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentTerm.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${currentTerm.startDate}  →  ${currentTerm.endDate}",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .background(KikaoColors.Gold)
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
            ) {
                Text(
                    text = "● CALENDAR ACTIVE",
                    color = KikaoColors.DeepIndigo,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun CalendarStatusCard(
    onPublishClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
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
                    text = "✓",
                    color = KikaoColors.Teal,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Calendar status",
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Institutional dates are active.",
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )
            }

            TextButton(
                onClick = onPublishClick
            ) {
                Text(
                    text = "Publish",
                    color = KikaoColors.Indigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun TermSelector(
    terms: List<AcademicTerm>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        terms.forEachIndexed { index, term ->

            val selected = index == selectedIndex

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        if (selected) {
                            KikaoColors.Indigo
                        } else {
                            Color.White
                        }
                    )
                    .clickable {
                        onSelect(index)
                    }
                    .padding(
                        horizontal = 15.dp,
                        vertical = 11.dp
                    )
            ) {

                Text(
                    text = term.name,
                    color = if (selected) {
                        Color.White
                    } else {
                        KikaoColors.Ink
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = term.code,
                    color = if (selected) {
                        Color.White.copy(alpha = 0.68f)
                    } else {
                        KikaoColors.MutedText
                    },
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun TermDetailsCard(
    term: AcademicTerm,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = term.name,
                        color = KikaoColors.Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = term.code,
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )
                }

                CalendarStatusPill(
                    text = term.status
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                CalendarDateBox(
                    label = "START DATE",
                    date = term.startDate,
                    modifier = Modifier.weight(1f)
                )

                CalendarDateBox(
                    label = "END DATE",
                    date = term.endDate,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color(0xFFEAF0F8))
                    .clickable(onClick = onEditClick)
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Edit term dates",
                        color = KikaoColors.Indigo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "›",
                        color = KikaoColors.Indigo,
                        fontSize = 21.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDateBox(
    label: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF6F8FB))
            .padding(13.dp)
    ) {

        Column {
            Text(
                text = label,
                color = KikaoColors.MutedText,
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = date,
                color = KikaoColors.Ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CalendarStatusPill(
    text: String
) {
    val background = when (text) {
        "Current" -> KikaoColors.TealLight
        "Upcoming" -> Color(0xFFFFF3D6)
        else -> Color(0xFFEAF0F8)
    }

    val foreground = when (text) {
        "Current" -> KikaoColors.Teal
        "Upcoming" -> Color(0xFF9A6700)
        else -> KikaoColors.Indigo
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(background)
            .padding(
                horizontal = 9.dp,
                vertical = 6.dp
            )
    ) {
        Text(
            text = text.uppercase(),
            color = foreground,
            fontSize = 8.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SemesterTransitionCard(
    currentTerm: AcademicTerm,
    nextTerm: AcademicTerm?
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F1FF)
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "SEMESTER TRANSITION",
                color = Color(0xFF7654B8),
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TransitionNode(
                    label = currentTerm.name,
                    date = currentTerm.endDate,
                    active = true
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(Color(0xFFD7CCF3))
                )

                TransitionNode(
                    label = nextTerm?.name ?: "Not configured",
                    date = nextTerm?.startDate ?: "—",
                    active = false
                )
            }
        }
    }
}

@Composable
private fun TransitionNode(
    label: String,
    date: String,
    active: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    if (active) {
                        KikaoColors.Indigo
                    } else {
                        Color(0xFFD7CCF3)
                    }
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            color = KikaoColors.Ink,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = date,
            color = KikaoColors.MutedText,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun HolidayCard(
    holiday: AcademicHoliday,
    onDelete: () -> Unit
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
                    .background(Color(0xFFFFF3D6)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★",
                    color = Color(0xFF9A6700),
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = holiday.name,
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = holiday.date,
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )
            }

            TextButton(
                onClick = onDelete
            ) {
                Text(
                    text = "Remove",
                    color = Color(0xFFB42318),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun AddHolidayButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(KikaoColors.Indigo)
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "+",
                color = KikaoColors.Gold,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Add holiday or break",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CalendarRulesCard() {
    Card(
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.TealLight
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(11.dp))

                Text(
                    text = "Calendar automation",
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Classes and analytics automatically follow configured term dates and academic breaks.",
                color = KikaoColors.MutedText,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun SaveCalendarCard(
    onSave: () -> Unit,
    onPublish: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "Ready to update?",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {

                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Save draft",
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onPublish,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KikaoColors.Gold,
                        contentColor = KikaoColors.DeepIndigo
                    )
                ) {
                    Text(
                        text = "Publish",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TermEditorDialog(
    term: AcademicTerm,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var start by remember { mutableStateOf(term.startDate) }
    var end by remember { mutableStateOf(term.endDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit ${term.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Start date") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("End date") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(text = "Save", color = KikaoColors.Indigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun HolidayEditorDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Break", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Event name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(9.dp))
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date range") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank() && date.isNotBlank(), onClick = { onAdd(name, date, "1 day", "Holiday") }) {
                Text(text = "Add", color = KikaoColors.Indigo, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column {
        Text(text = title, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminAcademicCalendarPreview() {
    MaterialTheme {
        AdminAcademicCalendarScreen()
    }
}
