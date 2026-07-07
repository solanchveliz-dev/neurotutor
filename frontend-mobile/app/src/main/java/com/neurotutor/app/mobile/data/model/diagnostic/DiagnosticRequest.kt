package com.neurotutor.app.mobile.data.model.diagnostic

data class DiagnosticRequest(
    val studentId: String,
    val respuestas: List<String>
)
