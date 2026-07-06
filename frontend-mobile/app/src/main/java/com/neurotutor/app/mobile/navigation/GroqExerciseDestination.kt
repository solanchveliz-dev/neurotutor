package com.neurotutor.app.mobile.navigation

import java.nio.charset.StandardCharsets
import java.util.Base64

data class GroqExerciseArguments(
    val studentId: Long,
    val moduleId: Long,
    val topic: String
)

object GroqExerciseDestination {
    const val STUDENT_ID_ARGUMENT = "studentId"
    const val MODULE_ID_ARGUMENT = "moduleId"
    const val TOPIC_ARGUMENT = "topic"

    const val route =
        "groqExercise/{$STUDENT_ID_ARGUMENT}/{$MODULE_ID_ARGUMENT}/{$TOPIC_ARGUMENT}"

    fun createRoute(studentId: Long, moduleId: Long, topic: String): String? {
        val normalizedTopic = topic.trim()
        if (studentId <= 0 || moduleId <= 0 || normalizedTopic.isBlank()) return null
        if (normalizedTopic.length > MAX_TOPIC_LENGTH) return null

        val encodedTopic = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(normalizedTopic.toByteArray(StandardCharsets.UTF_8))
        return "groqExercise/$studentId/$moduleId/$encodedTopic"
    }

    fun parse(
        studentId: String?,
        moduleId: String?,
        encodedTopic: String?
    ): GroqExerciseArguments? {
        val safeStudentId = studentId?.toLongOrNull()?.takeIf { it > 0 } ?: return null
        val safeModuleId = moduleId?.toLongOrNull()?.takeIf { it > 0 } ?: return null
        val safeEncodedTopic = encodedTopic
            ?.takeIf { it.isNotBlank() && it.length <= MAX_ENCODED_TOPIC_LENGTH }
            ?: return null

        val topic = runCatching {
            String(
                Base64.getUrlDecoder().decode(safeEncodedTopic),
                StandardCharsets.UTF_8
            ).trim()
        }.getOrNull()
            ?.takeIf { it.isNotBlank() && it.length <= MAX_TOPIC_LENGTH }
            ?: return null

        return GroqExerciseArguments(
            studentId = safeStudentId,
            moduleId = safeModuleId,
            topic = topic
        )
    }

    private const val MAX_TOPIC_LENGTH = 160
    private const val MAX_ENCODED_TOPIC_LENGTH = 256
}

