package com.neurotutor.app.mobile.ui.screens.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.local.ProgressManager
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class StudentDashboardUiState(
    val isLoading: Boolean = true,
    val nombreEstudiante: String = "",
    val gradoSeccion: String = "",
    val nivelActual: String = "",
    val puntosTotales: Int = 0,
    val modulos: List<ModuleItem> = emptyList(),
    val errorMessage: String? = null
)

class StudentDashboardViewModel(private val appContext: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    private lateinit var progressManager: ProgressManager
    private var hasClearedProgress = false

    init {
        progressManager = ProgressManager(appContext)
    }

    fun cargarInformacionReal(studentId: String) {
        val cleanId = studentId.replace("\"", "").trim()

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getStudentProfile(cleanId)

                if (response.isSuccessful && response.body() != null) {
                    val perfil = response.body()!!
                    val nivelEspanol = when (perfil.nivelActual) {
                        "COHETE", "AVANZADO" -> "Avanzado 🚀"
                        "FUEGO", "INTERMEDIO" -> "Intermedio 🔥"
                        else -> "Básico 🌱"
                    }

                    // Limpiar progreso SOLO LA PRIMERA VEZ (para empezar desde 0)
                    if (!hasClearedProgress) {
                        progressManager.clearAllProgressForStudent(cleanId)
                        hasClearedProgress = true
                    }

                    // Calcular progreso real basado en ejercicios completados localmente
                    val modulosConProgreso = perfil.modulos.map { modulo ->
                        val ejerciciosCompletados = progressManager.getCompletedExercisesCount(cleanId, modulo.id)
                        Log.d("Progress", "Módulo ${modulo.id} - Completados: $ejerciciosCompletados de ${modulo.ejerciciosTotales}")
                        modulo.copy(
                            ejerciciosCompletados = ejerciciosCompletados
                        )
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                nombreEstudiante = perfil.nombreCompleto,
                                gradoSeccion = perfil.gradoSeccion,
                                nivelActual = nivelEspanol,
                                puntosTotales = perfil.puntosTotales,
                                modulos = modulosConProgreso
                            )
                        }
                    }
                } else {
                    val code = response.code()
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Error $code: No se encontró el perfil.")
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

    fun refreshProgress(studentId: String) {
        cargarInformacionReal(studentId)
    }
}