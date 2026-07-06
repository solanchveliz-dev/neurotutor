package com.neurotutor.app.mobile.data.model.learning

import com.google.gson.annotations.SerializedName

data class SubmitPracticeAttemptRequest(
    @SerializedName("student_id")
    val studentId: Long,
    @SerializedName("modulo_id")
    val moduloId: Long,
    val answers: List<PracticeAnswerRequest>
)

data class PracticeAnswerRequest(
    @SerializedName("exercise_id")
    val exerciseId: Long,
    @SerializedName("selected_answer_index")
    val selectedAnswerIndex: Int
)

data class SubmitPracticeAttemptResponse(
    @SerializedName("attempt_id")
    val attemptId: Long,
    @SerializedName("correct_answers")
    val correctAnswers: Int,
    @SerializedName("total_questions")
    val totalQuestions: Int,
    @SerializedName("score_percentage")
    val scorePercentage: Int,
    @SerializedName("points_earned")
    val pointsEarned: Int,
    @SerializedName("practice_completed")
    val practiceCompleted: Boolean,
    @SerializedName("module_progress")
    val moduleProgress: Int
)
