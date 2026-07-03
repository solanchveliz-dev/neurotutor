package com.neurotutor.app.mobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.model.auth.UpdateProfileRequest
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.domain.mapper.ProgressMapper
import com.neurotutor.app.mobile.ui.components.BadgeMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfileData(studentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch in parallel
                val profileJob = launch { fetchProfile(studentId) }
                val progressJob = launch { fetchProgress(studentId) }
                
                profileJob.join()
                progressJob.join()
                
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
            }
        }
    }

    private suspend fun fetchProfile(studentId: String) {
        try {
            val response = RetrofitClient.apiService.getUserProfile(studentId)
            if (response.isSuccessful) {
                val profile = response.body()
                if (profile == null) {
                    _uiState.update {
                        it.copy(errorMessage = "El servidor devolvió un perfil vacío")
                    }
                    return
                }

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            name = profile.name,
                            email = profile.email,
                            grade = profile.grade,
                            section = profile.section,
                            gender = profile.gender.orEmpty(),
                            level = profile.level,
                            points = profile.points,
                            avatarUrl = profile.avatarUrl
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "No se pudo cargar el perfil (HTTP ${response.code()})"
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    errorMessage = e.localizedMessage
                        ?: "No se pudo conectar con el servidor"
                )
            }
        }
    }

    private suspend fun fetchProgress(studentId: String) {
        try {
            val response = RetrofitClient.apiService.getStudentProgress(studentId)
            if (response.isSuccessful && response.body() != null) {
                val progress = response.body()!!
                val completedCount = progress.modules.count { it.progressPercentage >= 100 }
                val earnedBadges = BadgeMapper.unlockedFromModules(progress.modules)
                
                // 🚀 Única lógica de cálculo para Perfil
                val mappedThemes = ProgressMapper.fromProgressResponse(progress.modules)
                
                withContext(Dispatchers.Main) {
                    _uiState.update { 
                        it.copy(
                            thematicProgress = mappedThemes,
                            modulesCompleted = completedCount,
                            medalsCount = earnedBadges.size,
                            earnedBadges = earnedBadges.take(3)
                        ) 
                    }
                }
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    fun logout(onSuccess: () -> Unit) {
        onSuccess()
    }

    fun updateProfile(
        studentId: String,
        name: String,
        grade: String,
        section: String,
        avatarUrl: String?,
        gender: String
    ) {
        if (name.isBlank()) {
            _uiState.update { it.copy(updateErrorMessage = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    updateErrorMessage = null,
                    updateSucceeded = false
                )
            }

            try {
                val response = RetrofitClient.apiService.updateUserProfile(
                    studentId = studentId,
                    request = UpdateProfileRequest(
                        name = name.trim(),
                        grade = grade.trim(),
                        section = section.trim(),
                        avatarUrl = avatarUrl?.trim()?.takeIf { it.isNotEmpty() },
                        gender = gender.trim()
                    )
                )

                val profile = response.body()
                if (response.isSuccessful && profile != null) {
                    _uiState.update {
                        it.copy(
                            name = profile.name,
                            email = profile.email,
                            grade = profile.grade,
                            section = profile.section,
                            gender = profile.gender.orEmpty(),
                            level = profile.level,
                            points = profile.points,
                            avatarUrl = profile.avatarUrl,
                            isUpdating = false,
                            updateSucceeded = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            updateErrorMessage = "No se pudo actualizar el perfil"
                        )
                    }
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdating = false,
                        updateErrorMessage = error.localizedMessage
                            ?: "No se pudo conectar con el servidor"
                    )
                }
            }
        }
    }

    fun clearUpdateStatus() {
        _uiState.update {
            it.copy(updateErrorMessage = null, updateSucceeded = false)
        }
    }
}
