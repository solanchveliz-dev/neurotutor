package com.neurotutor.app.mobile.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class TheoryViewModel : ViewModel() {

    /**
     * Notifica al backend que el estudiante ha completado la lectura de la teoría.
     * Esto activa el flag theory_completed en el ProgressService (33% de progreso).
     */
    fun markTheoryAsCompleted(studentId: String, moduleId: String) {
        val cleanStudentId = studentId.replace("\"", "").trim()
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Llamada al endpoint oficial POST /api/students/{id}/modules/{mid}/theory/complete
                RetrofitClient.apiService.completeTheory(cleanStudentId, moduleId)
            } catch (e: Exception) {
                // Error de red silencioso: No bloqueamos la experiencia del usuario si falla la red,
                // el hito se intentará sincronizar en la próxima sesión.
            }
        }
    }
}
