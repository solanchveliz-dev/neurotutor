package com.neurotutor.app.mobile.feature.groqexercise

internal object GroqExercisePrompt {
    const val ACTION = "TRY_SIMILAR_EXERCISE"
    const val SCREEN = "PRACTICE"

    fun build(topic: String): String {
        val safeTopic = topic.trim().take(MAX_TOPIC_LENGTH).ifBlank { "matemática primaria" }
        return """
            Genera un ejercicio de opción múltiple sobre $safeTopic.
            Usa una pregunta clara, entre 2 y 4 opciones y una sola respuesta correcta.
            Devuelve exclusivamente JSON válido, sin Markdown, con este formato:
            {"contents":[{"type":"MULTIPLE_CHOICE","exercise":{"id":"generated-1","question":"pregunta","options":["opción 1","opción 2"],"correctOptionIndex":0,"hint":"pista","successMessage":"mensaje positivo"}}]}
            correctOptionIndex es metadato interno de la app y debe apuntar a una opción existente.
        """.trimIndent()
    }

    fun educationalContext(topic: String): String =
        "Tema solicitado: ${topic.trim().take(MAX_TOPIC_LENGTH)}"

    private const val MAX_TOPIC_LENGTH = 160
}
