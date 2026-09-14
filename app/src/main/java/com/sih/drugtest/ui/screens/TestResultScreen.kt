package com.sih.drugtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TestResultScreen(
    onBackClick: () -> Unit,
    onRetakeClick: () -> Unit,
    onSaveRecordClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF062A47)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // -----------------------------
            // TOP BAR
            // -----------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Test Result",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF3976AD),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                ) {
                    Text(
                        text = "v1.2",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Marquis Test",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(
                    start = 8.dp,
                    bottom = 12.dp
                )
            )

            // -----------------------------
            // MAIN WHITE CARD
            // -----------------------------

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFCF5)
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    // -------------------------
                    // RESULT
                    // -------------------------

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFFE5E5),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color(0xFFE53935),
                                    shape = RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "!",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {

                            Text(
                                text = "Presumptive Positive",
                                color = Color(0xFF9B1717),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Marquis Test",
                                color = Color(0xFF182B45),
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // -------------------------
                    // CONFIDENCE
                    // -------------------------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Confidence",
                            color = Color(0xFF10284B),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "87%",
                            color = Color(0xFF10284B),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { 0.87f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFFE53935),
                        trackColor = Color(0xFFD9DEE3)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // -------------------------
                    // COLOUR COMPARISON
                    // -------------------------

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            ColourSample(
                                title = "Observed colour",
                                colour = Color(0xFFB45C3E)
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(120.dp)
                                    .background(Color.LightGray)
                            )

                            ColourSample(
                                title = "Expected colour",
                                colour = Color(0xFF7E426C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // -------------------------
                    // CALIBRATION MESSAGE
                    // -------------------------

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFDDF6E7),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Calibration passed",
                            tint = Color(0xFF00874E),
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {

                            Text(
                                text = "Calibration passed",
                                color = Color(0xFF075C37),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "Image and lighting conditions are valid.",
                                color = Color(0xFF075C37),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // -------------------------
                    // INFORMATION MESSAGE
                    // -------------------------

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFDDEEFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information",
                            tint = Color(0xFF15558A),
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {

                            Text(
                                text = "This is a presumptive result.",
                                color = Color(0xFF10284B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Text(
                                text = "Confirm with laboratory analysis.",
                                color = Color(0xFF15558A),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // -------------------------
                    // BUTTONS
                    // -------------------------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        OutlinedButton(
                            onClick = onRetakeClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Retake",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onSaveRecordClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF063B65)
                            )
                        ) {
                            Text(
                                text = "Save Record",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ColourSample(
    title: String,
    colour: Color
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color = Color(0xFF10284B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .size(105.dp)
                .background(
                    color = colour,
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
        )
    }
}