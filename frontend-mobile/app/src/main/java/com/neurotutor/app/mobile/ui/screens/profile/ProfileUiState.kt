package com.neurotutor.app.mobile.ui.screens.profile

import com.neurotutor.app.mobile.data.model.learning.ModuleItem

data class ProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val level: String = "",
    val points: Int = 0,
    val modulesCompleted: Int = 0,
    val medalsCount: Int = 0,
    val avatarUrl: String? = null,
    val modules: List<ModuleItem> = emptyList(),
    val errorMessage: String? = null
)
