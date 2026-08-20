package com.mwema.a2kikao.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors
import com.mwema.a2kikao.ui.viewmodels.FacilityViewModel

data class Facility(
    val id: String,
    val name: String,
    val type: String,
    val building: String,
    val floor: String
)

@Composable
fun CampusFacilityNavigator(
    modifier: Modifier = Modifier,
    viewModel: FacilityViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val realFacilities by viewModel.facilities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val displayFacilities = if (realFacilities.isNotEmpty()) realFacilities else listOf(
        Facility("1", "Lecture Hall A", "Hall", "Science Complex", "Ground"),
        Facility("2", "Computer Lab 3", "Lab", "ICT Centre", "1st Floor"),
        Facility("3", "Library Wing B", "Library", "Main Library", "2nd Floor")
    )

    val filteredFacilities = displayFacilities.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.building.contains(searchQuery, ignoreCase = true)
    }

    KikaoStudentScaffold(
        selectedTab = StudentTab.HOME,
        screenTitle = "Facility Navigator",
        screenSubtitle = "Find your way around campus",
        onBackClick = onBackClick,
        showScanButton = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search rooms or buildings") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFacilities) { facility ->
                    FacilityCard(facility)
                }
            }
        }
    }
}

@Composable
private fun FacilityCard(facility: Facility) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(KikaoColors.Indigo.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = KikaoColors.Indigo)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(facility.name, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text("${facility.building} · ${facility.floor}", fontSize = 11.sp, color = KikaoColors.MutedText)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CampusFacilityNavigatorPreview() {
    MaterialTheme {
        CampusFacilityNavigator()
    }
}
