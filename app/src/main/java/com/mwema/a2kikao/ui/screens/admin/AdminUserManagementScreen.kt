package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

data class InstitutionalAdmin(
    val id: String,
    val initials: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String,
    val status: String,
    val lastActive: String,
    val permissions: String,
    val accent: Color
)

private enum class AdminFilter {
    ALL,
    ACTIVE,
    PENDING,
    INACTIVE
}

@Composable
fun AdminUserManagementScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAddAdmin: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAdminSelected: (InstitutionalAdmin) -> Unit = {},
    onEditAdmin: (InstitutionalAdmin) -> Unit = {},
    onDeactivateAdmin: (InstitutionalAdmin) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(AdminFilter.ALL) }

    val admins = remember { demoInstitutionalAdmins() }

    val filteredAdmins = admins.filter { admin ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    admin.name.contains(searchQuery, ignoreCase = true) ||
                    admin.email.contains(searchQuery, ignoreCase = true) ||
                    admin.role.contains(searchQuery, ignoreCase = true) ||
                    admin.department.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            AdminFilter.ALL -> true
            AdminFilter.ACTIVE -> admin.status == "Active"
            AdminFilter.PENDING -> admin.status == "Pending"
            AdminFilter.INACTIVE -> admin.status == "Inactive"
        }

        matchesSearch && matchesFilter
    }

    val activeCount = admins.count { it.status == "Active" }
    val pendingCount = admins.count { it.status == "Pending" }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Admin management",
        screenSubtitle = "Institutional administrators",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7F9FC))
        ) {

            AdminManagementSubHeader(
                onBackClick = onBackClick,
                onAddAdmin = onAddAdmin
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 110.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    ManagementSummaryCard(
                        total = admins.size,
                        active = activeCount,
                        pending = pendingCount
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    AdminSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it }
                    )
                }

                item {
                    AdminFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }

                item {
                    Text(
                        text = "${filteredAdmins.size} administrator${if (filteredAdmins.size == 1) "" else "s"}",
                        color = KikaoColors.MutedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                if (filteredAdmins.isEmpty()) {
                    item {
                        EmptyAdminsState(
                            searchQuery = searchQuery,
                            onAddAdmin = onAddAdmin
                        )
                    }
                } else {
                    items(
                        items = filteredAdmins,
                        key = { it.id }
                    ) { admin ->

                        InstitutionalAdminCard(
                            admin = admin,
                            onClick = { onAdminSelected(admin) },
                            onEdit = { onEditAdmin(admin) },
                            onDeactivate = { onDeactivateAdmin(admin) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminManagementSubHeader(
    onBackClick: () -> Unit,
    onAddAdmin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "‹", color = KikaoColors.Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = "User management", color = KikaoColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "Institutional access control", color = KikaoColors.MutedText, fontSize = 11.sp)
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(KikaoColors.Indigo)
                .clickable(onClick = onAddAdmin),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ManagementSummaryCard(total: Int, active: Int, pending: Int) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "ADMINISTRATIVE ACCESS", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AdminSummaryMetric(total.toString(), "Total")
                AdminSummaryMetric(active.toString(), "Active")
                AdminSummaryMetric(pending.toString(), "Pending")
            }
        }
    }
}

@Composable
private fun AdminSummaryMetric(value: String, label: String) {
    Column {
        Text(text = value, color = KikaoColors.Gold, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
private fun AdminSearchField(value: String, onValueChange: (String) -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⌕", color = KikaoColors.MutedText, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = value, onValueChange = onValueChange, modifier = Modifier.weight(1f), singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = KikaoColors.Ink, fontSize = 13.sp),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) Text(text = "Search administrators...", color = KikaoColors.MutedText, fontSize = 13.sp)
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun AdminFilterRow(selectedFilter: AdminFilter, onFilterSelected: (AdminFilter) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AdminFilter.entries.forEach { filter ->
            val selected = filter == selectedFilter
            Box(modifier = Modifier.clip(RoundedCornerShape(11.dp)).background(if (selected) KikaoColors.Teal else Color.White).clickable { onFilterSelected(filter) }.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(text = filter.name.lowercase().replaceFirstChar { it.uppercase() }, color = if (selected) Color.White else KikaoColors.MutedText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InstitutionalAdminCard(admin: InstitutionalAdmin, onClick: () -> Unit, onEdit: () -> Unit, onDeactivate: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(admin.accent), contentAlignment = Alignment.Center) {
                    Text(text = admin.initials, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = admin.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = admin.email, color = KikaoColors.MutedText, fontSize = 11.sp)
                }
                StatusBadge(admin.status)
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFEDF1F6))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminActionButton("Edit", Modifier.weight(1f), onClick = onEdit)
                AdminActionButton(if (admin.status == "Inactive") "Activate" else "Deactivate", Modifier.weight(1f), admin.status != "Inactive", onDeactivate)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = when (status) {
        "Active" -> KikaoColors.Teal
        "Pending" -> Color(0xFF9A6700)
        else -> KikaoColors.MutedText
    }
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(text = status.uppercase(), color = color, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AdminActionButton(text: String, modifier: Modifier = Modifier, destructive: Boolean = false, onClick: () -> Unit) {
    Box(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(if (destructive) Color(0xFFFFF0F1) else Color(0xFFEAF0F8)).clickable(onClick = onClick).padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
        Text(text = text, color = if (destructive) Color(0xFFB42318) else KikaoColors.Indigo, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyAdminsState(searchQuery: String, onAddAdmin: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "No administrators found", color = KikaoColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onAddAdmin, colors = ButtonDefaults.buttonColors(containerColor = KikaoColors.Indigo)) {
            Text("Add Administrator")
        }
    }
}

private fun demoInstitutionalAdmins(): List<InstitutionalAdmin> {
    return listOf(
        InstitutionalAdmin("a1", "JM", "Jane Mwende", "jane@university.ac.ke", "Super Admin", "Admin", "Active", "2m ago", "Full", KikaoColors.Indigo),
        InstitutionalAdmin("a2", "DK", "David Kariuki", "david@university.ac.ke", "Academic Admin", "Academics", "Active", "18m ago", "Courses", KikaoColors.Teal)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AdminUserManagementPreview() {
    MaterialTheme {
        AdminUserManagementScreen()
    }
}
