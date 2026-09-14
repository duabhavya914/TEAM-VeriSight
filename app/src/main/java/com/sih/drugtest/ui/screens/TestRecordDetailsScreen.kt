package com.sih.drugtest.ui.screens

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sih.drugtest.SupabaseClient
import com.sih.drugtest.ui.components.AppBottomNavigation
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// =============================================================
// COLOURS
// =============================================================

private val DetailsBackground =
    Color(0xFF001B36)

private val DetailsSurface =
    Color(0xFF062B4B)

private val DetailsSurfaceLight =
    Color(0xFF0A365B)

private val DetailsCyan =
    Color(0xFF18DDEB)

private val DetailsIvory =
    Color(0xFFF7F2E7)

private val DetailsMuted =
    Color(0xFF9DB2C5)

private val DetailsBorder =
    Color(0xFF155078)

private val DetailsGreen =
    Color(0xFF58E8A5)

private val DetailsAmber =
    Color(0xFFFFC857)


// =============================================================
// COMPLETE TEST_RECORDS ROW
// =============================================================

@Serializable
data class TestRecordDetailsRow(

    val id: String,

    @SerialName("test_id")
    val testId: String,

    @SerialName("protocol_id")
    val protocolId: String? = null,

    @SerialName("protocol_version")
    val protocolVersion: String? = null,

    @SerialName("officer_id")
    val officerId: String? = null,

    @SerialName("captured_at")
    val capturedAt: String,

    val latitude: Double? = null,

    val longitude: Double? = null,

    @SerialName("image_storage_path")
    val imageStoragePath: String? = null,

    @SerialName("image_sha256")
    val imageSha256: String? = null,

    @SerialName("image_size_bytes")
    val imageSizeBytes: Long? = null,

    @SerialName("image_mime_type")
    val imageMimeType: String? = null,

    @SerialName("observed_r")
    val observedR: Double? = null,

    @SerialName("observed_g")
    val observedG: Double? = null,

    @SerialName("observed_b")
    val observedB: Double? = null,

    @SerialName("expected_r")
    val expectedR: Double? = null,

    @SerialName("expected_g")
    val expectedG: Double? = null,

    @SerialName("expected_b")
    val expectedB: Double? = null,

    @SerialName("rgb_distance")
    val rgbDistance: Double? = null,

    @SerialName("rgb_tolerance")
    val rgbTolerance: Double? = null,

    @SerialName("reference_card_detected")
    val referenceCardDetected: Boolean? = null,

    @SerialName("calibration_applied")
    val calibrationApplied: Boolean? = null,

    val result: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("analysis_completed_at")
    val analysisCompletedAt: String? = null
)


// =============================================================
// SCREEN
// =============================================================

