package com.neurotutor.app.mobile.ui.components

import com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse
import com.neurotutor.app.mobile.ui.screens.dashboard.DashboardBadgeUiModel
import java.time.LocalDateTime

object BadgeMapper {

    fun fromModules(modules: List<ModuleProgressResponse>): List<DashboardBadgeUiModel> =
        modules
            .asSequence()
            .filter { it.examPassed }
            .mapNotNull(::fromModule)
            .sortedWith(
                compareByDescending<DashboardBadgeUiModel> {
                    parseCompletedAt(it.completedAt)
                }
            )
            .toList()

    private fun fromModule(module: ModuleProgressResponse): DashboardBadgeUiModel? {
        val levelTag = BadgeLevelResources.normalizeLevelTag(module.level) ?: return null
        val levelName = BadgeLevelResources.levelNameFor(module.level) ?: return null
        val badgeRes = BadgeLevelResources.badgeResourceFor(module.level) ?: return null

        return DashboardBadgeUiModel(
            id = "${module.moduleId}:$levelTag",
            moduleId = module.moduleId,
            topic = module.title.trim(),
            level = levelName,
            levelTag = levelTag,
            badgeRes = badgeRes,
            completedAt = module.completedAt
        )
    }

    private fun parseCompletedAt(value: String?): LocalDateTime? =
        value?.let {
            runCatching { LocalDateTime.parse(it) }.getOrNull()
        }
}
