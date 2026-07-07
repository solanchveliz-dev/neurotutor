package com.neurotutor.app.mobile.ui.screens.learning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LevelSectionsUiState(
    val isLoading: Boolean = true,
    val progreso: Float = 0f,
    val teoriaCompletada: Boolean = false,
    val ejerciciosCompletados: Int = 0,
    val totalEjercicios: Int = 10,
    val examenDisponible: Boolean = false,
    val examPassed: Boolean = false,
    val mensajeTutor: String = "",
    val errorMessage: String? = null
)

class LevelSectionsViewModel(
    private val context: Context,
    private val studentId: String,
    private val moduleId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelSectionsUiState())
    val uiState: StateFlow<LevelSectionsUiState> = _uiState.asStateFlow()

    fun loadProgress() {
        val cleanStudentId = studentId.replace("\"", "").trim()
        
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 🚀 CONSUMO DE MÉTRICA OFICIAL DEL BACKEND (Lógica 33/66/100)
                val response = RetrofitClient.apiService.getModuleProgress(cleanStudentId, moduleId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    
                    val mensaje = when {
                        !data.theoryCompleted -> "¡Comienza con la teoría para empezar tu camino! 📚"
                        !data.practiceCompleted -> "¡Vas bien! Completa la práctica para desbloquear el examen 💪"
                        !data.examPassed -> "¡Excelente! Ya puedes dar el examen final 🎉"
                        else -> "¡Felicidades! Has dominado este nivel por completo 🏆"
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                progreso = data.progressPercentage / 100f,
                                teoriaCompletada = data.theoryCompleted,
                                ejerciciosCompletados = data.practiceCompletedCount,
                                totalEjercicios = data.practiceTotalCount,
                                examenDisponible = data.practiceCompleted,
                                examPassed = data.examPassed,
                                mensajeTutor = mensaje
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(isLoading = false, errorMessage = "Error al sincronizar progreso.") 
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { 
                        it.copy(isLoading = false, errorMessage = "Fallo de conexión: ${e.message}") 
                    }
                }
            }
        }
    }
}
