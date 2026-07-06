package com.neurotutor.app.mobile.domain.mapper

import com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse
import com.neurotutor.app.mobile.data.model.learning.ModuleItem

data class ThemeProgress(
    val name: String,
    val progressPercentage: Int,
    val levels: List<LevelProgressInfo>
)

data class LevelProgressInfo(
    val id: String,
    val title: String,
    val progress: Int,
    val isCompleted: Boolean
)

object ProgressMapper {
    private const val LEVELS_PER_THEME = 3

    fun fromProgressResponse(modules: List<ModuleProgressResponse>): List<ThemeProgress> {
        if (modules.isEmpty()) return emptyList()

        return modules
            .groupBy { extractThemeName(it.title) }
            .map { (themeName, themeModules) ->
                val levels = themeModules
                    .distinctBy { normalizeLevel(it.level) }
                    .map {
                        LevelProgressInfo(
                            id = it.moduleId,
                            title = it.title,
                            progress = it.progressPercentage,
                            isCompleted = it.progressPercentage >= 100
                        )
                    }

                ThemeProgress(
                    name = themeName,
                    progressPercentage = levels.sumOf { it.progress } / LEVELS_PER_THEME,
                    levels = levels
                )
            }
    }

    fun fromProfileWithLiveProgress(
        profileModules: List<ModuleItem>,
        liveProgress: List<ModuleProgressResponse>
    ): List<ModuleItem> {
        val liveProgressByTheme = liveProgress.groupBy { extractThemeName(it.title) }

        return profileModules
            .groupBy { it.temaNombre }
            .map { (themeName, profileThemeModules) ->
                val liveThemeModules = liveProgressByTheme.entries
                    .firstOrNull { (liveTheme, _) ->
                        liveTheme.equals(themeName, ignoreCase = true)
                    }
                    ?.value
                    .orEmpty()

                val themeProgress = liveThemeModules
                    .distinctBy { normalizeLevel(it.level) }
                    .sumOf { it.progressPercentage } / LEVELS_PER_THEME

                profileThemeModules.first().copy(progressPercentage = themeProgress)
            }
    }

    private fun extractThemeName(title: String): String {
        val baseTitle = title.substringBefore(":").trim()
        return baseTitle
            .replace(
                Regex(
                    """\s+(I{1,3}|[1-3]|BASICO|BÁSICO|INTERMEDIO|AVANZADO)$""",
                    RegexOption.IGNORE_CASE
                ),
                ""
            )
            .trim()
    }

    private fun normalizeLevel(level: String): String =
        when (level.trim().uppercase()) {
            "B", "BASICO", "BÁSICO" -> "B"
            "I", "INTERMEDIO" -> "I"
            "A", "AVANZADO" -> "A"
            else -> level.trim().uppercase()
        }
}
