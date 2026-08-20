package com.mwema.a2kikao.ui.screens.auth


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.data.UserProfile
import com.mwema.a2kikao.ui.viewmodels.LoginUiState
import com.mwema.a2kikao.ui.viewmodels.LoginViewModel
import com.mwema.a2kikao.ui.viewmodels.ResetUiState

private val LoginIndigo = Color(0xFF243B7A)
private val LoginDeepIndigo = Color(0xFF172B62)
private val LoginTeal = Color(0xFF0F9D8A)
private val LoginGold = Color(0xFFF4B740)
private val LoginInk = Color(0xFF172033)
private val LoginMuted = Color(0xFF64748B)

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: (UserProfile) -> Unit = {},
    onCreateAccount: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val resetState by viewModel.resetState.collectAsState()
    
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        ForgotPasswordDialog(
            onDismiss = { 
                showResetDialog = false
                viewModel.clearResetState()
            },
            onSendResetEmail = { resetEmail ->
                viewModel.sendPasswordResetEmail(resetEmail)
            },
            resetState = resetState
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(LoginDeepIndigo, LoginIndigo, Color(0xFF31539A))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBackgroundShapes()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KikaoLogo()

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome back",
                        color = LoginInk,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sign in to continue your learning journey.",
                        color = LoginMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("University email") },
                        placeholder = { Text("name@university.ac.ke") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = loginTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        placeholder = { Text("Enter your password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp),
                        colors = loginTextFieldColors()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showResetDialog = true }) {
                            Text(
                                text = "Forgot password?",
                                color = LoginTeal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState is LoginUiState.Error) {
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.signIn(email, password) },
                        enabled = uiState !is LoginUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginIndigo,
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sign in",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LaunchedEffect(uiState) {
                        if (uiState is LoginUiState.Success) {
                            onLoginSuccess((uiState as LoginUiState.Success).profile)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Use your verified university email.",
                        color = LoginMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.sp
                )

                TextButton(onClick = onCreateAccount) {
                    Text(
                        text = "Create account",
                        color = LoginGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSendResetEmail: (String) -> Unit,
    resetState: ResetUiState
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Password",
                fontWeight = FontWeight.Bold,
                color = LoginInk
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter your university email to receive a password reset link.",
                    fontSize = 14.sp,
                    color = LoginMuted
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("University email") },
                    placeholder = { Text("name@university.ac.ke") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = loginTextFieldColors(),
                    enabled = resetState !is ResetUiState.Loading
                )
                
                val state = resetState
                if (state is ResetUiState.Error) {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                if (resetState is ResetUiState.Success) {
                    Text(
                        text = "Reset email sent! Check your inbox.",
                        color = LoginTeal,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSendResetEmail(email) },
                enabled = email.isNotBlank() && resetState !is ResetUiState.Loading && resetState !is ResetUiState.Success,
                colors = ButtonDefaults.buttonColors(containerColor = LoginIndigo)
            ) {
                if (resetState is ResetUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Send Link")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = LoginMuted)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
private fun KikaoLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .background(LoginTeal),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "k",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Kikao",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "VERIFIED LEARNING",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LoginTeal,
    focusedLabelColor = LoginTeal,
    unfocusedBorderColor = Color(0xFFD9E0EA)
)

private fun DrawScope.drawBackgroundShapes() {
    drawCircle(
        color = LoginTeal.copy(alpha = 0.22f),
        radius = size.width * 0.55f,
        center = androidx.compose.ui.geometry.Offset(size.width * 1.05f, size.height * 0.12f)
    )

    drawCircle(
        color = LoginGold.copy(alpha = 0.14f),
        radius = size.width * 0.48f,
        center = androidx.compose.ui.geometry.Offset(size.width * -0.12f, size.height * 0.90f)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen()
    }
}