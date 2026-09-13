package com.sih.drugtest.model

data class EvidenceDraft(

    // -------------------------
    // ORIGINAL IMAGE
    // -------------------------

    val imagePath: String,

    val imageSha256: String,

    val imageSizeBytes: Long,

    val imageMimeType: String = "image/jpeg",


    // -------------------------
    // CAPTURE TIME
    // -------------------------

    val capturedAt: String,


    // -------------------------
    // LOCATION
    // Will be filled in Step 4
    // -------------------------

    val latitude: Double? = null,

    val longitude: Double? = null,

    val locationAccuracyM: Float? = null,


    // -------------------------
    // OFFICER
    // Will be filled in Step 5
    // -------------------------

    val officerId: String? = null,


    // -------------------------
    // PROTOCOL
    // Will be filled in Step 5
    // -------------------------

    val protocolId: String? = null,

    val testId: String? = null,

    val protocolVersion: String? = null,


    // -------------------------
    // SUPABASE STORAGE
    // Filled after upload
    // -------------------------

    val imageStoragePath: String? = null
)