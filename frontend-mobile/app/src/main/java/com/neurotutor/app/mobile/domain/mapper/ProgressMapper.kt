package com.neurotutor.app.mobile.domain.mapper

import com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse
import com.neurotutor.app.mobile.data.model.learning.ModuleItem

/**
 * Modelo de UI unificado para representar el progreso de un tema completo (ej. Fracciones)
 */
data class ThemeProgress(
    val name: String,
    val progressPercentage: Int,
    val modules: List<ModuleProgressResponse>
)

object ProgressMapper {

    /**
     * Agrupa módulos por tema y calcula el promedio real basado en la cantidad TOTAL de niveles
     * recibidos desde el backend (asumiendo que el backend envía la malla completa).
     */
    fun calculateThematicProgress(modules: List<ModuleProgressResponse>): List<ThemeProgress> {
        if (modules.isEmpty()) return emptyList()

        // Agrupamos por el nombre del tema extraído del título (ej: "Fracciones")
        // Nota: Idealmente el backend debería enviar el slug del tema, pero usamos el título como fallback dinámico
        return modules.groupBy { extractThemeName(it.title) }
            .map { (themeName, themeModules) ->
                val totalProgress = themeModules.sumOf { it.progressPercentage }
                // La regla oficial: Suma / Total de niveles existentes en el JSON (Backend debe enviar todos)
                val average = if (themeModules.isNotEmpty()) totalProgress / themeModules.size else 0
                
                ThemeProgress(
                    name = themeName,
                    progressPercentage = average,
                    modules = themeModules
                )
            }
    }

    /**
     * Helper para extraer el nombre del tema (Fracciones, Decimales, etc.)
     * Maneja formatos como "I: Fracciones Básicas" o simplemente "Fracciones"
     */
    private fun extractThemeName(title: String): String {
        return when {
            title.contains("Fracciones", ignoreCase = true) -> "Fracciones"
            title.contains("Decimales", ignoreCase = true) -> "Decimales"
            title.contains("Porcentajes", ignoreCase = true) -> "Porcentajes"
            else -> title.split(":").last().trim()
        }
    }
}
