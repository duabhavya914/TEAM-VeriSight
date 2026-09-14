package com.sih.drugtest.util

import com.sih.drugtest.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.File
import io.github.jan.supabase.storage.upload

object EvidenceStorage {

    private const val BUCKET_NAME = "evidence-images"

    suspend fun uploadOriginalImage(
        localImagePath: String,
        testId: String?,
        officerId: String?
    ): String {

        val imageFile = File(localImagePath)

        if (!imageFile.exists()) {
            throw IllegalStateException(
                "Original evidence image does not exist."
            )
        }

        val safeTestId =
            testId
                ?.takeIf { it.isNotBlank() }
                ?: "unknown-test"

        val safeOfficerId =
            officerId
                ?.takeIf { it.isNotBlank() }
                ?: "unknown-officer"


        // Example:
        // TEST_OFFICER_001/HER-001/evidence_123456789.jpg
        val storagePath =
            "$safeOfficerId/$safeTestId/${imageFile.name}"


        val bucket =
            SupabaseClient.client
                .storage
                .from(BUCKET_NAME)


        // Upload the exact original JPEG.
        // No resizing, recompression or modification.
        bucket.upload(
            storagePath,
            imageFile
        ) {
            upsert = false
        }


        return storagePath
    }
}