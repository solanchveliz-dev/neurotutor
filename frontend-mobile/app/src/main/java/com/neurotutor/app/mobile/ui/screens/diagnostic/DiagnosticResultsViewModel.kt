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
                val plantillaCorrectas = listOf("C", "C", "C", "B", "C", "B", "C", "B", "C", "D")
                val temas = listOf(
                    "Sustracción y Cantidades", "Multiplicación", "División y Reparto",
                    "Fracciones", "Decimales", "Ecuaciones", "Perímetros",
                    "Proporcionalidad", "Operaciones combinadas", "Promedios"
                )
                val explicaciones = listOf(
                    "Se suma 1826 + 478 para hallar el total de primaria.",
                    "8 cajas x 6 latas x S/20 = S/960.",
                    "1980 / 50 = 39.6, se redondea a 40 bolsas.",
                    "Hay 9 naranjas de 14 frutas totales (9/14).",
                    "1.8m + 2.4m = 4.2m.",
                    "X = 42 - 26 = 16.",
                    "24+24+12+12 = 72m de cerco.",
                    "18 paquetes son 6 grupos de 3. 6 x S/5 = S/30.",
                    "56 - 8 = 48. 48 / 4 = 12 bizcochos.",
                    "Suma total S/24 / 4 amigos = S/6."
                )

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
