package com.sih.drugtest.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih.drugtest.model.TestProtocol
import com.sih.drugtest.ui.components.AppBottomNavigation
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.Ivory
import com.sih.drugtest.ui.theme.NavyBackground
import com.sih.drugtest.ui.theme.SecondaryText

import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule

import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.GpsFixed

import androidx.compose.material.icons.outlined.WarningAmber

@Composable
fun ProtocolDetailsScreen(
    protocol: TestProtocol,
    onBackClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit
) {

    Scaffold(
        containerColor = NavyBackground,
        bottomBar = {
            AppBottomNavigation(
                selectedTab = "tests",
                onHomeClick = onHomeClick,
                onTestsClick = onTestsClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {

            // -------------------------------------------------
            // HEADER
            // -------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(onClick = onBackClick),
                    shape = CircleShape,
                    color = Color(0xFF0B3455),
                    border = BorderStroke(
                        width = 1.dp,
                        color = CyanAccent
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = CyanAccent,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = "Protocol Details",
                        color = Ivory,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Review before performing the test",
                        color = SecondaryText,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))


            // -------------------------------------------------
// MAIN PROTOCOL CARD
// -------------------------------------------------

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0B3455),
                border = BorderStroke(
                    1.dp,
                    CyanAccent.copy(alpha = 0.35f)
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {

                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = CyanAccent.copy(alpha = 0.10f),
                            border = BorderStroke(
                                1.dp,
                                CyanAccent.copy(alpha = 0.35f)
                            )
                        ) {

                            Box(
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Science,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(15.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = protocol.name,
                                color = Ivory,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp
                            )

                            Spacer(modifier = Modifier.height(7.dp))

                            Text(
                                text = "Test ID  •  ${protocol.testId}",
                                color = SecondaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "Target  •  ${protocol.targetSubstance}",
                                color = Ivory.copy(alpha = 0.92f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {

                        ProtocolBadge(
                            text = "Protocol ${protocol.version}"
                        )

                        ProtocolBadge(
                            text = protocol.testType
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                        color = Color.White.copy(alpha = 0.07f)
                    ) {}

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "DRUG CATEGORY",
                        color = CyanAccent.copy(alpha = 0.9f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = protocol.category,
                        color = SecondaryText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }


            // -------------------------------------------------
            // TEST INFORMATION
            // -------------------------------------------------

            Spacer(modifier = Modifier.height(30.dp))

            SectionTitle(
                title = "Test Information",
                icon = Icons.Outlined.Info
            )

            Spacer(modifier = Modifier.height(14.dp))

            TestInformationCard(
                method = protocol.method,
                reagents = protocol.reagents,
                category = protocol.category,
                targetSubstance = protocol.targetSubstance
            )


            Spacer(modifier = Modifier.height(24.dp))


            // -------------------------------------------------
            // PROCEDURE
            // -------------------------------------------------

            ProcedureSection(
                procedure = protocol.officerProcedure
            )


            Spacer(modifier = Modifier.height(30.dp))


            // -------------------------------------------------
            // EXPECTED RESULT
            // -------------------------------------------------

            ExpectedResultSection(
                protocol = protocol
            )


            Spacer(modifier = Modifier.height(14.dp))


            // -------------------------------------------------
            // IMPORTANT INTERPRETATION + CONFIRMATION
            // -------------------------------------------------

            ImportantResultCard(
                interpretation = protocol.interpretation,
                confirmationStatus = protocol.confirmationStatus
            )


            Spacer(modifier = Modifier.height(32.dp))


            // -------------------------------------------------
            // MANUAL REFERENCE
            // -------------------------------------------------

            SectionTitle(
                title = "Reference",
                icon = Icons.Outlined.MenuBook
            )

            Spacer(modifier = Modifier.height(14.dp))

            ReferenceCard(
                manualSection = protocol.manualSection,
                manualPage = protocol.manualPage,
                source = protocol.source
            )


            Spacer(modifier = Modifier.height(24.dp))


            // -------------------------------------------------
            // CAPTURE GUIDANCE
            // -------------------------------------------------

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFF0E3CB)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = NavyBackground
                        ) {

                            Box(
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Capture Guidance",
                            color = NavyBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = protocol.cameraCaptureNote
                            ?.takeIf { it.isNotBlank() }
                            ?: "Keep the test reaction and reference colour card clearly visible in the same image.",
                        color = Color(0xFF34495E),
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(20.dp))


            // -------------------------------------------------
            // CAPTURE BUTTON
            // -------------------------------------------------

            Button(
                onClick = onCaptureClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = NavyBackground
                )
            ) {

                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Capture Test Result",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// =============================================================
// PROCEDURE SECTION
// =============================================================

@Composable
private fun ProcedureSection(
    procedure: String?
) {

    SectionTitle(
        title = "Procedure",
        icon = Icons.Outlined.Description
    )

    Spacer(modifier = Modifier.height(10.dp))

    val steps = procedure
        ?.split(";")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0B3455),
        border = BorderStroke(
            width = 1.dp,
            color = CyanAccent.copy(alpha = 0.35f)
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 18.dp
            )
        ) {

            if (steps.isEmpty()) {

                Text(
                    text = "Procedure information is not available for this protocol.",
                    color = SecondaryText,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

            } else {

                steps.forEachIndexed { index, step ->

                    ProcedureStep(
                        number = index + 1,
                        text = step
                    )

                    if (index != steps.lastIndex) {
                        ProcedureConnector()
                    }
                }
            }
        }
    }
}


@Composable
private fun ProcedureStep(
    number: Int,
    text: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Surface(
            modifier = Modifier.size(34.dp),
            shape = CircleShape,
            color = CyanAccent
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = number.toString(),
                    color = NavyBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = Ivory,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}


@Composable
private fun ProcedureConnector() {

    Row {

        Box(
            modifier = Modifier.width(34.dp),
            contentAlignment = Alignment.Center
        ) {

            Surface(
                modifier = Modifier
                    .width(2.dp)
                    .height(15.dp),
                color = CyanAccent.copy(alpha = 0.40f)
            ) {}
        }

        Spacer(modifier = Modifier.width(14.dp))

        Spacer(modifier = Modifier.height(15.dp))
    }
}


// =============================================================
// EXPECTED RESULT SECTION
// =============================================================

@Composable
private fun ExpectedResultSection(
    protocol: TestProtocol
) {

    SectionTitle(
        title = "Expected Result",
        icon = Icons.Outlined.Verified
    )

    Spacer(modifier = Modifier.height(10.dp))


    // ---------------------------------------------------------
    // MAIN EXPECTED OBSERVATION
    // ---------------------------------------------------------

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF103F64),
        border = BorderStroke(
            width = 1.dp,
            color = CyanAccent.copy(alpha = 0.55f)
        )
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // visual colour indicator
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = expectedColourSwatch(
                    protocol.expectedObservation
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = Ivory.copy(alpha = 0.75f)
                )
            ) {}

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "EXPECTED OBSERVATION",
                    color = CyanAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                val observationStages = protocol.expectedObservation
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotBlank() }
                    ?: emptyList()

                if (observationStages.isEmpty()) {

                    Text(
                        text = "Not specified",
                        color = Ivory,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                } else {

                    Column {

                        observationStages.forEachIndexed { index, stage ->

                            Text(
                                text = stage,
                                color = Ivory,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 25.sp
                            )

                            if (index != observationStages.lastIndex) {

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp),
                                    color = CyanAccent.copy(alpha = 0.18f)
                                ) {}

                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }


    Spacer(modifier = Modifier.height(12.dp))


    // ---------------------------------------------------------
    // QUICK OBSERVATION DETAILS
    // ---------------------------------------------------------

    ObservationDetailsCard(
        colourTransition = protocol.colourTransition,
        observationLocation = protocol.observationLocation,
        observationTime = protocol.observationTime
    )


}


@Composable
private fun ResultInfoCard(
    title: String,
    value: String?
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = Color(0xFF0E3B5D),
        border = BorderStroke(
            width = 1.dp,
            color = CyanAccent.copy(alpha = 0.18f)
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 13.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = CyanAccent
            ) {}

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = SecondaryText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = displayValue(value),
                    color = Ivory,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// =============================================================
// COMMON COMPONENTS
// =============================================================

@Composable
private fun ProtocolBadge(
    text: String
) {

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CyanAccent.copy(alpha = 0.10f),
        border = BorderStroke(
            1.dp,
            CyanAccent.copy(alpha = 0.28f)
        )
    ) {

        Text(
            text = text,
            color = CyanAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 7.dp
            )
        )
    }
}


@Composable
private fun SectionTitle(
    title: String,
    icon: ImageVector
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier
                .width(4.dp)
                .height(28.dp),
            shape = RoundedCornerShape(4.dp),
            color = CyanAccent
        ) {}

        Spacer(modifier = Modifier.width(10.dp))

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CyanAccent,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = title,
            color = Ivory,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
private fun DetailsCard(
    content: @Composable ColumnScope.() -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0B3455),
        border = BorderStroke(
            width = 1.dp,
            color = CyanAccent.copy(alpha = 0.35f)
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
            content = content
        )
    }
}


@Composable
private fun DetailRow(
    label: String,
    value: String?
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Text(
            text = label,
            color = SecondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = displayValue(value),
            color = Ivory,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}


@Composable
private fun DetailDivider() {

    Spacer(modifier = Modifier.height(7.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {}

    Spacer(modifier = Modifier.height(7.dp))
}


// =============================================================
// SMALL HELPERS
// =============================================================

private fun displayValue(
    value: String?
): String {

    return value
        ?.takeIf { it.isNotBlank() }
        ?: "Not specified"
}


private fun expectedColourSwatch(
    expectedObservation: String?
): Color {

    val text = expectedObservation
        ?.lowercase()
        .orEmpty()

    return when {

        "purple" in text || "violet" in text ->
            Color(0xFF8E44AD)

        "blue" in text ->
            Color(0xFF3498DB)

        "green" in text ->
            Color(0xFF27AE60)

        "red" in text ->
            Color(0xFFE74C3C)

        "orange" in text ->
            Color(0xFFF39C12)

        "yellow" in text ->
            Color(0xFFF1C40F)

        "brown" in text ->
            Color(0xFF8D6E63)

        "pink" in text ->
            Color(0xFFE66AA2)

        else ->
            CyanAccent
    }
}

@Composable
private fun ObservationDetailsCard(
    colourTransition: String?,
    observationLocation: String?,
    observationTime: String?
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0B3455),
        border = BorderStroke(
            1.dp,
            CyanAccent.copy(alpha = 0.28f)
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
        ) {

            Text(
                text = "OBSERVATION DETAILS",
                color = CyanAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))


            ObservationRow(
                icon = Icons.Outlined.Palette,
                title = "Colour Transition",
                value = colourTransition,
                trailingColour = expectedColourSwatch(
                    colourTransition
                )
            )


            ObservationDivider()


            ObservationRow(
                icon = Icons.Outlined.Place,
                title = "Observation Location",
                value = observationLocation
            )


            ObservationDivider()


            ObservationRow(
                icon = Icons.Outlined.Schedule,
                title = "Observation Time",
                value = observationTime
            )
        }
    }
}

@Composable
private fun ObservationRow(
    icon: ImageVector,
    title: String,
    value: String?,
    trailingColour: Color? = null
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = CyanAccent.copy(alpha = 0.10f)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = SecondaryText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = displayValue(value),
                color = Ivory,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }


        if (trailingColour != null) {

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                modifier = Modifier.size(26.dp),
                shape = CircleShape,
                color = trailingColour,
                border = BorderStroke(
                    2.dp,
                    Ivory.copy(alpha = 0.55f)
                )
            ) {}
        }
    }
}

