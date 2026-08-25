package com.mwema.a2kikao.ui.screens.auth


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val KikaoIndigo = Color(0xFF243B7A)
private val KikaoDeepIndigo = Color(0xFF172B62)
private val KikaoTeal = Color(0xFF0F9D8A)
private val KikaoGold = Color(0xFFF4B740)

@Composable
fun KikaoSplashScreen(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "Splash animation")

    val outerRotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(11_000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Outer orbit"
    ).value

    val innerRotation = transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(8_000),
            repeatMode = RepeatMode.Restart
        ),
        label = "Inner orbit"
    ).value

    val logoPulse = transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Logo pulse"
    ).value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        KikaoDeepIndigo,
                        KikaoIndigo,
                        Color(0xFF31539A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 34.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KIKAO",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawCircle(color = KikaoTeal)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "SECURE SESSION",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(outerRotation)
                ) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    drawCircle(
                        color = KikaoGold,
                        radius = 6.dp.toPx(),
                        center = Offset(size.width / 2, 4.dp.toPx())
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(140.dp)
                        .rotate(innerRotation)
                ) {
                    drawArc(
                        color = KikaoGold.copy(alpha = 0.9f),
                        startAngle = 10f,
                        sweepAngle = 260f,
                        useCenter = false,
                        style = Stroke(
                            width = 1.8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .scale(logoPulse)
                        .rotate(-10f)
                        .clip(RoundedCornerShape(28.dp, 28.dp, 28.dp, 7.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF16B8A0), KikaoTeal)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "k",
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.rotate(10f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Kikao",
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-4).sp,
                modifier = Modifier.graphicsLayer {
                    alpha = (logoPulse - 0.96f) * 10f + 0.5f
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Every presence counts.\nEvery student matters.",
                color = Color.White.copy(alpha = 0.84f),
                fontSize = 17.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "Verified learning",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Attendance · insight · action",
                    color = Color.White.copy(alpha = 0.90f),
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                listOf(10.dp, 24.dp, 15.dp, 29.dp).forEach { barHeight ->
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .width(4.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(KikaoGold)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun KikaoSplashScreenPreview() {
    MaterialTheme {
        KikaoSplashScreen()
    }
}