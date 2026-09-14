package com.sih.drugtest.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestProtocol(

    val id: String,

    @SerialName("test_id")
    val testId: String,

    @SerialName("test_name")
    val name: String,

    @SerialName("drug_category")
    val category: String,

    @SerialName("target_substance")
    val targetSubstance: String,

    @SerialName("test_type")
    val testType: String,

    val method: String? = null,

    val reagents: String? = null,

    @SerialName("expected_observation")
    val expectedObservation: String? = null,

    @SerialName("colour_transition")
    val colourTransition: String? = null,

    @SerialName("observation_location")
    val observationLocation: String? = null,

    @SerialName("observation_time")
    val observationTime: String? = null,

    val interpretation: String? = null,

    @SerialName("confirmation_status")
    val confirmationStatus: String? = null,

    @SerialName("manual_section")
    val manualSection: String? = null,

    @SerialName("manual_page")
    val manualPage: String? = null,

    val source: String? = null,

    @SerialName("officer_procedure")
    val officerProcedure: String? = null,

    @SerialName("camera_capture_note")
    val cameraCaptureNote: String? = null,

    @SerialName("field_app_eligibility")
    val fieldAppEligibility: String? = null,

    @SerialName("app_protocol_version")
    val version: String
)