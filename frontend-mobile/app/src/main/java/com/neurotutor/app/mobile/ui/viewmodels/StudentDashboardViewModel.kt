package com.neurotutor.app.mobile.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.ui.models.ModuleItem
import com.neurotutor.app.mobile.ui.network.RetrofitClient
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

class StudentDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    fun cargarInformacionReal(studentId: String) {
        val cleanId = studentId.replace("\"", "").trim()
        
        // 🚀 MIRA ESTO EN EL LOGCAT DE ANDROID STUDIO
        Log.d("DashboardVM", "URL BASE: ${RetrofitClient.javaClass.simpleName}")
        Log.d("DashboardVM", "ID ENVIADO: '$cleanId'")

        if (cleanId.isEmpty() || cleanId == "null") {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ID de estudiante inválido.") }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitClient.apiService.getStudentProfile(cleanId)
                
                // 🚀 ESTO TE DIRÁ LA URL EXACTA QUE FALLA
                val requestUrl = response.raw().request.url
                Log.d("DashboardVM", "PETICIÓN ENVIADA A: $requestUrl")

                if (response.isSuccessful && response.body() != null) {
                    val perfil = response.body()!!
                    val nivelEspanol = when (perfil.nivelActual) {
                        "COHETE", "AVANZADO" -> "Avanzado 🚀"
                        "FUEGO", "INTERMEDIO" -> "Intermedio 🔥"
                        else -> "Básico 🌱"
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                nombreEstudiante = perfil.nombreCompleto,
                                gradoSeccion = perfil.gradoSeccion,
                                nivelActual = nivelEspanol,
                                puntosTotales = perfil.puntosTotales,
                                modulos = perfil.modulos
                            )
                        }
                    }
                } else {
                    val code = response.code()
                    Log.e("DashboardVM", "❌ ERROR $code en la URL: $requestUrl")
                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(isLoading = false, errorMessage = "Error $code: El servidor no encontró esta dirección.") 
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "❌ FALLO DE RED: ${e.message}")
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error de red: ${e.localizedMessage}")
                    }
                }
            }
        }
    }
}
