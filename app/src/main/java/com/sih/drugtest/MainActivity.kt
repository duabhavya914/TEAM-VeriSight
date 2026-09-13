package com.sih.drugtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sih.drugtest.model.TestProtocol
import com.sih.drugtest.ui.screens.AnalysisScreen
import com.sih.drugtest.ui.screens.AnalysisState
import com.sih.drugtest.ui.screens.CaptureScreen
import com.sih.drugtest.ui.screens.HomeScreen
import com.sih.drugtest.ui.screens.ProtocolDetailsScreen
import com.sih.drugtest.ui.screens.SelectTestScreen
import com.sih.drugtest.ui.screens.TestHistoryScreen
import com.sih.drugtest.ui.theme.DrugTestAppTheme
import io.github.jan.supabase.postgrest.from


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

    var selectedProtocol by remember {
        mutableStateOf<TestProtocol?>(null)
    }

    var capturedImagePath by remember {
        mutableStateOf<String?>(null)
    }

    var protocols by remember {
        mutableStateOf<List<TestProtocol>>(emptyList())
    }


    // ---------------------------------------------------------
    // FETCH PROTOCOLS FROM SUPABASE
    // ---------------------------------------------------------

    LaunchedEffect(Unit) {

        try {

            protocols = SupabaseClient.client
                .from("protocols")
                .select {

                    filter {

                        eq("is_active", true)

                        isIn(
                            "test_id",
                            listOf(
                                "HER-001",
                                "CAN-001",
                                "COC-001",
                                "AMP-001",
                                "BAR-001"
                            )
                        )
                    }
                }
                .decodeList<TestProtocol>()

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }


    // ---------------------------------------------------------
    // SIMPLE SCREEN NAVIGATION
    // ---------------------------------------------------------

    when (currentScreen) {


        // -----------------------------------------------------
        // HOME
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // SELECT TEST
        // -----------------------------------------------------

        "selectTest" -> SelectTestScreen(

            protocols = protocols,

            onTestSelected = { protocol ->

                selectedProtocol = protocol

                currentScreen = "protocolDetails"
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


        // -----------------------------------------------------
        // PROTOCOL DETAILS
        // -----------------------------------------------------

        "protocolDetails" -> {

            selectedProtocol?.let { protocol ->

                ProtocolDetailsScreen(

                    protocol = protocol,

                    onBackClick = {
                        currentScreen = "selectTest"
                    },

                    onCaptureClick = {
                        currentScreen = "capture"
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


        // -----------------------------------------------------
        // CAMERA CAPTURE
        // -----------------------------------------------------

        "capture" -> CaptureScreen(

            onBackClick = {
                currentScreen = "protocolDetails"
            },

            onCaptureClick = { imagePath ->

                capturedImagePath = imagePath

                currentScreen = "analysis"
            }
        )


        // -----------------------------------------------------
        // ANALYSIS
        // -----------------------------------------------------

        "analysis" -> AnalysisScreen(

            state = AnalysisState(

                progress = 78,

                imageCaptured = capturedImagePath != null,

                referenceCardDetected = true,

                colourExtracted = true,

                aiAnalysisInProgress = true,

                resultGenerated = false
            )
        )


        // -----------------------------------------------------
        // HISTORY
        // -----------------------------------------------------

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