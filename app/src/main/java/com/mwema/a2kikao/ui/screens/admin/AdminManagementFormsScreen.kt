package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.mwema.a2kikao.ui.viewmodels.AdminManagementViewModel

private enum class ManagementForm {
    NONE,
    STUDENT,
    COURSE,
    DEPARTMENT
}

@Composable
fun AdminManagementFormsScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminManagementViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStudentSaved: () -> Unit = {},
    onCourseSaved: () -> Unit = {},
    onDepartmentSaved: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var activeForm by rememberSaveable {
        mutableStateOf(ManagementForm.NONE)
    }

    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            when (activeForm) {
                ManagementForm.STUDENT -> onStudentSaved()
                ManagementForm.COURSE -> onCourseSaved()
                ManagementForm.DEPARTMENT -> onDepartmentSaved()
                else -> {}
            }
            viewModel.resetSuccess()
            activeForm = ManagementForm.NONE
        }
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.ACADEMICS,
        screenTitle = when (activeForm) {
            ManagementForm.NONE -> "Management workspace"
            ManagementForm.STUDENT -> "Add student"
            ManagementForm.COURSE -> "Course details"
            ManagementForm.DEPARTMENT -> "Add department"
            else -> ""
        },
        screenSubtitle = "Institution records and configuration",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding()) {
            
            // Sub-navigation breadcrumb / back action
            if (activeForm != ManagementForm.NONE) {
                TextButton(
                    onClick = { activeForm = ManagementForm.NONE },
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                ) {
                    Text("‹ Back to workspace", color = KikaoColors.TealLight, fontWeight = FontWeight.Bold)
                }
            } else {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                ) {
                    Text("‹ Back to dashboard", color = KikaoColors.TealLight, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (activeForm) {
                    ManagementForm.NONE -> {
                        ManagementFormsHome(
                            onAddStudent = { activeForm = ManagementForm.STUDENT },
                            onEditCourse = { activeForm = ManagementForm.COURSE },
                            onAddDepartment = { activeForm = ManagementForm.DEPARTMENT }
                        )
                    }

                    ManagementForm.STUDENT -> {
                        AddStudentForm(
                            isSaving = isSaving,
                            onCancel = { activeForm = ManagementForm.NONE },
                            onSave = { name, reg, email ->
                                viewModel.addStudent(name, reg, email)
                            }
                        )
                    }

                    ManagementForm.COURSE -> {
                        EditCourseForm(
                            onCancel = { activeForm = ManagementForm.NONE },
                            onSave = {
                                onCourseSaved()
                                activeForm = ManagementForm.NONE
                            }
                        )
                    }

                    ManagementForm.DEPARTMENT -> {
                        AddDepartmentForm(
                            isSaving = isSaving,
                            onCancel = { activeForm = ManagementForm.NONE },
                            onSave = { name, head ->
                                viewModel.addDepartment(name, head)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ManagementFormsHome(
    onAddStudent: () -> Unit,
    onEditCourse: () -> Unit,
    onAddDepartment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 40.dp)
    ) {

        ManagementOverviewCard()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "QUICK ACTIONS",
            color = Color.White.copy(alpha = 0.90f),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.9.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        ManagementOptionCard(
            icon = "♙",
            title = "Add student",
            subtitle = "Create a student profile and assign academic identity.",
            accent = KikaoColors.Teal,
            actionText = "Create",
            onClick = onAddStudent
        )

        Spacer(modifier = Modifier.height(12.dp))

        ManagementOptionCard(
            icon = "▣",
            title = "Edit course details",
            subtitle = "Update course info and department settings.",
            accent = KikaoColors.Indigo,
            actionText = "Edit",
            onClick = onEditCourse
        )

        Spacer(modifier = Modifier.height(12.dp))

        ManagementOptionCard(
            icon = "⌂",
            title = "Add department",
            subtitle = "Create a new department and assign an admin.",
            accent = KikaoColors.Gold,
            actionText = "Create",
            onClick = onAddDepartment
        )

        Spacer(modifier = Modifier.height(25.dp))

        RecentManagementCard()
    }
}

@Composable
private fun ManagementOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ADMIN WORKSPACE",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(
                        text = "Maintain records",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(15.dp)).background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Changes are reflected across all Kikao institutional analytics.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ManagementOptionCard(
    icon: String,
    title: String,
    subtitle: String,
    accent: Color,
    actionText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, color = accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(text = actionText, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun RecentManagementCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(text = "Recent activity", color = KikaoColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            ActivityRow("♙", "Student profile updated", "Today · 10:42 AM", KikaoColors.Teal)
            ActivityRow("▣", "Course details edited", "Yesterday", KikaoColors.Indigo, false)
        }
    }
}

@Composable
private fun ActivityRow(
    icon: String,
    title: String,
    detail: String,
    accent: Color,
    showDivider: Boolean = true
) {
    Column {
        Row(modifier = Modifier.padding(vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha = 0.11f)), contentAlignment = Alignment.Center) {
                Text(text = icon, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = KikaoColors.Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = detail, color = KikaoColors.MutedText, fontSize = 10.sp)
            }
            Text(text = "✓", color = KikaoColors.Teal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        if (showDivider) HorizontalDivider(color = Color(0xFFEDF1F6))
    }
}

@Composable
private fun AddStudentForm(isSaving: Boolean, onCancel: () -> Unit, onSave: (String, String, String) -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var regNo by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    ManagementFormContainer(title = "Student identity", subtitle = "Enter official academic info.") {
        FormTextField(fullName, { fullName = it }, "Full name", "e.g. Amani Mwangi")
        FormTextField(regNo, { regNo = it }, "Reg Number", "e.g. SC211/1234/2025")
        FormTextField(email, { email = it }, "Email", "student@university.ac.ke")
        Spacer(modifier = Modifier.height(12.dp))
        FormActions(
            saveLabel = "Create student",
            isSaving = isSaving,
            onCancel = onCancel,
            onSave = { onSave(fullName, regNo, email) }
        )
    }
}

@Composable
private fun EditCourseForm(onCancel: () -> Unit, onSave: () -> Unit) {
    var code by rememberSaveable { mutableStateOf("CSC 221") }
    var name by rememberSaveable { mutableStateOf("Database Systems") }

    ManagementFormContainer(title = "Course details", subtitle = "Update institutional course info.") {
        FormTextField(code, { code = it }, "Course code", "e.g. CSC 221")
        FormTextField(name, { name = it }, "Course name", "e.g. Database Systems")
        Spacer(modifier = Modifier.height(12.dp))
        FormActions(saveLabel = "Save changes", onCancel = onCancel, onSave = onSave)
    }
}

@Composable
private fun AddDepartmentForm(isSaving: Boolean, onCancel: () -> Unit, onSave: (String, String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var head by rememberSaveable { mutableStateOf("") }

    ManagementFormContainer(title = "Department setup", subtitle = "Create new academic unit.") {
        FormTextField(name, { name = it }, "Department name", "e.g. Computer Science")
        FormTextField(head, { head = it }, "HOD", "Lecturer name")
        Spacer(modifier = Modifier.height(12.dp))
        FormActions(
            saveLabel = "Create department",
            isSaving = isSaving,
            onCancel = onCancel,
            onSave = { onSave(name, head) }
        )
    }
}

@Composable
private fun ManagementFormContainer(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 110.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = title, color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = KikaoColors.MutedText, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(20.dp))
                content()
            }
        }
    }
}

@Composable
private fun FormTextField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, minLines: Int = 1) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(text = label, color = KikaoColors.Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = KikaoColors.MutedText, fontSize = 12.sp) },
            shape = RoundedCornerShape(14.dp), minLines = minLines, singleLine = minLines == 1
        )
    }
}

@Composable
private fun FormActions(saveLabel: String, isSaving: Boolean = false, onCancel: () -> Unit, onSave: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TextButton(onClick = onCancel, modifier = Modifier.weight(1f), enabled = !isSaving) {
            Text("Cancel", color = KikaoColors.MutedText, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1.5f),
            enabled = !isSaving,
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(saveLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminManagementFormsPreview() {
    MaterialTheme {
        AdminManagementFormsScreen()
    }
}
