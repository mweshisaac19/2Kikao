package com.mwema.a2kikao.ui.screens.student


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.AttendanceSupportViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AttendanceRequestType {
    LEAVE,
    DISPUTE
}

data class AttendanceRequest(
    val type: AttendanceRequestType,
    val reason: String,
    val details: String,
    val startDate: String,
    val endDate: String,
    val affectedSessionDate: String,
    val evidenceUri: Uri?,
    val classId: String? = null
)

@Composable
fun AttendanceSupportScreen(
    classId: String? = null,
    initialType: AttendanceRequestType = AttendanceRequestType.LEAVE,
    modifier: Modifier = Modifier,
    viewModel: AttendanceSupportViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onSubmissionComplete: () -> Unit = {}
) {
    var requestType by rememberSaveable { mutableStateOf(initialType) }
    var reason by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var affectedSessionDate by rememberSaveable { mutableStateOf("") }
    var evidenceUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val isSubmitted by viewModel.isSubmitted.collectAsState()

    val scope = rememberCoroutineScope()

    val evidencePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedFile ->
        evidenceUri = selectedFile
    }

    fun submitRequest() {
        val dateIsValid = if (requestType == AttendanceRequestType.LEAVE) {
            startDate.isNotBlank()
        } else {
            affectedSessionDate.isNotBlank()
        }

        if (reason.isBlank() || details.isBlank() || !dateIsValid) {
            errorMessage = "Please complete the required fields before submitting."
            return
        }

        viewModel.submitRequest(
            AttendanceRequest(
                type = requestType,
                reason = reason,
                details = details,
                startDate = startDate,
                endDate = endDate,
                affectedSessionDate = affectedSessionDate,
                evidenceUri = evidenceUri,
                classId = classId
            )
        )
    }

    LaunchedEffect(isSubmitted) {
        if (isSubmitted) {
            delay(1500)
            onSubmissionComplete()
        }
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SupportTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(KikaoColors.Background)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                if (isSubmitted) {
                    SubmissionSuccessContent()
                } else {
                    Text(
                        text = "Attendance support",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Database Systems · CSC 221",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    RequestTypeSwitch(
                        selectedType = requestType,
                        onTypeSelected = {
                            requestType = it
                            errorMessage = ""
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    if (requestType == AttendanceRequestType.LEAVE) {
                        LeaveRequestForm(
                            reason = reason,
                            onReasonChange = { reason = it },
                            startDate = startDate,
                            onStartDateChange = { startDate = it },
                            endDate = endDate,
                            onEndDateChange = { endDate = it },
                            details = details,
                            onDetailsChange = { details = it }
                        )
                    } else {
                        AttendanceDisputeForm(
                            reason = reason,
                            onReasonChange = { reason = it },
                            sessionDate = affectedSessionDate,
                            onSessionDateChange = { affectedSessionDate = it },
                            details = details,
                            onDetailsChange = { details = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EvidenceCard(
                        evidenceUri = evidenceUri,
                        onClick = { evidencePicker.launch("*/*") }
                    )

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFB42318),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = { submitRequest() },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KikaoColors.Indigo,
                            contentColor = Color.White
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (requestType == AttendanceRequestType.LEAVE) {
                                    "Submit leave request"
                                } else {
                                    "Submit attendance dispute"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Your lecturer will review this request and Kikao will notify you of the outcome.",
                        color = KikaoColors.MutedText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SupportTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .clickable(onClick = onBackClick)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "‹  Back",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
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
                text = "STUDENT SUPPORT",
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun RequestTypeSwitch(
    selectedType: AttendanceRequestType,
    onTypeSelected: (AttendanceRequestType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE7EDF7))
            .padding(4.dp)
    ) {
        RequestTypeOption(
            title = "Leave request",
            isSelected = selectedType == AttendanceRequestType.LEAVE,
            onClick = { onTypeSelected(AttendanceRequestType.LEAVE) },
            modifier = Modifier.weight(1f)
        )

        RequestTypeOption(
            title = "Attendance dispute",
            isSelected = selectedType == AttendanceRequestType.DISPUTE,
            onClick = { onTypeSelected(AttendanceRequestType.DISPUTE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RequestTypeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) KikaoColors.Indigo else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else KikaoColors.MutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LeaveRequestForm(
    reason: String,
    onReasonChange: (String) -> Unit,
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    details: String,
    onDetailsChange: (String) -> Unit
) {
    FormHeading(
        title = "Request approved leave",
        description = "Use this for a future absence, illness, or official university activity."
    )

    Spacer(modifier = Modifier.height(16.dp))

    KikaoInput("Reason for leave", reason, onReasonChange, "e.g. Medical appointment")

    KikaoInput("Start date", startDate, onStartDateChange, "e.g. 20 Aug 2026")

    KikaoInput(
        label = "End date (optional)",
        value = endDate,
        onValueChange = onEndDateChange,
        placeholder = "e.g. 21 Aug 2026"
    )

    KikaoInput(
        label = "Additional details",
        value = details,
        onValueChange = onDetailsChange,
        placeholder = "Briefly explain your request",
        singleLine = false
    )
}

@Composable
private fun AttendanceDisputeForm(
    reason: String,
    onReasonChange: (String) -> Unit,
    sessionDate: String,
    onSessionDateChange: (String) -> Unit,
    details: String,
    onDetailsChange: (String) -> Unit
) {
    FormHeading(
        title = "Report an attendance issue",
        description = "Tell us if a valid class check-in was not recorded correctly."
    )

    Spacer(modifier = Modifier.height(16.dp))

    KikaoInput(
        label = "What went wrong?",
        value = reason,
        onValueChange = onReasonChange,
        placeholder = "e.g. QR code stopped refreshing"
    )

    KikaoInput(
        label = "Affected class date",
        value = sessionDate,
        onValueChange = onSessionDateChange,
        placeholder = "e.g. 18 Aug 2026"
    )

    KikaoInput(
        label = "Tell us what happened",
        value = details,
        onValueChange = onDetailsChange,
        placeholder = "Include the time, room and any error you saw",
        singleLine = false
    )
}

@Composable
private fun FormHeading(
    title: String,
    description: String
) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(5.dp))

    Text(
        text = description,
        color = Color.White.copy(alpha = 0.82f),
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
}

@Composable
private fun KikaoInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 4,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KikaoColors.Teal,
            focusedLabelColor = KikaoColors.Teal,
            unfocusedBorderColor = Color(0xFFD9E0EA)
        )
    )
}

@Composable
private fun EvidenceCard(
    evidenceUri: Uri?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (evidenceUri == null) {
                Color.White
            } else {
                KikaoColors.TealLight
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (evidenceUri == null) {
                            Color(0xFFEAF0F8)
                        } else {
                            KikaoColors.Teal
                        }
                    )
                    .padding(11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (evidenceUri == null) "↑" else "✓",
                    color = if (evidenceUri == null) {
                        KikaoColors.Indigo
                    } else {
                        Color.White
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (evidenceUri == null) {
                        "Attach supporting evidence"
                    } else {
                        "Evidence attached"
                    },
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (evidenceUri == null) {
                        "Medical chit, letter, screenshot or photo"
                    } else {
                        evidenceUri.lastPathSegment ?: "Selected file"
                    },
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )
            }

            Text(
                text = if (evidenceUri == null) "Add" else "Change",
                color = KikaoColors.Teal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SubmissionSuccessContent() {
    Spacer(modifier = Modifier.height(95.dp))

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(KikaoColors.TealLight)
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✓",
            color = KikaoColors.Teal,
            fontSize = 56.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }

    Spacer(modifier = Modifier.height(25.dp))

    Text(
        text = "Request submitted",
        color = KikaoColors.Ink,
        fontSize = 27.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Your lecturer will review your request. Returning to class details...",
        color = KikaoColors.MutedText,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AttendanceSupportScreenPreview() {
    MaterialTheme {
        AttendanceSupportScreen()
    }
}