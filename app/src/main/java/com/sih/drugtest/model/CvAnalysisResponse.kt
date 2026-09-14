package com.sih.drugtest.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CvAnalysisResponse(

    @SerialName("test_id")
    val testId: String? = null,

    @SerialName("observed_rgb")
    val observedRgb: List<Double>? = null,

    @SerialName("expected_rgb")
    val expectedRgb: List<Double>? = null,

    val distance: Double? = null,

    val tolerance: Double? = null,

    @SerialName("colour_name")
    val colourName: String? = null,

    val result: String,

    @SerialName("validation_status")
    val validationStatus: String? = null,

    @SerialName("reference_card_detected")
    val referenceCardDetected: Boolean? = null,

    @SerialName("quality_status")
    val qualityStatus: String? = null,

    @SerialName("calibration_applied")
    val calibrationApplied: Boolean? = null,

    @SerialName("officer_id")
    val officerId: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    @SerialName("captured_at")
    val capturedAt: String? = null,

    @SerialName("image_sha256")
    val imageSha256: String? = null,

    @SerialName("image_storage_path")
    val imageStoragePath: String? = null,

    @SerialName("original_filename")
    val originalFilename: String? = null,

    val saved: Boolean? = null,

    val reason: String? = null
)