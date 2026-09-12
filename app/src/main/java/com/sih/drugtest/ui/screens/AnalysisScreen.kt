package com.sih.drugtest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnalysisScreen(
    state: AnalysisState
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Screen title
        Text(
            text = "Analysing...",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Description
        Text(
            text = "Our AI is analysing the colour reaction\n" +
                    "against standard reference data.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // Progress circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(180.dp)
        ) {

            CircularProgressIndicator(
                progress = {
                    state.progress / 100f
                },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${state.progress}%",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // Analysis steps
        AnalysisStep(
            completed = state.imageCaptured,
            current = false,
            text = "Image captured"
        )

        AnalysisStep(
            completed = state.referenceCardDetected,
            current = false,
            text = "Reference card detected"
        )

        AnalysisStep(
            completed = state.colourExtracted,
            current = false,
            text = "Colour extraction"
        )

        AnalysisStep(
            completed = false,
            current = state.aiAnalysisInProgress,
            text = "AI analysis in progress"
        )

        AnalysisStep(
            completed = state.resultGenerated,
            current = false,
            text = "Generating result"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // Information card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "⚡",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "Please do not close the app\n" +
                            "while analysis is in progress.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun AnalysisStep(
    completed: Boolean,
    current: Boolean,
    text: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Circle containing the status symbol
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    when {
                        completed -> MaterialTheme.colorScheme.primary
                        current -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = when {
                    completed -> "✓"
                    current -> "•"
                    else -> "○"
                },
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = text,
            style = if (current) {
                MaterialTheme.typography.bodyLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}