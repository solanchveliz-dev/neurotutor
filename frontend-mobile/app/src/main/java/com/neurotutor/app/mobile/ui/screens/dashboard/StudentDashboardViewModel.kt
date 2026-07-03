package com.neurotutor.app.mobile.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.domain.mapper.ProgressMapper
import com.neurotutor.app.mobile.ui.components.BadgeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

data class StudentDashboardUiState(
    val isLoading: Boolean = true,
    val nombreEstudiante: String = "",
    val gradoSeccion: String = "",
    val nivelActual: String = "",
    val puntosTotales: Int = 0,
    val overallProgress: Int = 0,
    val modulos: List<ModuleItem> = emptyList(),
    val earnedBadges: List<DashboardBadgeUiModel> = emptyList(),
    val errorMessage: String? = null
)

class StudentDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    fun cargarInformacionReal(studentId: String) {
        val cleanId = studentId.replace("\"", "").trim()

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profileDeferred = async { RetrofitClient.apiService.getStudentProfile(cleanId) }
                val progressDeferred = async { RetrofitClient.apiService.getStudentProgress(cleanId) }

                val profileResponse = profileDeferred.await()
                val progressResponse = progressDeferred.await()

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

                    val earnedBadges = BadgeMapper.fromModules(progressData?.modules.orEmpty())

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
                                earnedBadges = earnedBadges
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
}
