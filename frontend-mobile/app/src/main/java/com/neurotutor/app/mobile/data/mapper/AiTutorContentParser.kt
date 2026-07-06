package com.neurotutor.app.mobile.data.mapper

import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.InteractiveExercise
import com.neurotutor.app.mobile.data.model.ai.StepExplanation
import org.json.JSONArray
import org.json.JSONObject

object AiTutorContentParser {

    private const val SAFE_FALLBACK_MESSAGE =
        "Neo no pudo organizar esta respuesta. Inténtalo nuevamente."

    fun parse(answer: String): List<AiTutorContent> {
        val normalizedAnswer = answer.trim()
        if (normalizedAnswer.isEmpty()) {
            return listOf(AiTutorContent.Text(""))
        }
        if (!normalizedAnswer.looksLikeJson()) {
            return AiTutorVisualTextParser.parse(answer)
        }

        return runCatching {
            parseJson(normalizedAnswer)
        }.getOrElse {
            safeFallback()
        }
    }

    private fun parseJson(answer: String): List<AiTutorContent> {
        val root = JSONObject(answer)
        val contents = root.optJSONArray("contents") ?: return safeFallback()
        val parsedContents = buildList {
            for (index in 0 until contents.length()) {
                val contentObject = contents.optJSONObject(index) ?: continue
                parseContent(contentObject)?.let(::add)
            }
        }
        return parsedContents.ifEmpty(::safeFallback)
    }

    private fun parseContent(content: JSONObject): AiTutorContent? {
        return when (content.optString("type").uppercase()) {
            "TEXT" -> content.requiredText("text")?.let(AiTutorContent::Text)
            "STEP_EXPLANATION" -> parseStepExplanation(content)
            "SOCRATIC_QUESTION" -> content.requiredText("question")
                ?.let(AiTutorContent::SocraticQuestion)
            "MULTIPLE_CHOICE" -> parseMultipleChoice(content)
            "HINT_CARD" -> content.requiredText("text")?.let(AiTutorContent::HintCard)
            "VALIDATION_CARD" -> content.requiredText("text")?.let(AiTutorContent::ValidationCard)
            "SUCCESS_CARD" -> content.requiredText("text")?.let(AiTutorContent::SuccessCard)
            else -> null
        }
    }

    private fun parseStepExplanation(content: JSONObject): AiTutorContent? {
        val stepsArray = content.optJSONArray("steps") ?: return null
        val steps = stepsArray.nonBlankStrings()
        if (steps.isEmpty()) return null

        return AiTutorContent.StepExplanationContent(
            StepExplanation(
                title = content.optionalText("title"),
                introduction = content.optionalText("introduction"),
                steps = steps,
                conclusion = content.optionalText("conclusion")
            )
        )
    }

    private fun parseMultipleChoice(content: JSONObject): AiTutorContent? {
        val exercise = content.optJSONObject("exercise") ?: return null
        val id = exercise.requiredText("id") ?: return null
        val question = exercise.requiredText("question") ?: return null
        val options = exercise.optJSONArray("options")?.nonBlankStrings().orEmpty()
        if (options.size < 2 || !exercise.has("correctOptionIndex")) return null

        val correctOptionIndex = exercise.optInt("correctOptionIndex", -1)
        require(correctOptionIndex in options.indices) {
            "correctOptionIndex is outside the available options"
        }

        return AiTutorContent.MultipleChoice(
            InteractiveExercise(
                id = id,
                question = question,
                options = options,
                correctOptionIndex = correctOptionIndex,
                hint = exercise.optionalText("hint"),
                successMessage = exercise.optionalText("successMessage")
            )
        )
    }

    private fun JSONObject.requiredText(key: String): String? =
        optionalText(key)

    private fun JSONObject.optionalText(key: String): String? =
        AiTutorVisualTextParser.sanitize(optString(key))
            .takeIf(String::isNotEmpty)

    private fun JSONArray.nonBlankStrings(): List<String> = buildList {
        for (index in 0 until length()) {
            AiTutorVisualTextParser.sanitize(optString(index))
                .takeIf(String::isNotEmpty)
                ?.let(::add)
        }
    }

    private fun String.looksLikeJson(): Boolean =
        startsWith("{") || startsWith("[")

    private fun safeFallback(): List<AiTutorContent> =
        listOf(AiTutorContent.Text(SAFE_FALLBACK_MESSAGE))
}
