package com.sih.drugtest.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File


@Composable
fun CaptureScreen(
    onBackClick: () -> Unit,
    onCaptureClick: (String) -> Unit
) {

    val context = LocalContext.current

    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var captureMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isCapturing by remember {
        mutableStateOf(false)
    }

    /*
     * IMPORTANT:
     * This ImageCapture object is the SAME instance that:
     *
     * 1. gets bound to CameraX
     * 2. is used when the Capture button is pressed
     */
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(
                ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
            )
            .build()
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            cameraPermissionGranted = isGranted
        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Capture Test Image",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))


        if (cameraPermissionGranted) {

            CameraPreview(
                imageCapture = imageCapture,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .clip(
                        RoundedCornerShape(22.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Keep the test reaction and reference colour card visible.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))


            // ---------------------------------------------
            // REAL CAMERA CAPTURE BUTTON
            // ---------------------------------------------

            Button(
                onClick = {

                    if (isCapturing) {
                        return@Button
                    }

                    isCapturing = true
                    captureMessage = "Capturing image..."


                    // -------------------------------------
                    // PRIVATE EVIDENCE DIRECTORY
                    // -------------------------------------

                    val evidenceDirectory =
                        File(
                            context.filesDir,
                            "evidence"
                        )

                    if (!evidenceDirectory.exists()) {
                        evidenceDirectory.mkdirs()
                    }


                    /*
                     * Unique filename.
                     *
                     * This is the ORIGINAL evidence image.
                     */
                    val imageFile =
                        File(
                            evidenceDirectory,
                            "evidence_${System.currentTimeMillis()}.jpg"
                        )


                    val outputOptions =
                        ImageCapture.OutputFileOptions
                            .Builder(imageFile)
                            .build()


                    imageCapture.takePicture(

                        outputOptions,

                        ContextCompat.getMainExecutor(context),

                        object :
                            ImageCapture.OnImageSavedCallback {

                            override fun onImageSaved(
                                outputFileResults:
                                ImageCapture.OutputFileResults
                            ) {

                                isCapturing = false

                                captureMessage =
                                    "Original image saved successfully"

                                Log.d(
                                    "FieldTestSecure",
                                    "Original evidence image saved: ${imageFile.absolutePath}"
                                )

                                /*
                                 * IMPORTANT:
                                 *
                                 * We DO NOT:
                                 *
                                 * - resize this file
                                 * - crop this file
                                 * - apply filters
                                 * - recompress this file
                                 * - alter this file
                                 *
                                 * Later SHA-256 will be calculated
                                 * directly from THIS exact file.
                                 */

                                onCaptureClick(imageFile.absolutePath)
                            }


                            override fun onError(
                                exception:
                                ImageCaptureException
                            ) {

                                isCapturing = false

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

                enabled = !isCapturing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {

                Text(
                    text = if (isCapturing) {
                        "Capturing..."
                    } else {
                        "Capture Test Result"
                    }
                )
            }


            captureMessage?.let { message ->

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        } else {

            Text(
                text = "Camera access required",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "FieldTest Secure needs camera access to capture the test result."
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = {

                    cameraPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            ) {

                Text(
                    text = "Allow Camera"
                )
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = onBackClick
        ) {

            Text(
                text = "Back"
            )
        }
    }
}


@Composable
fun CameraPreview(
    imageCapture: ImageCapture,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val lifecycleOwner =
        LocalLifecycleOwner.current


    AndroidView(

        modifier = modifier
            .background(Color.Black),

        factory = { ctx ->

            val previewView =
                PreviewView(ctx).apply {

                    scaleType =
                        PreviewView.ScaleType.FILL_CENTER
                }


            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(ctx)


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


                    /*
                     * Bind BOTH:
                     *
                     * preview
                     * +
                     * imageCapture
                     */
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )

                } catch (
                    exception: Exception
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