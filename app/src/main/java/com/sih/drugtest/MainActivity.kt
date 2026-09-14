package com.sih.drugtest

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.sih.drugtest.model.CvAnalysisResponse
import com.sih.drugtest.model.EvidenceDraft
import com.sih.drugtest.model.TestProtocol
import com.sih.drugtest.network.CvApiClient
import com.sih.drugtest.ui.screens.AnalysisScreen
import com.sih.drugtest.ui.screens.AnalysisState
import com.sih.drugtest.ui.screens.CaptureScreen
import com.sih.drugtest.ui.screens.HomeScreen
import com.sih.drugtest.ui.screens.LoginScreen
import com.sih.drugtest.ui.screens.ProfileScreen
import com.sih.drugtest.ui.screens.ProtocolDetailsScreen
import com.sih.drugtest.ui.screens.SelectTestScreen
import com.sih.drugtest.ui.screens.SignupScreen
import com.sih.drugtest.ui.screens.SplashScreen
import com.sih.drugtest.ui.screens.TestHistoryScreen
import com.sih.drugtest.ui.screens.TestRecordDetailsScreen
import com.sih.drugtest.ui.theme.DrugTestAppTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File


// =============================================================
// SIGNUP MODEL
// =============================================================

@Serializable
data class OfficerSignupRow(

    @SerialName("user_id")
    val userId: String,

    @SerialName("full_name")
    val fullName: String
)


// =============================================================
// OFFICER PROFILE MODEL
// =============================================================

@Serializable
data class OfficerProfileRow(

    @SerialName("officer_id")
    val officerId: String,

    @SerialName("full_name")
    val fullName: String
)


// =============================================================
// MAIN ACTIVITY
// =============================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {

            DrugTestAppTheme {

                DrugTestApp()
            }
        }
    }
}


// =============================================================
// MAIN APP
// =============================================================

