package com.sih.drugtest.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// =============================================================
// ANALYSIS SCREEN COLOURS
// =============================================================

private val AnalysisBackground =
    Color(0xFF001B36)

private val AnalysisSurface =
    Color(0xFF062B4B)

private val AnalysisSurfaceLight =
    Color(0xFF0A365B)

private val AnalysisBorder =
    Color(0xFF155078)

private val AnalysisCyan =
    Color(0xFF18DDEB)

private val AnalysisIvory =
    Color(0xFFF7F2E7)

private val AnalysisMuted =
    Color(0xFF9DB2C5)

private val AnalysisGreen =
    Color(0xFF58E8A5)

private val AnalysisPositive =
    Color(0xFFFF6269)

private val AnalysisPositiveSurface =
    Color(0xFF351D2B)

private val AnalysisAmber =
    Color(0xFFFFC857)

private val RedValue =
    Color(0xFFFF6269)

private val GreenValue =
    Color(0xFF52E5A6)

private val BlueValue =
    Color(0xFF83AFFF)


// =============================================================
// MAIN ANALYSIS SCREEN
// =============================================================

@Composable
fun AnalysisScreen(

    state: AnalysisState,

    result: String? = null,

    observedRgb: List<Double>? = null,

    expectedRgb: List<Double>? = null,

    distance: Double? = null,

    tolerance: Double? = null,

    referenceCardDetected: Boolean = false,

    calibrationApplied: Boolean = false,

    saved: Boolean = false,

    onViewRecordClick: () -> Unit = {},

    onDoneClick: () -> Unit = {}
) {

    val analysisFinished =
        result != null


    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    AnalysisBackground
                )
                .statusBarsPadding()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 18.dp
                )
                .padding(
                    top = 18.dp,
                    bottom = 28.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        // =====================================================
        // SCREEN TITLE
        // =====================================================

        if (analysisFinished) {

            CompletedHeader()

        } else {

            AnalysingHeader(
                progress =
                    state.progress
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    24.dp
                )
        )


        // =====================================================
        // RESULT / PROGRESS CONTENT
        // =====================================================

        if (analysisFinished) {

            CleanResultCard(

                result =
                    result ?: "",

                observedRgb =
                    observedRgb,

                expectedRgb =
                    expectedRgb,

                distance =
                    distance,

                tolerance =
                    tolerance
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            VerificationCard(

                referenceCardDetected =
                    referenceCardDetected,

                calibrationApplied =
                    calibrationApplied,

                saved =
                    saved
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            AnalysisActions(

                onViewRecordClick =
                    onViewRecordClick,

                onDoneClick =
                    onDoneClick
            )

        } else {

            AnalysisProgressCard(
                state =
                    state
            )
        }
    }
}


// =============================================================
// COMPLETE HEADER
// =============================================================

@Composable
private fun CompletedHeader() {

    Column(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Text(

            text =
                "Analysis Complete",

            color =
                AnalysisIvory,

            fontSize =
                34.sp,

            lineHeight =
                38.sp,

            fontWeight =
                FontWeight.Bold,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(

            text =
                "Colour reaction analysis finished successfully.",

            color =
                AnalysisMuted,

            fontSize =
                15.sp,

            lineHeight =
                21.sp,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    22.dp
                )
        )


        // -----------------------------------------------------
        // SIMPLE SUCCESS MARK
        // -----------------------------------------------------

        Surface(

            modifier =
                Modifier.size(
                    82.dp
                ),

            shape =
                CircleShape,

            color =
                AnalysisSurface,

            border =
                BorderStroke(
                    3.dp,
                    AnalysisCyan
                )
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Text(

                    text =
                        "✓",

                    color =
                        AnalysisCyan,

                    fontSize =
                        42.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        Text(

            text =
                "Analysis successful",

            color =
                AnalysisMuted,

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// =============================================================
// ANALYSING HEADER
// =============================================================

@Composable
private fun AnalysingHeader(
    progress: Int
) {

    Column(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Text(

            text =
                "Analysing Test",

            color =
                AnalysisIvory,

            fontSize =
                32.sp,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(

            text =
                "Comparing the reaction colour with reference data.",

            color =
                AnalysisMuted,

            fontSize =
                15.sp,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    28.dp
                )
        )


        Box(

            modifier =
                Modifier.size(
                    100.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {


            CircularProgressIndicator(

                progress = {
                    progress / 100f
                },

                modifier =
                    Modifier.fillMaxSize(),

                color =
                    AnalysisCyan,

                trackColor =
                    AnalysisSurfaceLight,

                strokeWidth =
                    7.dp
            )


            Text(

                text =
                    "$progress%",

                color =
                    AnalysisIvory,

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


// =============================================================
// RESULT CARD
// =============================================================

@Composable
private fun CleanResultCard(

    result: String,

    observedRgb: List<Double>?,

    expectedRgb: List<Double>?,

    distance: Double?,

    tolerance: Double?
) {

    val isPositive =
        result.equals(
            "PRESUMPTIVE_POSITIVE",
            ignoreCase = true
        )


    val isInconclusive =
        result.equals(
            "INCONCLUSIVE",
            ignoreCase = true
        )


    val resultLabel =
        when {

            isPositive ->
                "Presumptive Positive"

            isInconclusive ->
                "Inconclusive"

            else ->
                result
                    .replace(
                        "_",
                        " "
                    )
                    .lowercase()
                    .replaceFirstChar {
                        it.uppercase()
                    }
        }


    val resultColor =
        when {

            isPositive ->
                AnalysisPositive

            isInconclusive ->
                AnalysisAmber

            else ->
                AnalysisCyan
        }


    val resultSurface =
        when {

            isPositive ->
                AnalysisPositiveSurface

            else ->
                AnalysisSurface
        }


    // =========================================================
    // PRIMARY RESULT
    // =========================================================

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                22.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    resultSurface
            ),

        border =
            BorderStroke(
                1.dp,
                resultColor.copy(
                    alpha = 0.55f
                )
            )
    ) {

        Column(

            modifier =
                Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
        ) {


            Text(

                text =
                    "RESULT",

                color =
                    AnalysisMuted,

                fontSize =
                    11.sp,

                letterSpacing =
                    2.5.sp,

                fontWeight =
                    FontWeight.SemiBold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        7.dp
                    )
            )


            Text(

                text =
                    resultLabel,

                color =
                    resultColor,

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
                        5.dp
                    )
            )


            Text(

                text =
                    if (isPositive) {

                        "Laboratory confirmation is required."

                    } else {

                        "The observed colour is outside the configured positive range."
                    },

                color =
                    AnalysisIvory.copy(
                        alpha = 0.82f
                    ),

                fontSize =
                    14.sp,

                lineHeight =
                    20.sp
            )
        }
    }


    Spacer(
        modifier =
            Modifier.height(
                14.dp
            )
    )


    // =========================================================
    // COLOUR DATA CARD
    // =========================================================

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                22.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    AnalysisSurface
            ),

        border =
            BorderStroke(
                1.dp,
                AnalysisBorder
            )
    ) {

        Column(

            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {


            ColourComparisonSection(

                title =
                    "Observed Reaction Colour",

                rgb =
                    observedRgb
            )


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            ThinDivider()


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            ColourComparisonSection(

                title =
                    "Expected Reference Colour",

                rgb =
                    expectedRgb
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            ThinDivider()


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {


                MetricItem(

                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    label =
                        "Colour Distance",

                    value =
                        distance?.let {

                            String.format(
                                "%.2f",
                                it
                            )

                        } ?: "-"
                )


                MetricItem(

                    modifier =
                        Modifier.weight(
                            1f
                        ),

                    label =
                        "Allowed Tolerance",

                    value =
                        tolerance?.let {

                            String.format(
                                "%.2f",
                                it
                            )

                        } ?: "-"
                )
            }
        }
    }
}


// =============================================================
// COLOUR COMPARISON SECTION
// =============================================================

@Composable
private fun ColourComparisonSection(

    title: String,

    rgb: List<Double>?
) {

    Text(

        text = title,

        color = AnalysisIvory,

        fontSize = 15.sp,

        fontWeight = FontWeight.SemiBold
    )


    Spacer(
        modifier = Modifier.height(12.dp)
    )


    if (
        rgb == null ||
        rgb.size < 3
    ) {

        Text(

            text = "Not available",

            color = AnalysisMuted,

            fontSize = 14.sp
        )

        return
    }


    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(
            10.dp
        )
    ) {


        CompactRgbValue(

            modifier = Modifier.weight(1f),

            label = "R",

            value = rgb[0],

            accent = RedValue
        )


        CompactRgbValue(

            modifier = Modifier.weight(1f),

            label = "G",

            value = rgb[1],

            accent = GreenValue
        )


        CompactRgbValue(

            modifier = Modifier.weight(1f),

            label = "B",

            value = rgb[2],

            accent = BlueValue
        )
    }
}


// =============================================================
// RGB VALUE
// =============================================================

@Composable
private fun CompactRgbValue(

    modifier: Modifier = Modifier,

    label: String,

    value: Double,

    accent: Color
) {

    Surface(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                14.dp
            ),

        color =
            AnalysisBackground.copy(
                alpha = 0.75f
            ),

        border =
            BorderStroke(
                1.dp,
                accent.copy(
                    alpha = 0.25f
                )
            )
    ) {

        Column(

            modifier =
                Modifier.padding(
                    vertical = 11.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            Text(

                text =
                    label,

                color =
                    accent,

                fontSize =
                    12.sp,

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
                    value
                        .toInt()
                        .toString(),

                color =
                    AnalysisIvory,

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


// =============================================================
// METRIC
// =============================================================

@Composable
private fun MetricItem(

    modifier: Modifier = Modifier,

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
                AnalysisMuted,

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
                AnalysisIvory,

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Bold
        )
    }
}


// =============================================================
// VERIFICATION CARD
// =============================================================

@Composable
private fun VerificationCard(

    referenceCardDetected: Boolean,

    calibrationApplied: Boolean,

    saved: Boolean
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
                    AnalysisSurface
            ),

        border =
            BorderStroke(
                1.dp,
                AnalysisBorder
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 16.dp
                    ),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically
        ) {


            VerificationItem(

                modifier =
                    Modifier.weight(
                        1f
                    ),

                verified =
                    referenceCardDetected,

                text =
                    if (referenceCardDetected) {
                        "Reference card\nverified"
                    } else {
                        "Reference card\nunavailable"
                    }
            )


            SmallVerticalDivider()


            VerificationItem(

                modifier =
                    Modifier.weight(
                        1f
                    ),

                verified =
                    calibrationApplied,

                text =
                    if (calibrationApplied) {
                        "Calibration\napplied"
                    } else {
                        "Calibration\nunavailable"
                    }
            )


            SmallVerticalDivider()


            VerificationItem(

                modifier =
                    Modifier.weight(
                        1f
                    ),

                verified =
                    saved,

                text =
                    if (saved) {
                        "Evidence\nsaved"
                    } else {
                        "Evidence\nnot saved"
                    }
            )
        }
    }
}


// =============================================================
// VERIFICATION ITEM
// =============================================================

@Composable
private fun VerificationItem(

    modifier: Modifier = Modifier,

    verified: Boolean,

    text: String
) {

    Row(

        modifier =
            modifier
                .padding(
                    horizontal = 7.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Surface(

            modifier =
                Modifier.size(
                    32.dp
                ),

            shape =
                CircleShape,

            color =
                Color.Transparent,

            border =
                BorderStroke(
                    2.dp,
                    if (verified) {
                        AnalysisCyan
                    } else {
                        AnalysisMuted
                    }
                )
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Text(

                    text =
                        if (verified) {
                            "✓"
                        } else {
                            "–"
                        },

                    color =
                        if (verified) {
                            AnalysisCyan
                        } else {
                            AnalysisMuted
                        },

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier =
                Modifier.size(
                    7.dp
                )
        )


        Text(

            text =
                text,

            color =
                AnalysisIvory.copy(
                    alpha = 0.88f
                ),

            fontSize =
                10.sp,

            lineHeight =
                14.sp
        )
    }
}


// =============================================================
// ACTION BUTTONS
// =============================================================

@Composable
private fun AnalysisActions(

    onViewRecordClick: () -> Unit,

    onDoneClick: () -> Unit
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(
                10.dp
            )
    ) {


        Surface(

            modifier =
                Modifier
                    .weight(
                        1f
                    )
                    .height(
                        58.dp
                    )
                    .clickable {
                        onViewRecordClick()
                    },

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            color =
                AnalysisSurface,

            border =
                BorderStroke(
                    1.dp,
                    AnalysisBorder
                )
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Row(

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Text(

                        text =
                            "▤",

                        color =
                            AnalysisIvory,

                        fontSize =
                            22.sp
                    )


                    Spacer(
                        modifier =
                            Modifier.size(
                                8.dp
                            )
                    )


                    Text(

                        text =
                            "View Record",

                        color =
                            AnalysisIvory,

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }


        Surface(

            modifier =
                Modifier
                    .weight(
                        1f
                    )
                    .height(
                        58.dp
                    )
                    .clickable {
                        onDoneClick()
                    },

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            color =
                AnalysisCyan
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Text(

                    text =
                        "Done",

                    color =
                        AnalysisBackground,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


// =============================================================
// ANALYSIS IN-PROGRESS CARD
// =============================================================

@Composable
private fun AnalysisProgressCard(
    state: AnalysisState
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
                    AnalysisSurface
            ),

        border =
            BorderStroke(
                1.dp,
                AnalysisBorder
            )
    ) {

        Column(

            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {


            ProgressStatusRow(

                completed =
                    state.imageCaptured,

                current =
                    false,

                text =
                    "Image captured"
            )


            ProgressStatusRow(

                completed =
                    state.referenceCardDetected,

                current =
                    false,

                text =
                    "Reference card detected"
            )


            ProgressStatusRow(

                completed =
                    state.colourExtracted,

                current =
                    false,

                text =
                    "Reaction colour extracted"
            )


            ProgressStatusRow(

                completed =
                    false,

                current =
                    state.aiAnalysisInProgress,

                text =
                    "Comparing reaction colour"
            )


            ProgressStatusRow(

                completed =
                    state.resultGenerated,

                current =
                    false,

                text =
                    "Generating result"
            )


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(

                text =
                    "Keep the app open while analysis is in progress.",

                color =
                    AnalysisMuted,

                fontSize =
                    12.sp
            )
        }
    }
}


// =============================================================
// PROGRESS STATUS ROW
// =============================================================

@Composable
private fun ProgressStatusRow(

    completed: Boolean,

    current: Boolean,

    text: String
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 7.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Surface(

            modifier =
                Modifier.size(
                    30.dp
                ),

            shape =
                CircleShape,

            color =
                when {

                    completed ->
                        AnalysisCyan

                    current ->
                        AnalysisSurfaceLight

                    else ->
                        AnalysisBackground
                },

            border =
                BorderStroke(
                    1.dp,
                    when {

                        completed ->
                            AnalysisCyan

                        current ->
                            AnalysisCyan

                        else ->
                            AnalysisBorder
                    }
                )
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Text(

                    text =
                        when {

                            completed ->
                                "✓"

                            current ->
                                "•"

                            else ->
                                ""
                        },

                    color =
                        if (completed) {
                            AnalysisBackground
                        } else {
                            AnalysisCyan
                        },

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }


        Spacer(
            modifier =
                Modifier.size(
                    12.dp
                )
        )


        Text(

            text =
                text,

            color =
                if (
                    completed ||
                    current
                ) {
                    AnalysisIvory
                } else {
                    AnalysisMuted
                },

            fontSize =
                14.sp,

            fontWeight =
                if (current) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                }
        )
    }
}


// =============================================================
// THIN DIVIDER
// =============================================================

@Composable
private fun ThinDivider() {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    1.dp
                )
                .background(
                    AnalysisBorder.copy(
                        alpha = 0.75f
                    )
                )
    )
}


// =============================================================
// SMALL VERTICAL DIVIDER
// =============================================================

@Composable
private fun SmallVerticalDivider() {

    Box(

        modifier =
            Modifier
                .size(
                    width = 1.dp,
                    height = 42.dp
                )
                .background(
                    AnalysisBorder
                )
    )
}


// =============================================================
// COMPATIBILITY COMPOSABLE
//
// Kept because other project code may still reference
// AnalysisStep().
// =============================================================

@Composable
fun AnalysisStep(

    completed: Boolean,

    current: Boolean,

    text: String
) {

    ProgressStatusRow(

        completed =
            completed,

        current =
            current,

        text =
            text
    )
}