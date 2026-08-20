package com.mwema.a2kikao.ui.screens.student


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File

private enum class SelfieState {
    READY,
    CAMERA,
    CAPTURING,
    VERIFYING,
    CONFIRMED,
    ERROR
}

@Composable
fun SelfieVerificationScreen(
    isSelfieRequired: Boolean = true,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onVerificationComplete: (Boolean) -> Unit = {},
    onVerifySelfie: suspend (File) -> Boolean = { true }
) {
    val context = LocalContext.current

    var selfieState by rememberSaveable { mutableStateOf(SelfieState.READY) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedSelfie by remember { mutableStateOf<File?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            selfieState = SelfieState.CAMERA
        } else {
            errorMessage = "Camera access is required for this identity check."
            selfieState = SelfieState.ERROR
        }
    }

    fun openSelfieCamera() {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCameraPermission) {
            selfieState = SelfieState.CAMERA
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun captureSelfie() {
        val camera = imageCapture ?: return
        val file = File(
            context.cacheDir,
            "kikao_selfie_${System.currentTimeMillis()}.jpg"
        )

        selfieState = SelfieState.CAPTURING

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        camera.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(
                    outputFileResults: ImageCapture.OutputFileResults
                ) {
                    capturedSelfie = file
                }

                override fun onError(exception: ImageCaptureException) {
                    errorMessage = "We could not capture your selfie. Please try again."
                    selfieState = SelfieState.ERROR
                }
            }
        )
    }

    LaunchedEffect(isSelfieRequired) {
        if (!isSelfieRequired) {
            delay(900)
            onVerificationComplete(true)
        }
    }

    LaunchedEffect(capturedSelfie) {
        val selfie = capturedSelfie ?: return@LaunchedEffect

        selfieState = SelfieState.VERIFYING

        val isApproved = try {
            onVerifySelfie(selfie)
        } catch (_: Exception) {
            false
        }

        if (isApproved) {
            selfieState = SelfieState.CONFIRMED
            delay(900)
            onVerificationComplete(true)
        } else {
            errorMessage = "We could not confirm your identity. Please retake your selfie."
            selfieState = SelfieState.ERROR
        }
    }

    SelfieVerificationLayout(
        modifier = modifier,
        isSelfieRequired = isSelfieRequired,
        selfieState = selfieState,
        errorMessage = errorMessage,
        onBackClick = onBackClick,
        onStartCamera = { openSelfieCamera() },
        onCaptureSelfie = { captureSelfie() },
        onTryAgain = {
            capturedSelfie = null
            errorMessage = ""
            selfieState = SelfieState.CAMERA
        },
        cameraContent = {
            if (selfieState == SelfieState.CAMERA || selfieState == SelfieState.CAPTURING) {
                FrontCameraPreview(
                    onImageCaptureReady = { imageCapture = it }
                )
            } else {
                SelfiePreviewBackground()
            }
        }
    )
}

