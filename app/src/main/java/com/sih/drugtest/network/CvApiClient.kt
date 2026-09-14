package com.sih.drugtest.network

import com.sih.drugtest.model.CvAnalysisResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File


object CvApiClient {

    // TEMPORARY.
    // We will replace this with the deployed HTTPS URL later.
    private const val BASE_URL =
        "http://192.168.29.49:8000"


    private val client =
        HttpClient(Android) {

            install(ContentNegotiation) {

                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }


    suspend fun analyzeImage(
        imagePath: String,
        testId: String,
        officerId: String,
        latitude: Double,
        longitude: Double,
        capturedAt: String
    ): CvAnalysisResponse {

        val imageFile =
            File(imagePath)


        if (!imageFile.exists()) {

            throw IllegalStateException(
                "Captured evidence image does not exist."
            )
        }


        val response =
            client.submitFormWithBinaryData(

                url =
                    "$BASE_URL/analyze",

                formData =
                    formData {

                        append(
                            "test_id",
                            testId
                        )

                        append(
                            "officer_id",
                            officerId
                        )

                        append(
                            "latitude",
                            latitude.toString()
                        )

                        append(
                            "longitude",
                            longitude.toString()
                        )

                        append(
                            "captured_at",
                            capturedAt
                        )


                        append(
                            "image",
                            imageFile.readBytes(),
                            Headers.build {

                                append(
                                    HttpHeaders.ContentType,
                                    "image/jpeg"
                                )

                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${imageFile.name}\""
                                )
                            }
                        )
                    }
            )


        if (!response.status.isSuccess()) {

            throw IllegalStateException(
                "CV API request failed with HTTP ${response.status.value}"
            )
        }


        return response.body()
    }
}