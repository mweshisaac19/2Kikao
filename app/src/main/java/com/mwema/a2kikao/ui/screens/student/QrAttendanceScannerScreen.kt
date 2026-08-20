package com.mwema.a2kikao.ui.screens.student

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview as CameraXPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.mwema.a2kikao.ui.viewmodels.QrScannerViewModel
import com.mwema.a2kikao.ui.viewmodels.QrVerificationState
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private enum class ScannerState {
    SCANNING,
    VERIFYING,
    ERROR
}

@Composable
fun QrAttendanceScannerScreen(
    modifier: Modifier = Modifier,
    viewModel: QrScannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onQrVerified: (ClassLocation) -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val verificationState by viewModel.verificationState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            isPreview || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var scannedValue by rememberSaveable { mutableStateOf<String?>(null) }
    var cameraSession by rememberSaveable { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(hasCameraPermission, isPreview) {
        if (!hasCameraPermission && !isPreview) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(scannedValue) {
        val qrValue = scannedValue ?: return@LaunchedEffect
        viewModel.verifyQrCode(qrValue)
    }

    LaunchedEffect(verificationState) {
        if (verificationState is QrVerificationState.Success) {
            onQrVerified((verificationState as QrVerificationState.Success).location)
            viewModel.resetState()
        }
    }

    val scannerState = when (verificationState) {
        QrVerificationState.Verifying -> ScannerState.VERIFYING
        is QrVerificationState.Error -> ScannerState.ERROR
        else -> ScannerState.SCANNING
    }

    val errorMessage = (verificationState as? QrVerificationState.Error)?.message ?: ""

    QrScannerLayout(
        modifier = modifier,
        scannerState = scannerState,
        errorMessage = errorMessage,
        onBackClick = onBackClick,
        onTryAgain = {
            scannedValue = null
            viewModel.resetState()
            cameraSession++
        },
        cameraContent = {
            if (hasCameraPermission) {
                key(cameraSession) {
                    QrCameraPreview(
                        onQrDetected = { qrValue ->
                            if (scannerState == ScannerState.SCANNING) {
                                scannedValue = qrValue
                            }
                        }
                    )
                }
            } else {
                CameraPermissionContent(
                    onAllowCamera = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    )
}

@Composable
private fun QrScannerLayout(
    scannerState: ScannerState,
    errorMessage: String,
    onBackClick: () -> Unit,
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
                        colors = listOf(
                            KikaoColors.DeepIndigo.copy(alpha = 0.84f),
                            Color.Transparent,
                            KikaoColors.DeepIndigo.copy(alpha = 0.94f)
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
            ScannerTopBar(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(38.dp))

            Text(
                text = "Mark attendance",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Scan the dynamic QR code shown by your lecturer.",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            QrScanFrame(
                isVerifying = scannerState == ScannerState.VERIFYING
            )

            Spacer(modifier = Modifier.height(18.dp))

            when (scannerState) {
                ScannerState.SCANNING -> ScannerHint(
                    title = "Searching for a secure code",
                    subtitle = "Keep the QR code inside the frame."
                )

                ScannerState.VERIFYING -> ScannerHint(
                    title = "Verifying your session",
                    subtitle = "Checking the code with Kikao..."
                )

                ScannerState.ERROR -> ScannerError(
                    message = errorMessage,
                    onTryAgain = onTryAgain
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ScannerSecurityNote()
        }
    }
}

@Composable
private fun ScannerTopBar(
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
                text = "SECURE CHECK-IN",
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun QrScanFrame(
    isVerifying: Boolean
) {
    Box(
        modifier = Modifier.size(252.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerLength = 58.dp.toPx()
            val stroke = 5.dp.toPx()
            val frameColor = if (isVerifying) KikaoColors.Gold else KikaoColors.Teal

            drawCorner(
                start = Offset(0f, cornerLength),
                horizontalEnd = Offset(cornerLength, 0f),
                verticalEnd = Offset(0f, 0f),
                color = frameColor,
                strokeWidth = stroke
            )

            drawCorner(
                start = Offset(size.width - cornerLength, 0f),
                horizontalEnd = Offset(size.width, 0f),
                verticalEnd = Offset(size.width, cornerLength),
                color = frameColor,
                strokeWidth = stroke
            )

            drawCorner(
                start = Offset(0f, size.height - cornerLength),
                horizontalEnd = Offset(0f, size.height),
                verticalEnd = Offset(cornerLength, size.height),
                color = frameColor,
                strokeWidth = stroke
            )

            drawCorner(
                start = Offset(size.width - cornerLength, size.height),
                horizontalEnd = Offset(size.width, size.height),
                verticalEnd = Offset(size.width, size.height - cornerLength),
                color = frameColor,
                strokeWidth = stroke
            )
        }

        if (isVerifying) {
            Surface(
                shape = CircleShape,
                color = KikaoColors.DeepIndigo.copy(alpha = 0.88f)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(62.dp)
                        .padding(13.dp),
                    color = KikaoColors.Gold,
                    strokeWidth = 4.dp
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCorner(
    start: Offset,
    horizontalEnd: Offset,
    verticalEnd: Offset,
    color: Color,
    strokeWidth: Float
) {
    drawLine(
        color = color,
        start = start,
        end = horizontalEnd,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    drawLine(
        color = color,
        start = horizontalEnd,
        end = verticalEnd,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

@Composable
private fun ScannerHint(
    title: String,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScannerError(
    message: String,
    onTryAgain: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onTryAgain,
            colors = ButtonDefaults.buttonColors(
                containerColor = KikaoColors.Gold,
                contentColor = KikaoColors.DeepIndigo
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Try again",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ScannerSecurityNote() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.13f)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(KikaoColors.Teal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "Your attendance is protected by\nsecure session verification.",
                color = Color.White.copy(alpha = 0.80f),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun CameraPermissionContent(
    onAllowCamera: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KikaoColors.DeepIndigo)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera access needed",
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Kikao needs your camera to scan your lecturer's secure attendance QR code.",
            color = Color.White.copy(alpha = 0.76f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAllowCamera,
            colors = ButtonDefaults.buttonColors(
                containerColor = KikaoColors.Gold,
                contentColor = KikaoColors.DeepIndigo
            )
        ) {
            Text(
                text = "Allow camera",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QrCameraPreview(
    onQrDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        BarcodeScanning.getClient(options)
    }

    DisposableEffect(lifecycleOwner, previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = CameraXPreview.Builder()
                .build()
                .also { it.surfaceProvider = previewView.surfaceProvider }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        QrCodeAnalyzer(
                            scanner = scanner,
                            onQrDetected = onQrDetected
                        )
                    )
                }

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
        }

        cameraProviderFuture.addListener(
            listener,
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            cameraExecutor.shutdown()
            scanner.close()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}

private class QrCodeAnalyzer(
    private val scanner: BarcodeScanner,
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val hasScanned = AtomicBoolean(false)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (hasScanned.get()) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val qrValue = barcodes.firstOrNull()?.rawValue

                if (
                    qrValue != null &&
                    hasScanned.compareAndSet(false, true)
                ) {
                    onQrDetected(qrValue)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

@Composable
private fun ScannerPreviewSurface() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF345282),
                        Color(0xFF142343)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(210.dp)) {
            drawCircle(
                color = KikaoColors.Teal.copy(alpha = 0.25f),
                radius = size.width / 2
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QrAttendanceScannerScreenPreview() {
    MaterialTheme {
        QrScannerLayout(
            scannerState = ScannerState.SCANNING,
            errorMessage = "",
            onBackClick = {},
            onTryAgain = {},
            cameraContent = { ScannerPreviewSurface() }
        )
    }
}
