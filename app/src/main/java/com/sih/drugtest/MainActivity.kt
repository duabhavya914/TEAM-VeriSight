package com.sih.drugtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sih.drugtest.ui.screens.HomeScreen
import com.sih.drugtest.ui.screens.SelectTestScreen
import com.sih.drugtest.ui.theme.DrugTestAppTheme


// Structure for records that will later come from the database

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrugTestAppTheme {
                DrugTestApp()
            }
        }
    }
}


@Composable
fun DrugTestApp() {

    var currentScreen by remember {
        mutableStateOf("home")
    }

    when (currentScreen) {

        "home" -> HomeScreen(
            recentRecords = emptyList(),
            onStartClick = {
                currentScreen = "selectTest"
            },
            onTestsClick = {
                currentScreen = "selectTest"
            }
        )

        "selectTest" -> SelectTestScreen(
            protocols = emptyList(),

            onTestSelected = { _ ->
                currentScreen = "capture"
            },

            onBackClick = {
                currentScreen = "home"
            },

            onHomeClick = {
                currentScreen = "home"
            },

            onTestsClick = {
                currentScreen = "selectTest"
            }
        )

        "capture" -> CaptureScreen(
            onBackClick = {
                currentScreen = "selectTest"
            },
            onCaptureClick = {
                currentScreen = "analysis"
            }
        )

        "analysis" -> AnalysisScreen(
            state = AnalysisState(
                progress = 78,
                imageCaptured = true,
                referenceCardDetected = true,
                colourExtracted = true,
                aiAnalysisInProgress = true,
                resultGenerated = false
            )
        )
    }
}


@Composable
fun CaptureScreen(
    onBackClick: () -> Unit,
    onCaptureClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Capture Test Image",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Camera will be added here.",
            modifier = Modifier.padding(
                top = 12.dp,
                bottom = 24.dp
            )
        )

        Button(
            onClick = onBackClick
        ) {
            Text("Back")
        }
    }
}