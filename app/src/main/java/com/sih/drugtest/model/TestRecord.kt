package com.sih.drugtest.model

data class TestRecord(
    val recordId: String,
    val testName: String,
    val result: String,
    val dateTime: String
)