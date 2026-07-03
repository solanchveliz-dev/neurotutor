package com.neurotutor.app.mobile.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.network.RetrofitClient
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
        val shouldShowLoading = _uiState.value.themes.isEmpty()

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = shouldShowLoading, errorMessage = null) }

            try {
                val response = RetrofitClient.apiService.getStudentProgress(cleanId)

                if (response.isSuccessful && response.body() != null) {
                    val progress = response.body()!!
                    
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
        val milestones = when (levelName) {
            "Básico" -> listOf(
                MilestoneUiModel("Explorador", item?.theoryCompleted ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) }),
                MilestoneUiModel("Practicante", item?.practiceCompleted ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) }),
                MilestoneUiModel("Primer Maestro", item?.examPassed ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) })
            )
            "Intermedio" -> listOf(
                MilestoneUiModel("Investigador", item?.theoryCompleted ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) }),
                MilestoneUiModel("Estratega", item?.practiceCompleted ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) }),
                MilestoneUiModel("Dominador", item?.examPassed ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) })
            )
            "Avanzado" -> listOf(
                MilestoneUiModel("Experto", item?.theoryCompleted ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) }),
                MilestoneUiModel("Preciso", item?.practiceCompleted ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) }),
                MilestoneUiModel("Maestro Supremo", item?.examPassed ?: false, R.drawable.general_medal, item?.completedAt?.let { formatDate(it) })
            )
            else -> emptyList()
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
