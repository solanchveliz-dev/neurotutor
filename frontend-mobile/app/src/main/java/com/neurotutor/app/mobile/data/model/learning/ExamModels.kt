package com.neurotutor.app.mobile.data.model.learning

import com.google.gson.annotations.SerializedName

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

data class SubmitFinalExamAttemptRequest(
    @SerializedName("student_id") val studentId: Long,
    @SerializedName("modulo_id") val moduleId: Long,
    val answers: List<FinalExamAnswerRequest>
)

data class FinalExamAnswerRequest(
    @SerializedName("question_id") val questionId: Long,
    @SerializedName("selected_answer_index") val selectedAnswerIndex: Int
)

data class SubmitFinalExamAttemptResponse(
    @SerializedName("attempt_id") val attemptId: Long,
    @SerializedName("correct_answers") val correctAnswers: Int,
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("score_percentage") val scorePercentage: Int,
    val passed: Boolean,
    @SerializedName("points_earned") val pointsEarned: Int,
    val message: String,
    @SerializedName("module_progress") val moduleProgress: Int,
    @SerializedName("unlocked_badge_id") val unlockedBadgeId: String?,
    @SerializedName("unlocked_achievement_codes") val unlockedAchievementCodes: List<String> = emptyList()
)
