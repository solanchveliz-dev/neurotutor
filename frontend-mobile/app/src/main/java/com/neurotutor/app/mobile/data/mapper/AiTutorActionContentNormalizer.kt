package com.neurotutor.app.mobile.data.mapper

import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.StepExplanation

object AiTutorActionContentNormalizer {

    private const val SAFE_SIMILAR_EXERCISE_MESSAGE =
        "Neo no pudo mostrar el ejercicio parecido. Intentalo nuevamente."

    fun normalize(
        action: String?,
        currentScreen: String?,
        parsedContents: List<AiTutorContent>,
        educationalContext: String
    ): List<AiTutorContent> {
        val normalizedAction = action?.uppercase() ?: return parsedContents
        
        return when (normalizedAction) {
            "SIMILAR_EXERCISE" -> {
                val interactive = parsedContents.filterIsInstance<AiTutorContent.MultipleChoice>().firstOrNull()
                if (interactive != null) {
                    listOf(interactive)
                } else {
                    val text = parsedContents.filterIsInstance<AiTutorContent.Text>().firstOrNull()
                    listOf(text ?: AiTutorContent.Text(SAFE_SIMILAR_EXERCISE_MESSAGE))
                }
            }

            "HINT" -> {
                val hint = parsedContents.filterIsInstance<AiTutorContent.HintCard>().firstOrNull()
                if (hint != null) {
                    listOf(hint)
                } else {
                    // Intentamos convertir la primera respuesta de texto en una HintCard para consistencia visual
                    val firstText = parsedContents.filterIsInstance<AiTutorContent.Text>().firstOrNull()
                    if (firstText != null) {
                        listOf(AiTutorContent.HintCard(firstText.text))
                    } else {
                        listOf(AiTutorContent.HintCard(localHint(educationalContext)))
                    }
                }
            }

            "EXPLAIN_STEP_BY_STEP", "PROCEDURE" -> {
                val explanation = parsedContents.filterIsInstance<AiTutorContent.StepExplanationContent>().firstOrNull()
                if (explanation != null) {
                    listOf(explanation)
                } else {
                    parsedContents.ifEmpty { listOf(localStepExplanation(educationalContext)) }
                }
            }

            else -> parsedContents
        }
    }

    private fun localHint(educationalContext: String): String =
        if (educationalContext.contains("fracci", ignoreCase = true)) {
            "Recuerda que el denominador indica en cuántas partes se divide el entero."
        } else {
            "Lee con cuidado qué es lo que te están pidiendo calcular exactamente."
        }

    private fun localStepExplanation(
        educationalContext: String
    ): AiTutorContent.StepExplanationContent {
        val isFractions = educationalContext.contains("fracci", ignoreCase = true)
        val steps = if (isFractions) {
            listOf(
                "Identifica si los denominadores son iguales.",
                "Si son distintos, busca un mínimo común denominador.",
                "Realiza la operación solo con los numeradores."
            )
        } else {
            listOf(
                "Anota los valores que ya conoces.",
                "Plantea la operación matemática necesaria.",
                "Calcula el resultado y verifica si responde a la pregunta."
            )
        }
        return AiTutorContent.StepExplanationContent(
            StepExplanation(
                title = "Guía de resolución",
                introduction = "Sigue estos pasos para llegar a la respuesta:",
                steps = steps,
                conclusion = "¿Cuál de estos pasos te parece más difícil?"
            )
        )
    }
}
