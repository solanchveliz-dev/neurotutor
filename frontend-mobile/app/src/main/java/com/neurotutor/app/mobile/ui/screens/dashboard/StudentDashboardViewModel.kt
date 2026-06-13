package com.neurotutor.app.mobile.ui.screens.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.data.model.learning.ModuleStatus
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

class StudentDashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    fun cargarInformacionReal(studentId: String) {
        val cleanId = studentId.replace("\"", "").trim()
        
        Log.d("DashboardVM", "🚀 Intentando llamar a API con ID limpio: '$cleanId'")

        if (cleanId.isEmpty() || cleanId == "null") {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Error: El usuario no inició sesión correctamente.") }
            return
        }

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
                    val errorBody = response.errorBody()?.string() ?: "Sin detalle"
                    Log.e("DashboardVM", "❌ Error $code: $errorBody")
                    
                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(isLoading = false, errorMessage = "Error $code: No se encontró tu perfil. Revisa que el ID exista en MySQL.") 
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Error de red: ${e.localizedMessage}")
                    }
                }
            }
        }
    }
}
