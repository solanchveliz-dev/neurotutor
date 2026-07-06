package com.neurotutor.app.mobile.data.model.ai

data class StepExplanation(
    val steps: List<String>,
    val title: String? = null,
    val introduction: String? = null,
    val conclusion: String? = null
)
