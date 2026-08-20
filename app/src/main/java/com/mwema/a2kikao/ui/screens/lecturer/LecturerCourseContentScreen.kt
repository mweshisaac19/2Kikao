package com.mwema.a2kikao.ui.screens.lecturer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private data class CourseMaterial(
    val id: String,
    val title: String,
    val type: String, // "PDF", "Link", "Video"
    val size: String,
    val dateUploaded: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerCourseContentScreen(
    courseId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onUpload: () -> Unit = {}
) {
    val materials = remember {
        mutableStateListOf(
            CourseMaterial("m1", "Lecture 1: Introduction to DB", "PDF", "2.4 MB", "10 Aug"),
            CourseMaterial("m2", "Database Schema Design Guide", "PDF", "1.1 MB", "12 Aug"),
            CourseMaterial("m3", "SQL Normalization Tutorial", "Video", "15 mins", "15 Aug"),
            CourseMaterial("m4", "Project Requirements", "PDF", "800 KB", "Yesterday")
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = KikaoColors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Course Content", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onUpload,
                containerColor = KikaoColors.Teal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = "Upload material")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Stats Row
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ContentStatCard(label = "Total Files", value = "${materials.size}", modifier = Modifier.weight(1f))
                ContentStatCard(label = "Storage Used", value = "45 MB", modifier = Modifier.weight(1f))
            }
            
            Text(
                text = "Course Materials",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = KikaoColors.Ink
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(materials) { material ->
                    MaterialRow(
                        material = material,
                        onDelete = { materials.remove(material) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentStatCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, color = KikaoColors.MutedText, fontSize = 10.sp)
            Text(text = value, color = KikaoColors.Indigo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MaterialRow(material: CourseMaterial, onDelete: () -> Unit) {
    val icon = when(material.type) {
        "PDF" -> Icons.Default.PictureAsPdf
        "Video" -> Icons.Default.PlayCircle
        else -> Icons.Default.InsertDriveFile
    }
    
    val accent = when(material.type) {
        "PDF" -> Color(0xFFDC3545)
        "Video" -> Color(0xFF0D6EFD)
        else -> KikaoColors.Teal
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = material.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KikaoColors.Ink)
                Text(text = "${material.size} · Uploaded ${material.dateUploaded}", fontSize = 11.sp, color = KikaoColors.MutedText)
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LecturerCourseContentPreview() {
    MaterialTheme {
        LecturerCourseContentScreen(courseId = "csc221")
    }
}