@Composable
private fun SelfieVerificationLayout(
    isSelfieRequired: Boolean,
    selfieState: SelfieState,
    errorMessage: String,
    onBackClick: () -> Unit,
    onStartCamera: () -> Unit,
    onCaptureSelfie: () -> Unit,
    onTryAgain: () -> Unit,
    cameraContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KikaoColors.DeepIndigo)
    ) {
        cameraContent()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            KikaoColors.DeepIndigo.copy(alpha = 0.88f),
                            Color.Transparent,
                            KikaoColors.DeepIndigo.copy(alpha = 0.96f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelfieTopBar(onBackClick)

            Spacer(modifier = Modifier.height(34.dp))

            if (!isSelfieRequired) {
                NoSelfieRequiredContent()
                return@Column
            }

            Text(
                text = when (selfieState) {
                    SelfieState.CONFIRMED -> "Identity confirmed"
                    SelfieState.VERIFYING -> "Confirming your identity"
                    else -> "Quick identity check"
                },
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (selfieState) {
                    SelfieState.CAMERA, SelfieState.CAPTURING ->
                        "Position your face inside the guide."
                    SelfieState.VERIFYING ->
                        "Matching your selfie with your verified student profile."
                    SelfieState.CONFIRMED ->
                        "Your attendance has been securely verified."
                    else ->
                        "You were randomly selected for a quick selfie verification."
                },
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            FaceGuide(
                isCameraOpen = selfieState == SelfieState.CAMERA ||
                        selfieState == SelfieState.CAPTURING,
                isVerified = selfieState == SelfieState.CONFIRMED
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (selfieState) {
                SelfieState.READY -> {
                    StatusPanel(
                        title = "Random verification selected",
                        description = "This protects students and prevents proxy attendance."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SelfieActionButton(
                        label = "Start identity check",
                        onClick = onStartCamera
                    )
                }

                SelfieState.CAMERA -> {
                    Text(
                        text = "Keep your face well lit and centred.",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SelfieActionButton(
                        label = "Capture selfie",
                        onClick = onCaptureSelfie
                    )
                }

                SelfieState.CAPTURING, SelfieState.VERIFYING -> {
                    CircularProgressIndicator(
                        color = KikaoColors.Gold,
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (selfieState == SelfieState.CAPTURING) {
                            "Capturing your selfie..."
                        } else {
                            "Verifying your identity..."
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                SelfieState.CONFIRMED -> {
                    StatusPanel(
                        title = "Identity verified",
                        description = "Attendance confirmation is being completed."
                    )
                }

                SelfieState.ERROR -> {
                    StatusPanel(
                        title = "Verification needed",
                        description = errorMessage,
                        isError = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SelfieActionButton(
                        label = "Try again",
                        onClick = onTryAgain
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Your selfie is used only to verify this attendance session.",
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SelfieTopBar(
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
                text = "IDENTITY CHECK",
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun FaceGuide(
    isCameraOpen: Boolean,
    isVerified: Boolean
) {
    Box(
        modifier = Modifier.size(238.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val guideColor = when {
                isVerified -> KikaoColors.Teal
                isCameraOpen -> KikaoColors.Gold
                else -> Color.White.copy(alpha = 0.7f)
            }

            drawCircle(
                color = guideColor.copy(alpha = 0.14f),
                radius = size.width * 0.48f
            )

            drawCircle(
                color = guideColor,
                radius = size.width * 0.34f,
                style = Stroke(width = 3.dp.toPx())
            )

            drawCircle(
                color = guideColor.copy(alpha = 0.35f),
                radius = size.width * 0.42f,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Surface(
            modifier = Modifier.size(82.dp),
            shape = CircleShape,
            color = if (isVerified) KikaoColors.Teal else KikaoColors.DeepIndigo.copy(alpha = 0.86f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = if (isVerified) "✓" else "◉",
                    color = Color.White,
                    fontSize = 33.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun StatusPanel(
    title: String,
    description: String,
    isError: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isError) {
            Color(0xFFB42318).copy(alpha = 0.90f)
        } else {
            Color.White.copy(alpha = 0.14f)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = description,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SelfieActionButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
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
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NoSelfieRequiredContent() {
    Spacer(modifier = Modifier.height(65.dp))

    Box(
        modifier = Modifier
            .size(112.dp)
            .clip(CircleShape)
            .background(KikaoColors.Teal),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = Color.White,
            fontSize = 52.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(
        text = "No additional check today",
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Your QR and location checks are complete. Finishing attendance verification...",
        color = Color.White.copy(alpha = 0.78f),
        fontSize = 14.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SelfiePreviewBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF325185),
                        KikaoColors.DeepIndigo
                    )
                )
            )
    )
}

@Composable
private fun FrontCameraPreview(
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(lifecycleOwner, previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = CameraXPreview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageCapture
            )

            onImageCaptureReady(imageCapture)
        }

        cameraProviderFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(context)
        )

        onDispose { }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SelfieVerificationScreenPreview() {
    MaterialTheme {
        SelfieVerificationLayout(
            isSelfieRequired = true,
            selfieState = SelfieState.READY,
            errorMessage = "",
            onBackClick = {},
            onStartCamera = {},
            onCaptureSelfie = {},
            onTryAgain = {},
            cameraContent = { SelfiePreviewBackground() }
        )
    }
}