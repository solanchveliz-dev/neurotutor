package com.neurotutor.app.mobile.feature.groqexercise

import android.util.Log

internal object GroqExerciseDiagnostics {
    private const val TAG = "GroqExercise"

    fun requestStarted() {
        Log.d(TAG, "request_started endpoint=/api/ai/tutor action=TRY_SIMILAR_EXERCISE")
    }

    fun responseReceived(statusCode: Int, hasBody: Boolean) {
        Log.d(TAG, "response_received status=$statusCode hasBody=$hasBody")
    }

    fun parseAttempt(candidateCount: Int, hasStructuredContent: Boolean) {
        Log.d(
            TAG,
            "parse_attempt candidates=$candidateCount hasStructuredContent=$hasStructuredContent"
        )
    }

    fun success() {
        Log.d(TAG, "exercise_loaded")
    }

    fun failure(reason: String) {
        Log.e(TAG, "exercise_failed reason=$reason")
    }

    fun exception(exception: Exception) {
        val safeMessage = exception.message
            ?.replace(Regex("(?i)(bearer|token|api[_-]?key)\\s*[:=]?\\s*\\S+"), "$1=[REDACTED]")
            ?.take(200)
            .orEmpty()
        Log.e(TAG, "exercise_exception type=${exception::class.java.simpleName} message=$safeMessage")
    }
}
