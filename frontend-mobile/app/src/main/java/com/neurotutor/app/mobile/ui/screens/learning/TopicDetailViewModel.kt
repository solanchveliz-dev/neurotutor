package com.neurotutor.app.mobile.ui.screens.learning

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.learning.ModuleStatus
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private var loadJob: Job? = null
    private var lastLoadedAt = 0L
    private var lastRequestKey: String? = null

    fun loadTopicDetails(studentId: String, moduleId: String, force: Boolean = false) {
        val cleanStudentId = studentId.replace("\"", "").trim()
        val requestKey = "$cleanStudentId:$moduleId"
        val hasFreshData = lastRequestKey == requestKey &&
                _uiState.value.levels.isNotEmpty() &&
                SystemClock.elapsedRealtime() - lastLoadedAt < CACHE_TTL_MS

        if (!force && hasFreshData) return
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(isLoading = it.levels.isEmpty(), errorMessage = null)
            }
            try {
                val response = RetrofitClient.apiService.getTopicRuta(moduleId, cleanStudentId)
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    val levelsFromApi = body.map { moduleItem ->
                        LevelItem(
                            levelId = moduleItem.id,
                            name = moduleItem.titulo,
                            description = "Contenido adaptado a tu progreso",
                            status = moduleItem.estado,
                            progress = moduleItem.progressPercentage / 100f
                        )
                    }

                    lastRequestKey = requestKey
                    lastLoadedAt = SystemClock.elapsedRealtime()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            topicTitle = "Ruta de Aprendizaje",
                            levels = levelsFromApi,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar niveles: ${response.code()}"
                        )
                    }
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Fallo de conexión: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    companion object {
        private const val CACHE_TTL_MS = 30_000L
    }
}
