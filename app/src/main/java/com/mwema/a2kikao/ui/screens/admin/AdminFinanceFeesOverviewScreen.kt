package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import java.util.Locale
import kotlin.math.roundToInt

private enum class FinancePeriod(
    val label: String
) {
    THIS_MONTH("This month"),
    THIS_TERM("This term"),
    THIS_YEAR("This year")
}

private data class FinanceSummary(
    val collected: Double,
    val expected: Double,
    val outstanding: Double,
    val scholarships: Double,
    val students: Int,
    val paidStudents: Int,
    val overdueStudents: Int
)

private data class FinanceDepartment(
    val name: String,
    val students: Int,
    val expected: Double,
    val collected: Double
)

private data class ScholarshipRecord(
    val category: String,
    val students: Int,
    val amount: Double,
    val status: String
)

private data class FinanceTransaction(
    val student: String,
    val reference: String,
    val amount: Double,
    val type: String,
    val date: String,
    val status: String
)

@Composable
fun AdminFinanceFeesOverviewScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onViewTransactions: () -> Unit = {},
    onViewScholarships: () -> Unit = {},
    onViewOutstanding: () -> Unit = {},
    onExportReport: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedPeriod by remember {
        mutableStateOf(FinancePeriod.THIS_TERM)
    }

    val finance = remember(selectedPeriod) {
        demoFinanceData(selectedPeriod)
    }

    val collectionRate =
        if (finance.expected == 0.0) {
            0
        } else {
            ((finance.collected / finance.expected) * 100)
                .roundToInt()
        }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Finance & fees",
        screenSubtitle = "Institutional financial overview",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding: PaddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .padding(bottom = 110.dp)
        ) {

            FinanceHeroCard(
                collected = finance.collected,
                expected = finance.expected,
                collectionRate = collectionRate,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            FinancePeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = {
                    selectedPeriod = it
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FinanceMetricGrid(
                finance = finance,
                onOutstandingClick = onViewOutstanding,
                onScholarshipClick = onViewScholarships
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Collection performance",
                subtitle = "How institutional fees are tracking"
            )

            Spacer(modifier = Modifier.height(10.dp))

            CollectionPerformanceCard(
                finance = finance,
                collectionRate = collectionRate
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Department performance",
                subtitle = "Fee collection by academic unit"
            )

            Spacer(modifier = Modifier.height(10.dp))

            DepartmentPerformanceCard(
                departments = demoDepartments()
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Scholarship overview",
                subtitle = "Financial aid currently supported"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ScholarshipOverviewCard(
                records = demoScholarships(),
                onViewAll = onViewScholarships
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Recent transactions",
                subtitle = "Latest fee activity across the institution"
            )

            Spacer(modifier = Modifier.height(10.dp))

            RecentTransactionsCard(
                transactions = demoTransactions(),
                onViewAll = onViewTransactions
            )

            Spacer(modifier = Modifier.height(24.dp))

            FinanceInsightCard(
                finance = finance,
                collectionRate = collectionRate
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExportFinanceCard(
                onExport = onExportReport
            )
        }
    }
}

@Composable
private fun FinanceHeroCard(
    collected: Double,
    expected: Double,
    collectionRate: Int,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Column(
            modifier = Modifier.padding(21.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "‹",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Text(
                            text = "FINANCE COMMAND CENTER",
                            color = Color.White.copy(alpha = 0.68f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Institutional revenue",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                FinancePercentageRing(
                    percentage = collectionRate
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "KES ${formatMoney(collected)}",
                color = KikaoColors.Gold,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "collected of KES ${formatMoney(expected)} expected",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(15.dp))

            FinanceProgressBar(
                progress = collectionRate / 100f
            )

            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$collectionRate% collected",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "KES ${formatMoney(expected - collected)} remaining",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun FinancePercentageRing(
    percentage: Int
) {
    Box(
        modifier = Modifier.size(70.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                style = Stroke(7.dp.toPx())
            )

            drawArc(
                color = KikaoColors.Gold,
                startAngle = -90f,
                sweepAngle = percentage * 3.6f,
                useCenter = false,
                style = Stroke(
                    width = 7.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = "$percentage%",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun FinanceProgressBar(
    progress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(KikaoColors.Gold)
        )
    }
}

@Composable
private fun FinancePeriodSelector(
    selectedPeriod: FinancePeriod,
    onPeriodSelected: (FinancePeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        FinancePeriod.entries.forEach { period ->

            val selected = period == selectedPeriod

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) {
                            KikaoColors.Indigo
                        } else {
                            Color.White
                        }
                    )
                    .clickable {
                        onPeriodSelected(period)
                    }
                    .padding(
                        horizontal = 15.dp,
                        vertical = 10.dp
                    )
            ) {
                Text(
                    text = period.label,
                    color = if (selected) {
                        Color.White
                    } else {
                        KikaoColors.MutedText
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FinanceMetricGrid(
    finance: FinanceSummary,
    onOutstandingClick: () -> Unit,
    onScholarshipClick: () -> Unit
) {
    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            FinanceMetricCard(
                title = "Collected",
                value = "KES ${formatMoney(finance.collected)}",
                caption = "Received",
                accent = KikaoColors.Teal,
                modifier = Modifier.weight(1f)
            )

            FinanceMetricCard(
                title = "Outstanding",
                value = "KES ${formatMoney(finance.outstanding)}",
                caption = "Awaiting payment",
                accent = Color(0xFFD97706),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOutstandingClick() }
            )
        }

        Spacer(modifier = Modifier.height(11.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            FinanceMetricCard(
                title = "Paid students",
                value = finance.paidStudents.toString(),
                caption = "of ${finance.students}",
                accent = KikaoColors.Indigo,
                modifier = Modifier.weight(1f)
            )

            FinanceMetricCard(
                title = "Scholarships",
                value = "KES ${formatMoney(finance.scholarships)}",
                caption = "Financial aid",
                accent = Color(0xFF8B5CF6),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onScholarshipClick() }
            )
        }
    }
}

@Composable
private fun FinanceMetricCard(
    title: String,
    value: String,
    caption: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = KikaoColors.MutedText,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = KikaoColors.Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = caption,
                color = KikaoColors.MutedText,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun CollectionPerformanceCard(
    finance: FinanceSummary,
    collectionRate: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Collection rate",
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "$collectionRate%",
                        color = KikaoColors.Ink,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .background(KikaoColors.TealLight)
                        .padding(
                            horizontal = 10.dp,
                            vertical = 7.dp
                        )
                ) {
                    Text(
                        text = if (collectionRate >= 80) {
                            "HEALTHY"
                        } else {
                            "MONITOR"
                        },
                        color = KikaoColors.Teal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            FinanceBarChart(
                collected = finance.collected,
                expected = finance.expected
            )
        }
    }
}

@Composable
private fun FinanceBarChart(
    collected: Double,
    expected: Double
) {
    val values = listOf(
        expected * 0.64,
        expected * 0.71,
        expected * 0.77,
        expected * 0.82,
        expected * 0.88,
        collected
    )

    val max = expected.coerceAtLeast(1.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(125.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { index, value ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(25.dp)
                        .height(
                            (90f * (value / max))
                                .coerceIn(10.0, 90.0)
                                .dp
                        )
                        .clip(
                            RoundedCornerShape(
                                topStart = 7.dp,
                                topEnd = 7.dp
                            )
                        )
                        .background(
                            if (index == values.lastIndex) {
                                KikaoColors.Teal
                            } else {
                                Color(0xFFDDE5F0)
                            }
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "W${index + 1}",
                    color = KikaoColors.MutedText,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun DepartmentPerformanceCard(
    departments: List<FinanceDepartment>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            departments.forEachIndexed { index, department ->

                DepartmentRow(department)

                if (index < departments.lastIndex) {
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
private fun DepartmentRow(
    department: FinanceDepartment
) {
    val rate =
        ((department.collected / department.expected) * 100)
            .roundToInt()
            .coerceIn(0, 100)

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = department.name,
                    color = KikaoColors.Ink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${department.students} students",
                    color = KikaoColors.MutedText,
                    fontSize = 9.sp
                )
            }

            Text(
                text = "$rate%",
                color = if (rate >= 80) {
                    KikaoColors.Teal
                } else {
                    Color(0xFFD97706)
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF0F3F8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(rate / 100f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (rate >= 80) {
                            KikaoColors.Teal
                        } else {
                            Color(0xFFD97706)
                        }
                    )
            )
        }
    }
}

@Composable
private fun ScholarshipOverviewCard(
    records: List<ScholarshipRecord>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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

            records.forEachIndexed { index, record ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(39.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0EAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦",
                            color = Color(0xFF8B5CF6),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(11.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = record.category,
                            color = KikaoColors.Ink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${record.students} students · ${record.status}",
                            color = KikaoColors.MutedText,
                            fontSize = 9.sp
                        )
                    }

                    Text(
                        text = "KES ${formatMoney(record.amount)}",
                        color = Color(0xFF8B5CF6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                if (index < records.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "View scholarship records  ›",
                color = KikaoColors.Indigo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun RecentTransactionsCard(
    transactions: List<FinanceTransaction>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            transactions.forEachIndexed { index, transaction ->

                TransactionRow(transaction)

                if (index < transactions.lastIndex) {
                    Spacer(modifier = Modifier.height(13.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "View all transactions  ›",
                color = KikaoColors.Indigo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: FinanceTransaction
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (transaction.status == "Completed") {
                        KikaoColors.TealLight
                    } else {
                        Color(0xFFFFF2CC)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "K",
                color = if (transaction.status == "Completed") {
                    KikaoColors.Teal
                } else {
                    Color(0xFF9A6700)
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.student,
                color = KikaoColors.Ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${transaction.reference} · ${transaction.date}",
                color = KikaoColors.MutedText,
                fontSize = 9.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "KES ${formatMoney(transaction.amount)}",
                color = KikaoColors.Ink,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = transaction.status,
                color = if (transaction.status == "Completed") {
                    KikaoColors.Teal
                } else {
                    Color(0xFFD97706)
                },
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FinanceInsightCard(
    finance: FinanceSummary,
    collectionRate: Int
) {
    val overduePercentage =
        if (finance.students == 0) {
            0
        } else {
            ((finance.overdueStudents.toDouble() / finance.students) * 100)
                .roundToInt()
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAF8F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(KikaoColors.Teal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↗",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Finance insight",
                    color = KikaoColors.Teal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (collectionRate >= 80) {
                        "Fee collection is performing strongly."
                    } else {
                        "Fee collection needs administrative attention."
                    },
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "$overduePercentage% of students are currently overdue. Use records to identify students who may need support.",
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ExportFinanceCard(
    onExport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExport() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↓",
                    color = KikaoColors.Gold,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Export finance report",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Generate a finance summary report",
                    color = Color.White.copy(alpha = 0.68f),
                    fontSize = 10.sp
                )
            }

            Text(
                text = "›",
                color = KikaoColors.Gold,
                fontSize = 26.sp
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            color = KikaoColors.Ink,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            color = KikaoColors.MutedText,
            fontSize = 11.sp
        )
    }
}

private fun demoFinanceData(
    period: FinancePeriod
): FinanceSummary {
    return when (period) {

        FinancePeriod.THIS_MONTH -> FinanceSummary(
            collected = 12_840_000.0,
            expected = 16_200_000.0,
            outstanding = 3_360_000.0,
            scholarships = 1_420_000.0,
            students = 1850,
            paidStudents = 1418,
            overdueStudents = 214
        )

        FinancePeriod.THIS_TERM -> FinanceSummary(
            collected = 86_450_000.0,
            expected = 103_800_000.0,
            outstanding = 17_350_000.0,
            scholarships = 8_640_000.0,
            students = 1850,
            paidStudents = 1524,
            overdueStudents = 214
        )

        FinancePeriod.THIS_YEAR -> FinanceSummary(
            collected = 174_800_000.0,
            expected = 215_600_000.0,
            outstanding = 40_800_000.0,
            scholarships = 17_900_000.0,
            students = 1850,
            paidStudents = 1487,
            overdueStudents = 256
        )
    }
}

private fun demoDepartments(): List<FinanceDepartment> {
    return listOf(
        FinanceDepartment("Comp Science", 420, 24_800_000.0, 22_320_000.0),
        FinanceDepartment("Business", 510, 28_900_000.0, 23_120_000.0),
        FinanceDepartment("Engineering", 360, 21_600_000.0, 18_360_000.0)
    )
}

private fun demoScholarships(): List<ScholarshipRecord> {
    return listOf(
        ScholarshipRecord("Government", 86, 3_440_000.0, "Active"),
        ScholarshipRecord("Merit awards", 31, 1_550_000.0, "Active")
    )
}

private fun demoTransactions(): List<FinanceTransaction> {
    return listOf(
        FinanceTransaction("Brian Otieno", "KKA-84921", 45_000.0, "Fee payment", "Today · 11:42", "Completed"),
        FinanceTransaction("Grace Njeri", "SCH-2048", 60_000.0, "Scholarship", "Yesterday", "Processed")
    )
}

private fun formatMoney(
    amount: Double
): String {
    return when {
        amount >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", amount / 1_000_000)
        amount >= 1_000 -> String.format(Locale.getDefault(), "%.0fK", amount / 1_000)
        else -> String.format(Locale.getDefault(), "%.0f", amount)
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminFinanceFeesOverviewScreenPreview() {
    MaterialTheme {
        AdminFinanceFeesOverviewScreen()
    }
}
