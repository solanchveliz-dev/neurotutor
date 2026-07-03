package com.neurotutor.app.mobile.ui.screens.achievements

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val themes: List<ThemeAchievementUiModel> = emptyList(),
    val errorMessage: String? = null
)

data class ThemeAchievementUiModel(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: Int,
    val levelGroups: List<LevelGroupUiModel>,
    val isComingSoon: Boolean = false
)

data class LevelGroupUiModel(
    val levelName: String, // "Básico", "Intermedio", "Avanzado"
    val milestones: List<MilestoneUiModel>
)

data class MilestoneUiModel(
    val name: String,        // "Explorador", "Investigador", etc.
    val isUnlocked: Boolean, // Mapeado de theory/practice/exam
    val badgeRes: Int,       // R.drawable.general_medal
    val unlockedDate: String? = null
)
