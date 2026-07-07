package com.neurotutor.app.mobile.data.model.common

data class QuestionResult(
    val numeroPregunta: Int,
    val esCorrecta: Boolean,
    val temaEvaluado: String,
    val respuestaEstudiante: String,
    val respuestaCorrecta: String,
    val explicacion: String
)
