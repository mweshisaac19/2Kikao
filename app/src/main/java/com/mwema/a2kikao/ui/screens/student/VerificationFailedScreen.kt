package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private val FailureRed = Color(0xFFDC3545)
private val FailureLightRed = Color(0xFFFFEAEC)

@Composable
fun VerificationFailedScreen(
    modifier: Modifier = Modifier,
    reason: String = "We could not complete your attendance verification.",
    onTryAgain: () -> Unit = {},
    onSupportClick: () -> Unit = {}
) {
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
                color = FailureRed.copy(alpha = 0.16f),
                radius = size.width * 0.50f,
                center = Offset(size.width * 1.03f, size.height * 0.16f)
            )

            drawCircle(
                color = KikaoColors.Gold.copy(alpha = 0.10f),
                radius = size.width * 0.44f,
                center = Offset(size.width * -0.10f, size.height * 0.86f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
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

            FailureMark()

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Verification incomplete",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your attendance has not been recorded yet.",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            FailureReasonCard(
                reason = reason,
                onSupportClick = onSupportClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Retry using the lecturer's current QR code.",
                color = Color.White.copy(alpha = 0.67f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTryAgain,
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
                    text = "Retry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FailureMark() {
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = FailureRed.copy(alpha = 0.16f),
                radius = size.width * 0.48f
            )

            drawCircle(
                color = FailureRed.copy(alpha = 0.28f),
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
            color = FailureRed
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "!",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun FailureReasonCard(
    reason: String,
    onSupportClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onSupportClick)
                    .background(FailureLightRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = FailureRed,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "What happened?",
                color = KikaoColors.Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = reason,
                color = KikaoColors.MutedText,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FailureStep(label = "QR")
                FailureStep(label = "Location")
                FailureStep(label = "Identity")
            }
        }
    }
}

@Composable
private fun FailureStep(
    label: String
) {
    Box(
        modifier = Modifier
            .background(FailureLightRed, RoundedCornerShape(9.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = FailureRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerificationFailedScreenPreview() {
    MaterialTheme {
        VerificationFailedScreen()
    }
}