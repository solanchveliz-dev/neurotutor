package com.neurotutor.app.mobile.data.mapper

import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.ai.InteractiveExercise
import java.util.UUID

/**
 * Converts common tutor-style Markdown sections into domain content while the
 * backend still returns plain text. Unknown formats remain readable plain text.
 */
internal object AiTutorVisualTextParser {

    private val sectionPattern = Regex(
        pattern = """(?im)^\s*(?:#{1,6}\s*)?\*{0,2}(Validaci[oó]n|Pista|Pregunta gu[ií]a|Explicaci[oó]n|Ejercicio parecido|Ejercicio)\*{0,2}\s*:?\s*(.*)$"""
    )
    private val markdownDecoration = Regex("""(\*\*|__|`|^#{1,6}\s*)""", RegexOption.MULTILINE)
    private val optionPattern = Regex("""^\s*([A-Da-d]|\d+)[\).:-]\s+(.+?)\s*$""")
    private val answerPattern = Regex(
        """(?i)^\s*(?:respuesta correcta|opci[oó]n correcta|opci[oó]n)\s*:\s*([A-Da-d]|\d+)\s*$"""
    )
    private val hintPattern = Regex("""(?i)^\s*pista\s*:\s*(.+)$""")

    fun parse(answer: String): List<AiTutorContent> {
        val normalizedAnswer = answer.trim()
        
        // 1. Intentar parsear todo el bloque como un ejercicio si no hay secciones explícitas
        val matches = sectionPattern.findAll(normalizedAnswer).toList()
        if (matches.isEmpty()) {
            val standaloneExercise = parseExercise(normalizedAnswer)
            if (standaloneExercise != null) {
                return listOf(AiTutorContent.MultipleChoice(standaloneExercise))
            }
            return listOf(AiTutorContent.Text(normalizedAnswer.cleanMarkdown()))
        }

        // 2. Parsear por secciones si existen headers
        val contents = buildList {
            val introduction = normalizedAnswer.substring(0, matches.first().range.first).cleanMarkdown()
            introduction.takeIf(String::isNotBlank)?.let {
                add(AiTutorContent.Text(it))
            }

            matches.forEachIndexed { index, match ->
                val title = match.groupValues[1].normalizedTitle()
                val inlineText = match.groupValues[2]
                val sectionEnd = matches.getOrNull(index + 1)?.range?.first ?: normalizedAnswer.length
                val bodyStart = match.range.last + 1
                val body = buildString {
                    append(inlineText)
                    if (bodyStart < sectionEnd) {
                        if (isNotEmpty()) append('\n')
                        append(normalizedAnswer.substring(bodyStart, sectionEnd))
                    }
                }.trim()

                when (title) {
                    "VALIDACION" -> body.cleanMarkdown()
                        .takeIf(String::isNotBlank)
                        ?.let { add(AiTutorContent.ValidationCard(it)) }
                    "PISTA" -> body.cleanMarkdown()
                        .takeIf(String::isNotBlank)
                        ?.let { add(AiTutorContent.HintCard(it)) }
                    "PREGUNTA GUIA" -> body.cleanMarkdown()
                        .takeIf(String::isNotBlank)
                        ?.let { add(AiTutorContent.SocraticQuestion(it)) }
                    "EJERCICIO PARECIDO", "EJERCICIO" -> {
                        parseExercise(body)?.let { add(AiTutorContent.MultipleChoice(it)) }
                            ?: body.cleanMarkdown()
                                .takeIf(String::isNotBlank)
                                ?.let { add(AiTutorContent.Text(it)) }
                    }
                    else -> body.cleanMarkdown()
                        .takeIf(String::isNotBlank)
                        ?.let { add(AiTutorContent.Text(it)) }
                }
            }
        }

        return contents.attachExerciseHints().ifEmpty {
            listOf(AiTutorContent.Text(normalizedAnswer.cleanMarkdown()))
        }
    }

    private fun List<AiTutorContent>.attachExerciseHints(): List<AiTutorContent> {
        val result = mutableListOf<AiTutorContent>()
        var index = 0
        while (index < size) {
            val current = this[index]
            val next = getOrNull(index + 1)
            if (current is AiTutorContent.MultipleChoice && next is AiTutorContent.HintCard) {
                result += current.copy(
                    exercise = current.exercise.copy(hint = next.text)
                )
                index += 2
            } else {
                result += current
                index++
            }
        }
        return result
    }

    internal fun parseExercise(body: String): InteractiveExercise? {
        val lines = body.lines().map(String::trim).filter(String::isNotEmpty)
        val options = lines.mapNotNull { line ->
            optionPattern.matchEntire(line)?.let { match ->
                match.groupValues[1] to match.groupValues[2].cleanMarkdown()
            }
        }
        if (options.size < 2) return null

        val answerToken = lines.firstNotNullOfOrNull { line ->
            answerPattern.matchEntire(line)?.groupValues?.get(1)
        } ?: return null
        val correctIndex = options.indexOfFirst { (label, _) ->
            label.equals(answerToken, ignoreCase = true)
        }
        if (correctIndex !in options.indices) return null

        val firstOptionIndex = lines.indexOfFirst { optionPattern.matches(it) }
        val question = lines
            .take(firstOptionIndex)
            .filterNot { answerPattern.matches(it) || hintPattern.matches(it) }
            .joinToString(" ")
            .cleanMarkdown()
        if (question.isBlank()) return null

        val hint = lines.firstNotNullOfOrNull { line ->
            hintPattern.matchEntire(line)?.groupValues?.get(1)?.cleanMarkdown()
        }

        return InteractiveExercise(
            id = "visual-${UUID.randomUUID()}",
            question = question,
            options = options.map { it.second },
            correctOptionIndex = correctIndex,
            hint = hint,
            successMessage = "¡Muy bien! Puedes volver al ejercicio original."
        )
    }

    internal fun sanitize(text: String): String =
        text.replace(Regex("""\[([^\]]+)]\([^)]+\)"""), "$1")
            .replace(Regex("""(?<!\*)\*([^*\n]+)\*(?!\*)"""), "$1")
            .replace(Regex("""(?<!_)_([^_\n]+)_(?!_)"""), "$1")
            .replace(markdownDecoration, "")
            .replace(Regex("""(?m)^\s*[-•]\s+"""), "• ")
            .trim()

    private fun String.cleanMarkdown(): String = sanitize(this)

    private fun String.normalizedTitle(): String =
        uppercase()
            .replace('Ó', 'O')
            .replace('Í', 'I')
}
