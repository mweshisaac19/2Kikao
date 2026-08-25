package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSaveComplete: () -> Unit = {}
) {
    var firstName by rememberSaveable { mutableStateOf("Prof. Amani") }
    var lastName by rememberSaveable { mutableStateOf("Mwangi") }
    var email by rememberSaveable { mutableStateOf("amani.mwangi@university.ac.ke") }
    var department by rememberSaveable { mutableStateOf("Computer Science") }
    var faculty by rememberSaveable { mutableStateOf("Faculty of Science & IT") }
    
    var showSavedMessage by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Photo Section
                ProfileIdentityHeader(name = "$firstName $lastName", faculty = faculty)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Fields
                SectionTitle("Personal Information")
                ProfileField(label = "First Name", value = firstName, onValueChange = { firstName = it })
                ProfileField(label = "Last Name", value = lastName, onValueChange = { lastName = it })
                ProfileField(label = "Email Address", value = email, onValueChange = { email = it })
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle("Professional Details")
                ProfileField(label = "Department", value = department, onValueChange = { department = it })
                ProfileField(label = "Faculty", value = faculty, onValueChange = { faculty = it })
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Button(
                    onClick = {
                        showSavedMessage = true
                        // Simulate network call
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            if (showSavedMessage) {
                SavedProfileMessage(onDismiss = { 
                    showSavedMessage = false
                    onSaveComplete()
                })
            }
        }
    }
}

@Composable
private fun ProfileIdentityHeader(name: String, faculty: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(80.dp).clip(CircleShape).background(KikaoColors.Indigo),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "PM", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(text = name, color = KikaoColors.Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = faculty, color = KikaoColors.MutedText, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Change Photo", color = KikaoColors.Teal, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title, 
        color = KikaoColors.Indigo, 
        fontSize = 14.sp, 
        fontWeight = FontWeight.ExtraBold, 
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun ProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = label, color = KikaoColors.MutedText, fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KikaoColors.Teal)
        )
    }
}

@Composable
private fun SavedProfileMessage(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Profile Saved", fontWeight = FontWeight.Bold) },
        text = { Text("Your academic profile has been successfully updated.") },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Teal)) {
                Text("Continue")
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfilePreview() {
    MaterialTheme {
        EditProfileScreen()
    }
}
