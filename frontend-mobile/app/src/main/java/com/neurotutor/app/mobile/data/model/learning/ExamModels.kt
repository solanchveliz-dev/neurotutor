package com.neurotutor.app.mobile.data.model.learning

// ==================== EXÁMENES MEJORADOS ====================

data class ExamPassedResponse(
    val alreadyPassed: Boolean,
    val passedAt: String? = null
)

data class SubmitExamRequest(
    val studentId: String,
    val moduloId: String,
    val level: String,
    val score: Int
)

data class SubmitExamResponse(
    val success: Boolean,
    val message: String,
    val pointsEarned: Int,
    val levelUp: Boolean,
    val newLevel: String?,
    val topicCompleted: Boolean
)