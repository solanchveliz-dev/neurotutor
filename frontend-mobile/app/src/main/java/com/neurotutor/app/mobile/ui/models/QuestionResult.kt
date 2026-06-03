package com.neurotutor.app.mobile.ui.models

data class QuestionResult(
    val numeroPregunta: Int,      // Del 1 al 10
    val esCorrecta: Boolean,       // true = ✔️, false = ❌
    val temaEvaluado: String,      // Ej: "Suma de Fracciones Heterogéneas"
    val respuestaEstudiante: String, // Lo que marcó el niño
    val respuestaCorrecta: String,   // La solución real
    val explicacion: String        // 🚀 NUEVO: Para cumplir con HU-13
)