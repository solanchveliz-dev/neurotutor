package com.neurotutor.app.mobile.ui.screens.achievements

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val modules: List<ModuleAchievementUiModel> = emptyList(),
    val errorMessage: String? = null
)

data class ModuleAchievementUiModel(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val levels: List<LevelAchievementUiModel>,
    val isComingSoon: Boolean = false
)

data class LevelAchievementUiModel(
    val levelTag: String,
    val levelName: String,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null,
    val badgeRes: Int
)
