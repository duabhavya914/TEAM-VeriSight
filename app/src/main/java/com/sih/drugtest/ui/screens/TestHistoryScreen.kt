package com.sih.drugtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.sih.drugtest.ui.components.AppBottomNavigation

data class TestHistoryRecord(
    val testName: String,
    val dateTime: String,
    val result: String
)

@Composable
fun TestHistoryScreen(
    customRecords: List<TestHistoryRecord> = emptyList(),
    onBackClick: () -> Unit,
    onRecordClick: (TestHistoryRecord) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTestsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    val defaultRecords = remember {
        listOf(
            TestHistoryRecord(
                testName = "Marquis Test",
                dateTime = "16 Apr 2025, 14:32",
                result = "Positive"
            ),
            TestHistoryRecord(
                testName = "Cobalt Thiocyanate",
                dateTime = "14 Apr 2025, 11:20",
                result = "Negative"
            ),
            TestHistoryRecord(
                testName = "Duquenois-Levine",
                dateTime = "12 Apr 2025, 09:15",
                result = "Inconclusive"
            ),
            TestHistoryRecord(
                testName = "Mandelin Test",
                dateTime = "10 Apr 2025, 16:40",
                result = "Negative"
            ),
            TestHistoryRecord(
                testName = "Marquis Test",
                dateTime = "08 Apr 2025, 13:05",
                result = "Positive"
            )
        )
    }

    val records = if (customRecords.isNotEmpty()) customRecords + defaultRecords else defaultRecords

    val filteredRecords = records.filter { record ->

        val matchesSearch =
            record.testName.contains(
                searchQuery,
                ignoreCase = true
            )

        val matchesFilter =
            selectedFilter == "All" ||
                    record.result == selectedFilter

        matchesSearch && matchesFilter
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                selectedTab = "history",
                onHomeClick = onHomeClick,
                onTestsClick = onTestsClick,
                onHistoryClick = onHistoryClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "Test History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text("Search records...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // Filter buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    text = "All",
                    selected = selectedFilter == "All",
                    onClick = {
                        selectedFilter = "All"
                    }
                )

                FilterChip(
                    text = "Positive",
                    selected = selectedFilter == "Positive",
                    onClick = {
                        selectedFilter = "Positive"
                    }
                )

                FilterChip(
                    text = "Negative",
                    selected = selectedFilter == "Negative",
                    onClick = {
                        selectedFilter = "Negative"
                    }
                )

                FilterChip(
                    text = "Inconclusive",
                    selected = selectedFilter == "Inconclusive",
                    onClick = {
                        selectedFilter = "Inconclusive"
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // History records
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                items(filteredRecords) { record ->

                    TestHistoryItem(
                        record = record,
                        onClick = {
                            onRecordClick(record)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {

        Text(
            text = text,
            color = if (selected) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
fun TestHistoryItem(
    record: TestHistoryRecord,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Test bottle icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    when (record.testName) {
                        "Marquis Test" ->
                            Color(0xFF8B1E1E)

                        "Cobalt Thiocyanate" ->
                            Color(0xFF1976D2)

                        "Duquenois-Levine" ->
                            Color(0xFF6A1B9A)

                        "Mandelin Test" ->
                            Color(0xFF008F5A)

                        else ->
                            MaterialTheme.colorScheme.primary
                    }
                )
        )

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        // Test information
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = record.testName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = record.dateTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Result badge
        ResultBadge(
            result = record.result
        )
    }
}


@Composable
private fun ResultBadge(
    result: String
) {

    val backgroundColor = when (result) {

        "Positive" ->
            Color(0xFFFFE5E8)

        "Negative" ->
            Color(0xFFE2F6EC)

        "Inconclusive" ->
            Color(0xFFFFF0D6)

        else ->
            MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when (result) {

        "Positive" ->
            Color(0xFFD32F2F)

        "Negative" ->
            Color(0xFF16834B)

        "Inconclusive" ->
            Color(0xFFB77900)

        else ->
            MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(
                horizontal = 10.dp,
                vertical = 6.dp
            )
    ) {

        Text(
            text = result,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
