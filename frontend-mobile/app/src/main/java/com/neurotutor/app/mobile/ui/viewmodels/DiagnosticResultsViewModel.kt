package com.neurotutor.app.mobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.ui.network.RetrofitClient

// 🚀 IMPORTACIONES CORRECTAS DE TU CARPETA DE MODELOS
import com.neurotutor.app.mobile.ui.models.QuestionResult
import com.neurotutor.app.mobile.ui.models.DiagnosticRequest
import com.neurotutor.app.mobile.ui.models.DiagnosticResponse

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiagnosticResultsUiState(
    val isLoading: Boolean = false,
    val nivelAsignado: String = "",
    val mensaje: String = "", // 🚀 Sincronizado con state.mensaje de tu LevelAssignmentScreen
    val totalAciertos: Int = 0, // 🚀 Sincronizado con state.totalAciertos de tu LevelAssignmentScreen
    val listaResultados: List<QuestionResult> = emptyList(),
    val preguntaSeleccionada: QuestionResult? = null,
    val errorMessage: String? = null
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
                    "Sustracción y Problemas de cantidad", "Multiplicación y cajas de pintura",
                    "División de bolsas de alimento", "Fracciones en conjunto de frutas",
                    "Suma de decimales en longitudes", "Ecuaciones de primer grado",
                    "Perímetros de terrenos geométricos", "Proporcionalidad y tablas de costos",
                    "Operaciones combinadas con bizcochos", "Análisis de datos y optimización de precios"
                )

                // 🧠 Resoluciones detalladas recuperadas (HU-13)
                val resolucionesEjercicios = listOf(
                    "Resolución: Para calcular cuántos tarros quedan, restamos el total inicial menos lo vendido. Operación: 450 - 180 = 270 tarros.",
                    "Resolución: Multiplicamos las filas por las columnas para hallar la cantidad total de cajas organizadas en el almacén. Operación: 12 filas × 8 columnas = 96 cajas.",
                    "Resolución: Repartir de forma equitativa implica dividir. Dividimos los kilogramos totales de alimento entre los sacos disponibles. Operación: 120 kg ÷ 6 sacos = 20 kg por saco.",
                    "Resolución: Contamos las manzanas pintadas sobre el total del grupo. Si hay 3 pintadas de un total de 4, representa los tres cuartos (3/4) del total.",
                    "Resolución: Colocamos los números alineando verticalmente sus comas decimales y sumamos de manera tradicional. Operación: 2.5m + 1.75m = 4.25m.",
                    "Resolución: Para despejar la incógnita 'x', el número que está sumando (+15) pasa al otro lado de la igualdad restando. Operación: x = 50 - 15 -> x = 35.",
                    "Resolución: El perímetro es el contorno. Sumamos las longitudes de los cuatro lados del terreno rectangular (dos largos y dos anchos). Operación: 20 + 20 + 10 + 10 = 60 metros.",
                    "Resolución: Al ser magnitudes directamente proporcionales, si la cantidad de cuadernos se duplica, el precio a pagar también se duplicará exactamente. Operación: 3 cuadernos por S/.9 significa que 6 cuadernos cuestan S/.18.",
                    "Resolución: Según la jerarquía de operaciones, primero debemos resolver la multiplicación y al resultado restarle el valor final. Operación: (5 × 6) - 4 = 30 - 4 = 26.",
                    "Resolución: Calculamos el costo individual dividiendo el precio del paquete entre sus unidades y elegimos el menor valor unitario para ahorrar."
                )

                var contadorCorrectasLocal = 0
                val resultadosCalculados = ArrayList<QuestionResult>()

                for (i in 0 until 10) {
                    val respAlumno = respuestasAlumno.getOrNull(i) ?: ""
                    val respCorrecta = plantillaCorrectas[i]
                    val esCorrecta = respAlumno.equals(respCorrecta, ignoreCase = true)

                    if (esCorrecta) contadorCorrectasLocal++

                    resultadosCalculados.add(
                        QuestionResult(
                            numeroPregunta = i + 1,
                            temaEvaluado = temas[i],
                            respuestaEstudiante = "Opción ($respAlumno)",
                            respuestaCorrecta = "Opción ($respCorrecta)",
                            esCorrecta = esCorrecta,
                            explicacion = resolucionesEjercicios[i]
                        )
                    )
                }

                val request = DiagnosticRequest(studentId, respuestasAlumno)
                val response = RetrofitClient.apiService.submitDiagnostic(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nivelAsignado = body.nivel,
                            mensaje = body.mensaje, // Mapeado exacto
                            totalAciertos = contadorCorrectasLocal, // Mapeado exacto
                            listaResultados = resultadosCalculados,
                            preguntaSeleccionada = resultadosCalculados.firstOrNull()
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error en la respuesta del servidor.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun seleccionarPregunta(resultado: QuestionResult) {
        _uiState.update { it.copy(preguntaSeleccionada = resultado) }
    }

    fun obtenerResultadosDiagnostico(): DiagnosticResultsUiState {
        return _uiState.value
    }
}