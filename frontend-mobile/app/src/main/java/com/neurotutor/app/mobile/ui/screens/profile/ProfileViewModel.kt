package com.neurotutor.app.mobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            name = profile.name,
                            email = profile.email,
                            level = profile.level,
                            points = profile.points,
                            avatarUrl = profile.avatarUrl
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    private suspend fun fetchProgress(studentId: String) {
        try {
            val response = RetrofitClient.apiService.getStudentProgress(studentId)
            if (response.isSuccessful && response.body() != null) {
                val progress = response.body()!!
                val completedCount = progress.modules.count { it.examPassed }
                val earnedBadges = BadgeMapper.fromModules(progress.modules)
                
                // 🚀 Única lógica de cálculo para Perfil
                val mappedThemes = ProgressMapper.fromProgressResponse(progress.modules)
                
                withContext(Dispatchers.Main) {
                    _uiState.update { 
                        it.copy(
                            thematicProgress = mappedThemes,
                            modulesCompleted = completedCount,
                            earnedBadges = earnedBadges,
                            medalsCount = earnedBadges.size
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
}
