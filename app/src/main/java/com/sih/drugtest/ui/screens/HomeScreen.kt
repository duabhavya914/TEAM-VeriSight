package com.sih.drugtest.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih.drugtest.R
import com.sih.drugtest.SupabaseClient
import com.sih.drugtest.model.TestRecord
import com.sih.drugtest.ui.components.AppBottomNavigation
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.DarkNavy
import com.sih.drugtest.ui.theme.DividerColour
import com.sih.drugtest.ui.theme.Ivory
import com.sih.drugtest.ui.theme.NavyBackground
import com.sih.drugtest.ui.theme.SecondaryText
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// =============================================================
// DATABASE MODEL USED ONLY BY HOME RECENT ACTIVITY
// =============================================================

@Serializable
data class HomeRecentRecordRow(

    val id: String,

    @SerialName("test_id")
    val testId: String,

    val result: String? = null,

    @SerialName("captured_at")
    val capturedAt: String,

    @SerialName("officer_id")
    val officerId: String? = null
)


// =============================================================
// HOME SCREEN
// =============================================================

@Composable
fun HomeScreen(

    officerId: String,

    onStartClick: () -> Unit,

    onTestsClick: () -> Unit,

    onHistoryClick: () -> Unit,

    onProfileClick: () -> Unit,

    onRecordClick: (String) -> Unit
) {


    // =========================================================
    // RECENT HISTORY STATE
    // =========================================================

    var recentRecords by remember {
        mutableStateOf<List<TestRecord>>(
            emptyList()
        )
    }


    var recentLoading by remember {
        mutableStateOf(true)
    }


    // =========================================================
    // FETCH LATEST 3 REAL RECORDS
    // =========================================================

    LaunchedEffect(officerId) {


        recentLoading =
            true


        recentRecords =
            emptyList()


        if (officerId.isBlank()) {


            Log.w(
                "HomeRecent",
                "Officer ID is blank. Recent activity fetch skipped."
            )


            recentLoading =
                false


            return@LaunchedEffect
        }


        try {


            Log.d(
                "HomeRecent",
                "Fetching recent records for $officerId"
            )


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
                    .decodeList<HomeRecentRecordRow>()


            // -------------------------------------------------
            // NEWEST FIRST + ONLY TOP 3
            // -------------------------------------------------

            val latestThree =
                databaseRows
                    .sortedByDescending {
                        it.capturedAt
                    }
                    .take(3)


            recentRecords =
                latestThree.map { row ->


                    TestRecord(

                        recordId =
                            row.id,

                        testName =
                            getHomeTestDisplayName(
                                row.testId
                            ),

                        result =
                            formatHomeResult(
                                row.result
                            ),

                        dateTime =
                            formatHomeCapturedAt(
                                row.capturedAt
                            )
                    )
                }


            Log.d(
                "HomeRecent",
                "Loaded ${recentRecords.size} recent records"
            )


            latestThree.forEach { row ->

                Log.d(
                    "HomeRecent",
                    "Recent record: ${row.id} | ${row.testId} | ${row.result}"
                )
            }


        } catch (e: Exception) {


            Log.e(
                "HomeRecent",
                "Failed to load recent activity",
                e
            )


            recentRecords =
                emptyList()


        } finally {


            recentLoading =
                false
        }
    }


    Scaffold(

        containerColor =
            NavyBackground,

        bottomBar = {

            AppBottomNavigation(

                selectedTab =
                    "home",

                onHomeClick = {
                    // Already on Home
                },

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
                    .padding(
                        innerPadding
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    )
                    .padding(
                        top = 14.dp,
                        bottom = 14.dp
                    )
        ) {


            HomeHeader()


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            HeroSection()


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            ColourPaletteSection()


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            StartTestCard(
                onClick =
                    onStartClick
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            RecentActivitySection(

                records =
                    recentRecords,

                isLoading =
                    recentLoading,

                onRecordClick =
                    onRecordClick,

                onViewAllClick =
                    onHistoryClick
            )
        }
    }
}


// =============================================================
// HEADER
// =============================================================

@Composable
fun HomeHeader() {


    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Surface(

            modifier =
                Modifier.size(
                    42.dp
                ),

            shape =
                RoundedCornerShape(
                    12.dp
                ),

            color =
                Color(0xFF0B3455),

            border =
                BorderStroke(
                    2.dp,
                    CyanAccent
                )
        ) {


            Box(

                contentAlignment =
                    Alignment.Center
            ) {


                Icon(

                    imageVector =
                        Icons.Outlined.VerifiedUser,

                    contentDescription =
                        "FieldTest Secure logo",

                    tint =
                        CyanAccent,

                    modifier =
                        Modifier.size(
                            26.dp
                        )
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(
                    9.dp
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
                    "FieldTest Secure",

                color =
                    Ivory,

                fontSize =
                    18.sp,

                fontWeight =
                    FontWeight.Bold,

                maxLines =
                    1
            )


            Text(

                text =
                    "CAPTURE • CALIBRATE • VERIFY",

                color =
                    CyanAccent,

                fontSize =
                    7.sp,

                letterSpacing =
                    1.sp,

                maxLines =
                    1
            )
        }
    }
}


// =============================================================
// HERO
// =============================================================

@Composable
fun HeroSection() {


    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    235.dp
                )
    ) {


        Image(

            painter =
                painterResource(
                    id =
                        R.drawable.hero_test
                ),

            contentDescription =
                "Colour test calibration",

            contentScale =
                ContentScale.Crop,

            modifier =
                Modifier
                    .align(
                        Alignment.CenterEnd
                    )
                    .fillMaxHeight()
                    .width(
                        155.dp
                    )
        )


        Column(

            modifier =
                Modifier
                    .align(
                        Alignment.CenterStart
                    )
                    .fillMaxWidth(
                        0.66f
                    ),

            verticalArrangement =
                Arrangement.Center
        ) {


            Text(

                text =
                    "Reliable results.",

                color =
                    Ivory,

                fontSize =
                    28.sp,

                lineHeight =
                    32.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "Verifiable records.",

                color =
                    CyanAccent,

                fontSize =
                    28.sp,

                lineHeight =
                    32.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            Text(

                text =
                    "Capture, calibrate and document field tests with confidence.",

                color =
                    SecondaryText,

                fontSize =
                    14.sp,

                lineHeight =
                    20.sp
            )
        }
    }
}


// =============================================================
// COLOUR PALETTE
// =============================================================

@Composable
fun ColourPaletteSection() {


    Column {


        Row(

            horizontalArrangement =
                Arrangement.spacedBy(
                    7.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            PaletteSquare(
                Color(0xFF7B3FF2)
            )


            PaletteSquare(
                Color(0xFF00DCE8)
            )


            PaletteSquare(
                Color(0xFFFFC43D)
            )


            PaletteSquare(
                Color(0xFFFFF8E8)
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )


        Text(

            text =
                "SCIENCE IN THE FIELD",

            color =
                SecondaryText,

            fontSize =
                8.sp,

            lineHeight =
                10.sp,

            letterSpacing =
                2.4.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    2.dp
                )
        )


        Text(

            text =
                "TRUST IN EVIDENCE",

            color =
                SecondaryText,

            fontSize =
                8.sp,

            lineHeight =
                10.sp,

            letterSpacing =
                2.4.sp
        )
    }
}


// =============================================================
// PALETTE SQUARE
// =============================================================

@Composable
fun PaletteSquare(
    colour: Color
) {


    Surface(

        modifier =
            Modifier.size(
                16.dp
            ),

        shape =
            RoundedCornerShape(
                1.dp
            ),

        color =
            colour

    ) {}
}


// =============================================================
// START TEST CARD
// =============================================================

@Composable
fun StartTestCard(
    onClick: () -> Unit
) {


    Surface(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),

        shape =
            RoundedCornerShape(
                22.dp
            ),

        color =
            CyanAccent
    ) {


        Row(

            modifier =
                Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Icon(

                imageVector =
                    Icons.Default.CameraAlt,

                contentDescription =
                    "Start camera",

                tint =
                    DarkNavy,

                modifier =
                    Modifier.size(
                        44.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.width(
                        14.dp
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
                        "Start New Test",

                    color =
                        DarkNavy,

                    fontSize =
                        20.sp,

                    lineHeight =
                        24.sp,

                    fontWeight =
                        FontWeight.Bold,

                    maxLines =
                        1
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(

                    text =
                        "Capture a test reaction and reference card",

                    color =
                        DarkNavy,

                    fontSize =
                        13.sp,

                    lineHeight =
                        17.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )


            Surface(

                modifier =
                    Modifier.size(
                        44.dp
                    ),

                shape =
                    RoundedCornerShape(
                        50.dp
                    ),

                color =
                    DarkNavy
            ) {


                Box(

                    contentAlignment =
                        Alignment.Center
                ) {


                    Text(

                        text =
                            "→",

                        color =
                            Ivory,

                        fontSize =
                            27.sp
                    )
                }
            }
        }
    }
}


// =============================================================
// RECENT ACTIVITY
// =============================================================

@Composable
fun RecentActivitySection(

    records: List<TestRecord>,

    isLoading: Boolean,

    onRecordClick: (String) -> Unit,

    onViewAllClick: () -> Unit
) {


    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Ivory
            )
    ) {


        Column(

            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {


            // =================================================
            // TITLE + VIEW ALL
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Text(

                    text =
                        "Recent Activity",

                    color =
                        NavyBackground,

                    fontSize =
                        20.sp,

                    lineHeight =
                        24.sp,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                if (records.isNotEmpty()) {


                    TextButton(

                        onClick =
                            onViewAllClick
                    ) {


                        Text(

                            text =
                                "View all",

                            color =
                                Color(0xFF075BBB),

                            fontSize =
                                12.sp
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
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
                            .height(
                                100.dp
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {


                    CircularProgressIndicator(

                        modifier =
                            Modifier.size(
                                28.dp
                            ),

                        color =
                            NavyBackground,

                        strokeWidth =
                            3.dp
                    )
                }


            } else if (records.isEmpty()) {


                // =============================================
                // NO RECORDS
                // =============================================

                EmptyRecentActivity()


            } else {


                // =============================================
                // TOP 3 REAL RECORDS
                // =============================================

                records
                    .take(3)
                    .forEachIndexed { index, record ->


                        RecentRecordRow(

                            record =
                                record,

                            onClick = {

                                onRecordClick(
                                    record.recordId
                                )
                            }
                        )


                        if (
                            index <
                            records.take(3).lastIndex
                        ) {


                            HorizontalDivider(

                                color =
                                    DividerColour,

                                modifier =
                                    Modifier.padding(
                                        vertical = 9.dp
                                    )
                            )
                        }
                    }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            SecurityStrip()
        }
    }
}


// =============================================================
// EMPTY RECENT ACTIVITY
// =============================================================

@Composable
fun EmptyRecentActivity() {


    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        color =
            Color(0xFFF4EDDD)
    ) {


        Column(

            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            Text(

                text =
                    "No test records yet",

                color =
                    NavyBackground,

                fontSize =
                    16.sp,

                lineHeight =
                    20.sp,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(

                text =
                    "Completed tests will appear here.",

                color =
                    Color(0xFF607080),

                fontSize =
                    12.sp,

                lineHeight =
                    16.sp
            )
        }
    }
}


// =============================================================
// RECENT RECORD ROW
// =============================================================

@Composable
fun RecentRecordRow(

    record: TestRecord,

    onClick: () -> Unit
) {


    Surface(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),

        shape =
            RoundedCornerShape(
                12.dp
            ),

        color =
            Color.Transparent
    ) {


        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 4.dp,
                        vertical = 6.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // =================================================
            // RECORD INFO
            // =================================================

            Column(

                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {


                Text(

                    text =
                        record.testName,

                    color =
                        NavyBackground,

                    fontSize =
                        15.sp,

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
                        record.result,

                    color =
                        getHomeResultColor(
                            record.result
                        ),

                    fontSize =
                        13.sp,

                    fontWeight =
                        FontWeight.SemiBold
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

                    color =
                        Color(0xFF607080),

                    fontSize =
                        11.sp
                )
            }


            // =================================================
            // OPEN INDICATOR
            // =================================================

            Surface(

                modifier =
                    Modifier.size(
                        34.dp
                    ),

                shape =
                    RoundedCornerShape(
                        50.dp
                    ),

                color =
                    Color(0xFFE7F5F6)
            ) {


                Box(

                    contentAlignment =
                        Alignment.Center
                ) {


                    Text(

                        text =
                            "›",

                        color =
                            NavyBackground,

                        fontSize =
                            24.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


// =============================================================
// SECURITY STRIP
// =============================================================

@Composable
fun SecurityStrip() {


    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        color =
            Color(0xFFF4EDDD)
    ) {


        Row(

            modifier =
                Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 11.dp
                ),

            horizontalArrangement =
                Arrangement.SpaceEvenly,

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            SecurityItem(

                icon =
                    Icons.Default.LocationOn,

                title =
                    "GPS",

                subtitle =
                    "Ready"
            )


            SecurityItem(

                icon =
                    Icons.Default.Shield,

                title =
                    "SHA-256",

                subtitle =
                    "Secured"
            )


            SecurityItem(

                icon =
                    Icons.Default.Description,

                title =
                    "Protocol",

                subtitle =
                    "Versioned"
            )
        }
    }
}


// =============================================================
// SECURITY ITEM
// =============================================================

@Composable
fun SecurityItem(

    icon: ImageVector,

    title: String,

    subtitle: String
) {


    Column(

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Icon(

            imageVector =
                icon,

            contentDescription =
                title,

            tint =
                NavyBackground,

            modifier =
                Modifier.size(
                    23.dp
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    3.dp
                )
        )


        Text(

            text =
                title,

            color =
                NavyBackground,

            fontSize =
                11.sp,

            fontWeight =
                FontWeight.Bold
        )


        Text(

            text =
                subtitle,

            color =
                Color(0xFF607080),

            fontSize =
                9.sp
        )
    }
}


// =============================================================
// TEST DISPLAY NAME
// =============================================================

private fun getHomeTestDisplayName(
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

private fun formatHomeResult(
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
// RESULT COLOUR
// =============================================================

private fun getHomeResultColor(
    result: String
): Color {


    return when (
        result.lowercase()
    ) {


        "positive" ->
            Color(0xFFC94A4A)


        "negative" ->
            Color(0xFF237A45)


        "inconclusive" ->
            Color(0xFFB77900)


        else ->
            Color(0xFF607080)
    }
}


// =============================================================
// UTC -> IST
// =============================================================

private fun formatHomeCapturedAt(
    capturedAt: String
): String {


    return try {


        val parsed =
            OffsetDateTime.parse(
                capturedAt
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


        capturedAt
    }
}