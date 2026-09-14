package com.sih.drugtest.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sih.drugtest.model.EvidenceDraft
import com.sih.drugtest.util.EvidenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import kotlin.coroutines.resume


// =============================================================
// COLOURS
// =============================================================

private val CaptureBackground = Color(0xFF001B36)
private val CaptureSurface = Color(0xFF062B4B)
private val CaptureSurfaceLight = Color(0xFF0A365B)
private val CaptureCyan = Color(0xFF18DDEB)
private val CaptureIvory = Color(0xFFF7F2E7)
private val CaptureMuted = Color(0xFF9DB2C5)
private val CaptureBorder = Color(0xFF155078)


// =============================================================
// CAPTURE SCREEN
// =============================================================

@Composable
fun CaptureScreen(
    onBackClick: () -> Unit,
    onCaptureClick: (EvidenceDraft) -> Unit,
    retakeMessage: String? = null,
    analysisInProgress: Boolean = false
) {

    val context =
        LocalContext.current

    val lifecycleOwner =
        LocalLifecycleOwner.current


    var cameraPermissionGranted by remember {

        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    var locationPermissionGranted by remember {

        mutableStateOf(

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||

                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }


    var captureMessage by remember {
        mutableStateOf<String?>(null)
    }


    var isCapturing by remember {
        mutableStateOf(false)
    }


    val imageCapture =
        remember {

            ImageCapture.Builder()
                .setCaptureMode(
                    ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
                )
                .build()
        }


    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            cameraPermissionGranted =
                isGranted
        }


    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] == true

            val coarseGranted =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] == true

            locationPermissionGranted =
                fineGranted || coarseGranted
        }


    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CaptureBackground)
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    )
                    .padding(
                        top = 14.dp,
                        bottom = 28.dp
                    )
        ) {


            // =================================================
            // HEADER
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(44.dp),
                    shape =
                        CircleShape,
                    color =
                        CaptureSurface,
                    border =
                        BorderStroke(
                            1.dp,
                            CaptureBorder
                        )
                ) {

                    IconButton(
                        onClick = onBackClick,
                        enabled =
                            !isCapturing &&
                                    !analysisInProgress
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                "Back",
                            tint =
                                if (
                                    isCapturing ||
                                    analysisInProgress
                                ) {
                                    CaptureMuted
                                } else {
                                    CaptureIvory
                                }
                        )
                    }
                }


                Column(
                    modifier =
                        Modifier.padding(
                            start = 14.dp
                        )
                ) {

                    Text(
                        text =
                            "Capture Test Image",
                        color =
                            CaptureIvory,
                        fontSize =
                            24.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Frame the reaction and reference card",
                        color =
                            CaptureMuted,
                        fontSize =
                            13.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )


            // =================================================
            // CAMERA AVAILABLE
            // =================================================

            if (cameraPermissionGranted) {


                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(405.dp)
                            .clip(
                                RoundedCornerShape(
                                    26.dp
                                )
                            )
                            .border(
                                width = 1.dp,
                                color =
                                    CaptureBorder,
                                shape =
                                    RoundedCornerShape(
                                        26.dp
                                    )
                            )
                ) {

                    CameraPreview(
                        imageCapture =
                            imageCapture,
                        modifier =
                            Modifier.fillMaxSize()
                    )


                    Surface(
                        modifier =
                            Modifier
                                .align(
                                    Alignment.TopStart
                                )
                                .padding(14.dp),
                        shape =
                            RoundedCornerShape(
                                50.dp
                            ),
                        color =
                            Color.Black.copy(
                                alpha = 0.55f
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 7.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier =
                                    Modifier
                                        .size(7.dp)
                                        .background(
                                            CaptureCyan,
                                            CircleShape
                                        )
                            )

                            Text(
                                text =
                                    "  LIVE CAMERA",
                                color =
                                    Color.White,
                                fontSize =
                                    11.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                letterSpacing =
                                    0.6.sp
                            )
                        }
                    }


                    CameraScanOverlay(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(22.dp)
                    )


                    if (
                        analysisInProgress ||
                        isCapturing
                    ) {

                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Black.copy(
                                            alpha = 0.50f
                                        )
                                    ),
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Surface(
                                shape =
                                    RoundedCornerShape(
                                        20.dp
                                    ),
                                color =
                                    CaptureSurface.copy(
                                        alpha = 0.96f
                                    ),
                                border =
                                    BorderStroke(
                                        1.dp,
                                        CaptureCyan.copy(
                                            alpha = 0.5f
                                        )
                                    )
                            ) {

                                Column(
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 28.dp,
                                            vertical = 22.dp
                                        ),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    CircularProgressIndicator(
                                        modifier =
                                            Modifier.size(
                                                34.dp
                                            ),
                                        color =
                                            CaptureCyan,
                                        strokeWidth =
                                            3.dp
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                14.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            if (
                                                analysisInProgress
                                            ) {
                                                "Analysing test"
                                            } else {
                                                "Securing evidence"
                                            },
                                        color =
                                            CaptureIvory,
                                        fontWeight =
                                            FontWeight.Bold,
                                        fontSize =
                                            16.sp
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                4.dp
                                            )
                                    )

                                    Text(
                                        text =
                                            if (
                                                analysisInProgress
                                            ) {
                                                "Please keep this screen open"
                                            } else {
                                                "Preparing image and location"
                                            },
                                        color =
                                            CaptureMuted,
                                        fontSize =
                                            12.sp
                                    )
                                }
                            }
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    color =
                        CaptureSurface,
                    border =
                        BorderStroke(
                            1.dp,
                            CaptureBorder
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.padding(
                                14.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(
                                    34.dp
                                ),
                            shape =
                                CircleShape,
                            color =
                                CaptureCyan.copy(
                                    alpha = 0.14f
                                )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.Info,
                                    contentDescription =
                                        null,
                                    tint =
                                        CaptureCyan,
                                    modifier =
                                        Modifier.size(
                                            19.dp
                                        )
                                )
                            }
                        }


                        Text(
                            text =
                                "Keep the test reaction and the full reference colour card clearly visible inside the frame.",
                            color =
                                Color(0xFFD8E3EC),
                            fontSize =
                                13.sp,
                            lineHeight =
                                18.sp,
                            modifier =
                                Modifier.padding(
                                    start = 12.dp
                                )
                        )
                    }
                }


                if (retakeMessage != null) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )

                    RetakeWarningCard(
                        reason =
                            retakeMessage
                    )
                }


                if (!locationPermissionGranted) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),
                        color =
                            CaptureSurface,
                        border =
                            BorderStroke(
                                1.dp,
                                Color(0xFFB98B2F)
                                    .copy(
                                        alpha = 0.7f
                                    )
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(
                                    16.dp
                                )
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.LocationOn,
                                    contentDescription =
                                        null,
                                    tint =
                                        Color(0xFFFFC857)
                                )

                                Text(
                                    text =
                                        " Location required",
                                    color =
                                        CaptureIvory,
                                    fontWeight =
                                        FontWeight.Bold,
                                    fontSize =
                                        15.sp
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        8.dp
                                    )
                            )


                            Text(
                                text =
                                    "Location is stored with the evidence record to verify where the field test was performed.",
                                color =
                                    CaptureMuted,
                                fontSize =
                                    13.sp,
                                lineHeight =
                                    18.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        12.dp
                                    )
                            )


                            Button(
                                onClick = {

                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                modifier =
                                    Modifier.fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(
                                        12.dp
                                    ),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            Color(0xFFFFC857),
                                        contentColor =
                                            CaptureBackground
                                    )
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.LocationOn,
                                    contentDescription =
                                        null,
                                    modifier =
                                        Modifier.size(
                                            19.dp
                                        )
                                )

                                Text(
                                    text =
                                        "  Allow Location",
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )


                Button(

                    onClick = {


                        if (
                            isCapturing ||
                            analysisInProgress
                        ) {

                            return@Button
                        }


                        if (!locationPermissionGranted) {

                            captureMessage =
                                "Please allow location access before capturing evidence."


                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )


                            return@Button
                        }


                        isCapturing =
                            true


                        captureMessage =
                            "Capturing image..."


                        val capturedAt =
                            EvidenceUtils.currentTimestamp()


                        val evidenceDirectory =
                            File(
                                context.filesDir,
                                "evidence"
                            )


                        if (!evidenceDirectory.exists()) {

                            evidenceDirectory.mkdirs()
                        }


                        val imageFile =
                            File(
                                evidenceDirectory,
                                "evidence_${System.currentTimeMillis()}.jpg"
                            )


                        val outputOptions =
                            ImageCapture.OutputFileOptions
                                .Builder(
                                    imageFile
                                )
                                .build()


                        imageCapture.takePicture(

                            outputOptions,

                            ContextCompat.getMainExecutor(
                                context
                            ),

                            object :
                                ImageCapture.OnImageSavedCallback {


                                override fun onImageSaved(
                                    outputFileResults:
                                    ImageCapture.OutputFileResults
                                ) {

                                    captureMessage =
                                        "Securing evidence..."


                                    lifecycleOwner.lifecycleScope.launch {

                                        try {


                                            captureMessage =
                                                "Getting test location..."


                                            val location =
                                                withTimeoutOrNull(
                                                    15_000L
                                                ) {

                                                    getCurrentEvidenceLocation(
                                                        context
                                                    )
                                                }


                                            val evidenceDraft =
                                                withContext(
                                                    Dispatchers.IO
                                                ) {


                                                    val sha256 =
                                                        EvidenceUtils
                                                            .calculateSha256(
                                                                imageFile
                                                            )


                                                    val imageSize =
                                                        imageFile.length()


                                                    EvidenceDraft(

                                                        imagePath =
                                                            imageFile.absolutePath,

                                                        imageSha256 =
                                                            sha256,

                                                        imageSizeBytes =
                                                            imageSize,

                                                        imageMimeType =
                                                            "image/jpeg",

                                                        capturedAt =
                                                            capturedAt,

                                                        latitude =
                                                            location?.latitude,

                                                        longitude =
                                                            location?.longitude,

                                                        locationAccuracyM =
                                                            location?.accuracy
                                                    )
                                                }


                                            Log.d(
                                                "FieldTestSecure",
                                                "Original image: ${evidenceDraft.imagePath}"
                                            )


                                            Log.d(
                                                "FieldTestSecure",
                                                "SHA-256: ${evidenceDraft.imageSha256}"
                                            )


                                            Log.d(
                                                "FieldTestSecure",
                                                "Size bytes: ${evidenceDraft.imageSizeBytes}"
                                            )


                                            Log.d(
                                                "FieldTestSecure",
                                                "Captured at: ${evidenceDraft.capturedAt}"
                                            )


                                            Log.d(
                                                "FieldTestSecure",
                                                "Latitude: ${evidenceDraft.latitude}"
                                            )


                                            Log.d(
                                                "FieldTestSecure",
                                                "Longitude: ${evidenceDraft.longitude}"
                                            )


                                            Log.d(
                                                "FieldTestSecure",
                                                "Location accuracy (m): ${evidenceDraft.locationAccuracyM}"
                                            )


                                            if (location == null) {

                                                Log.w(
                                                    "FieldTestSecure",
                                                    "Location unavailable for this capture."
                                                )


                                                captureMessage =
                                                    "Image secured, but location could not be determined."

                                            } else {

                                                Log.d(
                                                    "FieldTestSecure",
                                                    "Location provider: ${location.provider}"
                                                )


                                                captureMessage =
                                                    "Sending image for analysis..."
                                            }


                                            isCapturing =
                                                false


                                            onCaptureClick(
                                                evidenceDraft
                                            )


                                        } catch (
                                            exception: Exception
                                        ) {

                                            isCapturing =
                                                false


                                            captureMessage =
                                                "Evidence processing failed: ${exception.message}"


                                            Log.e(
                                                "FieldTestSecure",
                                                "Evidence processing failed",
                                                exception
                                            )
                                        }
                                    }
                                }


                                override fun onError(
                                    exception:
                                    ImageCaptureException
                                ) {

                                    isCapturing =
                                        false


                                    captureMessage =
                                        "Capture failed: ${exception.message}"


                                    Log.e(
                                        "FieldTestSecure",
                                        "Image capture failed",
                                        exception
                                    )
                                }
                            }
                        )
                    },


                    enabled =
                        !isCapturing &&
                                !analysisInProgress,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                62.dp
                            ),

                    shape =
                        RoundedCornerShape(
                            18.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                CaptureCyan,
                            contentColor =
                                CaptureBackground,
                            disabledContainerColor =
                                CaptureCyan.copy(
                                    alpha = 0.45f
                                ),
                            disabledContentColor =
                                CaptureBackground.copy(
                                    alpha = 0.7f
                                )
                        )
                ) {


                    if (
                        isCapturing ||
                        analysisInProgress
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(
                                    21.dp
                                ),
                            color =
                                CaptureBackground,
                            strokeWidth =
                                2.5.dp
                        )

                        Text(
                            text =
                                if (
                                    analysisInProgress
                                ) {
                                    "   Analysing Test..."
                                } else {
                                    "   Securing Evidence..."
                                },
                            fontSize =
                                16.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                    } else {

                        Icon(
                            imageVector =
                                Icons.Outlined.CameraAlt,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(
                                    24.dp
                                )
                        )

                        Text(
                            text =
                                "  Capture Test Result",
                            fontSize =
                                17.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }


                captureMessage?.let { message ->

                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),
                        color =
                            CaptureSurfaceLight
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Shield,
                                contentDescription =
                                    null,
                                tint =
                                    CaptureCyan,
                                modifier =
                                    Modifier.size(
                                        18.dp
                                    )
                            )

                            Text(
                                text =
                                    if (analysisInProgress) {
                                        "  Please wait while the captured test is analysed."
                                    } else {
                                        "  $message"
                                    },
                                color =
                                    Color(0xFFD8E3EC),
                                fontSize =
                                    12.sp,
                                lineHeight =
                                    17.sp
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.Center,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Shield,
                        contentDescription =
                            null,
                        tint =
                            CaptureMuted,
                        modifier =
                            Modifier.size(
                                14.dp
                            )
                    )

                    Text(
                        text =
                            "  GPS + SHA-256 evidence protection",
                        color =
                            CaptureMuted,
                        fontSize =
                            11.sp
                    )
                }
            }


            else {

                Spacer(
                    modifier =
                        Modifier.height(
                            70.dp
                        )
                )


                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            24.dp
                        ),
                    color =
                        CaptureSurface,
                    border =
                        BorderStroke(
                            1.dp,
                            CaptureBorder
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(
                                26.dp
                            ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(
                                    70.dp
                                ),
                            shape =
                                CircleShape,
                            color =
                                CaptureCyan.copy(
                                    alpha = 0.12f
                                )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.CameraAlt,
                                    contentDescription =
                                        null,
                                    tint =
                                        CaptureCyan,
                                    modifier =
                                        Modifier.size(
                                            34.dp
                                        )
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )


                        Text(
                            text =
                                "Camera access required",
                            color =
                                CaptureIvory,
                            fontSize =
                                21.sp,
                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )


                        Text(
                            text =
                                "FieldTest Secure needs camera access to capture and verify the field-test result.",
                            color =
                                CaptureMuted,
                            fontSize =
                                14.sp,
                            lineHeight =
                                20.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    20.dp
                                )
                        )


                        Button(
                            onClick = {

                                cameraPermissionLauncher.launch(
                                    Manifest.permission.CAMERA
                                )
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        54.dp
                                    ),
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        CaptureCyan,
                                    contentColor =
                                        CaptureBackground
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.CameraAlt,
                                contentDescription =
                                    null
                            )

                            Text(
                                text =
                                    "  Allow Camera",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


// =============================================================
// CAMERA SCAN CORNERS
// =============================================================

@Composable
private fun CameraScanOverlay(
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier =
            modifier
    ) {

        val lineColor =
            CaptureCyan.copy(
                alpha = 0.92f
            )

        val stroke =
            4.dp.toPx()

        val length =
            34.dp.toPx()


        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    0f,
                    length
                ),
            end =
                Offset(
                    0f,
                    0f
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )

        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    0f,
                    0f
                ),
            end =
                Offset(
                    length,
                    0f
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )


        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    size.width - length,
                    0f
                ),
            end =
                Offset(
                    size.width,
                    0f
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )

        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    size.width,
                    0f
                ),
            end =
                Offset(
                    size.width,
                    length
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )


        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    0f,
                    size.height - length
                ),
            end =
                Offset(
                    0f,
                    size.height
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )

        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    0f,
                    size.height
                ),
            end =
                Offset(
                    length,
                    size.height
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )


        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    size.width - length,
                    size.height
                ),
            end =
                Offset(
                    size.width,
                    size.height
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )

        drawLine(
            color =
                lineColor,
            start =
                Offset(
                    size.width,
                    size.height - length
                ),
            end =
                Offset(
                    size.width,
                    size.height
                ),
            strokeWidth =
                stroke,
            cap =
                StrokeCap.Round
        )
    }
}


