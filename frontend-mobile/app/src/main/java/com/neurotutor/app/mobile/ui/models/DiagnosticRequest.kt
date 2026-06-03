package com.neurotutor.app.mobile.ui.models

data class DiagnosticRequest(
    val studentId: String,
    val respuestas: List<String>
)