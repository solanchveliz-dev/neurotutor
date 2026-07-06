package com.neurotutor.app.mobile.data.model.ai

data class InteractiveExercise(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val hint: String? = null,
    val successMessage: String? = null
)
