package com.sih.drugtest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.Ivory
import com.sih.drugtest.ui.theme.NavyBackground
import com.sih.drugtest.ui.theme.SecondaryText

import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import com.sih.drugtest.ui.components.AppBottomNavigation

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.graphics.Color
import com.sih.drugtest.model.TestProtocol

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SelectTestScreen(
    protocols: List<TestProtocol>,
    onTestSelected: (TestProtocol) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    val filteredProtocols = protocols.filter { protocol ->

        protocol.name.contains(
            searchQuery,
            ignoreCase = true
        ) ||
                protocol.category.contains(
                    searchQuery,
                    ignoreCase = true
                ) ||
                protocol.version.contains(
                    searchQuery,
                    ignoreCase = true
                )
    }

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBackground)
                .padding(innerPadding)
        ) {

            SelectTestMoleculeBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 20.dp)
            ) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable(onClick = onBackClick),
                        shape = CircleShape,
                        color = NavyBackground,
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
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Select Test",
                            color = Ivory,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Choose a field-test protocol",
                            color = SecondaryText,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "Choose the field-test protocol you are using.",
                    color = SecondaryText,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search test protocols",
                            color = SecondaryText,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Ivory,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Ivory,
                        unfocusedTextColor = Ivory,
                        cursorColor = CyanAccent,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CyanAccent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Information box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Ivory
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 15.dp,
                            vertical = 13.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            color = NavyBackground
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Information",
                                    tint = Ivory,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(11.dp))

                        Text(
                            text = "Select the protocol that matches your physical test kit.",
                            color = NavyBackground,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Available Protocols heading
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Available Protocols",
                        color = Ivory,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    if (protocols.isNotEmpty()) {
                        Text(
                            text = "${filteredProtocols.size} protocols",
                            color = SecondaryText,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Database-driven section
                if (protocols.isEmpty()) {

                    NoProtocolsAvailable()

                } else if (filteredProtocols.isEmpty()) {

                    NoProtocolsFound()

                } else {

                    filteredProtocols.forEach { protocol ->

                        ProtocolCard(
                            protocol = protocol,
                            onClick = {
                                onTestSelected(protocol)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0B3455)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 15.dp,
                            vertical = 13.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Protocol security",
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(11.dp))

                        Text(
                            text = "Protocols are version-controlled for consistent results.",
                            color = SecondaryText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProtocolCard(
    protocol: TestProtocol,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Ivory
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(15.dp),
                color = CyanAccent.copy(alpha = 0.15f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Science,
                        contentDescription = protocol.name,
                        tint = NavyBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = protocol.name,
                    color = NavyBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = protocol.category,
                    color = Color(0xFF607080),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Protocol ${protocol.version}",
                    color = NavyBackground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Open protocol",
                tint = NavyBackground,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun NoProtocolsAvailable() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Ivory.copy(alpha = 0.96f)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 20.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.Science,
                contentDescription = null,
                tint = NavyBackground,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No protocols available",
                color = NavyBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Protocols loaded from the database will appear here.",
                color = Color(0xFF607080),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun NoProtocolsFound() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Ivory.copy(alpha = 0.96f)
    ) {
        Text(
            text = "No matching protocols found.",
            color = NavyBackground,
            fontSize = 13.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
}

@Composable
fun SelectTestMoleculeBackground() {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        val lineColour = CyanAccent.copy(alpha = 0.16f)
        val nodeColour = CyanAccent.copy(alpha = 0.26f)

        val root3 = 1.732f

        fun createHexagon(
            center: Offset,
            radius: Float
        ): List<Offset> {
            return List(6) { index ->
                val angle = Math.toRadians((60.0 * index) - 30.0)
                Offset(
                    x = center.x + radius * cos(angle).toFloat(),
                    y = center.y + radius * sin(angle).toFloat()
                )
            }
        }

        fun drawHexagon(
            center: Offset,
            radius: Float
        ) {
            val points = createHexagon(center, radius)

            points.forEachIndexed { index, point ->
                val next = points[(index + 1) % points.size]

                drawLine(
                    color = lineColour,
                    start = point,
                    end = next,
                    strokeWidth = 5.8f,
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = nodeColour,
                    radius = 7.8f,
                    center = point
                )
            }
        }

        fun drawBond(
            start: Offset,
            end: Offset
        ) {
            drawLine(
                color = lineColour,
                start = start,
                end = end,
                strokeWidth = 5.8f,
                cap = StrokeCap.Round
            )
        }

        // =========================
        // TOP RIGHT CLUSTER
        // slightly smaller
        // =========================

        val rTop = size.width * 0.060f

        val topMain = Offset(
            x = size.width * 0.84f,
            y = size.height * 0.17f
        )

        val topRight = Offset(
            x = topMain.x + root3 * rTop,
            y = topMain.y
        )

        val topLower = Offset(
            x = topMain.x + (root3 / 2f) * rTop,
            y = topMain.y + 1.5f * rTop
        )

        drawHexagon(topMain, rTop)
        drawHexagon(topRight, rTop)
        drawHexagon(topLower, rTop)

        val topBranch1 = Offset(
            x = topMain.x - (root3 / 2f) * rTop,
            y = topMain.y - 1.5f * rTop
        )

        val topBranch2 = Offset(
            x = topBranch1.x,
            y = topBranch1.y - 26f
        )

        drawBond(
            start = Offset(
                x = topMain.x - (root3 / 2f) * rTop,
                y = topMain.y - 0.5f * rTop
            ),
            end = topBranch1
        )

        drawBond(
            start = topBranch1,
            end = topBranch2
        )

        drawCircle(
            color = nodeColour,
            radius = 7.8f,
            center = topBranch1
        )

        drawCircle(
            color = nodeColour,
            radius = 8.5f,
            center = topBranch2
        )

        // =========================
        // BOTTOM RIGHT CLUSTER
        // pushed more to the right
        // =========================

        val rBottom = size.width * 0.068f

        val bottomMain = Offset(
            x = size.width * 0.89f,
            y = size.height * 0.73f
        )

        val bottomRight = Offset(
            x = bottomMain.x + root3 * rBottom,
            y = bottomMain.y
        )

        val bottomLowerLeft = Offset(
            x = bottomMain.x - (root3 / 2f) * rBottom,
            y = bottomMain.y + 1.5f * rBottom
        )

        drawHexagon(bottomMain, rBottom)
        drawHexagon(bottomRight, rBottom)
        drawHexagon(bottomLowerLeft, rBottom)

        val bottomBranch = Offset(
            x = bottomMain.x - (root3 / 2f) * rBottom,
            y = bottomMain.y - 1.5f * rBottom
        )

        drawBond(
            start = Offset(
                x = bottomMain.x - (root3 / 2f) * rBottom,
                y = bottomMain.y - 0.5f * rBottom
            ),
            end = bottomBranch
        )

        drawCircle(
            color = nodeColour,
            radius = 7.8f,
            center = bottomBranch
        )
    }
}