@Composable
private fun ObservationDivider() {

    Spacer(modifier = Modifier.height(14.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = Color.White.copy(alpha = 0.07f)
    ) {}

    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun TestInformationCard(
    method: String?,
    reagents: String?,
    category: String?,
    targetSubstance: String?
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0B3455),
        border = BorderStroke(
            1.dp,
            CyanAccent.copy(alpha = 0.28f)
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
        ) {

            TestInfoRow(
                icon = Icons.Outlined.Science,
                title = "Method",
                value = method
            )

            TestInfoDivider()

            TestInfoRow(
                icon = Icons.Outlined.Biotech,
                title = "Reagents",
                value = reagents
            )

            TestInfoDivider()

            TestInfoRow(
                icon = Icons.Outlined.Category,
                title = "Drug Category",
                value = category
            )

            TestInfoDivider()

            TestInfoRow(
                icon = Icons.Outlined.GpsFixed,
                title = "Target Substance",
                value = targetSubstance
            )
        }
    }
}

@Composable
private fun TestInfoRow(
    icon: ImageVector,
    title: String,
    value: String?
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = CyanAccent.copy(alpha = 0.10f)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = SecondaryText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = displayValue(value),
                color = Ivory,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun TestInfoDivider() {

    Spacer(modifier = Modifier.height(14.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = Color.White.copy(alpha = 0.07f)
    ) {}

    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun ReferenceCard(
    manualSection: String?,
    manualPage: String?,
    source: String?
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0B3455),
        border = BorderStroke(
            1.dp,
            CyanAccent.copy(alpha = 0.22f)
        )
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
        ) {

            ReferenceRow(
                label = "Manual Section",
                value = manualSection
            )

            ReferenceDivider()

            ReferenceRow(
                label = "Manual Page",
                value = manualPage
            )

            ReferenceDivider()

            ReferenceRow(
                label = "Source",
                value = source
            )
        }
    }
}

