package com.sih.drugtest.ui.screens

data class AnalysisState(
    val progress: Int,
    val imageCaptured: Boolean,
    val referenceCardDetected: Boolean,
    val colourExtracted: Boolean,
    val aiAnalysisInProgress: Boolean,
    val resultGenerated: Boolean
)