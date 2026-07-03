package com.neurotutor.app.mobile.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.ModuleStatus
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LevelItem(
    val levelId: String,
    val name: String,
    val description: String,
    val status: ModuleStatus,
    val progress: Float = 0f
)

data class TopicDetailUiState(
    val isLoading: Boolean = false,
    val topicTitle: String = "",
    val levels: List<LevelItem> = emptyList(),
    val errorMessage: String? = null
)

class TopicDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TopicDetailUiState())
    val uiState: StateFlow<TopicDetailUiState> = _uiState.asStateFlow()

    fun loadTopicDetails(studentId: String, moduleId: String) {
        val cleanStudentId = studentId.replace("\"", "").trim()

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // 🚀 CONSUMO DE RUTA CON PROGRESO OFICIAL (33/66/100)
                val response = RetrofitClient.apiService.getTopicRuta(moduleId, cleanStudentId)

                if (response.isSuccessful && response.body() != null) {
                    val levelsFromApi = response.body()!!.map { moduleItem ->
                        LevelItem(
                            levelId = moduleItem.id,
                            name = moduleItem.titulo,
                            description = "Contenido adaptado a tu progreso",
                            status = moduleItem.estado,
                            // Sincronización con la única fuente de verdad: ProgressService
                            progress = moduleItem.progressPercentage / 100f
                        )
                    }

                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                topicTitle = "Ruta de Aprendizaje",
                                levels = levelsFromApi
                            ) 
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { 
                            it.copy(isLoading = false, errorMessage = "Error al cargar niveles: ${response.code()}") 
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
