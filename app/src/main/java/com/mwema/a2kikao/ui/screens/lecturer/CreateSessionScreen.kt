package com.mwema.a2kikao.ui.screens.lecturer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.mwema.a2kikao.data.CourseClass
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.CreateSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSessionScreen(
    modifier: Modifier = Modifier,
    initialCourseCode: String? = null,
    viewModel: CreateSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onSessionCreated: (String, String, String, String, String, Int) -> Unit = { _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val lecturerClasses by viewModel.lecturerClasses.collectAsState()
    
    var courseCode by rememberSaveable { mutableStateOf(initialCourseCode ?: "") }
    var topic by rememberSaveable { mutableStateOf("") }
    var room by rememberSaveable { mutableStateOf("") }
    var duration by rememberSaveable { mutableStateOf("2 hrs") }
    var startTime by rememberSaveable { mutableStateOf("10:00 AM") }
    
    val locationState by viewModel.currentLocation.collectAsState()
    val isCreating by viewModel.isCreating.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val createdSessionId by viewModel.createdSessionId.collectAsState()

    var showCourseDropdown by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            fetchCurrentLocation(context, viewModel)
        }
    }

    LaunchedEffect(createSuccess) {
        if (createSuccess && createdSessionId != null) {
            onSessionCreated(createdSessionId!!, courseCode, topic, room, duration, 120)
        }
    }

    // Auto-fill room/time if a class is selected
    LaunchedEffect(courseCode, lecturerClasses) {
        val selectedClass = lecturerClasses.find { it.code == courseCode }
        if (selectedClass != null) {
            room = selectedClass.room
            startTime = selectedClass.time.substringBefore("-").trim()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Session", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Session Details", color = KikaoColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box {
                        OutlinedTextField(
                            value = courseCode,
                            onValueChange = { courseCode = it },
                            label = { Text("Course Code") },
                            placeholder = { Text("e.g. CSC 221") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { showCourseDropdown = true }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Select Course")
                                }
                            }
                        )
                        
                        DropdownMenu(
                            expanded = showCourseDropdown,
                            onDismissRequest = { showCourseDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            if (lecturerClasses.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No classes found", color = KikaoColors.MutedText) },
                                    onClick = { showCourseDropdown = false }
                                )
                            } else {
                                lecturerClasses.forEach { cls ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(cls.code, fontWeight = FontWeight.Bold)
                                                Text(cls.name, fontSize = 11.sp, color = KikaoColors.MutedText)
                                            }
                                        },
                                        onClick = {
                                            courseCode = cls.code
                                            showCourseDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic / Lesson Name") },
                        placeholder = { Text("e.g. Intro to Firebase") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Room / Hall") },
                        placeholder = { Text("e.g. Lab 3") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start Time") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Location Verification Section
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (locationState != null) KikaoColors.TealLight else Color(0xFFFFF7ED)
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (locationState != null) KikaoColors.Teal else Color(0xFFF97316)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (locationState != null) "Location Secured" else "Location Required",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (locationState != null) KikaoColors.Teal else Color(0xFF9A3412)
                        )
                        Text(
                            text = if (locationState != null) 
                                "Lat: ${String.format("%.4f", locationState?.latitude)}, Lng: ${String.format("%.4f", locationState?.longitude)}"
                                else "Used to verify students are in class.",
                            fontSize = 12.sp,
                            color = KikaoColors.MutedText
                        )
                    }
                    
                    if (locationState == null) {
                        TextButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    fetchCurrentLocation(context, viewModel)
                                } else {
                                    permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                                }
                            }
                        ) {
                            Text("Verify", fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Button(
                onClick = {
                    viewModel.createSession(courseCode, topic, room, startTime, duration)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = courseCode.isNotBlank() && locationState != null && !isCreating,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Create and Open for Attendance", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun fetchCurrentLocation(context: Context, viewModel: CreateSessionViewModel) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.setLocation(location.latitude, location.longitude)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateSessionScreenPreview() {
    MaterialTheme {
        CreateSessionScreen()
    }
}
