package com.mwema.a2kikao.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.UserProfile
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.SignUpUiState
import com.mwema.a2kikao.ui.viewmodels.SignUpViewModel

private val KikaoIndigo = Color(0xFF243B7A)
private val KikaoDeepIndigo = Color(0xFF172B62)
private val KikaoTeal = Color(0xFF0F9D8A)
private val KikaoGold = Color(0xFFF4B740)
private val KikaoText = Color(0xFF172033)
private val KikaoMutedText = Color(0xFF64748B)

enum class UserRole(val label: String, val description: String) {
    STUDENT("Student", "Track attendance and learning progress"),
    LECTURER("Lecturer", "Manage classes and student attendance"),
    ADMIN("Administrator", "Manage university access and reports")
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onSignUpSuccess: (UserRole) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.STUDENT) }

    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var registrationNumber by rememberSaveable { mutableStateOf("") }
    var employeeNumber by rememberSaveable { mutableStateOf("") }
    var school by rememberSaveable { mutableStateOf("") }
    var department by rememberSaveable { mutableStateOf("") }
    var course by rememberSaveable { mutableStateOf("") }
    var className by rememberSaveable { mutableStateOf("") }
    var yearOfStudy by rememberSaveable { mutableStateOf("") }
    var campus by rememberSaveable { mutableStateOf("") }
    var academicTitle by rememberSaveable { mutableStateOf("") }
    var classesTaught by rememberSaveable { mutableStateOf("") }
    var roleTitle by rememberSaveable { mutableStateOf("") }
    var administrativeUnit by rememberSaveable { mutableStateOf("") }
    var officePhone by rememberSaveable { mutableStateOf("") }
    var inviteCode by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(KikaoDeepIndigo, KikaoIndigo, Color(0xFF31539A))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawKikaoBackground()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Join Kikao",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create a verified university account.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "I am a",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            UserRole.entries.forEach { role ->
                RoleCard(
                    role = role,
                    isSelected = selectedRole == role,
                    onClick = { selectedRole = role }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Account details",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            KikaoTextField(
                label = "Full name",
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Enter your full name"
            )

            KikaoTextField(
                label = "University email",
                value = email,
                onValueChange = { email = it },
                placeholder = "name@university.ac.ke"
            )

            when (selectedRole) {
                UserRole.STUDENT -> {
                    KikaoTextField(
                        label = "Registration number",
                        value = registrationNumber,
                        onValueChange = { registrationNumber = it },
                        placeholder = "e.g. SC211/1234/2025"
                    )
                    KikaoTextField("School / Faculty", school, { school = it }, "e.g. School of Computing")
                    KikaoTextField("Department", department, { department = it }, "e.g. Computer Science")
                    KikaoTextField("Course", course, { course = it }, "e.g. BSc Computer Science")
                    KikaoTextField("Class / Cohort", className, { className = it }, "e.g. CS 2.1")
                    KikaoTextField("Year of study", yearOfStudy, { yearOfStudy = it }, "e.g. Second year")
                    KikaoTextField("Campus", campus, { campus = it }, "e.g. Main Campus")
                }

                UserRole.LECTURER -> {
                    KikaoTextField(
                        label = "Employee number",
                        value = employeeNumber,
                        onValueChange = { employeeNumber = it },
                        placeholder = "Enter your staff number"
                    )
                    KikaoTextField("Academic title", academicTitle, { academicTitle = it }, "e.g. Dr., Mr., Ms.")
                    KikaoTextField("School / Faculty", school, { school = it }, "e.g. School of Computing")
                    KikaoTextField("Department", department, { department = it }, "e.g. Computer Science")
                    KikaoTextField("Campus", campus, { campus = it }, "e.g. Main Campus")
                    KikaoTextField(
                        label = "Classes you teach",
                        value = classesTaught,
                        onValueChange = { classesTaught = it },
                        placeholder = "e.g. CSC 210 — CS 2.1, CSC 310 — CS 3.2",
                        singleLine = false
                    )
                }

                UserRole.ADMIN -> {
                    KikaoTextField(
                        label = "Employee number",
                        value = employeeNumber,
                        onValueChange = { employeeNumber = it },
                        placeholder = "Enter your staff number"
                    )
                    KikaoTextField(
                        label = "Role / position",
                        value = roleTitle,
                        onValueChange = { roleTitle = it },
                        placeholder = "e.g. Dean of Students"
                    )
                    KikaoTextField(
                        label = "Administrative unit",
                        value = administrativeUnit,
                        onValueChange = { administrativeUnit = it },
                        placeholder = "e.g. Academic Affairs"
                    )
                    KikaoTextField("Campus", campus, { campus = it }, "e.g. Main Campus")
                    KikaoTextField(
                        label = "Office phone number",
                        value = officePhone,
                        onValueChange = { officePhone = it },
                        placeholder = "e.g. +254 712 345 678"
                    )
                    KikaoTextField(
                        label = "Administrator invite code",
                        value = inviteCode,
                        onValueChange = { inviteCode = it },
                        placeholder = "Issued by your university"
                    )
                }
            }

            Text(
                text = "Your details will be verified by the university before full access is granted.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            KikaoTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "Create a secure password",
                isPassword = true
            )

            KikaoTextField(
                label = "Confirm password",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Re-enter your password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState is SignUpUiState.Error) {
                Text(
                    text = (uiState as SignUpUiState.Error).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    val profile = UserProfile(
                        fullName = fullName,
                        email = email,
                        role = selectedRole,
                        registrationNumber = registrationNumber,
                        employeeNumber = employeeNumber,
                        school = school,
                        department = department,
                        course = course,
                        className = className,
                        yearOfStudy = yearOfStudy,
                        campus = campus,
                        academicTitle = academicTitle,
                        classesTaught = classesTaught,
                        roleTitle = roleTitle,
                        administrativeUnit = administrativeUnit,
                        officePhone = officePhone
                    )
                    viewModel.signUp(email, password, profile)
                },
                enabled = uiState !is SignUpUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KikaoIndigo,
                    contentColor = Color.White
                )
            ) {
                if (uiState is SignUpUiState.Loading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Create verified account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LaunchedEffect(uiState) {
                if (uiState is SignUpUiState.Success) {
                    onSignUpSuccess(selectedRole)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun RoleCard(
    role: UserRole,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White.copy(alpha = 0.20f) else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, KikaoGold) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = role.label,
                color = if (isSelected) Color.White else KikaoText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = role.description,
                color = if (isSelected) Color.White.copy(alpha = 0.85f) else KikaoMutedText,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun KikaoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.8f)) },
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.5f)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KikaoTeal,
            focusedLabelColor = KikaoTeal,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            cursorColor = KikaoTeal,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

private fun DrawScope.drawKikaoBackground() {
    drawCircle(
        color = KikaoTeal.copy(alpha = 0.22f),
        radius = size.width * 0.55f,
        center = Offset(size.width * 1.05f, size.height * 0.12f)
    )

    drawCircle(
        color = KikaoGold.copy(alpha = 0.14f),
        radius = size.width * 0.48f,
        center = Offset(size.width * -0.12f, size.height * 0.90f)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen()
    }
}
