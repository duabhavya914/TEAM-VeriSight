package com.sih.drugtest.util

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object EvidenceUtils {

    /**
     * Calculates SHA-256 directly from the exact file bytes.
     *
     * The image is only READ here.
     * It is never resized, decoded, recompressed or modified.
     */
    fun calculateSha256(file: File): String {

        val digest = MessageDigest.getInstance("SHA-256")

        FileInputStream(file).use { inputStream ->

            val buffer = ByteArray(8192)

            var bytesRead: Int

            while (
                inputStream.read(buffer).also {
                    bytesRead = it
                } != -1
            ) {

                digest.update(
                    buffer,
                    0,
                    bytesRead
                )
            }
        }

        return digest
            .digest()
            .joinToString("") { byte ->

                "%02x".format(
                    byte.toInt() and 0xFF
                )
            }
    }


    /**
     * Creates a standard UTC timestamp such as:
     *
     * 2026-09-13T07:32:14.125Z
     */
    fun currentTimestamp(): String {

        val formatter = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.US
        )

        formatter.timeZone =
            TimeZone.getTimeZone("UTC")

        return formatter.format(
            Date()
        )
    }
}