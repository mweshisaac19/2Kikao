package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.LecturerNotificationViewModel

private enum class NotificationType(
    val label: String,
    val description: String,
    val icon: String
) {
    ANNOUNCEMENT(
        "Announcement",
        "General information for your students",
        "!"
    ),
    ACADEMIC(
        "Academic",
        "Grades, assessments or academic updates",
        "A"
    ),
    ATTENDANCE(
        "Attendance",
        "Attendance and class participation updates",
        "✓"
    ),
    REMINDER(
        "Reminder",
        "Remind students about something important",
        "⏰"
    )
}

@Composable
fun LecturerNotificationPostingScreen(
    modifier: Modifier = Modifier,
    viewModel: LecturerNotificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onPostComplete: () -> Unit = {},
    onTabSelected: (LecturerTab) -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(NotificationType.ANNOUNCEMENT) }
    var isUrgent by remember { mutableStateOf(false) }

    val isPosting by viewModel.isPosting.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()

    LaunchedEffect(postSuccess) {
        if (postSuccess) {
            onPostComplete()
            viewModel.resetSuccess()
        }
    }

    KikaoLecturerScaffold(
        modifier = modifier,
        selectedTab = LecturerTab.HOME,
        screenTitle = "Post update",
        screenSubtitle = "Send a notification to your class",
        onTabSelected = onTabSelected
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            
            TextButton(onClick = onBack) {
                Text("‹ Cancel and go back", color = KikaoColors.MutedText)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Notification Type Selection
            TypeSelector(
                selected = selectedType,
                onSelected = { selectedType = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Form Fields
            NotificationForm(
                title = title,
                onTitleChange = { title = it },
                message = message,
                onMessageChange = { message = it },
                isUrgent = isUrgent,
                onUrgentChange = { isUrgent = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Post Button
            Button(
                onClick = {
                    viewModel.postNotification(
                        title = title,
                        message = message,
                        type = selectedType.name.lowercase(),
                        classId = null // Could be passed as a param if specific to a class
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo),
                enabled = title.isNotBlank() && message.isNotBlank() && !isPosting
            ) {
                if (isPosting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Post Notification", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TypeSelector(
    selected: NotificationType,
    onSelected: (NotificationType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Text("Notification type", fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(KikaoColors.TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selected.icon, color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(selected.label, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                    Text(selected.description, fontSize = 11.sp, color = KikaoColors.MutedText)
                }
            }
        }
        
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            NotificationType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.label) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationForm(
    title: String,
    onTitleChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    isUrgent: Boolean,
    onUrgentChange: (Boolean) -> Unit
) {
    Column {
        Text("Title", fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. CAT 1 Results Posted") },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KikaoColors.Teal)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text("Message", fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            placeholder = { Text("Detailed information for your students...") },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KikaoColors.Teal)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isUrgent,
                onCheckedChange = onUrgentChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KikaoColors.Teal)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Mark as important", fontWeight = FontWeight.Bold, color = KikaoColors.Ink, fontSize = 14.sp)
                Text("Will highlight this post for students", color = KikaoColors.MutedText, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotificationPostingPreview() {
    MaterialTheme {
        LecturerNotificationPostingScreen()
    }
}
