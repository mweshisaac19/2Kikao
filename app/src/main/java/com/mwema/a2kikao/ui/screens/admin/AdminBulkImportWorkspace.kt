package com.mwema.a2kikao.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwema.a2kikao.ui.theme.KikaoColors

private enum class ImportDataset {
    STUDENTS,
    LECTURERS
}

private enum class ImportState {
    EMPTY,
    READY,
    VALIDATING,
    VALIDATED,
    IMPORTED
}

private data class ImportRecord(
    val name: String,
    val identifier: String,
    val department: String,
    val status: String,
    val valid: Boolean
)

@Composable
fun AdminBulkImportWorkspace(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onChooseFile: () -> Unit = {},
    onDownloadTemplate: (String) -> Unit = {},
    onImportComplete: () -> Unit = {},
    onTabSelected: (AdminTab) -> Unit = {}
) {
    var selectedDataset by remember {
        mutableStateOf(ImportDataset.STUDENTS)
    }

    var importState by remember {
        mutableStateOf(ImportState.EMPTY)
    }

    var fileName by remember {
        mutableStateOf("")
    }

    var records by remember {
        mutableStateOf<List<ImportRecord>>(emptyList())
    }

    KikaoAdminScaffold(
        modifier = modifier,
        selectedTab = AdminTab.PROFILE,
        screenTitle = "Bulk import",
        screenSubtitle = "Import institutional records securely",
        onNotificationClick = onNotificationClick,
        onProfileClick = onProfileClick,
        onTabSelected = onTabSelected
    ) { innerPadding: PaddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 110.dp)
        ) {

            BulkImportHero()

            Spacer(modifier = Modifier.height(22.dp))

            ImportProgress(
                state = importState
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("What are you importing?")

            Spacer(modifier = Modifier.height(10.dp))

            DatasetSelector(
                selectedDataset = selectedDataset,
                onSelect = {
                    selectedDataset = it
                    importState = ImportState.EMPTY
                    fileName = ""
                    records = emptyList()
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle("Upload CSV")

            Spacer(modifier = Modifier.height(10.dp))

            UploadWorkspaceCard(
                fileName = fileName,
                state = importState,
                onChooseFile = {
                    onChooseFile()
                    fileName = if (selectedDataset == ImportDataset.STUDENTS) {
                        "students_2026.csv"
                    } else {
                        "lecturers_2026.csv"
                    }
                    importState = ImportState.READY
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TemplateCard(
                dataset = selectedDataset,
                onDownload = {
                    onDownloadTemplate(datasetName(selectedDataset))
                }
            )

            if (importState == ImportState.READY ||
                importState == ImportState.VALIDATED ||
                importState == ImportState.IMPORTED
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("Import preview")

                Spacer(modifier = Modifier.height(10.dp))

                if (records.isEmpty()) {
                    records = demoRecords(selectedDataset)
                }

                ImportSummaryCard(
                    records = records
                )

                Spacer(modifier = Modifier.height(12.dp))

                ImportPreviewTable(
                    records = records
                )
            }

            if (importState == ImportState.VALIDATED ||
                importState == ImportState.IMPORTED
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                ImportValidationCard(
                    records = records
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            when (importState) {

                ImportState.EMPTY -> {
                    ImportInstructionCard()
                }

                ImportState.READY -> {
                    Button(
                        onClick = {
                            importState = ImportState.VALIDATED
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KikaoColors.Indigo
                        )
                    ) {
                        Text(
                            text = "Validate CSV",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                ImportState.VALIDATING -> {
                    ValidationProgressCard()
                }

                ImportState.VALIDATED -> {

                    Button(
                        onClick = {
                            importState = ImportState.IMPORTED
                            onImportComplete()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KikaoColors.Teal
                        )
                    ) {
                        Text(
                            text = "Import ${records.size} records",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Records will be added to the institutional database.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = KikaoColors.MutedText,
                        fontSize = 10.sp
                    )
                }

                ImportState.IMPORTED -> {
                    ImportCompleteCard(
                        importedCount = records.count { it.valid }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel and return",
                    color = KikaoColors.MutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BulkImportHero() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.Indigo
        )
    ) {
        Column(
            modifier = Modifier.padding(21.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(KikaoColors.Teal),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⇧",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = "DATA OPERATIONS",
                        color = KikaoColors.Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Bulk import workspace",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add hundreds of students or lecturers in minutes using a structured CSV file.",
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ImportProgress(
    state: ImportState
) {
    val currentStep = when (state) {
        ImportState.EMPTY -> 1
        ImportState.READY -> 2
        ImportState.VALIDATING -> 3
        ImportState.VALIDATED -> 3
        ImportState.IMPORTED -> 4
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ProgressStep(
            number = "1",
            label = "Select",
            active = currentStep >= 1
        )

        ProgressLine()

        ProgressStep(
            number = "2",
            label = "Upload",
            active = currentStep >= 2
        )

        ProgressLine()

        ProgressStep(
            number = "3",
            label = "Validate",
            active = currentStep >= 3
        )

        ProgressLine()

        ProgressStep(
            number = "4",
            label = "Import",
            active = currentStep >= 4
        )
    }
}

@Composable
private fun ProgressStep(
    number: String,
    label: String,
    active: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(31.dp)
                .clip(CircleShape)
                .background(
                    if (active) {
                        KikaoColors.Indigo
                    } else {
                        Color(0xFFE4E8EF)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = if (active) {
                    Color.White
                } else {
                    KikaoColors.MutedText
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (active) {
                KikaoColors.Ink
            } else {
                KikaoColors.MutedText
            },
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RowScope.ProgressLine() {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .background(Color(0xFFDCE2EA))
    )
}

@Composable
private fun DatasetSelector(
    selectedDataset: ImportDataset,
    onSelect: (ImportDataset) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        DatasetCard(
            modifier = Modifier.weight(1f),
            title = "Students",
            subtitle = "Student records",
            icon = "S",
            selected = selectedDataset == ImportDataset.STUDENTS,
            onClick = {
                onSelect(ImportDataset.STUDENTS)
            }
        )

        DatasetCard(
            modifier = Modifier.weight(1f),
            title = "Lecturers",
            subtitle = "Faculty records",
            icon = "L",
            selected = selectedDataset == ImportDataset.LECTURERS,
            onClick = {
                onSelect(ImportDataset.LECTURERS)
            }
        )
    }
}

@Composable
private fun DatasetCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                KikaoColors.Indigo
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 0.dp else 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) {
                            KikaoColors.Teal
                        } else {
                            Color(0xFFEAF0F8)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = if (selected) {
                        Color.White
                    } else {
                        KikaoColors.Indigo
                    },
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(11.dp))

            Text(
                text = title,
                color = if (selected) {
                    Color.White
                } else {
                    KikaoColors.Ink
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                color = if (selected) {
                    Color.White.copy(alpha = 0.68f)
                } else {
                    KikaoColors.MutedText
                },
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun UploadWorkspaceCard(
    fileName: String,
    state: ImportState,
    onChooseFile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onChooseFile),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        if (fileName.isBlank()) {
                            Color(0xFFEAF0F8)
                        } else {
                            KikaoColors.TealLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (fileName.isBlank()) "↑" else "✓",
                    color = if (fileName.isBlank()) {
                        KikaoColors.Indigo
                    } else {
                        KikaoColors.Teal
                    },
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (fileName.isBlank()) {

                Text(
                    text = "Choose a CSV file",
                    color = KikaoColors.Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tap here to browse your device",
                    color = KikaoColors.MutedText,
                    fontSize = 11.sp
                )

            } else {

                Text(
                    text = fileName,
                    color = KikaoColors.Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "CSV file ready for validation",
                    color = KikaoColors.Teal,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onChooseFile,
                    shape = RoundedCornerShape(11.dp)
                ) {
                    Text(
                        text = "Replace file",
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    dataset: ImportDataset,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E8)
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KikaoColors.Gold),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↓",
                    color = KikaoColors.DeepIndigo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Need the correct format?",
                    color = KikaoColors.Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Download the ${datasetName(dataset)} CSV template.",
                    color = KikaoColors.MutedText,
                    fontSize = 10.sp
                )
            }

            TextButton(
                onClick = onDownload
            ) {
                Text(
                    text = "Download",
                    color = Color(0xFF9A6700),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ImportSummaryCard(
    records: List<ImportRecord>
) {
    val valid = records.count { it.valid }
    val invalid = records.size - valid

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {

        ImportStatCard(
            modifier = Modifier.weight(1f),
            value = records.size.toString(),
            label = "Records",
            color = KikaoColors.Indigo
        )

        ImportStatCard(
            modifier = Modifier.weight(1f),
            value = valid.toString(),
            label = "Valid",
            color = KikaoColors.Teal
        )

        ImportStatCard(
            modifier = Modifier.weight(1f),
            value = invalid.toString(),
            label = "Errors",
            color = Color(0xFFB42318)
        )
    }
}

@Composable
private fun ImportStatCard(
    modifier: Modifier,
    value: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(13.dp)
        ) {

            Text(
                text = value,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = label,
                color = KikaoColors.MutedText,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ImportPreviewTable(
    records: List<ImportRecord>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {

                TableHeader("NAME", 120.dp)
                TableHeader("ID", 105.dp)
                TableHeader("DEPARTMENT", 120.dp)
                TableHeader("STATUS", 70.dp)
            }

            Spacer(modifier = Modifier.height(9.dp))

            records.take(5).forEach { record ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFEDF1F6))
                )

                Spacer(modifier = Modifier.height(9.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TableCell(
                        text = record.name,
                        width = 120.dp
                    )

                    TableCell(
                        text = record.identifier,
                        width = 105.dp
                    )

                    TableCell(
                        text = record.department,
                        width = 120.dp
                    )

                    Text(
                        text = if (record.valid) "VALID" else "ERROR",
                        color = if (record.valid) {
                            KikaoColors.Teal
                        } else {
                            Color(0xFFB42318)
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))
            }
        }
    }
}

@Composable
private fun TableHeader(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = text,
        color = KikaoColors.MutedText,
        fontSize = 9.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = text,
        color = KikaoColors.Ink,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun ImportValidationCard(
    records: List<ImportRecord>
) {
    val valid = records.count { it.valid }
    val invalid = records.size - valid

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (invalid == 0) {
                KikaoColors.TealLight
            } else {
                Color(0xFFFFEAEC)
            }
        )
    ) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (invalid == 0) {
                            KikaoColors.Teal
                        } else {
                            Color(0xFFB42318)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (invalid == 0) "✓" else "!",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (invalid == 0) {
                        "CSV passed validation"
                    } else {
                        "Review $invalid invalid records"
                    },
                    color = KikaoColors.Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (invalid == 0) {
                        "$valid records are ready to be imported into Kikao."
                    } else {
                        "Invalid records will not be imported."
                    },
                    color = KikaoColors.MutedText,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ImportInstructionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF4F6FA)
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "Before importing",
                color = KikaoColors.Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            InstructionRow(
                number = "1",
                text = "Download the official Kikao CSV template."
            )

            InstructionRow(
                number = "2",
                text = "Fill in the required institutional information."
            )

            InstructionRow(
                number = "3",
                text = "Upload the completed CSV and validate it."
            )

            InstructionRow(
                number = "4",
                text = "Review the preview before confirming the import."
            )
        }
    }
}

@Composable
private fun InstructionRow(
    number: String,
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(KikaoColors.Indigo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = text,
            color = KikaoColors.MutedText,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun ValidationProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            Text(
                text = "Validating your CSV...",
                color = KikaoColors.Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8ECF2))
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(KikaoColors.Teal)
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Checking required fields, duplicate records and identifiers.",
                color = KikaoColors.MutedText,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ImportCompleteCard(
    importedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = KikaoColors.TealLight
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(KikaoColors.Teal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Import completed",
                color = KikaoColors.Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "$importedCount records have been successfully added to Kikao.",
                color = KikaoColors.MutedText,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun datasetName(
    dataset: ImportDataset
): String {
    return when (dataset) {
        ImportDataset.STUDENTS -> "student"
        ImportDataset.LECTURERS -> "lecturer"
    }
}

private fun demoRecords(
    dataset: ImportDataset
): List<ImportRecord> {
    return if (dataset == ImportDataset.STUDENTS) {
        listOf(
            ImportRecord(
                "Amani Mwangi",
                "SC211/1234/2025",
                "Computer Science",
                "Valid",
                true
            ),
            ImportRecord(
                "Brian Otieno",
                "SC211/1241/2025",
                "Computer Science",
                "Valid",
                true
            )
        )
    } else {
        listOf(
            ImportRecord(
                "Dr. Jane Wambui",
                "LEC/1042",
                "Computer Science",
                "Valid",
                true
            ),
            ImportRecord(
                "Prof. Mark Otieno",
                "LEC/1081",
                "Information Technology",
                "Valid",
                true
            )
        )
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text.uppercase(),
        color = KikaoColors.MutedText,
        fontSize = 10.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.9.sp
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun AdminBulkImportWorkspacePreview() {
    MaterialTheme {
        AdminBulkImportWorkspace()
    }
}
