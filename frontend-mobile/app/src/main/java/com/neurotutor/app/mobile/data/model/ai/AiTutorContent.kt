package com.neurotutor.app.mobile.data.model.ai

sealed interface AiTutorContent {
    data class Text(val text: String) : AiTutorContent

    data class StepExplanationContent(
        val explanation: StepExplanation
    ) : AiTutorContent

    data class SocraticQuestion(
        val question: String
    ) : AiTutorContent

    data class MultipleChoice(
        val exercise: InteractiveExercise
    ) : AiTutorContent

    data class HintCard(
        val text: String
    ) : AiTutorContent

    data class ValidationCard(
        val text: String
    ) : AiTutorContent

    data class SuccessCard(
        val text: String
    ) : AiTutorContent
}
