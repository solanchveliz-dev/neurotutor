package com.neurotutor.app.mobile.feature.groqexercise

@Suppress("UNUSED_PARAMETER")
internal object GroqExerciseDiagnostics {
    fun requestStarted() = Unit
    fun responseReceived(statusCode: Int, hasBody: Boolean) = Unit
    fun parseAttempt(candidateCount: Int, hasStructuredContent: Boolean) = Unit
    fun success() = Unit
    fun failure(reason: String) = Unit
    fun exception(exception: Exception) = Unit
}
