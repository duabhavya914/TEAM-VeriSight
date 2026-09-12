package com.sih.drugtest.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.sih.drugtest.model.TestRecord
import com.sih.drugtest.ui.components.AppBottomNavigation
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.DarkNavy
import com.sih.drugtest.ui.theme.DividerColour
import com.sih.drugtest.ui.theme.Ivory
import com.sih.drugtest.ui.theme.NavyBackground
import com.sih.drugtest.ui.theme.SecondaryText


@Composable
fun HomeScreen(
    recentRecords: List<TestRecord>,
    onStartClick: () -> Unit,
    onTestsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Scaffold(
        containerColor = NavyBackground,
        bottomBar = {
            AppBottomNavigation(
                selectedTab = "home",
                onHomeClick = {},
                onTestsClick = onTestsClick,
                onHistoryClick = onHistoryClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 14.dp)
        ) {
            HomeHeader()

            Spacer(modifier = Modifier.height(18.dp))

            HeroSection()

            Spacer(modifier = Modifier.height(12.dp))

            ColourPaletteSection()

            Spacer(modifier = Modifier.height(16.dp))

            StartTestCard(
                onClick = onStartClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            RecentActivitySection(
                records = recentRecords
            )
        }
    }
}


@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0B3455),
            border = BorderStroke(2.dp, CyanAccent)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = "FieldTest Secure logo",
                    tint = CyanAccent,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(9.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "FieldTest Secure",
                color = Ivory,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = "CAPTURE • CALIBRATE • VERIFY",
                color = CyanAccent,
                fontSize = 7.sp,
                letterSpacing = 1.sp,
                maxLines = 1
            )
        }

        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Ivory,
            border = BorderStroke(2.dp, CyanAccent)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "OP",
                    color = NavyBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(235.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.hero_test),
            contentDescription = "Colour test calibration",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(155.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.66f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reliable results.",
                color = Ivory,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Verifiable records.",
                color = CyanAccent,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Capture, calibrate and document field tests with confidence.",
                color = SecondaryText,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}


@Composable
fun ColourPaletteSection() {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PaletteSquare(Color(0xFF7B3FF2))
            PaletteSquare(Color(0xFF00DCE8))
            PaletteSquare(Color(0xFFFFC43D))
            PaletteSquare(Color(0xFFFFF8E8))
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "SCIENCE IN THE FIELD",
            color = SecondaryText,
            fontSize = 8.sp,
            lineHeight = 10.sp,
            letterSpacing = 2.4.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "TRUST IN EVIDENCE",
            color = SecondaryText,
            fontSize = 8.sp,
            lineHeight = 10.sp,
            letterSpacing = 2.4.sp
        )
    }
}


@Composable
fun PaletteSquare(
    colour: Color
) {
    Surface(
        modifier = Modifier.size(16.dp),
        shape = RoundedCornerShape(1.dp),
        color = colour
    ) {}
}


@Composable
fun StartTestCard(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = CyanAccent
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Start camera",
                tint = DarkNavy,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Start New Test",
                    color = DarkNavy,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Capture a test reaction and reference card",
                    color = DarkNavy,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = DarkNavy
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "→",
                        color = Ivory,
                        fontSize = 27.sp
                    )
                }
            }
        }
    }
}


@Composable
fun RecentActivitySection(
    records: List<TestRecord>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Ivory
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Recent Activity",
                    color = NavyBackground,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (records.isNotEmpty()) {
                    TextButton(onClick = {}) {
                        Text(
                            text = "View all",
                            color = Color(0xFF075BBB),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (records.isEmpty()) {

                EmptyRecentActivity()

            } else {

                records.take(2).forEachIndexed { index, record ->

                    RecentRecordRow(record)

                    if (index < records.take(2).lastIndex) {
                        HorizontalDivider(
                            color = DividerColour,
                            modifier = Modifier.padding(vertical = 9.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SecurityStrip()
        }
    }
}


@Composable
fun EmptyRecentActivity() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF4EDDD)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 14.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "No test records yet",
                color = NavyBackground,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Completed tests will appear here.",
                color = Color(0xFF607080),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}


@Composable
fun RecentRecordRow(
    record: TestRecord
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = record.testName,
            color = NavyBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = record.result,
            color = Color(0xFFCA5A23),
            fontSize = 13.sp
        )

        Text(
            text = record.dateTime,
            color = Color(0xFF607080),
            fontSize = 11.sp
        )
    }
}


@Composable
fun SecurityStrip() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF4EDDD)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 11.dp
            ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecurityItem(
                icon = Icons.Default.LocationOn,
                title = "GPS",
                subtitle = "Ready"
            )

            SecurityItem(
                icon = Icons.Default.Shield,
                title = "SHA-256",
                subtitle = "Secured"
            )

            SecurityItem(
                icon = Icons.Default.Description,
                title = "Protocol",
                subtitle = "Versioned"
            )
        }
    }
}


@Composable
fun SecurityItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = NavyBackground,
            modifier = Modifier.size(23.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            color = NavyBackground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subtitle,
            color = Color(0xFF607080),
            fontSize = 9.sp
        )
    }
}