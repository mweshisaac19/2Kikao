package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

private enum class AssetFilter {
    ALL,
    ROOMS,
    LABS,
    EQUIPMENT
}

data class CampusAsset(
    val id: String,
    val name: String,
    val code: String,
    val building: String,
    val floor: String,
    val type: String,
    val capacity: Int,
    val status: String,
    val assignedCourse: String? = null,
    val equipment: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCampusMapAssetRegistryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAssetSelected: (CampusAsset) -> Unit = {},
    onAddAsset: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(AssetFilter.ALL) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedAsset by remember { mutableStateOf<CampusAsset?>(null) }

    val assets = remember {
        listOf(
            CampusAsset(
                id = "room_101",
                name = "Lecture Hall A",
                code = "LH-A01",
                building = "Science Complex",
                floor = "Ground Floor",
                type = "Lecture Room",
                capacity = 180,
                status = "Available",
                assignedCourse = "CSC 210",
                equipment = listOf("Projector", "Smart Board", "Wi-Fi", "PA System")
            ),
            CampusAsset(
                id = "lab_201",
                name = "Computer Lab 1",
                code = "LAB-C01",
                building = "ICT Centre",
                floor = "1st Floor",
                type = "Computer Lab",
                capacity = 60,
                status = "In use",
                assignedCourse = "CSC 221",
                equipment = listOf("60 PCs", "Projector", "Wi-Fi", "UPS")
            )
        )
    }

    val filteredAssets = assets.filter { asset ->
        val matchesSearch =
            searchQuery.isBlank() ||
                    asset.name.contains(searchQuery, ignoreCase = true) ||
                    asset.code.contains(searchQuery, ignoreCase = true) ||
                    asset.building.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            AssetFilter.ALL -> true
            AssetFilter.ROOMS ->
                asset.type.contains("Room", ignoreCase = true) ||
                        asset.type.contains("Theatre", ignoreCase = true)
            AssetFilter.LABS ->
                asset.type.contains("Lab", ignoreCase = true)
            AssetFilter.EQUIPMENT ->
                asset.equipment.isNotEmpty()
        }

        matchesSearch && matchesFilter
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.HOME,
        screenTitle = "Campus & Assets",
        screenSubtitle = "Manage physical infrastructure",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text("‹ Back to command center", color = KikaoColors.Teal, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    CampusSummaryHeader()
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        placeholder = {
                            Text(
                                text = "Search rooms or buildings",
                                color = KikaoColors.MutedText
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = KikaoColors.Teal
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                items(
                    items = filteredAssets,
                    key = { it.id }
                ) { asset ->
                    CampusAssetCard(
                        asset = asset,
                        onClick = {
                            selectedAsset = asset
                            onAssetSelected(asset)
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }

            FloatingActionButton(
                onClick = onAddAsset,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(22.dp),
                containerColor = KikaoColors.Gold,
                contentColor = KikaoColors.DeepIndigo
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add asset")
            }
        }
    }

    selectedAsset?.let { asset ->
        AssetDetailsDialog(
            asset = asset,
            onDismiss = { selectedAsset = null }
        )
    }
}

@Composable
private fun CampusSummaryHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = KikaoColors.Indigo)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "FACILITIES OVERVIEW", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CampusMetric("126", "Rooms")
                CampusMetric("18", "Labs")
                CampusMetric("92%", "Uptime")
            }
        }
    }
}

@Composable
private fun CampusMetric(value: String, label: String) {
    Column {
        Text(text = value, color = KikaoColors.Gold, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
    }
}

@Composable
private fun CampusAssetCard(
    asset: CampusAsset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(KikaoColors.Indigo.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = KikaoColors.Indigo)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = asset.name, color = KikaoColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "${asset.code} · ${asset.building}", color = KikaoColors.MutedText, fontSize = 11.sp)
            }
            Text(text = asset.status, color = if(asset.status == "Available") KikaoColors.Teal else Color(0xFFB42318), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AssetDetailsDialog(asset: CampusAsset, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = asset.name, fontWeight = FontWeight.Bold) },
        text = { Text(text = "Building: ${asset.building}\nFloor: ${asset.floor}\nCapacity: ${asset.capacity} students") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminCampusMapAssetRegistryPreview() {
    MaterialTheme {
        AdminCampusMapAssetRegistryScreen()
    }
}
