package com.neurotutor.app.mobile.data.repository

import android.util.Log
import com.neurotutor.app.mobile.data.mapper.AiTutorContentParser
import com.neurotutor.app.mobile.data.mapper.AiTutorActionContentNormalizer
import com.neurotutor.app.mobile.data.model.ai.AiTutorContent
import com.neurotutor.app.mobile.data.model.common.AiTutorRequest
import com.neurotutor.app.mobile.data.model.common.AiTutorResponse
import com.neurotutor.app.mobile.data.network.RetrofitClient
import retrofit2.Response

data class AiTutorPrompt(
    val studentId: Long,
    val moduleId: Long,
    val question: String,
    val educationalContext: String,
    val currentScreen: String? = null,
    val action: String? = null
)

sealed interface AiTutorRepositoryResult {
    data class Success(
        val contents: List<AiTutorContent>,
        val rawAnswer: String
    ) : AiTutorRepositoryResult

    data class Error(
        val message: String
    ) : AiTutorRepositoryResult
}

fun interface AiTutorRepositoryContract {
    suspend fun askTutor(prompt: AiTutorPrompt): AiTutorRepositoryResult
}

class AiTutorRepository(
    private val askTutorCall: suspend (AiTutorRequest) -> Response<AiTutorResponse> =
        RetrofitClient.apiService::askTutor,
    private val contentParser: (String) -> List<AiTutorContent> =
        AiTutorContentParser::parse
) : AiTutorRepositoryContract {

    override suspend fun askTutor(prompt: AiTutorPrompt): AiTutorRepositoryResult {
        return try {
            val response = askTutorCall(
                AiTutorRequest(
                    studentId = prompt.studentId,
                    moduleId = prompt.moduleId,
                    question = prompt.question,
                    context = prompt.educationalContext,
                    currentScreen = prompt.currentScreen,
                    action = prompt.action
                )
            )
            val answer = response.body()?.answer
            if (response.isSuccessful && answer != null) {
                val structuredContent = response.body()?.structuredContent
                
                // 2. Logs en Repository
                Log.d("NEO_DEBUG", "Repository: answer received (length=${answer.length})")
                Log.d("NEO_DEBUG", "Repository: structuredContent length=${structuredContent?.length ?: 0}")
                
                val contentSource = structuredContent
                    ?.takeIf(String::isNotBlank)
                    ?: answer
                val parsedContents = contentParser(contentSource)
                
                Log.d("NEO_DEBUG", "Repository: contents parsed (count=${parsedContents.size})")

                val normalizedContents = AiTutorActionContentNormalizer.normalize(
                    action = prompt.action,
                    currentScreen = prompt.currentScreen,
                    parsedContents = parsedContents,
                    educationalContext = prompt.educationalContext
                )
                
                AiTutorRepositoryResult.Success(
                    contents = normalizedContents,
                    rawAnswer = answer
                )
            } else {
                Log.e("NEO_DEBUG", "Repository: Response NOT successful or answer NULL. code=${response.code()}")
                AiTutorRepositoryResult.Error(SAFE_ERROR_MESSAGE)
            }
        } catch (e: Exception) {
            Log.e("NEO_DEBUG", "Repository: Exception during askTutor", e)
            AiTutorRepositoryResult.Error(SAFE_NETWORK_ERROR_MESSAGE)
        }
    }

    private companion object {
        const val SAFE_ERROR_MESSAGE =
            "Neo no pudo responder en este momento. Inténtalo nuevamente."
        const val SAFE_NETWORK_ERROR_MESSAGE =
            "No pudimos conectar con Neo. Revisa tu conexión e inténtalo nuevamente."
    }
}
