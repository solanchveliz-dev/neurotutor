package com.neurotutor.app.mobile.data.model.common

data class AiTutorRequest(
    val studentId: Long,
    val moduleId: Long,
    val question: String,
    val context: String? = null,
    val currentScreen: String? = null,
    val action: String? = null
)
