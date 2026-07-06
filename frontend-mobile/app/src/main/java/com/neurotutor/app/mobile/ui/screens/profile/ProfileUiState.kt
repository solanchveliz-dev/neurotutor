package com.neurotutor.app.mobile.ui.screens.profile

import com.neurotutor.app.mobile.domain.mapper.ThemeProgress
import com.neurotutor.app.mobile.ui.components.LearningBadgeUiModel

data class ProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val grade: String = "",
    val section: String = "",
    val gender: String = "",
    val level: String = "",
    val points: Int = 0,
    val modulesCompleted: Int = 0,
    val medalsCount: Int = 0,
    val avatarUrl: String? = null,
    val thematicProgress: List<ThemeProgress> = emptyList(),
    val earnedBadges: List<LearningBadgeUiModel> = emptyList(),
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateErrorMessage: String? = null,
    val updateSucceeded: Boolean = false
)
