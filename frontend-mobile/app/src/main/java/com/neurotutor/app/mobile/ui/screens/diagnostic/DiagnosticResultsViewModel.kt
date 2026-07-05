package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.diagnostic.DiagnosticRequest
import com.neurotutor.app.mobile.data.model.diagnostic.DiagnosticResponse
import com.neurotutor.app.mobile.data.model.common.QuestionResult
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiagnosticResultsUiState(
    val isLoading: Boolean = false,
    val nivelAsignado: String = "",
    val mensaje: String = "",
    val totalAciertos: Int = 0,
    val listaResultados: List<QuestionResult> = emptyList(),
    val preguntaSeleccionada: QuestionResult? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class DiagnosticResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiagnosticResultsUiState())
    val uiState: StateFlow<DiagnosticResultsUiState> = _uiState.asStateFlow()

    fun procesarResultadoExamen(studentId: String, respuestasAlumno: List<String>) {
        if (_uiState.value.listaResultados.isNotEmpty() || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val plantillaCorrectas = DiagnosticEducationCatalog.lessons.map { it.correctLetter }
                val temas = DiagnosticEducationCatalog.lessons.map { it.topic }
                val explicaciones = DiagnosticEducationCatalog.lessons.map { it.conclusion }

                var aciertos = 0
                val resultados = ArrayList<QuestionResult>()

                for (i in 0 until 10) {
                    val resp = respuestasAlumno.getOrNull(i) ?: ""
                    val esCorrecta = resp.equals(plantillaCorrectas[i], ignoreCase = true)
                    if (esCorrecta) aciertos++

                    resultados.add(QuestionResult(
                        numeroPregunta = i + 1,
                        esCorrecta = esCorrecta,
                        temaEvaluado = temas[i],
                        respuestaEstudiante = "Opción ($resp)",
                        respuestaCorrecta = "Opción (${plantillaCorrectas[i]})",
                        explicacion = explicaciones[i]
                    ))
                }

                val request = DiagnosticRequest(studentId, respuestasAlumno)
                val response = RetrofitClient.apiService.submitDiagnostic(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nivelAsignado = body.nivel,
                            mensaje = body.mensaje,
                            totalAciertos = aciertos,
                            listaResultados = resultados,
                            preguntaSeleccionada = resultados.firstOrNull(),
                            isSuccess = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error en el servidor") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun seleccionarPregunta(resultado: QuestionResult) {
        _uiState.update { it.copy(preguntaSeleccionada = resultado) }
    }

    fun obtenerResultadosDiagnostico() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
