package com.sih.drugtest.ui.screens

data class AnalysisState(
    val progress: Int = 78,
    val imageCaptured: Boolean = true,
    val referenceCardDetected: Boolean = true,
    val colourExtracted: Boolean = true,
    val aiAnalysisInProgress: Boolean = true,
    val resultGenerated: Boolean = false
)
