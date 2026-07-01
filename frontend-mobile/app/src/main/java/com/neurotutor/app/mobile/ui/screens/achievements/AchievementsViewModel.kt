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

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Obtenemos el progreso del estudiante que contiene los módulos y estados de examen
                val response = RetrofitClient.apiService.getStudentProgress(cleanId)

                if (response.isSuccessful && response.body() != null) {
                    val progress = response.body()!!
                    
                    // Mapeamos los datos del backend a nuestro modelo de UI
                    // Filtramos y agrupamos por módulo (tema)
                    val moduleGroups = progress.modules.groupBy { it.title }
                    
                    val modulesUi = mutableListOf<ModuleAchievementUiModel>()
                    
                    // Procesamos "Fracciones" primero si existe (según requerimiento visual)
                    val fraccionesData = moduleGroups["Fracciones"]
                    if (fraccionesData != null) {
                        modulesUi.add(mapToModuleUi("Fracciones", "Dominas las fracciones en cualquier situación.", R.drawable.fraction_neo_chat, fraccionesData))
                    } else {
                        // Si no viene del backend, pero es el módulo principal, lo mostramos vacío/bloqueado o no lo mostramos?
                        // La regla dice NO HARDCODEAR, pero si el backend no lo devuelve, no se muestra.
                        // Sin embargo, para la consistencia visual del diseño:
                    }

                    // Módulos futuros (Próximamente) - Estos sí pueden ser estáticos visualmente según el prompt
                    modulesUi.add(
                        ModuleAchievementUiModel(
                            id = "dec",
                            title = "Decimales",
                            description = "Resuelves operaciones con decimales con confianza.",
                            iconRes = R.drawable.ic_decimales,
                            levels = emptyList(),
                            isComingSoon = true
                        )
                    )
                    modulesUi.add(
                        ModuleAchievementUiModel(
                            id = "por",
                            title = "Porcentajes",
                            description = "Comprendes y aplicas porcentajes fácilmente.",
                            iconRes = R.drawable.ic_porcentajes,
                            levels = emptyList(),
                            isComingSoon = true
                        )
                    )
                    modulesUi.add(
                        ModuleAchievementUiModel(
                            id = "opc",
                            title = "Operaciones combinadas",
                            description = "Dominas operaciones de varios pasos.",
                            iconRes = R.drawable.ic_modulo_default,
                            levels = emptyList(),
                            isComingSoon = true
                        )
                    )

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                modules = modulesUi
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "No se pudo cargar el progreso.")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun mapToModuleUi(title: String, desc: String, icon: Int, items: List<com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse>): ModuleAchievementUiModel {
        val levels = listOf(
            createLevelUi("B", "Básico", R.drawable.achievement_basic, items),
            createLevelUi("I", "Intermedio", R.drawable.achievement_intermediate, items),
            createLevelUi("A", "Avanzado", R.drawable.achievement_advanced, items)
        )
        
        return ModuleAchievementUiModel(
            id = items.firstOrNull()?.moduleId ?: UUID.randomUUID().toString(),
            title = title,
            description = desc,
            iconRes = icon,
            levels = levels
        )
    }

    private fun createLevelUi(tag: String, name: String, badge: Int, items: List<com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse>): LevelAchievementUiModel {
        val item = items.find { it.level == tag }
        val isUnlocked = item?.examPassed ?: false
        val date = item?.completedAt?.let { formatDate(it) }
        
        return LevelAchievementUiModel(
            levelTag = tag,
            levelName = name,
            isUnlocked = isUnlocked,
            unlockedDate = if (isUnlocked) date else null,
            badgeRes = badge
        )
    }

    private fun formatDate(dateStr: String): String {
        return try {
            // Asumiendo formato ISO del backend, ajustamos a dd/MM/yyyy
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }
}
