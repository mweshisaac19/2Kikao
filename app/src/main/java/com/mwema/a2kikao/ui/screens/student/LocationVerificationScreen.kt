package com.mwema.a2kikao.ui.screens.student


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class ClassLocation(
    val id: String = "",
    val className: String,
    val roomName: String,
    val latitude: Double,
    val longitude: Double,
    val allowedRadiusMeters: Float = 100f
)

private enum class LocationState {
    READY,
    CHECKING,
    CONFIRMED,
    OUTSIDE_ZONE,
    ERROR
}

@Composable
fun LocationVerificationScreen(
    classLocation: ClassLocation,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLocationVerified: () -> Unit = {}
) {
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationState by rememberSaveable { mutableStateOf(LocationState.READY) }
    var distanceFromClass by rememberSaveable { mutableStateOf<Float?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    @android.annotation.SuppressLint("MissingPermission")
    fun checkLocation() {
        locationState = LocationState.CHECKING
        errorMessage = ""

        val cancellationTokenSource = CancellationTokenSource()

        locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { currentLocation ->
            if (currentLocation == null) {
                errorMessage = "We could not find your location. Turn on GPS and try again."
                locationState = LocationState.ERROR
                return@addOnSuccessListener
            }

            val result = FloatArray(1)

            Location.distanceBetween(
                currentLocation.latitude,
                currentLocation.longitude,
                classLocation.latitude,
                classLocation.longitude,
                result
            )

            distanceFromClass = result[0]

            locationState = if (result[0] <= classLocation.allowedRadiusMeters) {
                LocationState.CONFIRMED
            } else {
                LocationState.OUTSIDE_ZONE
            }
        }.addOnFailureListener {
            errorMessage = "Location check failed. Please try again."
            locationState = LocationState.ERROR
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            checkLocation()
        } else {
            errorMessage = "Location access is needed to verify your class attendance."
            locationState = LocationState.ERROR
        }
    }

    fun beginLocationVerification() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            checkLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(locationState) {
        if (locationState == LocationState.CONFIRMED) {
            delay(900.milliseconds)
            onLocationVerified()
        }
    }

    LocationVerificationLayout(
        modifier = modifier,
        classLocation = classLocation,
        locationState = locationState,
        distanceFromClass = distanceFromClass,
        errorMessage = errorMessage,
        onBackClick = onBackClick,
        onCheckLocation = { beginLocationVerification() }
    )
}

@Composable
private fun LocationVerificationLayout(
    classLocation: ClassLocation,
    locationState: LocationState,
    distanceFromClass: Float?,
    errorMessage: String,
    onBackClick: () -> Unit,
    onCheckLocation: () -> Unit,
    modifier: Modifier = Modifier
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
                color = KikaoColors.Teal.copy(alpha = 0.18f),
                radius = size.width * 0.55f,
                center = Offset(size.width * 1.08f, size.height * 0.18f)
            )

            drawCircle(
                color = KikaoColors.Gold.copy(alpha = 0.12f),
                radius = size.width * 0.47f,
                center = Offset(size.width * -0.10f, size.height * 0.90f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationTopBar(onBackClick)

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = when (locationState) {
                    LocationState.CONFIRMED -> "Location confirmed"
                    else -> "Verify your location"
                },
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (locationState) {
                    LocationState.CONFIRMED ->
                        "You are inside the class attendance zone."
                    else ->
                        "Confirm that you are at ${classLocation.roomName}."
                },
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            LocationTrustZone(
                isConfirmed = locationState == LocationState.CONFIRMED,
                isChecking = locationState == LocationState.CHECKING
            )

            Spacer(modifier = Modifier.height(34.dp))

            LocationStatusCard(
                classLocation = classLocation,
                locationState = locationState,
                distanceFromClass = distanceFromClass,
                errorMessage = errorMessage,
                onCheckLocation = onCheckLocation
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Kikao only checks your location at attendance time.",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LocationTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.16f))
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Light
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "KIKAO",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.8.sp
            )

            Text(
                text = "LOCATION CHECK",
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun LocationTrustZone(
    isConfirmed: Boolean,
    isChecking: Boolean
) {
    Box(
        modifier = Modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val activeColor = if (isConfirmed) KikaoColors.Teal else KikaoColors.Gold

            drawCircle(
                color = activeColor.copy(alpha = 0.10f),
                radius = size.width * 0.48f
            )

            drawCircle(
                color = activeColor.copy(alpha = 0.16f),
                radius = size.width * 0.35f
            )

            drawCircle(
                color = activeColor.copy(alpha = 0.24f),
                radius = size.width * 0.22f
            )

            drawCircle(
                color = activeColor,
                radius = 8.dp.toPx()
            )

            drawLine(
                color = Color.White.copy(alpha = 0.50f),
                start = Offset(size.width / 2, 12.dp.toPx()),
                end = Offset(size.width / 2, size.height - 12.dp.toPx()),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawLine(
                color = Color.White.copy(alpha = 0.50f),
                start = Offset(12.dp.toPx(), size.height / 2),
                end = Offset(size.width - 12.dp.toPx(), size.height / 2),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Surface(
            modifier = Modifier.size(94.dp),
            shape = CircleShape,
            color = if (isConfirmed) KikaoColors.Teal else KikaoColors.DeepIndigo.copy(alpha = 0.88f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = KikaoColors.Gold,
                        strokeWidth = 4.dp
                    )
                } else {
                    Text(
                        text = if (isConfirmed) "✓" else "⌖",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationStatusCard(
    classLocation: ClassLocation,
    locationState: LocationState,
    distanceFromClass: Float?,
    errorMessage: String,
    onCheckLocation: () -> Unit
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
            Text(
                text = classLocation.className,
                color = KikaoColors.Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = classLocation.roomName,
                color = KikaoColors.MutedText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (locationState) {
                LocationState.READY -> {
                    Text(
                        text = "Your attendance zone is within ${classLocation.allowedRadiusMeters.toInt()} metres of this class.",
                        color = KikaoColors.MutedText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CheckLocationButton(onClick = onCheckLocation)
                }

                LocationState.CHECKING -> {
                    Text(
                        text = "Finding your secure location...",
                        color = KikaoColors.Indigo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LocationState.CONFIRMED -> {
                    StatusPill(
                        text = "LOCATION VERIFIED",
                        background = KikaoColors.TealLight,
                        textColor = KikaoColors.Teal
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "You are ${distanceFromClass?.toInt() ?: 0}m from the class location.",
                        color = KikaoColors.MutedText,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                LocationState.OUTSIDE_ZONE -> {
                    StatusPill(
                        text = "OUTSIDE ATTENDANCE ZONE",
                        background = Color(0xFFFFF2CC),
                        textColor = Color(0xFF9A6700)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "You are ${distanceFromClass?.toInt() ?: 0}m away. Move closer to ${classLocation.roomName} and try again.",
                        color = KikaoColors.MutedText,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CheckLocationButton(onClick = onCheckLocation)
                }

                LocationState.ERROR -> {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFB42318),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CheckLocationButton(onClick = onCheckLocation)
                }
            }
        }
    }
}

@Composable
private fun CheckLocationButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = KikaoColors.Indigo,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Check my location",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusPill(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(background)
            .padding(horizontal = 11.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.4.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LocationVerificationScreenPreview() {
    MaterialTheme {
        LocationVerificationLayout(
            classLocation = ClassLocation(
                className = "Database Systems",
                roomName = "Lab 3",
                latitude = -1.286389,
                longitude = 36.817223,
                allowedRadiusMeters = 100f
            ),
            locationState = LocationState.READY,
            distanceFromClass = null,
            errorMessage = "",
            onBackClick = {},
            onCheckLocation = {}
        )
    }
}