@Composable
fun DrugTestApp() {


    // =========================================================
    // START WITH SPLASH SCREEN
    // =========================================================

    var currentScreen by remember {
        mutableStateOf("splash")
    }


    var pendingLaunchScreen by remember {
        mutableStateOf<String?>(null)
    }


    var selectedProtocol by remember {
        mutableStateOf<TestProtocol?>(null)
    }


    var evidenceDraft by remember {
        mutableStateOf<EvidenceDraft?>(null)
    }


    var cvAnalysisResponse by remember {
        mutableStateOf<CvAnalysisResponse?>(null)
    }


    var protocols by remember {
        mutableStateOf<List<TestProtocol>>(emptyList())
    }


    var isCvRequestInProgress by remember {
        mutableStateOf(false)
    }


    var authLoading by remember {
        mutableStateOf(false)
    }


    var signupError by remember {
        mutableStateOf<String?>(null)
    }


    var loginError by remember {
        mutableStateOf<String?>(null)
    }


    // =========================================================
    // SELECTED HISTORY RECORD
    // =========================================================

    var selectedHistoryRecordId by remember {
        mutableStateOf<String?>(null)
    }


    // =========================================================
    // CURRENT OFFICER PROFILE
    // =========================================================

    var currentOfficerName by remember {
        mutableStateOf("")
    }


    var currentOfficerId by remember {
        mutableStateOf("")
    }


    var currentOfficerEmail by remember {
        mutableStateOf("")
    }


    val lifecycleOwner =
        LocalLifecycleOwner.current


    val context =
        LocalContext.current


    // =========================================================
    // LOAD CURRENT OFFICER PROFILE
    // =========================================================

    suspend fun loadOfficerProfile() {

        try {

            val user =
                SupabaseClient.client.auth
                    .currentUserOrNull()


            if (user == null) {

                Log.w(
                    "OfficerProfile",
                    "No authenticated user."
                )

                return
            }


            currentOfficerEmail =
                user.email ?: ""


            val profile =
                SupabaseClient.client
                    .from("officers")
                    .select {

                        filter {

                            eq(
                                "user_id",
                                user.id
                            )
                        }
                    }
                    .decodeSingle<OfficerProfileRow>()


            currentOfficerName =
                profile.fullName


            currentOfficerId =
                profile.officerId


            Log.d(
                "OfficerProfile",
                "Loaded officer: ${profile.officerId}"
            )


        } catch (e: Exception) {

            Log.e(
                "OfficerProfile",
                "Failed to load officer profile",
                e
            )
        }
    }


    // =========================================================
    // SPLASH SCREEN TIMER
    // =========================================================

    LaunchedEffect(Unit) {

        delay(1600)


        if (currentScreen == "splash") {

            currentScreen =
                pendingLaunchScreen ?: "loading"
        }
    }


    // =========================================================
    // RESTORE / OBSERVE SESSION
    // =========================================================

    LaunchedEffect(Unit) {

        SupabaseClient.client.auth
            .sessionStatus
            .collect { status ->

                when (status) {


                    SessionStatus.Initializing -> {

                        Log.d(
                            "AuthSession",
                            "Checking stored session..."
                        )
                    }


                    is SessionStatus.Authenticated -> {

                        Log.d(
                            "AuthSession",
                            "Authenticated session available."
                        )


                        loadOfficerProfile()


                        if (currentScreen == "splash") {

                            pendingLaunchScreen =
                                "home"
                        }

                        else if (
                            currentScreen == "loading" ||
                            currentScreen == "login"
                        ) {

                            currentScreen =
                                "home"
                        }
                    }


                    is SessionStatus.NotAuthenticated -> {

                        Log.d(
                            "AuthSession",
                            "No authenticated session."
                        )


                        if (currentScreen == "splash") {

                            pendingLaunchScreen =
                                "login"
                        }

                        else if (currentScreen == "loading") {

                            currentScreen =
                                "login"
                        }
                    }


                    is SessionStatus.RefreshFailure -> {

                        Log.e(
                            "AuthSession",
                            "Stored session could not be refreshed."
                        )


                        if (currentScreen == "splash") {

                            pendingLaunchScreen =
                                "login"

                        } else {

                            currentScreen =
                                "login"
                        }
                    }
                }
            }
    }


    // =========================================================
    // FETCH PROTOCOLS
    // =========================================================

    LaunchedEffect(Unit) {

        try {

            protocols =
                SupabaseClient.client
                    .from("protocols")
                    .select {

                        filter {

                            eq(
                                "is_active",
                                true
                            )

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


            Log.d(
                "ProtocolFetch",
                "Fetched protocols count: ${protocols.size}"
            )


        } catch (e: Exception) {

            Log.e(
                "ProtocolFetch",
                "Failed to fetch protocols",
                e
            )
        }
    }


    // =========================================================
    // SCREEN NAVIGATION
    // =========================================================

    when (currentScreen) {


        // =====================================================
        // SPLASH
        // =====================================================

        "splash" -> {

            SplashScreen()
        }


        // =====================================================
        // LOADING
        // =====================================================

        "loading" -> {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }


        // =====================================================
        // LOGIN
        // =====================================================

        "login" -> LoginScreen(

            isLoading =
                authLoading,

            errorMessage =
                loginError,

            onCreateAccountClick = {

                loginError =
                    null

                signupError =
                    null

                currentScreen =
                    "signup"
            },

            onLoginClick = { email, password ->

                lifecycleOwner.lifecycleScope.launch {

                    authLoading =
                        true

                    loginError =
                        null


                    try {

                        SupabaseClient.client.auth.signInWith(
                            Email
                        ) {

                            this.email =
                                email

                            this.password =
                                password
                        }


                        val user =
                            SupabaseClient.client.auth
                                .currentUserOrNull()


                        if (user == null) {

                            loginError =
                                "Login failed."

                            return@launch
                        }


                        loadOfficerProfile()


                        Toast.makeText(
                            context,
                            "Login successful",
                            Toast.LENGTH_SHORT
                        ).show()


                        currentScreen =
                            "home"


                    } catch (e: Exception) {

                        Log.e(
                            "Login",
                            "Login failed",
                            e
                        )


                        loginError =
                            "Invalid email or password."


                    } finally {

                        authLoading =
                            false
                    }
                }
            }
        )


        // =====================================================
        // SIGNUP
        // =====================================================

        "signup" -> SignupScreen(

            isLoading =
                authLoading,

            errorMessage =
                signupError,

            onLoginClick = {

                signupError =
                    null

                loginError =
                    null

                currentScreen =
                    "login"
            },

            onSignupClick = { fullName, email, password ->

                lifecycleOwner.lifecycleScope.launch {

                    authLoading =
                        true

                    signupError =
                        null


                    try {

                        SupabaseClient.client.auth.signUpWith(
                            Email
                        ) {

                            this.email =
                                email

                            this.password =
                                password
                        }


                        val user =
                            SupabaseClient.client.auth
                                .currentUserOrNull()


                        if (user == null) {

                            signupError =
                                "Could not create account."

                            return@launch
                        }


                        val officerRow =
                            OfficerSignupRow(

                                userId =
                                    user.id,

                                fullName =
                                    fullName
                            )


                        SupabaseClient.client
                            .from("officers")
                            .insert(
                                officerRow
                            )


                        Toast.makeText(
                            context,
                            "Account created successfully. Please login.",
                            Toast.LENGTH_LONG
                        ).show()


                        SupabaseClient.client.auth
                            .signOut()


                        currentOfficerName =
                            ""

                        currentOfficerId =
                            ""

                        currentOfficerEmail =
                            ""


                        loginError =
                            null


                        currentScreen =
                            "login"


                    } catch (e: Exception) {

                        Log.e(
                            "Signup",
                            "Signup failed",
                            e
                        )


                        signupError =

                            when {

                                e.message
                                    ?.contains(
                                        "user_already_exists",
                                        ignoreCase = true
                                    ) == true -> {

                                    "An account with this email already exists."
                                }


                                e.message
                                    ?.contains(
                                        "permission denied",
                                        ignoreCase = true
                                    ) == true -> {

                                    "Could not create officer profile. Please try again."
                                }


                                else -> {

                                    "Signup failed. Please try again."
                                }
                            }


                    } finally {

                        authLoading =
                            false
                    }
                }
            }
        )


        // =====================================================
// HOME
// =====================================================

        "home" -> HomeScreen(

            officerId =
                currentOfficerId,

            onStartClick = {

                currentScreen =
                    "selectTest"
            },

            onTestsClick = {

                currentScreen =
                    "selectTest"
            },

            onHistoryClick = {

                currentScreen =
                    "history"
            },

            onProfileClick = {

                lifecycleOwner.lifecycleScope.launch {

                    loadOfficerProfile()

                    currentScreen =
                        "profile"
                }
            },

            onRecordClick = { recordId ->

                Log.d(
                    "RecordDetails",
                    "Home recent record selected: $recordId"
                )


                selectedHistoryRecordId =
                    recordId


                currentScreen =
                    "recordDetails"
            }
        )


        // =====================================================
        // PROFILE
        // =====================================================

        "profile" -> ProfileScreen(

            fullName =
                if (currentOfficerName.isBlank()) {
                    "Officer"
                } else {
                    currentOfficerName
                },

            officerId =
                if (currentOfficerId.isBlank()) {
                    "Loading..."
                } else {
                    currentOfficerId
                },

            email =
                if (currentOfficerEmail.isBlank()) {
                    "Loading..."
                } else {
                    currentOfficerEmail
                },


            onLogoutClick = {

                lifecycleOwner.lifecycleScope.launch {

                    try {

                        SupabaseClient.client.auth
                            .signOut()


                        currentOfficerName =
                            ""

                        currentOfficerId =
                            ""

                        currentOfficerEmail =
                            ""


                        selectedProtocol =
                            null


                        evidenceDraft =
                            null


                        cvAnalysisResponse =
                            null


                        selectedHistoryRecordId =
                            null


                        Toast.makeText(
                            context,
                            "Logged out successfully",
                            Toast.LENGTH_SHORT
                        ).show()


                        currentScreen =
                            "login"


                    } catch (e: Exception) {

                        Log.e(
                            "Logout",
                            "Logout failed",
                            e
                        )
                    }
                }
            },

            onHomeClick = {

                currentScreen =
                    "home"
            },

            onTestsClick = {

                currentScreen =
                    "selectTest"
            },

            onHistoryClick = {

                currentScreen =
                    "history"
            }
        )


        // =====================================================
        // SELECT TEST
        // =====================================================

        "selectTest" -> SelectTestScreen(

            protocols =
                protocols,

            onTestSelected = { protocol ->

                selectedProtocol =
                    protocol

                currentScreen =
                    "protocolDetails"
            },

            onBackClick = {

                currentScreen =
                    "home"
            },

            onHomeClick = {

                currentScreen =
                    "home"
            },

            onTestsClick = {

                currentScreen =
                    "selectTest"
            },

            onHistoryClick = {

                currentScreen =
                    "history"
            },

            onProfileClick = {

                lifecycleOwner.lifecycleScope.launch {

                    loadOfficerProfile()

                    currentScreen =
                        "profile"
                }
            }
        )


        // =====================================================
        // PROTOCOL DETAILS
        // =====================================================

        "protocolDetails" -> {

            selectedProtocol?.let { protocol ->

                ProtocolDetailsScreen(

                    protocol =
                        protocol,

                    onBackClick = {

                        currentScreen =
                            "selectTest"
                    },

                    onCaptureClick = {

                        evidenceDraft =
                            null

                        cvAnalysisResponse =
                            null

                        currentScreen =
                            "capture"
                    },

                    onHomeClick = {

                        currentScreen =
                            "home"
                    },

                    onTestsClick = {

                        currentScreen =
                            "selectTest"
                    },

                    onHistoryClick = {

                        currentScreen =
                            "history"
                    },

                    onProfileClick = {

                        lifecycleOwner.lifecycleScope.launch {

                            loadOfficerProfile()

                            currentScreen =
                                "profile"
                        }
                    }
                )
            }
        }


        // =====================================================
        // CAMERA CAPTURE
        // =====================================================

        "capture" -> CaptureScreen(

            onBackClick = {

                if (!isCvRequestInProgress) {

                    currentScreen =
                        "protocolDetails"
                }
            },

            retakeMessage =

                if (
                    cvAnalysisResponse
                        ?.result
                        ?.equals(
                            "RETAKE",
                            ignoreCase = true
                        ) == true
                ) {

                    cvAnalysisResponse?.reason

                } else {

                    null
                },

            analysisInProgress =
                isCvRequestInProgress,

            onCaptureClick = { draft ->


                if (isCvRequestInProgress) {

                    File(
                        draft.imagePath
                    ).delete()

                    return@CaptureScreen
                }


                val protocol =
                    selectedProtocol


                if (protocol == null) {

                    File(
                        draft.imagePath
                    ).delete()

                    return@CaptureScreen
                }


                // -------------------------------------------------
                // REAL OFFICER ID
                // -------------------------------------------------

                if (currentOfficerId.isBlank()) {

                    Log.e(
                        "CvApi",
                        "Officer ID is unavailable."
                    )

                    File(
                        draft.imagePath
                    ).delete()

                    return@CaptureScreen
                }


                val enrichedDraft =
                    draft.copy(

                        officerId =
                            currentOfficerId,

                        protocolId =
                            protocol.id,

                        testId =
                            protocol.testId,

                        protocolVersion =
                            protocol.version
                    )


                val latitude =
                    enrichedDraft.latitude


                val longitude =
                    enrichedDraft.longitude


                if (
                    latitude == null ||
                    longitude == null
                ) {

                    File(
                        enrichedDraft.imagePath
                    ).delete()

                    return@CaptureScreen
                }


                cvAnalysisResponse =
                    null


                isCvRequestInProgress =
                    true


                lifecycleOwner.lifecycleScope.launch {

                    try {

                        val response =
                            CvApiClient.analyzeImage(

                                imagePath =
                                    enrichedDraft.imagePath,

                                testId =
                                    enrichedDraft.testId
                                        ?: throw IllegalStateException(
                                            "Missing test ID"
                                        ),

                                officerId =
                                    enrichedDraft.officerId
                                        ?: throw IllegalStateException(
                                            "Missing officer ID"
                                        ),

                                latitude =
                                    latitude,

                                longitude =
                                    longitude,

                                capturedAt =
                                    enrichedDraft.capturedAt
                            )


                        // =================================================
                        // CV RESPONSE LOGGING
                        // =================================================

                        Log.d(
                            "CvResult",
                            "Result=${response.result}"
                        )

                        Log.d(
                            "CvResult",
                            "Reason=${response.reason}"
                        )

                        Log.d(
                            "CvResult",
                            "ReferenceCardDetected=${response.referenceCardDetected}"
                        )

                        Log.d(
                            "CvResult",
                            "CalibrationApplied=${response.calibrationApplied}"
                        )

                        Log.d(
                            "CvResult",
                            "ObservedRGB=${response.observedRgb}"
                        )

                        Log.d(
                            "CvResult",
                            "ExpectedRGB=${response.expectedRgb}"
                        )

                        Log.d(
                            "CvResult",
                            "Distance=${response.distance}"
                        )

                        Log.d(
                            "CvResult",
                            "Tolerance=${response.tolerance}"
                        )

                        Log.d(
                            "CvResult",
                            "Saved=${response.saved}"
                        )

                        Log.d(
                            "CvResult",
                            "ImageStoragePath=${response.imageStoragePath}"
                        )


                        // =================================================
                        // RETAKE
                        // =================================================

                        if (
                            response.result.equals(
                                "RETAKE",
                                ignoreCase = true
                            )
                        ) {

                            Log.w(
                                "CvResult",
                                "RETAKE REQUIRED: ${response.reason}"
                            )


                            File(
                                enrichedDraft.imagePath
                            ).delete()


                            evidenceDraft =
                                null


                            cvAnalysisResponse =
                                response


                            currentScreen =
                                "capture"


                            return@launch
                        }


                        // =================================================
                        // ERROR
                        // =================================================

                        if (
                            response.result.equals(
                                "ERROR",
                                ignoreCase = true
                            )
                        ) {

                            Log.e(
                                "CvResult",
                                "CV RESULT ERROR: ${response.reason}"
                            )


                            evidenceDraft =
                                null


                            cvAnalysisResponse =
                                response


                            currentScreen =
                                "capture"


                            return@launch
                        }


                        // =================================================
                        // SUCCESSFUL ANALYSIS
                        // =================================================

                        Log.d(
                            "CvResult",
                            "ANALYSIS COMPLETED SUCCESSFULLY"
                        )


                        val apiHash =
                            response.imageSha256


                        if (
                            apiHash != null &&
                            apiHash != enrichedDraft.imageSha256
                        ) {

                            throw IllegalStateException(
                                "SHA-256 mismatch between Android and Python API."
                            )
                        }


                        val completedDraft =
                            enrichedDraft.copy(

                                imageStoragePath =
                                    response.imageStoragePath
                            )


                        evidenceDraft =
                            completedDraft


                        cvAnalysisResponse =
                            response


                        currentScreen =
                            "analysis"


                    } catch (e: Exception) {

                        Log.e(
                            "CvApi",
                            "CV API request failed",
                            e
                        )


                        Log.e(
                            "CvResult",
                            "CV REQUEST FAILED: ${e.message}",
                            e
                        )


                        evidenceDraft =
                            null


                    } finally {

                        isCvRequestInProgress =
                            false
                    }
                }
            }
        )


        // =====================================================
// ANALYSIS
// =====================================================

        "analysis" -> {

            val response =
                cvAnalysisResponse


            AnalysisScreen(

                state =
                    AnalysisState(

                        progress =
                            100,

                        imageCaptured =
                            evidenceDraft != null,

                        referenceCardDetected =
                            response
                                ?.referenceCardDetected
                                ?: false,

                        colourExtracted =
                            response
                                ?.observedRgb
                                    != null,

                        aiAnalysisInProgress =
                            false,

                        resultGenerated =
                            response != null
                    ),

                result =
                    response?.result,

                observedRgb =
                    response?.observedRgb,

                expectedRgb =
                    response?.expectedRgb,

                distance =
                    response?.distance,

                tolerance =
                    response?.tolerance,

                referenceCardDetected =
                    response
                        ?.referenceCardDetected
                        ?: false,

                calibrationApplied =
                    response
                        ?.calibrationApplied
                        ?: false,

                saved =
                    response
                        ?.saved
                        ?: false,


                // =================================================
                // VIEW EXACT SAVED RECORD
                // =================================================

                onViewRecordClick = {

                    val storagePath =
                        response?.imageStoragePath


                    if (storagePath.isNullOrBlank()) {

                        Toast.makeText(
                            context,
                            "Saved test record is unavailable",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        lifecycleOwner.lifecycleScope.launch {

                            try {

                                Log.d(
                                    "RecordDetails",
                                    "Finding record for image: $storagePath"
                                )


                                val savedRecord =
                                    SupabaseClient.client
                                        .from("test_records")
                                        .select {

                                            filter {

                                                eq(
                                                    "image_storage_path",
                                                    storagePath
                                                )
                                            }
                                        }
                                        .decodeSingle<SavedTestRecordRow>()


                                Log.d(
                                    "RecordDetails",
                                    "Analysis record selected: ${savedRecord.id}"
                                )


                                selectedHistoryRecordId =
                                    savedRecord.id


                                currentScreen =
                                    "recordDetails"


                            } catch (e: Exception) {

                                Log.e(
                                    "RecordDetails",
                                    "Could not open saved test record",
                                    e
                                )


                                Toast.makeText(
                                    context,
                                    "Could not open test record",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },


                // =================================================
                // DONE -> HOME
                // =================================================

                onDoneClick = {

                    Log.d(
                        "AnalysisScreen",
                        "Done clicked - returning to Home"
                    )


                    selectedProtocol =
                        null


                    evidenceDraft =
                        null


                    cvAnalysisResponse =
                        null


                    currentScreen =
                        "home"
                }
            )
        }
        // =====================================================
        // HISTORY
        // =====================================================

        "history" -> TestHistoryScreen(

            officerId =
                currentOfficerId,

            onBackClick = {

                currentScreen =
                    "home"
            },

            onRecordClick = { record ->

                Log.d(
                    "RecordDetails",
                    "History record selected: ${record.id}"
                )


                selectedHistoryRecordId =
                    record.id


                currentScreen =
                    "recordDetails"
            },

            onHomeClick = {

                currentScreen =
                    "home"
            },

            onTestsClick = {

                currentScreen =
                    "selectTest"
            },

            onProfileClick = {

                lifecycleOwner.lifecycleScope.launch {

                    loadOfficerProfile()

                    currentScreen =
                        "profile"
                }
            }
        )


        // =====================================================
        // TEST RECORD DETAILS
        // =====================================================

        "recordDetails" -> {

            val recordId =
                selectedHistoryRecordId


            if (recordId != null) {

                TestRecordDetailsScreen(

                    recordId =
                        recordId,

                    onBackClick = {

                        currentScreen =
                            "history"
                    },

                    onHomeClick = {

                        selectedHistoryRecordId =
                            null


                        currentScreen =
                            "home"
                    },

                    onTestsClick = {

                        selectedHistoryRecordId =
                            null


                        currentScreen =
                            "selectTest"
                    },

                    onHistoryClick = {

                        currentScreen =
                            "history"
                    },

                    onProfileClick = {

                        lifecycleOwner.lifecycleScope.launch {

                            selectedHistoryRecordId =
                                null


                            loadOfficerProfile()


                            currentScreen =
                                "profile"
                        }
                    }
                )

            } else {

                Box(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }
        }
    }
}

// =============================================================
// SAVED TEST RECORD ID MODEL
// =============================================================

@Serializable
data class SavedTestRecordRow(

    val id: String
)