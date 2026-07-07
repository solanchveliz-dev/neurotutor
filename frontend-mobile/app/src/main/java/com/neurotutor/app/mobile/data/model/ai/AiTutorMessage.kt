package com.neurotutor.app.mobile.data.model.ai

import java.util.UUID

data class AiTutorMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: AiTutorMessageSender,
    val contents: List<AiTutorContent>,
    val suggestedActions: List<AiTutorAction> = emptyList(),
    val timestampMillis: Long = System.currentTimeMillis()
)

enum class AiTutorMessageSender {
    STUDENT,
    NEO,
    SYSTEM
}
