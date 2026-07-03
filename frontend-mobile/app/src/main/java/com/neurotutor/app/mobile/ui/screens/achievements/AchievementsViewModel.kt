package com.neurotutor.app.mobile.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.ui.components.BadgeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AchievementsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    fun loadAchievements(studentId: String) {
        val cleanId = studentId.replace("\"", "").trim()
        val shouldShowLoading = _uiState.value.themes.isEmpty() &&
                _uiState.value.achievementHistory.isEmpty()

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = shouldShowLoading, errorMessage = null) }

            try {
                val response = RetrofitClient.apiService.getStudentProgress(cleanId)
                val achievementsResponse =
                    RetrofitClient.apiService.getStudentAchievements(cleanId)

                if (response.isSuccessful && response.body() != null) {
                    val progress = response.body()!!
                    val achievementHistory = achievementsResponse.body()
                        ?.unlocked
                        .orEmpty()
                        .map { achievement ->
                            AchievementHistoryItem(
                                id = achievement.id,
                                action = achievementAction(achievement.code, achievement.description),
                                completedAt = achievement.unlockedAt?.let(::formatDate)
                            )
                        }
                    
                    // 🚀 NORMALIZACIÓN: Unificar todos los niveles de un tema en un solo grupo
                    // Elimina la división de "Fracciones I", "Fracciones II", etc.
                    val moduleGroups = progress.modules.groupBy { 
                        normalizeThemeName(it.title)
                    }
                    
                    val themesUi = moduleGroups.map { (tema, niveles) ->
                        mapToThemeUi(tema, niveles)
                    }.toMutableList()

                    // 🚀 CONTENIDO PRÓXIMO: Insertar temas futuros de forma explícita
                    val currentThemeTitles = themesUi.map { it.title.uppercase() }
                    
                    if ("DECIMALES" !in currentThemeTitles) {
                        themesUi.add(createComingSoonTheme("DECIMALES", R.drawable.ic_decimales))
                    }
                    if ("PORCENTAJES" !in currentThemeTitles) {
                        themesUi.add(createComingSoonTheme("PORCENTAJES", R.drawable.ic_porcentajes))
                    }
                    if ("GEOMETRÍA" !in currentThemeTitles) {
                        themesUi.add(createComingSoonTheme("GEOMETRÍA", R.drawable.ic_modulo_default))
                    }

                    // Ordenar: Reales primero, luego alfabético
                    val sortedThemes = themesUi.sortedWith(
                        compareBy<ThemeAchievementUiModel> { it.isComingSoon }
                            .thenBy { it.title }
                    )

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                themes = sortedThemes,
                                achievementHistory = achievementHistory,
                                errorMessage = null
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "No se pudo sincronizar el álbum de insignias.")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun achievementAction(code: String, fallback: String): String =
        when (code) {
            "DIAGNOSTIC_COMPLETED" -> "Completaste tu evaluación diagnóstica."
            "FIRST_THEORY_COMPLETED" -> "Completaste tu primera teoría."
            "FIRST_PRACTICE_PASSED" -> "Aprobaste tu primera práctica."
            "FIRST_EXAM_PASSED" -> "Aprobaste tu primer examen final."
            "FIRST_MODULE_COMPLETED" -> "Completaste tu primer módulo."
            "BASIC_LEVEL_COMPLETED" -> "Completaste el nivel Básico."
            "INTERMEDIATE_LEVEL_COMPLETED" -> "Completaste el nivel Intermedio."
            "ADVANCED_LEVEL_COMPLETED" -> "Completaste el nivel Avanzado."
            "POINTS_100" -> "Alcanzaste tus primeros 100 puntos."
            else -> fallback
        }

    /**
     * Identifica el tema raíz basándose en el título para agrupar niveles académicos.
     */
    private fun normalizeThemeName(title: String): String {
        return when {
            title.contains("Fracciones", ignoreCase = true) -> "FRACCIONES"
            title.contains("Decimales", ignoreCase = true) -> "DECIMALES"
            title.contains("Porcentajes", ignoreCase = true) -> "PORCENTAJES"
            title.contains("Geometría", ignoreCase = true) -> "GEOMETRÍA"
            else -> title.uppercase()
        }
    }

    private fun createComingSoonTheme(title: String, icon: Int): ThemeAchievementUiModel {
        return ThemeAchievementUiModel(
            id = "soon_${title.lowercase()}",
            title = title,
            description = "Mundo en construcción...",
            iconRes = icon,
            levelGroups = emptyList(),
            isComingSoon = true
        )
    }

    private fun mapToThemeUi(
        title: String, 
        items: List<com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse>
    ): ThemeAchievementUiModel {
        
        // Generar la estructura de 3 niveles académicos para el tema
        val levelGroups = listOf(
            createLevelGroupUi("Básico", items),
            createLevelGroupUi("Intermedio", items),
            createLevelGroupUi("Avanzado", items)
        )
        
        return ThemeAchievementUiModel(
            id = items.firstOrNull()?.moduleId ?: "theme_${title.lowercase()}",
            title = title,
            description = "Completa todos los retos de $title para alcanzar la maestría suprema.",
            iconRes = R.drawable.fraction_neo_chat,
            levelGroups = levelGroups
        )
    }

    private fun createLevelGroupUi(
        levelName: String, 
        items: List<com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse>
    ): LevelGroupUiModel {
        
        // Buscar el módulo que corresponde al nivel académico específico dentro del tema
        val item = items.find { 
            it.level.startsWith(levelName.take(1), ignoreCase = true) || it.level.contains(levelName, ignoreCase = true)
        }
        
        // Mapear los hitos basados en la Opción A (9 insignias por tema)
        val milestones = BadgeMapper
            .badgesForLevel(level = levelName, module = item)
            .map { badge ->
                MilestoneUiModel(
                    name = badge.name,
                    isUnlocked = badge.isUnlocked,
                    badgeRes = badge.iconRes
                )
            }
        
        return LevelGroupUiModel(
            levelName = levelName,
            milestones = milestones
        )
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }
}
