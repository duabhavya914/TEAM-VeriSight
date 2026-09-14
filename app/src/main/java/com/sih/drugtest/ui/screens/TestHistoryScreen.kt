package com.sih.drugtest.ui.screens

import android.util.Log
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sih.drugtest.SupabaseClient
import com.sih.drugtest.ui.components.AppBottomNavigation
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// =============================================================
// SUPABASE DATABASE ROW
// =============================================================

@Serializable
data class TestRecordRow(

    val id: String,

    @SerialName("test_id")
    val testId: String,

    @SerialName("captured_at")
    val capturedAt: String,

    val result: String? = null,

    @SerialName("officer_id")
    val officerId: String? = null,

    @SerialName("image_storage_path")
    val imageStoragePath: String? = null,

    @SerialName("image_sha256")
    val imageSha256: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null
)


// =============================================================
// HISTORY DISPLAY MODEL
// =============================================================

data class TestHistoryRecord(

    val id: String,

    val testId: String,

    val testName: String,

    val dateTime: String,

    val result: String,

    val officerId: String? = null,

    val imageStoragePath: String? = null,

    val imageSha256: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null
)


// =============================================================
// HISTORY SCREEN
// =============================================================

@Composable
fun TestHistoryScreen(

    officerId: String,

    onBackClick: () -> Unit,

    onRecordClick: (TestHistoryRecord) -> Unit = {},

    onHomeClick: () -> Unit = {},

    onTestsClick: () -> Unit = {},

    onProfileClick: () -> Unit = {}
) {

    // =========================================================
    // SEARCH STATE
    // =========================================================

    var searchQuery by remember {
        mutableStateOf("")
    }


    // =========================================================
    // FILTER STATE
    // =========================================================

    var selectedFilter by remember {
        mutableStateOf("All")
    }


    // =========================================================
    // HISTORY DATA
    // =========================================================

    var records by remember {
        mutableStateOf<List<TestHistoryRecord>>(
            emptyList()
        )
    }


    var isLoading by remember {
        mutableStateOf(true)
    }


    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // =========================================================
    // FETCH REAL TEST RECORDS FOR CURRENT OFFICER
    // =========================================================

    LaunchedEffect(officerId) {

        isLoading = true

        errorMessage = null

        records = emptyList()


        // -----------------------------------------------------
        // OFFICER ID MUST EXIST
        // -----------------------------------------------------

        if (officerId.isBlank()) {

            Log.w(
                "HistoryFetch",
                "Officer ID is blank. History fetch cancelled."
            )


            errorMessage =
                "Officer profile is not available."


            isLoading = false


            return@LaunchedEffect
        }


        try {

            Log.d(
                "HistoryFetch",
                "Loading history for officer: $officerId"
            )


            // -------------------------------------------------
            // FETCH ONLY CURRENT OFFICER RECORDS
            // -------------------------------------------------

            val databaseRows =
                SupabaseClient.client
                    .from("test_records")
                    .select {

                        filter {

                            eq(
                                "officer_id",
                                officerId
                            )
                        }
                    }
                    .decodeList<TestRecordRow>()


            Log.d(
                "HistoryFetch",
                "Fetched ${databaseRows.size} records for $officerId"
            )


            databaseRows.forEach { row ->

                Log.d(
                    "HistoryFetch",
                    "Record: ${row.id} | ${row.testId} | ${row.result} | ${row.capturedAt}"
                )
            }


            // -------------------------------------------------
            // CONVERT DATABASE ROWS TO UI RECORDS
            // -------------------------------------------------

            records =
                databaseRows
                    .sortedByDescending {
                        it.capturedAt
                    }
                    .map { row ->

                        TestHistoryRecord(

                            id =
                                row.id,

                            testId =
                                row.testId,

                            testName =
                                getTestDisplayName(
                                    row.testId
                                ),

                            dateTime =
                                formatCapturedAt(
                                    row.capturedAt
                                ),

                            result =
                                formatResult(
                                    row.result
                                ),

                            officerId =
                                row.officerId,

                            imageStoragePath =
                                row.imageStoragePath,

                            imageSha256 =
                                row.imageSha256,

                            latitude =
                                row.latitude,

                            longitude =
                                row.longitude
                        )
                    }


        } catch (e: Exception) {

            Log.e(
                "HistoryFetch",
                "Could not load test history",
                e
            )


            errorMessage =
                e.message
                    ?: "Could not load test history."


        } finally {

            isLoading = false
        }
    }


    // =========================================================
    // SEARCH + FILTER
    // =========================================================

    val filteredRecords =
        records.filter { record ->

            val matchesSearch =

                record.testName.contains(
                    searchQuery,
                    ignoreCase = true
                ) ||

                        record.testId.contains(
                            searchQuery,
                            ignoreCase = true
                        )


            val matchesFilter =

                selectedFilter == "All" ||

                        record.result.equals(
                            selectedFilter,
                            ignoreCase = true
                        )


            matchesSearch && matchesFilter
        }


    // =========================================================
    // SCREEN
    // =========================================================

    Scaffold(

        containerColor =
            MaterialTheme.colorScheme.background,

        bottomBar = {

            AppBottomNavigation(

                selectedTab =
                    "history",

                onHomeClick =
                    onHomeClick,

                onTestsClick =
                    onTestsClick,

                onHistoryClick = {
                    // Already on History
                },

                onProfileClick =
                    onProfileClick
            )
        }

    ) { innerPadding ->


        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(
                        innerPadding
                    )
                    .statusBarsPadding()
        ) {


            // =================================================
            // HEADER
            // =================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            end = 16.dp,
                            top = 8.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                IconButton(
                    onClick =
                        onBackClick
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Back"
                    )
                }


                Text(

                    text =
                        "Test History",

                    style =
                        MaterialTheme.typography.headlineSmall,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            // =================================================
            // SEARCH
            // =================================================

            OutlinedTextField(

                value =
                    searchQuery,

                onValueChange = {

                    searchQuery = it
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                placeholder = {

                    Text(
                        text =
                            "Search by test..."
                    )
                },

                leadingIcon = {

                    Icon(

                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Search"
                    )
                },

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // FILTERS
            // =================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {


                HistoryFilterChip(

                    text =
                        "All",

                    selected =
                        selectedFilter == "All",

                    onClick = {

                        selectedFilter =
                            "All"
                    }
                )


                HistoryFilterChip(

                    text =
                        "Positive",

                    selected =
                        selectedFilter == "Positive",

                    onClick = {

                        selectedFilter =
                            "Positive"
                    }
                )


                HistoryFilterChip(

                    text =
                        "Inconclusive",

                    selected =
                        selectedFilter == "Inconclusive",

                    onClick = {

                        selectedFilter =
                            "Inconclusive"
                    }
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // LOADING
            // =================================================

            if (isLoading) {


                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(
                                1f
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {


                    CircularProgressIndicator()
                }


            } else if (errorMessage != null) {


                // =============================================
                // ERROR
                // =============================================

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(
                                1f
                            )
                            .padding(
                                24.dp
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {


                    Text(

                        text =
                            "Could not load test history.\n\n$errorMessage",

                        color =
                            MaterialTheme.colorScheme.error
                    )
                }


            } else if (filteredRecords.isEmpty()) {


                // =============================================
                // EMPTY HISTORY
                // =============================================

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(
                                1f
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {


                    Text(

                        text =

                            if (
                                searchQuery.isNotBlank() ||
                                selectedFilter != "All"
                            ) {

                                "No matching test records."

                            } else {

                                "No completed tests yet."
                            },

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }


            } else {


                // =============================================
                // REAL SUPABASE HISTORY
                // =============================================

                LazyColumn(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(
                                1f
                            )
                ) {


                    items(

                        items =
                            filteredRecords,

                        key = {
                            it.id
                        }

                    ) { record ->


                        TestHistoryItem(

                            record =
                                record,

                            onClick = {

                                onRecordClick(
                                    record
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


// =============================================================
// DISPLAY TEST NAME
// =============================================================

private fun getTestDisplayName(
    testId: String
): String {


    return when (testId) {


        "HER-001" ->
            "Marquis Test"


        "CAN-001" ->
            "Duquenois-Levine"


        "COC-001" ->
            "Cobalt Thiocyanate"


        "AMP-001" ->
            "Amphetamine Test"


        "BAR-001" ->
            "Barbiturate Test"


        else ->
            testId
    }
}


// =============================================================
// RESULT FORMATTER
// =============================================================

private fun formatResult(
    result: String?
): String {


    return when {


        result.equals(
            "PRESUMPTIVE_POSITIVE",
            ignoreCase = true
        ) ->

            "Positive"


        result.equals(
            "POSITIVE",
            ignoreCase = true
        ) ->

            "Positive"


        result.equals(
            "INCONCLUSIVE",
            ignoreCase = true
        ) ->

            "Inconclusive"


        result.equals(
            "NEGATIVE",
            ignoreCase = true
        ) ->

            "Negative"


        else ->

            result
                ?.replace(
                    "_",
                    " "
                )
                ?.lowercase()
                ?.replaceFirstChar {
                    it.uppercase()
                }
                ?: "Unknown"
    }
}


// =============================================================
// TIMESTAMP FORMATTER
// UTC DATABASE TIME -> INDIA TIME
// =============================================================

private fun formatCapturedAt(
    capturedAt: String
): String {


    return try {


        // -----------------------------------------------------
        // PARSE TIMESTAMP STORED BY SUPABASE / PYTHON
        //
        // Example:
        // 2026-09-14T05:00:00+00:00
        // -----------------------------------------------------

        val storedTime =
            OffsetDateTime.parse(
                capturedAt
            )


        // -----------------------------------------------------
        // CONVERT SAME INSTANT TO INDIA STANDARD TIME
        // -----------------------------------------------------

        val indiaTime =
            storedTime.atZoneSameInstant(
                ZoneId.of(
                    "Asia/Kolkata"
                )
            )


        // -----------------------------------------------------
        // USER FRIENDLY DISPLAY
        //
        // Example:
        // 14 Sep 2026, 10:30 AM
        // -----------------------------------------------------

        indiaTime.format(
            DateTimeFormatter.ofPattern(
                "dd MMM yyyy, hh:mm a"
            )
        )


    } catch (
        exception: Exception
    ) {


        Log.e(
            "HistoryTime",
            "Could not format timestamp: $capturedAt",
            exception
        )


        capturedAt
    }
}


// =============================================================
// FILTER CHIP
// =============================================================

@Composable
private fun HistoryFilterChip(

    text: String,

    selected: Boolean,

    onClick: () -> Unit
) {


    Box(

        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(
                        20.dp
                    )
                )
                .background(

                    if (selected) {

                        MaterialTheme
                            .colorScheme
                            .primary

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
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

            text =
                text,

            color =

                if (selected) {

                    Color.White

                } else {

                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                },

            style =
                MaterialTheme.typography.labelLarge
        )
    }
}


// =============================================================
// HISTORY ITEM
// =============================================================

@Composable
fun TestHistoryItem(

    record: TestHistoryRecord,

    onClick: () -> Unit
) {


    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {

                    onClick()
                }
                .padding(
                    horizontal = 20.dp,
                    vertical = 14.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        // =====================================================
        // TEST ICON
        // =====================================================

        Box(

            modifier =
                Modifier
                    .size(
                        34.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            7.dp
                        )
                    )
                    .background(

                        when (record.testId) {


                            "HER-001" ->
                                Color(0xFF8B1E1E)


                            "COC-001" ->
                                Color(0xFF1976D2)


                            "CAN-001" ->
                                Color(0xFF6A1B9A)


                            "AMP-001" ->
                                Color(0xFF008F5A)


                            "BAR-001" ->
                                Color(0xFF795548)


                            else ->
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        }
                    )
        )


        Spacer(
            modifier =
                Modifier.size(
                    16.dp
                )
        )


        // =====================================================
        // TEST INFORMATION
        // =====================================================

        Column(

            modifier =
                Modifier.weight(
                    1f
                )
        ) {


            Text(

                text =
                    record.testName,

                style =
                    MaterialTheme.typography.bodyLarge,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Text(

                text =
                    record.testId,

                style =
                    MaterialTheme.typography.labelMedium,

                color =
                    MaterialTheme.colorScheme.primary
            )


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Text(

                text =
                    record.dateTime,

                style =
                    MaterialTheme.typography.bodyMedium,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }


        // =====================================================
        // RESULT BADGE
        // =====================================================

        ResultBadge(

            result =
                record.result
        )
    }
}


// =============================================================
// RESULT BADGE
// =============================================================

@Composable
fun ResultBadge(
    result: String
) {


    val backgroundColor =

        when (result) {


            "Positive" ->
                Color(0xFFFFE5E8)


            "Inconclusive" ->
                Color(0xFFFFF0D6)


            "Negative" ->
                Color(0xFFE0F4E7)


            else ->
                MaterialTheme
                    .colorScheme
                    .surfaceVariant
        }


    val textColor =

        when (result) {


            "Positive" ->
                Color(0xFFD32F2F)


            "Inconclusive" ->
                Color(0xFFB77900)


            "Negative" ->
                Color(0xFF237A45)


            else ->
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        }


    Box(

        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(
                        8.dp
                    )
                )
                .background(
                    backgroundColor
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
    ) {


        Text(

            text =
                result,

            color =
                textColor,

            style =
                MaterialTheme.typography.labelMedium,

            fontWeight =
                FontWeight.Bold
        )
    }
}