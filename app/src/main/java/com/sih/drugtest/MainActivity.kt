package com.sih.drugtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sih.drugtest.ui.screens.AnalysisScreen
import com.sih.drugtest.ui.screens.AnalysisState
import com.sih.drugtest.ui.screens.CaptureScreen
import com.sih.drugtest.ui.screens.HomeScreen
import com.sih.drugtest.ui.screens.SelectTestScreen
import com.sih.drugtest.ui.theme.DrugTestAppTheme
import com.sih.drugtest.ui.screens.TestHistoryScreen




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
            },
            onHistoryClick = {
                currentScreen = "history"
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
        "history" -> TestHistoryScreen(
            onBackClick = {
                currentScreen = "home"
            },

            onRecordClick = { record ->
                // Record Details screen will be connected later
            },

            onHomeClick = {
                currentScreen = "home"
            },

            onTestsClick = {
                currentScreen = "selectTest"
            }
        )
    }
}