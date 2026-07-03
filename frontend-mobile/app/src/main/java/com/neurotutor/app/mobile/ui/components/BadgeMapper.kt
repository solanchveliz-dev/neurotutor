package com.neurotutor.app.mobile.ui.components

import androidx.annotation.DrawableRes
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse

data class LearningBadgeUiModel(
    val id: String,
    val moduleId: String?,
    val name: String,
    val levelName: String,
    @get:DrawableRes val iconRes: Int,
    val isUnlocked: Boolean
)

object BadgeMapper {

    fun fromModules(modules: List<ModuleProgressResponse>): List<LearningBadgeUiModel> =
        modules.flatMap { module ->
            badgesForLevel(level = module.level, module = module)
        }

    fun unlockedFromModules(modules: List<ModuleProgressResponse>): List<LearningBadgeUiModel> =
        fromModules(modules).filter(LearningBadgeUiModel::isUnlocked)

    fun badgesForLevel(
        level: String,
        module: ModuleProgressResponse?
    ): List<LearningBadgeUiModel> {
        val levelTag = BadgeLevelResources.normalizeLevelTag(level) ?: return emptyList()
        val levelName = BadgeLevelResources.levelNameFor(level) ?: return emptyList()
        val iconRes = BadgeLevelResources.badgeResourceFor(level) ?: R.drawable.general_medal
        val definitions = when (levelTag) {
            "B" -> listOf(
                BadgeDefinition("THEORY", "Explorador", module?.theoryCompleted == true),
                BadgeDefinition("PRACTICE", "Practicante", module?.practiceCompleted == true),
                BadgeDefinition("EXAM", "Primer Maestro", module?.examPassed == true)
            )
            "I" -> listOf(
                BadgeDefinition("THEORY", "Investigador", module?.theoryCompleted == true),
                BadgeDefinition("PRACTICE", "Estratega", module?.practiceCompleted == true),
                BadgeDefinition("EXAM", "Dominador", module?.examPassed == true)
            )
            "A" -> listOf(
                BadgeDefinition("THEORY", "Experto", module?.theoryCompleted == true),
                BadgeDefinition("PRACTICE", "Preciso", module?.practiceCompleted == true),
                BadgeDefinition("EXAM", "Maestro Supremo", module?.examPassed == true)
            )
            else -> emptyList()
        }

        return definitions.map { definition ->
            LearningBadgeUiModel(
                id = "${module?.moduleId ?: levelTag}:${definition.code}",
                moduleId = module?.moduleId,
                name = definition.name,
                levelName = levelName,
                iconRes = iconRes,
                isUnlocked = definition.isUnlocked
            )
        }
    }

    private data class BadgeDefinition(
        val code: String,
        val name: String,
        val isUnlocked: Boolean
    )
}