@Composable
private fun ReferenceRow(
    label: String,
    value: String?
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            color = SecondaryText,
            fontSize = 11.sp,
            modifier = Modifier.weight(0.42f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = displayValue(value),
            color = Ivory,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.58f)
        )
    }
}

@Composable
private fun ReferenceDivider() {

    Spacer(modifier = Modifier.height(10.dp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = Color.White.copy(alpha = 0.06f)
    ) {}

    Spacer(modifier = Modifier.height(10.dp))
}



@Composable
private fun ImportantResultCard(
    interpretation: String?,
    confirmationStatus: String?
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF3A2E18),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFF2C96D).copy(alpha = 0.75f)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            // ---------------------------------------------
            // HEADER
            // ---------------------------------------------

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = Color(0xFFF2C96D)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.WarningAmber,
                            contentDescription = null,
                            tint = NavyBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {

                    Text(
                        text = "RESULT INTERPRETATION",
                        color = Color(0xFFF2C96D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Review before proceeding",
                        color = Ivory.copy(alpha = 0.70f),
                        fontSize = 11.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(18.dp))


            // ---------------------------------------------
            // INTERPRETATION
            // ---------------------------------------------

            Text(
                text = "Interpretation",
                color = Ivory.copy(alpha = 0.65f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = displayValue(
                    interpretation
                ),
                color = Ivory,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 27.sp
            )


            Spacer(modifier = Modifier.height(18.dp))


            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color(0xFFF2C96D).copy(alpha = 0.22f)
            ) {}


            Spacer(modifier = Modifier.height(18.dp))


            // ---------------------------------------------
            // CONFIRMATION STATUS
            // ---------------------------------------------

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFF0E3CB)
            ) {

                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = NavyBackground
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = null,
                                tint = Color(0xFFF2C96D),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "CONFIRMATION STATUS",
                            color = Color(0xFF7A6328),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = displayValue(
                                confirmationStatus
                            ),
                            color = NavyBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(14.dp))


            Row(
                verticalAlignment = Alignment.Top
            ) {

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFFF2C96D),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Field-test results are presumptive and should be interpreted together with the stated confirmation requirement.",
                    color = Ivory.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
