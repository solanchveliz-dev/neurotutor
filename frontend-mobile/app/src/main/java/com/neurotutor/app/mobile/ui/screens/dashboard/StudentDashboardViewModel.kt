package com.neurotutor.app.mobile.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.data.model.auth.AchievementResponse
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.domain.mapper.ProgressMapper
import com.neurotutor.app.mobile.ui.components.BadgeMapper
import com.neurotutor.app.mobile.ui.components.LearningBadgeUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import android.os.SystemClock

data class StudentDashboardUiState(
    val isLoading: Boolean = true,
    val nombreEstudiante: String = "",
    val gradoSeccion: String = "",
    val nivelActual: String = "",
    val puntosTotales: Int = 0,
    val overallProgress: Int = 0,
    val modulos: List<ModuleItem> = emptyList(),
    val unlockedAchievements: List<AchievementResponse> = emptyList(),
    val latestAcademicBadge: LearningBadgeUiModel? = null,
    val errorMessage: String? = null
)

class StudentDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var lastLoadedAt = 0L

    fun cargarInformacionReal(studentId: String, force: Boolean = false) {
        val cleanId = studentId.replace("\"", "").trim()
        val hasFreshData = _uiState.value.nombreEstudiante.isNotBlank() &&
                SystemClock.elapsedRealtime() - lastLoadedAt < CACHE_TTL_MS
        if (!force && hasFreshData) return
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = it.nombreEstudiante.isBlank(), errorMessage = null)
            }
            try {
                val profileDeferred = async { RetrofitClient.apiService.getStudentProfile(cleanId) }
                val progressDeferred = async { RetrofitClient.apiService.getStudentProgress(cleanId) }
                val achievementsDeferred = async {
                    RetrofitClient.apiService.getStudentAchievements(cleanId)
                }

                val profileResponse = profileDeferred.await()
                val progressResponse = progressDeferred.await()
                val achievementsResponse = achievementsDeferred.await()

                if (profileResponse.isSuccessful && profileResponse.body() != null) {
                    val perfil = profileResponse.body()!!
                    val progressData = if (progressResponse.isSuccessful) progressResponse.body() else null
                    
                    val nivelEspanol = when (perfil.nivelActual) {
                        "COHETE", "AVANZADO" -> "Avanzado 🚀"
                        "FUEGO", "INTERMEDIO" -> "Intermedio 🔥"
                        else -> "Básico 🌱"
                    }

                    // 🚀 CRUCE DE DATOS: Usar ProgressMapper para inyectar progreso vivo en la malla del perfil
                    val modulosSincronizados = ProgressMapper.fromProfileWithLiveProgress(
                        profileModules = perfil.modulos,
                        liveProgress = progressData?.modules.orEmpty()
                    )

                    val allAchievements = achievementsResponse.body()?.unlocked.orEmpty()
                    val hasLevelCompletion = allAchievements.any {
                        it.code in LEVEL_COMPLETION_CODES
                    }
                    val unlockedAchievements = allAchievements.filterNot {
                        hasLevelCompletion && it.code == "FIRST_MODULE_COMPLETED"
                    }
                    val latestAcademicBadge = unlockedAchievements
                        .firstOrNull()
                        ?.takeIf { it.code in LEVEL_COMPLETION_CODES }
                        ?.let { achievement ->
                            examBadgeForLevelCompletion(
                                achievementCode = achievement.code,
                                modules = progressData?.modules.orEmpty()
                            )
                        }
                    lastLoadedAt = SystemClock.elapsedRealtime()

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                nombreEstudiante = perfil.nombreCompleto,
                                gradoSeccion = perfil.gradoSeccion,
                                nivelActual = nivelEspanol,
                                puntosTotales = perfil.puntosTotales,
                                overallProgress = progressData?.overallProgress ?: 0,
                                modulos = modulosSincronizados,
                                unlockedAchievements = unlockedAchievements,
                                latestAcademicBadge = latestAcademicBadge
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                }
            }
        }
    }

    fun refreshProgress(studentId: String) {
        cargarInformacionReal(studentId)
    }

    private fun examBadgeForLevelCompletion(
        achievementCode: String,
        modules: List<com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse>
    ): LearningBadgeUiModel? {
        val level = when (achievementCode) {
            "BASIC_LEVEL_COMPLETED" -> "B"
            "INTERMEDIATE_LEVEL_COMPLETED" -> "I"
            "ADVANCED_LEVEL_COMPLETED" -> "A"
            else -> return null
        }
        val module = modules.firstOrNull {
            it.examPassed && it.level.startsWith(level, ignoreCase = true)
        }
        return BadgeMapper.badgesForLevel(level, module)
            .firstOrNull { it.trigger == "EXAM" && it.isUnlocked }
    }

    companion object {
        private const val CACHE_TTL_MS = 30_000L
        private val LEVEL_COMPLETION_CODES = setOf(
            "BASIC_LEVEL_COMPLETED",
            "INTERMEDIATE_LEVEL_COMPLETED",
            "ADVANCED_LEVEL_COMPLETED"
        )
    }
}
