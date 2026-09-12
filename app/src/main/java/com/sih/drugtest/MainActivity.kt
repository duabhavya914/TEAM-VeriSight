package com.sih.drugtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sih.drugtest.model.TestProtocol
import com.sih.drugtest.model.TestRecord
import com.sih.drugtest.ui.screens.AnalysisScreen
import com.sih.drugtest.ui.screens.CaptureScreen
import com.sih.drugtest.ui.screens.HomeScreen
import com.sih.drugtest.ui.screens.LocationMapScreen
import com.sih.drugtest.ui.screens.ProtocolGuideScreen
import com.sih.drugtest.ui.screens.RecordDetailsData
import com.sih.drugtest.ui.screens.RecordDetailsScreen
import com.sih.drugtest.ui.screens.SaveTestRecordData
import com.sih.drugtest.ui.screens.SaveTestRecordScreen
import com.sih.drugtest.ui.screens.SelectTestScreen
import com.sih.drugtest.ui.screens.SettingsScreen
import com.sih.drugtest.ui.screens.SplashScreen
import com.sih.drugtest.ui.screens.TestHistoryRecord
import com.sih.drugtest.ui.screens.TestHistoryScreen
import com.sih.drugtest.ui.screens.TestResultScreen
import com.sih.drugtest.ui.theme.DrugTestAppTheme

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
        mutableStateOf("splash")
    }

    val protocolsList = remember {
        listOf(
            TestProtocol(
                id = "marquis",
                name = "Marquis Test",
                category = "Detects MDMA, amphetamines",
                version = "v1.2"
            ),
            TestProtocol(
                id = "cobalt",
                name = "Cobalt Thiocyanate Test",
                category = "Detects cocaine",
                version = "v1.1"
            ),
            TestProtocol(
                id = "duquenois",
                name = "Duquenois-Levine Test",
                category = "Detects cannabis (THC)",
                version = "v1.0"
            ),
            TestProtocol(
                id = "mandelin",
                name = "Mandelin Test",
                category = "Detects amphetamines, opioids",
                version = "v1.1"
            ),
            TestProtocol(
                id = "froehde",
                name = "Froehde Test",
                category = "Detects opioids",
                version = "v1.0"
            ),
            TestProtocol(
                id = "scott",
                name = "Scott Test",
                category = "Detects cocaine (presumptive)",
                version = "v1.0"
            )
        )
    }

    var selectedProtocol by remember {
        mutableStateOf(protocolsList.first())
    }

    var savedHistoryRecords by remember {
        mutableStateOf(
            listOf(
                TestHistoryRecord(
                    testName = "Marquis Test",
                    dateTime = "16 Apr 2025, 14:32",
                    result = "Positive"
                ),
                TestHistoryRecord(
                    testName = "Cobalt Thiocyanate",
                    dateTime = "14 Apr 2025, 11:20",
                    result = "Negative"
                ),
                TestHistoryRecord(
                    testName = "Duquenois-Levine",
                    dateTime = "12 Apr 2025, 09:15",
                    result = "Inconclusive"
                ),
                TestHistoryRecord(
                    testName = "Mandelin Test",
                    dateTime = "10 Apr 2025, 16:40",
                    result = "Negative"
                ),
                TestHistoryRecord(
                    testName = "Marquis Test",
                    dateTime = "08 Apr 2025, 13:05",
                    result = "Positive"
                )
            )
        )
    }

    var recentRecordsList by remember {
        mutableStateOf(
            listOf(
                TestRecord(
                    recordId = "FTS-2025-003482",
                    testName = "Marquis Test",
                    result = "Presumptive Positive",
                    dateTime = "16 Apr 2025, 14:32"
                ),
                TestRecord(
                    recordId = "FTS-2025-003481",
                    testName = "Cobalt Thiocyanate",
                    result = "Negative",
                    dateTime = "14 Apr 2025, 11:20"
                )
            )
        )
    }

    var currentRecordDetailsData by remember {
        mutableStateOf(
            RecordDetailsData(
                recordId = "FTS-2025-003482",
                testName = "Marquis Test",
                result = "Presumptive Positive",
                confidence = "87%",
                dateTime = "16 Apr 2025, 14:32",
                operatorId = "IN-TN-1047",
                location = "12.9716° N, 77.5946° E",
                imageSha256 = "3f2a6e9c4d7b1a0e...",
                protocolVersion = "v1.2",
                verificationStatus = "Verified"
            )
        )
    }

    when (currentScreen) {

        /*
         * 1. SPLASH SCREEN
         */
        "splash" -> SplashScreen(
            onSplashComplete = {
                currentScreen = "home"
            }
        )

        /*
         * 2. HOME DASHBOARD
         */
        "home" -> HomeScreen(
            recentRecords = recentRecordsList,
            onStartClick = {
                currentScreen = "selectTest"
            },
            onTestsClick = {
                currentScreen = "selectTest"
            },
            onHistoryClick = {
                currentScreen = "history"
            },
            onSettingsClick = {
                currentScreen = "settings"
            },
            onRecordClick = { record ->
                currentRecordDetailsData = RecordDetailsData(
                    recordId = record.recordId,
                    testName = record.testName,
                    result = record.result,
                    confidence = "87%",
                    dateTime = record.dateTime,
                    operatorId = "IN-TN-1047",
                    location = "12.9716° N, 77.5946° E",
                    imageSha256 = "3f2a6e9c4d7b1a0e...",
                    protocolVersion = "v1.2"
                )
                currentScreen = "recordDetails"
            }
        )

        /*
         * 3. SELECT TEST PROTOCOL
         */
        "selectTest" -> SelectTestScreen(
            protocols = protocolsList,
            onTestSelected = { protocol ->
                selectedProtocol = protocol
                currentScreen = "protocolGuide"
            },
            onBackClick = {
                currentScreen = "home"
            },
            onHomeClick = {
                currentScreen = "home"
            },
            onTestsClick = {
                currentScreen = "selectTest"
            },
            onHistoryClick = {
                currentScreen = "history"
            },
            onSettingsClick = {
                currentScreen = "settings"
            }
        )

        /*
         * 4. PROTOCOL GUIDE
         */
        "protocolGuide" -> ProtocolGuideScreen(
            protocol = selectedProtocol,
            onBackClick = {
                currentScreen = "selectTest"
            },
            onContinueToCapture = {
                currentScreen = "capture"
            }
        )

        /*
         * 5. CAMERA CAPTURE
         */
        "capture" -> CaptureScreen(
            onBackClick = {
                currentScreen = "protocolGuide"
            },
            onCaptureClick = {
                currentScreen = "analysis"
            }
        )

        /*
         * 6. ANALYSIS PROCESSING
         */
        "analysis" -> AnalysisScreen(
            onAnalysisComplete = {
                currentScreen = "testResult"
            }
        )

        /*
         * 7. TEST RESULT
         */
        "testResult" -> TestResultScreen(
            onBackClick = {
                currentScreen = "home"
            },
            onRetakeClick = {
                currentScreen = "capture"
            },
            onSaveRecordClick = {
                currentScreen = "saveRecord"
            }
        )

        /*
         * 8. SAVE RECORD
         */
        "saveRecord" -> SaveTestRecordScreen(
            data = SaveTestRecordData(
                testName = selectedProtocol.name,
                result = "Presumptive Positive",
                dateTime = "16 Apr 2025, 14:32",
                operatorId = "IN-TN-1047",
                location = "12.9716° N, 77.5946° E"
            ),
            onBackClick = {
                currentScreen = "testResult"
            },
            onLocationClick = {
                currentScreen = "locationMap"
            },
            onSaveClick = { _, _, _ ->
                val newHistoryRecord = TestHistoryRecord(
                    testName = selectedProtocol.name,
                    dateTime = "16 Apr 2025, 14:32",
                    result = "Positive"
                )
                savedHistoryRecords = listOf(newHistoryRecord) + savedHistoryRecords
                recentRecordsList = listOf(
                    TestRecord(
                        recordId = "FTS-2025-003483",
                        testName = selectedProtocol.name,
                        result = "Presumptive Positive",
                        dateTime = "16 Apr 2025, 14:32"
                    )
                ) + recentRecordsList
                currentScreen = "history"
            },
            onHomeClick = {
                currentScreen = "home"
            },
            onTestsClick = {
                currentScreen = "selectTest"
            },
            onHistoryClick = {
                currentScreen = "history"
            },
            onSettingsClick = {
                currentScreen = "settings"
            }
        )

        /*
         * 9. RECORD DETAILS
         */
        "recordDetails" -> RecordDetailsScreen(
            data = currentRecordDetailsData,
            onBackClick = {
                currentScreen = "history"
            },
            onLocationClick = {
                currentScreen = "locationMap"
            },
            onViewEvidenceClick = {},
            onShareReportClick = {},
            onExportPdfClick = {}
        )

        /*
         * 10. TEST HISTORY
         */
        "history" -> TestHistoryScreen(
            customRecords = savedHistoryRecords,
            onBackClick = {
                currentScreen = "home"
            },
            onRecordClick = { record ->
                currentRecordDetailsData = RecordDetailsData(
                    recordId = "FTS-2025-003482",
                    testName = record.testName,
                    result = record.result,
                    confidence = "87%",
                    dateTime = record.dateTime,
                    operatorId = "IN-TN-1047",
                    location = "12.9716° N, 77.5946° E",
                    imageSha256 = "3f2a6e9c4d7b1a0e...",
                    protocolVersion = "v1.2"
                )
                currentScreen = "recordDetails"
            },
            onHomeClick = {
                currentScreen = "home"
            },
            onTestsClick = {
                currentScreen = "selectTest"
            },
            onHistoryClick = {
                currentScreen = "history"
            },
            onSettingsClick = {
                currentScreen = "settings"
            }
        )

        /*
         * 11. LOCATION MAP
         */
        "locationMap" -> LocationMapScreen(
            location = "12.9716° N, 77.5946° E",
            placeName = "Bengaluru, Karnataka",
            dateTime = "16 Apr 2025, 14:32",
            onBackClick = {
                currentScreen = "saveRecord"
            }
        )

        /*
         * 12. SETTINGS & PROFILE
         */
        "settings" -> SettingsScreen(
            officerId = "IN-TN-1047",
            onBackClick = {
                currentScreen = "home"
            },
            onHomeClick = {
                currentScreen = "home"
            },
            onTestsClick = {
                currentScreen = "selectTest"
            },
            onHistoryClick = {
                currentScreen = "history"
            },
            onLogOutClick = {
                currentScreen = "splash"
            }
        )
    }
}
