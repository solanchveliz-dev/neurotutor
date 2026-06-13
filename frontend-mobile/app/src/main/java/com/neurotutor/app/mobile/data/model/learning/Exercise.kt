package com.neurotutor.app.mobile.data.model.learning

data class Exercise(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val tutorExplanation: String,
    val points: Int = 10
)