// =============================================================
// RETAKE WARNING
// =============================================================

@Composable
private fun RetakeWarningCard(
    reason: String
) {

    val guidance =
        when {

            reason.contains(
                "reference card not detected",
                ignoreCase = true
            ) ->

                "Keep the full reference colour card inside the frame and make sure its marker is clearly visible."


            reason.contains(
                "quality",
                ignoreCase = true
            ) ->

                "Avoid glare, blur and strong shadows. Hold the phone steady and use even lighting."


            reason.contains(
                "reaction",
                ignoreCase = true
            ) ->

                "Keep both the test reaction and the complete reference card visible in the same image."


            else ->

                "Capture the test again with the reaction area and full reference card clearly visible."
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF3A2028)
            ),
        border =
            BorderStroke(
                1.dp,
                Color(0xFFFF716C)
                    .copy(
                        alpha = 0.65f
                    )
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    16.dp
                ),
            verticalAlignment =
                Alignment.Top
        ) {

            Surface(
                modifier =
                    Modifier.size(
                        38.dp
                    ),
                shape =
                    CircleShape,
                color =
                    Color(0xFFFF716C)
                        .copy(
                            alpha = 0.15f
                        )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Refresh,
                        contentDescription =
                            null,
                        tint =
                            Color(0xFFFF8A84),
                        modifier =
                            Modifier.size(
                                21.dp
                            )
                    )
                }
            }


            Column(
                modifier =
                    Modifier.padding(
                        start = 13.dp
                    )
            ) {

                Text(
                    text =
                        "Retake required",
                    color =
                        Color(0xFFFF9D98),
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )


                Text(
                    text =
                        reason,
                    color =
                        CaptureIvory,
                    fontSize =
                        13.sp,
                    lineHeight =
                        18.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            7.dp
                        )
                )


                Text(
                    text =
                        guidance,
                    color =
                        Color(0xFFD7C7C9),
                    fontSize =
                        12.sp,
                    lineHeight =
                        17.sp
                )
            }
        }
    }
}


