package com.neurotutor.app.mobile.ui.screens.learning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.local.ProgressManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

data class LevelSectionsUiState(
    val isLoading: Boolean = true,
    val progreso: Float = 0f,
    val teoriaCompletada: Boolean = false,
    val ejerciciosCompletados: Int = 0,
    val totalEjercicios: Int = 10,
    val examenDisponible: Boolean = false,
    val mensajeTutor: String = ""
)

class LevelSectionsViewModel(
    private val context: Context,
    private val studentId: String,
    private val moduleId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelSectionsUiState())
    val uiState: StateFlow<LevelSectionsUiState> = _uiState.asStateFlow()

    private lateinit var progressManager: ProgressManager

    init {
        progressManager = ProgressManager(context)
    }

    fun loadProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            val completados = progressManager.getCompletedExercisesCount(studentId, moduleId)
            val total = 10  // 10 ejercicios prácticos por módulo
            val progreso = if (total > 0) completados.toFloat() / total else 0f
            val teoriaCompletada = progreso > 0f  // O podrías tener un flag específico
            val examenDisponible = completados >= total  // Examen disponible solo con todos los ejercicios completados

            val mensaje = when {
                completados == 0 -> "¡Comienza con la teoría y luego practica! 📚"
                completados < total / 2 -> "¡Vas bien! Sigue practicando 💪"
                completados < total -> "¡Muy bien! Ya casi completas los ejercicios 🎯"
                else -> "¡Excelente! Ya puedes dar el examen final 🎉"
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    progreso = progreso,
                    teoriaCompletada = teoriaCompletada,
                    ejerciciosCompletados = completados,
                    totalEjercicios = total,
                    examenDisponible = examenDisponible,
                    mensajeTutor = mensaje
                )
            }
        }
    }
}