package com.neurotutor.app.mobile.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta NeuroTutor
val NeuroBlue = Color(0xFF3B82F6)
val NeuroPurple = Color(0xFF7C3AED)
val NeuroSky = Color(0xFFE0F2FE)
val NeuroGreen = Color(0xFF22C55E)
val NeuroOrange = Color(0xFFF59E0B)
val NeuroRed = Color(0xFFEF4444)
val NeuroWhite = Color(0xFFFFFFFF)

// Gradientes
val GradientPrincipal = listOf(NeuroBlue, NeuroPurple)
val GradientExito = listOf(Color(0xFF10B981), Color(0xFF059669))
val GradientEstudiante = listOf(Color(0xFF4F46E5), Color(0xFF6366F1))
val GradientNaranja = listOf(Color(0xFFF59E0B), Color(0xFFFB923C))
val GradientMorado = listOf(NeuroPurple, Color(0xFF8B5CF6))

// Fondos
val FondoGradiente = listOf(
    Color(0xFFEEF2FF),  // indigo-50
    NeuroWhite,         // white
    Color(0xFFFAF5FF)   // purple-50
)

// Colores base existentes
val MoradoActivo = NeuroPurple
val TextoBase = Color(0xFF1F2937)      // ← Corregido: faltaba Color()
val FondoPanelEstudiante = NeuroSky.copy(alpha = 0.3f)
val CardBackground = NeuroWhite
val InputBackground = Color(0xFFF9FAFB)
val TextDark = Color(0xFF1F2937)
val TextGray = Color(0xFF6B7280)
val BackgroundLight = Color(0xFFF5F7FA)
val TabUnselected = Color(0xFFF3F4F6)
val TabSelected = NeuroPurple