package com.neurotutor.app.mobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.data.network.RetrofitClient
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
                val profileDeferred = launch { fetchProfile(studentId) }
                val progressDeferred = launch { fetchProgress(studentId) }
                val achievementsDeferred = launch { fetchAchievements(studentId) }
                
                // Wait for all to finish if needed, but launch starts them in parallel
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
                            avatarUrl = profile.avatarUrl,
                            modulesCompleted = profile.modulesCompleted,
                            medalsCount = profile.medalsCount
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    private suspend fun fetchProgress(studentId: String) {
        try {
            val response = RetrofitClient.apiService.getStudentProgress(studentId)
            if (response.isSuccessful && response.body() != null) {
                val progress = response.body()!!
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(modules = progress.modules) }
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    private suspend fun fetchAchievements(studentId: String) {
        // Implementation for achievements if needed, currently state holds count
        // and Screen might need the list for MedalsCard
    }

    fun logout(onSuccess: () -> Unit) {
        // Clear local session logic here
        onSuccess()
    }
}