@Composable
fun TestRecordDetailsScreen(

    recordId: String,

    onBackClick: () -> Unit,

    onHomeClick: () -> Unit = {},

    onTestsClick: () -> Unit = {},

    onHistoryClick: () -> Unit = {},

    onProfileClick: () -> Unit = {}
) {

    var record by remember {
        mutableStateOf<TestRecordDetailsRow?>(null)
    }

    var imageBytes by remember {
        mutableStateOf<ByteArray?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var imageLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var imageError by remember {
        mutableStateOf<String?>(null)
    }

    var showFullImage by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // FETCH COMPLETE RECORD
    // =========================================================

    LaunchedEffect(recordId) {

        isLoading = true
        errorMessage = null
        imageError = null
        record = null
        imageBytes = null

        try {

            Log.d(
                "RecordDetails",
                "Loading record: $recordId"
            )

            val databaseRecord =
                SupabaseClient.client
                    .from("test_records")
                    .select {

                        filter {

                            eq(
                                "id",
                                recordId
                            )
                        }
                    }
                    .decodeSingle<TestRecordDetailsRow>()


            record =
                databaseRecord


            Log.d(
                "RecordDetails",
                "Record loaded: ${databaseRecord.id}"
            )


            // =================================================
            // DOWNLOAD ORIGINAL IMAGE
            // =================================================

            val storagePath =
                databaseRecord.imageStoragePath


            if (!storagePath.isNullOrBlank()) {

                imageLoading = true

                try {

                    Log.d(
                        "RecordDetails",
                        "Downloading evidence: $storagePath"
                    )

                    val downloadedBytes =
                        SupabaseClient.client
                            .storage
                            .from(
                                "evidence-images"
                            )
                            .downloadAuthenticated(
                                storagePath
                            )

                    imageBytes =
                        downloadedBytes


                    Log.d(
                        "RecordDetails",
                        "Evidence downloaded: ${downloadedBytes.size} bytes"
                    )

                } catch (e: Exception) {

                    Log.e(
                        "RecordDetails",
                        "Evidence image download failed",
                        e
                    )

                    imageError =
                        e.message
                            ?: "Could not load evidence image."

                } finally {

                    imageLoading = false
                }
            }

        } catch (e: Exception) {

            Log.e(
                "RecordDetails",
                "Could not load record",
                e
            )

            errorMessage =
                e.message
                    ?: "Could not load test record."

        } finally {

            isLoading = false
        }
    }


    // =========================================================
    // FULL IMAGE
    // =========================================================

    val currentImageBytes =
        imageBytes


    if (
        showFullImage &&
        currentImageBytes != null
    ) {

        FullEvidenceImageDialog(

            imageBytes =
                currentImageBytes,

            onDismiss = {

                showFullImage =
                    false
            }
        )
    }


    // =========================================================
    // MAIN SCREEN
    // =========================================================

    Scaffold(

        containerColor =
            DetailsBackground,

        bottomBar = {

            AppBottomNavigation(

                selectedTab =
                    "history",

                onHomeClick =
                    onHomeClick,

                onTestsClick =
                    onTestsClick,

                onHistoryClick =
                    onHistoryClick,

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
                        DetailsBackground
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
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Surface(

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        DetailsSurface,

                    border =
                        BorderStroke(
                            1.dp,
                            DetailsBorder
                        )
                ) {


                    IconButton(
                        onClick =
                            onBackClick
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back",

                            tint =
                                DetailsIvory
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            14.dp
                        )
                )


                Column {


                    Text(

                        text =
                            "Test Record Details",

                        color =
                            DetailsIvory,

                        fontSize =
                            24.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Text(

                        text =
                            "VERIFIED FIELD EVIDENCE",

                        color =
                            DetailsCyan,

                        fontSize =
                            9.sp,

                        letterSpacing =
                            1.8.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }


            // =================================================
            // CONTENT STATES
            // =================================================

            when {


                isLoading -> {


                    Box(

                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center
                    ) {


                        Column(

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {


                            CircularProgressIndicator(
                                color =
                                    DetailsCyan
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        14.dp
                                    )
                            )


                            Text(

                                text =
                                    "Loading secure evidence…",

                                color =
                                    DetailsMuted
                            )
                        }
                    }
                }


                errorMessage != null -> {


                    Box(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    24.dp
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {


                        DetailsCard {


                            Text(

                                text =
                                    "Unable to load test record",

                                color =
                                    DetailsIvory,

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize =
                                    18.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        8.dp
                                    )
                            )


                            Text(

                                text =
                                    errorMessage
                                        ?: "Unknown error",

                                color =
                                    DetailsMuted
                            )
                        }
                    }
                }


                record != null -> {


                    val currentRecord =
                        record!!


                    Column(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(
                                    rememberScrollState()
                                )
                                .padding(
                                    horizontal = 16.dp
                                )
                                .padding(
                                    bottom = 24.dp
                                )
                    ) {


                        SummaryCard(

                            record =
                                currentRecord,

                            imageBytes =
                                imageBytes,

                            imageLoading =
                                imageLoading
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        EvidenceImageCard(

                            record =
                                currentRecord,

                            imageBytes =
                                imageBytes,

                            imageLoading =
                                imageLoading,

                            imageError =
                                imageError,

                            onViewImage = {

                                if (imageBytes != null) {

                                    showFullImage =
                                        true
                                }
                            }
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        // =================================================
                        // BASIC INFORMATION
                        // =================================================

                        SectionCard(

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Default.Description,

                                    contentDescription =
                                        null,

                                    tint =
                                        DetailsCyan
                                )
                            },

                            title =
                                "Basic Test Information"
                        ) {


                            DetailPairRow(

                                label1 =
                                    "Record ID",

                                value1 =
                                    currentRecord.id,

                                label2 =
                                    "Protocol ID",

                                value2 =
                                    currentRecord.protocolId
                                        ?: "Not available"
                            )


                            DetailsDivider()


                            DetailPairRow(

                                label1 =
                                    "Test ID",

                                value1 =
                                    currentRecord.testId,

                                label2 =
                                    "Protocol Version",

                                value2 =
                                    currentRecord.protocolVersion
                                        ?: "Not available"
                            )


                            DetailsDivider()


                            DetailRow(

                                label =
                                    "Officer ID",

                                value =
                                    currentRecord.officerId
                                        ?: "Not available"
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        // =================================================
                        // LOCATION
                        // =================================================

                        SectionCard(

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Default.LocationOn,

                                    contentDescription =
                                        null,

                                    tint =
                                        DetailsCyan
                                )
                            },

                            title =
                                "Location"
                        ) {


                            DetailPairRow(

                                label1 =
                                    "Latitude",

                                value1 =
                                    currentRecord.latitude
                                        ?.toString()
                                        ?: "Not available",

                                label2 =
                                    "Longitude",

                                value2 =
                                    currentRecord.longitude
                                        ?.toString()
                                        ?: "Not available"
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        // =================================================
                        // COLOUR ANALYSIS
                        // =================================================

                        SectionCard(

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Default.Palette,

                                    contentDescription =
                                        null,

                                    tint =
                                        DetailsCyan
                                )
                            },

                            title =
                                "Colour Analysis"
                        ) {


                            ColourValuesRow(
                                record =
                                    currentRecord
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        12.dp
                                    )
                            )


                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        8.dp
                                    )
                            ) {


                                MetricBox(

                                    modifier =
                                        Modifier.weight(
                                            1f
                                        ),

                                    title =
                                        "RGB Distance",

                                    value =
                                        formatNumber(
                                            currentRecord.rgbDistance
                                        )
                                )


                                MetricBox(

                                    modifier =
                                        Modifier.weight(
                                            1f
                                        ),

                                    title =
                                        "Tolerance",

                                    value =
                                        formatNumber(
                                            currentRecord.rgbTolerance
                                        )
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        8.dp
                                    )
                            )


                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        8.dp
                                    )
                            ) {


                                BooleanMetricBox(

                                    modifier =
                                        Modifier.weight(
                                            1f
                                        ),

                                    title =
                                        "Reference Card Detected",

                                    value =
                                        currentRecord.referenceCardDetected
                                )


                                BooleanMetricBox(

                                    modifier =
                                        Modifier.weight(
                                            1f
                                        ),

                                    title =
                                        "Calibration Applied",

                                    value =
                                        currentRecord.calibrationApplied
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        // =================================================
                        // EVIDENCE INTEGRITY
                        // =================================================

                        SectionCard(

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Default.VerifiedUser,

                                    contentDescription =
                                        null,

                                    tint =
                                        DetailsCyan
                                )
                            },

                            title =
                                "Evidence Integrity"
                        ) {


                            DetailRow(

                                label =
                                    "SHA-256 Hash",

                                value =
                                    currentRecord.imageSha256
                                        ?: "Not available",

                                monospace =
                                    true
                            )


                            DetailsDivider()


                            DetailPairRow(

                                label1 =
                                    "Image Size",

                                value1 =
                                    formatImageSize(
                                        currentRecord.imageSizeBytes
                                    ),

                                label2 =
                                    "MIME Type",

                                value2 =
                                    currentRecord.imageMimeType
                                        ?: "Not available"
                            )


                            DetailsDivider()


                            DetailRow(

                                label =
                                    "Storage Path",

                                value =
                                    currentRecord.imageStoragePath
                                        ?: "Not available",

                                monospace =
                                    true
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        // =================================================
                        // AUDIT TIMELINE
                        // =================================================

                        SectionCard(

                            icon = {

                                Icon(

                                    imageVector =
                                        Icons.Default.AccessTime,

                                    contentDescription =
                                        null,

                                    tint =
                                        DetailsCyan
                                )
                            },

                            title =
                                "Audit Timeline"
                        ) {


                            TimelineItem(

                                title =
                                    "Captured At",

                                value =
                                    formatDateTime(
                                        currentRecord.capturedAt
                                    )
                            )


                            TimelineConnector()


                            TimelineItem(

                                title =
                                    "Created At",

                                value =
                                    formatDateTime(
                                        currentRecord.createdAt
                                    )
                            )


                            TimelineConnector()


                            TimelineItem(

                                title =
                                    "Analysis Completed At",

                                value =
                                    formatDateTime(
                                        currentRecord.analysisCompletedAt
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}


// =============================================================
// SUMMARY CARD
// =============================================================

@Composable
private fun SummaryCard(

    record: TestRecordDetailsRow,

    imageBytes: ByteArray?,

    imageLoading: Boolean
) {


    // Copy nullable state into a stable local value.
    val bytes =
        imageBytes


    DetailsCard {


        if (bytes != null) {


            val bitmap =
                BitmapFactory.decodeByteArray(
                    bytes,
                    0,
                    bytes.size
                )


            bitmap?.let { decodedBitmap ->


                Image(

                    bitmap =
                        decodedBitmap.asImageBitmap(),

                    contentDescription =
                        "Original captured evidence",

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                220.dp
                            )
                            .clip(
                                RoundedCornerShape(
                                    16.dp
                                )
                            ),

                    contentScale =
                        ContentScale.Crop
                )
            }


        } else {


            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            180.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                16.dp
                            )
                        )
                        .background(
                            DetailsSurfaceLight
                        ),

                contentAlignment =
                    Alignment.Center
            ) {


                if (imageLoading) {


                    CircularProgressIndicator(
                        color =
                            DetailsCyan
                    )


                } else {


                    Icon(

                        imageVector =
                            Icons.Default.Image,

                        contentDescription =
                            null,

                        tint =
                            DetailsMuted,

                        modifier =
                            Modifier.size(
                                46.dp
                            )
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        ResultBadgeLarge(
            result =
                formatResult(
                    record.result
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        Text(

            text =
                getTestDisplayName(
                    record.testId
                ),

            color =
                DetailsIvory,

            fontSize =
                25.sp,

            fontWeight =
                FontWeight.Bold
        )


        Text(

            text =
                "Test ID  •  ${record.testId}",

            color =
                DetailsMuted,

            fontSize =
                14.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(

            text =
                formatDateTime(
                    record.capturedAt
                ),

            color =
                DetailsIvory,

            fontSize =
                15.sp
        )
    }
}


// =============================================================
// EVIDENCE IMAGE CARD
// =============================================================

@Composable
private fun EvidenceImageCard(

    record: TestRecordDetailsRow,

    imageBytes: ByteArray?,

    imageLoading: Boolean,

    imageError: String?,

    onViewImage: () -> Unit
) {


    DetailsCard {


        Row(

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Surface(

                modifier =
                    Modifier.size(
                        48.dp
                    ),

                shape =
                    RoundedCornerShape(
                        12.dp
                    ),

                color =
                    DetailsSurfaceLight
            ) {


                Box(

                    contentAlignment =
                        Alignment.Center
                ) {


                    Icon(

                        imageVector =
                            Icons.Default.Image,

                        contentDescription =
                            null,

                        tint =
                            DetailsCyan
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )


            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {


                Text(

                    text =
                        "Original Evidence Image",

                    color =
                        DetailsIvory,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =

                        when {

                            imageLoading ->
                                "Loading secure evidence…"

                            imageBytes != null ->
                                "✓ Stored securely"

                            imageError != null ->
                                "Image unavailable"

                            else ->
                                "No image stored"
                        },

                    color =

                        if (imageBytes != null) {

                            DetailsGreen

                        } else {

                            DetailsMuted
                        }
                )


                Text(

                    text =
                        "${formatImageSize(record.imageSizeBytes)}  •  " +
                                "${record.imageMimeType ?: "Unknown type"}",

                    color =
                        DetailsMuted,

                    fontSize =
                        13.sp
                )
            }
        }


        if (imageError != null) {


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(

                text =
                    imageError,

                color =
                    DetailsAmber,

                fontSize =
                    12.sp
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        Button(

            onClick =
                onViewImage,

            enabled =
                imageBytes != null,

            modifier =
                Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        DetailsSurfaceLight,

                    contentColor =
                        DetailsCyan,

                    disabledContainerColor =
                        DetailsSurfaceLight,

                    disabledContentColor =
                        DetailsMuted
                ),

            shape =
                RoundedCornerShape(
                    14.dp
                )
        ) {


            Icon(

                imageVector =
                    Icons.Default.Fullscreen,

                contentDescription =
                    null
            )


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Text(
                text =
                    "View Full Image"
            )
        }
    }
}


// =============================================================
// SECTION CARD
// =============================================================

@Composable
private fun SectionCard(

    icon: @Composable () -> Unit,

    title: String,

    content: @Composable ColumnScope.() -> Unit
) {


    DetailsCard {


        Row(

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            icon()


            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )


            Text(

                text =
                    title,

                color =
                    DetailsIvory,

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    14.dp
                )
        )


        content()
    }
}


// =============================================================
// GENERIC CARD
// =============================================================

@Composable
private fun DetailsCard(

    content: @Composable ColumnScope.() -> Unit
) {


    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        color =
            DetailsSurface,

        border =
            BorderStroke(
                1.dp,
                DetailsBorder
            )
    ) {


        Column(

            modifier =
                Modifier.padding(
                    16.dp
                ),

            content =
                content
        )
    }
}


// =============================================================
// TWO COLUMN ROW
// =============================================================

@Composable
private fun DetailPairRow(

    label1: String,

    value1: String,

    label2: String,

    value2: String
) {


    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(
                16.dp
            )
    ) {


        DetailCell(

            modifier =
                Modifier.weight(
                    1f
                ),

            label =
                label1,

            value =
                value1
        )


        DetailCell(

            modifier =
                Modifier.weight(
                    1f
                ),

            label =
                label2,

            value =
                value2
        )
    }
}


// =============================================================
// DETAIL CELL
// =============================================================

@Composable
private fun DetailCell(

    modifier: Modifier,

    label: String,

    value: String
) {


    Column(
        modifier =
            modifier
    ) {


        Text(

            text =
                label,

            color =
                DetailsMuted,

            fontSize =
                12.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    4.dp
                )
        )


        Text(

            text =
                value,

            color =
                DetailsIvory,

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// =============================================================
// DETAIL ROW
// =============================================================

@Composable
private fun DetailRow(

    label: String,

    value: String,

    monospace: Boolean = false
) {


    Column {


        Text(

            text =
                label,

            color =
                DetailsMuted,

            fontSize =
                12.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    5.dp
                )
        )


        Text(

            text =
                value,

            color =
                DetailsIvory,

            fontSize =
                if (monospace) {
                    12.sp
                } else {
                    14.sp
                },

            fontFamily =
                if (monospace) {
                    FontFamily.Monospace
                } else {
                    FontFamily.Default
                }
        )
    }
}


// =============================================================
// COLOUR VALUES
// =============================================================

@Composable
private fun ColourValuesRow(
    record: TestRecordDetailsRow
) {


    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {


        ColourValueCard(

            modifier =
                Modifier.weight(
                    1f
                ),

            title =
                "Observed RGB",

            red =
                record.observedR,

            green =
                record.observedG,

            blue =
                record.observedB
        )


        ColourValueCard(

            modifier =
                Modifier.weight(
                    1f
                ),

            title =
                "Expected RGB",

            red =
                record.expectedR,

            green =
                record.expectedG,

            blue =
                record.expectedB
        )
    }
}


// =============================================================
// COLOUR CARD
// =============================================================

@Composable
private fun ColourValueCard(

    modifier: Modifier,

    title: String,

    red: Double?,

    green: Double?,

    blue: Double?
) {


    val hasColour =
        red != null &&
                green != null &&
                blue != null


    val redValue =
        red
            ?.toInt()
            ?.coerceIn(
                0,
                255
            )
            ?: 0


    val greenValue =
        green
            ?.toInt()
            ?.coerceIn(
                0,
                255
            )
            ?: 0


    val blueValue =
        blue
            ?.toInt()
            ?.coerceIn(
                0,
                255
            )
            ?: 0


    val displayColor =

        if (hasColour) {

            Color(
                red =
                    redValue,

                green =
                    greenValue,

                blue =
                    blueValue
            )

        } else {

            DetailsSurfaceLight
        }


    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            DetailsSurfaceLight
    ) {


        Row(

            modifier =
                Modifier.padding(
                    12.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Box(

                modifier =
                    Modifier
                        .size(
                            50.dp
                        )
                        .clip(
                            RoundedCornerShape(
                                10.dp
                            )
                        )
                        .background(
                            displayColor
                        )
            )


            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )


            Column {


                Text(

                    text =
                        title,

                    color =
                        DetailsMuted,

                    fontSize =
                        11.sp
                )


                Text(

                    text =
                        if (hasColour) {

                            "$redValue, $greenValue, $blueValue"

                        } else {

                            "Not available"
                        },

                    color =
                        DetailsIvory,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


// =============================================================
// METRIC BOX
// =============================================================

@Composable
private fun MetricBox(

    modifier: Modifier,

    title: String,

    value: String
) {


    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            DetailsSurfaceLight
    ) {


        Column(

            modifier =
                Modifier.padding(
                    12.dp
                )
        ) {


            Text(

                text =
                    title,

                color =
                    DetailsMuted,

                fontSize =
                    11.sp
            )


            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(

                text =
                    value,

                color =
                    DetailsIvory,

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


// =============================================================
// BOOLEAN BOX
// =============================================================

@Composable
private fun BooleanMetricBox(

    modifier: Modifier,

    title: String,

    value: Boolean?
) {


    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            DetailsSurfaceLight
    ) {


        Column(

            modifier =
                Modifier.padding(
                    12.dp
                )
        ) {


            Text(

                text =
                    title,

                color =
                    DetailsMuted,

                fontSize =
                    11.sp
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(

                text =

                    when (value) {

                        true ->
                            "✓  Yes"

                        false ->
                            "✕  No"

                        null ->
                            "Unknown"
                    },

                color =

                    when (value) {

                        true ->
                            DetailsGreen

                        false ->
                            DetailsAmber

                        null ->
                            DetailsMuted
                    },

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


// =============================================================
// RESULT BADGE
// =============================================================

@Composable
private fun ResultBadgeLarge(
    result: String
) {


    val badgeColor =

        when (result.lowercase()) {

            "positive" ->
                Color(0xFFFF6B78)

            "negative" ->
                DetailsGreen

            "inconclusive" ->
                DetailsAmber

            else ->
                DetailsMuted
        }


    Surface(

        shape =
            RoundedCornerShape(
                30.dp
            ),

        color =
            badgeColor.copy(
                alpha =
                    0.14f
            ),

        border =
            BorderStroke(
                1.dp,
                badgeColor
            )
    ) {


        Text(

            text =
                result.uppercase(),

            modifier =
                Modifier.padding(
                    horizontal = 15.dp,
                    vertical = 7.dp
                ),

            color =
                badgeColor,

            fontSize =
                13.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}


// =============================================================
// TIMELINE ITEM
// =============================================================

@Composable
private fun TimelineItem(

    title: String,

    value: String
) {


    Row(

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Box(

            modifier =
                Modifier
                    .size(
                        12.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            50.dp
                        )
                    )
                    .background(
                        DetailsCyan
                    )
        )


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Column {


            Text(

                text =
                    title,

                color =
                    DetailsMuted,

                fontSize =
                    12.sp
            )


            Text(

                text =
                    value,

                color =
                    DetailsIvory,

                fontWeight =
                    FontWeight.Medium
            )
        }
    }
}


// =============================================================
// TIMELINE CONNECTOR
// =============================================================

@Composable
private fun TimelineConnector() {


    Box(

        modifier =
            Modifier
                .padding(
                    start = 5.dp
                )
                .width(
                    2.dp
                )
                .height(
                    20.dp
                )
                .background(
                    DetailsBorder
                )
    )
}


// =============================================================
// DIVIDER
// =============================================================

@Composable
private fun DetailsDivider() {


    HorizontalDivider(

        modifier =
            Modifier.padding(
                vertical = 12.dp
            ),

        color =
            DetailsBorder.copy(
                alpha =
                    0.7f
            )
    )
}


// =============================================================
// FULL IMAGE DIALOG
// =============================================================

@Composable
private fun FullEvidenceImageDialog(

    imageBytes: ByteArray,

    onDismiss: () -> Unit
) {


    // imageBytes is NON-NULL here, so decode directly.
    val bitmap =
        BitmapFactory.decodeByteArray(
            imageBytes,
            0,
            imageBytes.size
        )


    Dialog(

        onDismissRequest =
            onDismiss,

        properties =
            DialogProperties(
                usePlatformDefaultWidth =
                    false
            )
    ) {


        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black
                    )
        ) {


            bitmap?.let { decodedBitmap ->


                Image(

                    bitmap =
                        decodedBitmap.asImageBitmap(),

                    contentDescription =
                        "Full evidence image",

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                10.dp
                            ),

                    contentScale =
                        ContentScale.Fit
                )
            }


            IconButton(

                onClick =
                    onDismiss,

                modifier =
                    Modifier
                        .align(
                            Alignment.TopEnd
                        )
                        .statusBarsPadding()
                        .padding(
                            14.dp
                        )
                        .background(
                            Color.Black.copy(
                                alpha =
                                    0.6f
                            ),
                            RoundedCornerShape(
                                50.dp
                            )
                        )
            ) {


                Icon(

                    imageVector =
                        Icons.Default.Close,

                    contentDescription =
                        "Close image",

                    tint =
                        Color.White
                )
            }
        }
    }
}


// =============================================================
// TEST NAME
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
// RESULT FORMAT
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
            "NEGATIVE",
            ignoreCase = true
        ) ->

            "Negative"


        result.equals(
            "INCONCLUSIVE",
            ignoreCase = true
        ) ->

            "Inconclusive"


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
// UTC -> IST
// =============================================================

private fun formatDateTime(
    value: String?
): String {


    if (value.isNullOrBlank()) {

        return "Not available"
    }


    return try {


        val parsed =
            OffsetDateTime.parse(
                value
            )


        val indiaTime =
            parsed.atZoneSameInstant(
                ZoneId.of(
                    "Asia/Kolkata"
                )
            )


        indiaTime.format(
            DateTimeFormatter.ofPattern(
                "dd MMM yyyy, hh:mm a"
            )
        )


    } catch (e: Exception) {


        value
    }
}


// =============================================================
// IMAGE SIZE
// =============================================================

private fun formatImageSize(
    bytes: Long?
): String {


    if (bytes == null) {

        return "Not available"
    }


    val megabytes =
        bytes.toDouble() /
                (
                        1024.0 *
                                1024.0
                        )


    return String.format(
        "%.2f MB",
        megabytes
    )
}


// =============================================================
// NUMBER FORMAT
// =============================================================

private fun formatNumber(
    value: Double?
): String {


    return if (value == null) {

        "Not available"

    } else {

        String.format(
            "%.2f",
            value
        )
    }
}