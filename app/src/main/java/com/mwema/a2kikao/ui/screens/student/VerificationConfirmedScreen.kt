package com.mwema.a2kikao.ui.screens.student


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.AttendanceConfirmationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VerificationConfirmedScreen(
    sessionId: String,
    modifier: Modifier = Modifier,
    viewModel: AttendanceConfirmationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    className: String = "Database Systems",
    classCode: String = "CSC 221",
    roomName: String = "Lab 3",
    onFinish: () -> Unit = {}
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val recordSuccess by viewModel.recordSuccess.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.confirmAttendance(sessionId)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        KikaoColors.DeepIndigo,
                        KikaoColors.Indigo,
                        Color(0xFF31539A)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = KikaoColors.Teal.copy(alpha = 0.20f),
                radius = size.width * 0.54f,
                center = Offset(size.width * 1.02f, size.height * 0.12f)
            )

            drawCircle(
                color = KikaoColors.Gold.copy(alpha = 0.13f),
                radius = size.width * 0.42f,
                center = Offset(size.width * -0.08f, size.height * 0.84f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KIKAO",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            ConfirmationMark()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = if (recordSuccess) "Attendance confirmed" else if (isRecording) "Recording attendance..." else "Finalizing...",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (recordSuccess) "You are securely checked in for this class." else "Please wait while we secure your spot.",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            AttendanceReceipt(
                className = className,
                classCode = classCode,
                roomName = roomName
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "QR · Location · Identity verified",
                color = Color.White.copy(alpha = 0.67f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KikaoColors.Gold,
                    contentColor = KikaoColors.DeepIndigo
                )
            ) {
                Text(
                    text = "Finish",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ConfirmationMark() {
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = KikaoColors.Teal.copy(alpha = 0.18f),
                radius = size.width * 0.48f
            )

            drawCircle(
                color = KikaoColors.Teal.copy(alpha = 0.28f),
                radius = size.width * 0.35f
            )

            drawCircle(
                color = KikaoColors.Gold,
                radius = size.width * 0.28f,
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Surface(
            modifier = Modifier.size(86.dp),
            shape = CircleShape,
            color = KikaoColors.Teal
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun AttendanceReceipt(
    className: String,
    classCode: String,
    roomName: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "ATTENDANCE RECEIPT",
                color = KikaoColors.Teal,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = className,
                color = KikaoColors.Ink,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "$classCode · $roomName",
                color = KikaoColors.MutedText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ReceiptDetail(
                    label = "Status",
                    value = "Present",
                    valueColor = KikaoColors.Teal
                )

                ReceiptDetail(
                    label = "Time",
                    value = "14:02",
                    valueColor = KikaoColors.Ink
                )

                ReceiptDetail(
                    label = "Date",
                    value = "18 Aug",
                    valueColor = KikaoColors.Ink
                )
            }
        }
    }
}

@Composable
private fun ReceiptDetail(
    label: String,
    value: String,
    valueColor: Color
) {
    Column {
        Text(
            text = label,
            color = KikaoColors.MutedText,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerificationConfirmedScreenPreview() {
    MaterialTheme {
        VerificationConfirmedScreen(sessionId = "demo")
    }
}