package com.neurotutor.app.mobile.data.model.learning

data class PreguntaPractica(
    val pregunta: String,
    val opciones: List<String>,
    val respuestaCorrecta: Int
)