// =============================================================
// GET CURRENT LOCATION
// =============================================================

private suspend fun getCurrentEvidenceLocation(
    context: Context
): Location? {

    val finePermission =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    val coarsePermission =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    Log.d(
        "FieldTestLocation",
        "Fine permission: $finePermission"
    )


    Log.d(
        "FieldTestLocation",
        "Coarse permission: $coarsePermission"
    )


    if (
        !finePermission &&
        !coarsePermission
    ) {

        Log.w(
            "FieldTestLocation",
            "No location permission available."
        )

        return null
    }


    val locationManager =
        context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager


    val gpsEnabled =
        try {

            locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )

        } catch (e: Exception) {

            false
        }


    val networkEnabled =
        try {

            locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )

        } catch (e: Exception) {

            false
        }


    Log.d(
        "FieldTestLocation",
        "GPS enabled: $gpsEnabled"
    )


    Log.d(
        "FieldTestLocation",
        "Network enabled: $networkEnabled"
    )


    if (
        !gpsEnabled &&
        !networkEnabled
    ) {

        Log.w(
            "FieldTestLocation",
            "No location provider is enabled."
        )

        return null
    }


    return suspendCancellableCoroutine { continuation ->


        val listener =
            object :
                LocationListener {


                override fun onLocationChanged(
                    location: Location
                ) {

                    Log.d(
                        "FieldTestLocation",
                        "Location received from ${location.provider}"
                    )


                    Log.d(
                        "FieldTestLocation",
                        "Latitude: ${location.latitude}"
                    )


                    Log.d(
                        "FieldTestLocation",
                        "Longitude: ${location.longitude}"
                    )


                    Log.d(
                        "FieldTestLocation",
                        "Accuracy: ${location.accuracy} m"
                    )


                    try {

                        locationManager.removeUpdates(
                            this
                        )

                    } catch (e: Exception) {

                        Log.w(
                            "FieldTestLocation",
                            "Could not remove location updates.",
                            e
                        )
                    }


                    if (
                        continuation.isActive
                    ) {

                        continuation.resume(
                            location
                        )
                    }
                }


                override fun onProviderEnabled(
                    provider: String
                ) {

                    Log.d(
                        "FieldTestLocation",
                        "Provider enabled: $provider"
                    )
                }


                override fun onProviderDisabled(
                    provider: String
                ) {

                    Log.d(
                        "FieldTestLocation",
                        "Provider disabled: $provider"
                    )
                }


                @Deprecated(
                    "Deprecated in Android"
                )
                override fun onStatusChanged(
                    provider: String?,
                    status: Int,
                    extras: android.os.Bundle?
                ) {
                }
            }


        try {


            if (networkEnabled) {

                Log.d(
                    "FieldTestLocation",
                    "Requesting NETWORK location..."
                )


                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            }


            if (
                finePermission &&
                gpsEnabled
            ) {

                Log.d(
                    "FieldTestLocation",
                    "Requesting GPS location..."
                )


                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    listener,
                    Looper.getMainLooper()
                )
            }


        } catch (
            securityException:
            SecurityException
        ) {

            Log.e(
                "FieldTestLocation",
                "Location permission error.",
                securityException
            )


            try {

                locationManager.removeUpdates(
                    listener
                )

            } catch (_: Exception) {
            }


            if (
                continuation.isActive
            ) {

                continuation.resume(
                    null
                )
            }


        } catch (
            exception:
            Exception
        ) {

            Log.e(
                "FieldTestLocation",
                "Location request failed.",
                exception
            )


            try {

                locationManager.removeUpdates(
                    listener
                )

            } catch (_: Exception) {
            }


            if (
                continuation.isActive
            ) {

                continuation.resume(
                    null
                )
            }
        }


        continuation.invokeOnCancellation {

            try {

                locationManager.removeUpdates(
                    listener
                )

            } catch (e: Exception) {

                Log.w(
                    "FieldTestLocation",
                    "Could not cancel location listener.",
                    e
                )
            }
        }
    }
}


// =============================================================
// CAMERA PREVIEW
// =============================================================

@Composable
fun CameraPreview(
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier
) {

    val context =
        LocalContext.current


    val lifecycleOwner =
        LocalLifecycleOwner.current


    AndroidView(

        modifier =
            modifier.background(
                Color.Black
            ),

        factory = { ctx ->


            val previewView =
                PreviewView(
                    ctx
                ).apply {

                    scaleType =
                        PreviewView.ScaleType.FILL_CENTER
                }


            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(
                    ctx
                )


            cameraProviderFuture.addListener({


                val cameraProvider =
                    cameraProviderFuture.get()


                val preview =
                    Preview.Builder()
                        .build()
                        .also {

                            it.surfaceProvider =
                                previewView.surfaceProvider
                        }


                val cameraSelector =
                    CameraSelector.DEFAULT_BACK_CAMERA


                try {

                    cameraProvider.unbindAll()


                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )


                } catch (
                    exception:
                    Exception
                ) {

                    Log.e(
                        "FieldTestSecure",
                        "Camera binding failed",
                        exception
                    )
                }


            },
                ContextCompat.getMainExecutor(
                    context
                )
            )


            previewView
        }
    )
}