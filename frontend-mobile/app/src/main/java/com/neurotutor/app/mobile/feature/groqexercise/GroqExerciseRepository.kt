package com.neurotutor.app.mobile.feature.groqexercise

import com.neurotutor.app.mobile.data.mapper.AiTutorContentParser
import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.common.AiTutorRequest
import com.neurotutor.app.mobile.data.model.common.AiTutorResponse
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.CancellationException
import org.json.JSONObject
import retrofit2.Response

sealed interface GroqExerciseResult {
    data class Success(val exercise: GroqExercise) : GroqExerciseResult
    data class Failure(val message: String = SAFE_EXERCISE_ERROR) : GroqExerciseResult
}

fun interface GroqExerciseRepositoryContract {
    suspend fun loadExercise(
        studentId: Long,
        moduleId: Long,
        topic: String
    ): GroqExerciseResult
}

class GroqExerciseRepository(
    private val askTutorCall: suspend (AiTutorRequest) -> Response<AiTutorResponse> =
        RetrofitClient.apiService::askTutor
) : GroqExerciseRepositoryContract {

    override suspend fun loadExercise(
        studentId: Long,
        moduleId: Long,
        topic: String
    ): GroqExerciseResult {
        if (studentId <= 0 || moduleId <= 0) {
            GroqExerciseDiagnostics.failure("invalid_arguments")
            return GroqExerciseResult.Failure()
        }

        return try {
            GroqExerciseDiagnostics.requestStarted()
            val response = askTutorCall(
                AiTutorRequest(
                    studentId = studentId,
                    moduleId = moduleId,
                    question = GroqExercisePrompt.build(topic),
                    context = GroqExercisePrompt.educationalContext(topic),
                    currentScreen = GroqExercisePrompt.SCREEN,
                    action = GroqExercisePrompt.ACTION
                )
            )

            GroqExerciseDiagnostics.responseReceived(
                statusCode = response.code(),
                hasBody = response.body() != null
            )
            if (!response.isSuccessful) {
                GroqExerciseDiagnostics.failure("http_${response.code()}")
                return GroqExerciseResult.Failure()
            }

            val body = response.body()
            if (body == null) {
                GroqExerciseDiagnostics.failure("empty_response_body")
                return GroqExerciseResult.Failure()
            }

            val exercise = GroqExerciseResponseParser.parse(body)
            if (exercise == null) {
                GroqExerciseDiagnostics.failure("missing_valid_multiple_choice")
                GroqExerciseResult.Failure()
            } else {
                GroqExerciseDiagnostics.success()
                GroqExerciseResult.Success(exercise)
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (exception: Exception) {
            GroqExerciseDiagnostics.exception(exception)
            GroqExerciseResult.Failure()
        }
    }
}

private object GroqExerciseResponseParser {
    private val codeFence = Regex(
        pattern = """^```(?:json)?\s*|\s*```$""",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    fun parse(response: AiTutorResponse): GroqExercise? {
        val candidates = buildList {
            response.structuredContent
                ?.takeIf(String::isNotBlank)
                ?.let { addAll(extractJsonCandidates(it)) }
            response.answer
                .takeIf(String::isNotBlank)
                ?.let { addAll(extractJsonCandidates(it)) }
        }.distinct()

        GroqExerciseDiagnostics.parseAttempt(
            candidateCount = candidates.size,
            hasStructuredContent = !response.structuredContent.isNullOrBlank()
        )

        return candidates.firstNotNullOfOrNull { candidate ->
            AiTutorContentParser.parse(candidate)
                .filterIsInstance<AiTutorContent.MultipleChoice>()
                .firstNotNullOfOrNull { content -> content.exercise.toGroqExercise() }
        }
    }

    private fun extractJsonCandidates(raw: String): List<String> {
        val normalized = raw.trim().replace(codeFence, "").trim()
        if (normalized.isBlank()) return emptyList()

        return buildList {
            val root = runCatching { JSONObject(normalized) }.getOrNull()
            when (val structured = root?.opt("structuredContent")) {
                is JSONObject -> add(structured.toString())
                is String -> structured.takeIf(String::isNotBlank)?.let(::add)
            }

            root?.optString("answer")
                ?.trim()
                ?.replace(codeFence, "")
                ?.trim()
                ?.takeIf { it.startsWith("{") }
                ?.let(::add)

            add(normalized)
        }
    }

    private fun com.neurotutor.app.mobile.data.model.ai.InteractiveExercise.toGroqExercise(): GroqExercise? =
        GroqExercise(
            id = id.orEmpty(),
            question = question.orEmpty(),
            options = options.orEmpty().map { it.orEmpty() },
            correctOptionIndex = correctOptionIndex,
            hint = hint.orEmpty(),
            successMessage = successMessage.orEmpty()
        ).takeIf(GroqExercise::isValid)
}
