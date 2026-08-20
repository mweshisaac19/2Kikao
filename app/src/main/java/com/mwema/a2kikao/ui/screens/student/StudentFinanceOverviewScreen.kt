package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.mwema.a2kikao.data.PaymentItem
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.StudentFinanceViewModel

@Composable
fun StudentFinanceOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: StudentFinanceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onTabSelected: (StudentTab) -> Unit = {}
) {
    val financeRecord by viewModel.financeRecord.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentBalance = financeRecord?.currentBalance ?: 45000.0
    val payments = financeRecord?.payments ?: listOf(
        PaymentItem("1", 20000.0, "12 Aug 2026", "M-Pesa", "Fee Payment"),
        PaymentItem("2", 15000.0, "01 Aug 2026", "Bank Transfer", "Semester Deposit")
    )

    KikaoStudentScaffold(
        selectedTab = StudentTab.PROFILE,
        screenTitle = "Finance & Fees",
        screenSubtitle = "Your institutional balance",
        onBackClick = onBackClick,
        onTabSelected = onTabSelected,
        showScanButton = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 100.dp)
        ) {
            BalanceCard(currentBalance)
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("Payment History")
            
            if (payments.isEmpty()) {
                Text("No payment history found.", color = KikaoColors.MutedText, fontSize = 14.sp)
            } else {
                payments.forEach { payment ->
                    PaymentItemRow(payment)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("CURRENT BALANCE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("KES ${String.format("%,.0f", balance)}", color = KikaoColors.Gold, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)
            ) {
                Text("Make Payment", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PaymentItemRow(payment: PaymentItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(KikaoColors.TealLight), contentAlignment = Alignment.Center) {
                Text(payment.method.take(1), color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(payment.description, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(payment.date, fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            Text("KES ${String.format("%,.0f", payment.amount)}", fontWeight = FontWeight.ExtraBold, color = KikaoColors.Ink)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentFinanceOverviewPreview() {
    MaterialTheme {
        StudentFinanceOverviewScreen()
    }
}
