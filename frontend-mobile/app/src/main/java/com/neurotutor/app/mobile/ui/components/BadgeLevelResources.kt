package com.neurotutor.app.mobile.ui.components

import androidx.annotation.DrawableRes
import com.neurotutor.app.mobile.R

object BadgeLevelResources {

    fun normalizeLevelTag(level: String): String? =
        when (level.trim().uppercase()) {
            "B", "BASICO", "BÁSICO", "BASIC" -> "B"
            "I", "INTERMEDIO", "INTERMEDIATE" -> "I"
            "A", "AVANZADO", "ADVANCED" -> "A"
            else -> null
        }

    fun levelNameFor(level: String): String? =
        when (normalizeLevelTag(level)) {
            "B" -> "Básico"
            "I" -> "Intermedio"
            "A" -> "Avanzado"
            else -> null
        }

    @DrawableRes
    fun badgeResourceFor(level: String): Int? =
        when (normalizeLevelTag(level)) {
            "B" -> R.drawable.achievement_basic
            "I" -> R.drawable.achievement_intermediate
            "A" -> R.drawable.achievement_advanced
            else -> null
        }
}
