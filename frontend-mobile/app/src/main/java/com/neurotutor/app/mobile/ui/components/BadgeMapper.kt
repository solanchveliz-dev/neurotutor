package com.neurotutor.app.mobile.ui.components

import androidx.annotation.DrawableRes
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse

data class LearningBadgeUiModel(
    val id: String,
    val moduleId: String?,
    val name: String,
    val levelName: String,
    val trigger: String,
    @get:DrawableRes val iconRes: Int,
    val isUnlocked: Boolean
)

object BadgeMapper {
    private val definitions = listOf(
        BadgeDefinition("basic_1", "Explorador", "B", "THEORY", R.drawable.basic_1),
        BadgeDefinition("basic_2", "Practicante", "B", "PRACTICE", R.drawable.basic_2),
        BadgeDefinition("basic_3", "Primer Maestro", "B", "EXAM", R.drawable.basic_3),
        BadgeDefinition("intermediate_1", "Investigador", "I", "THEORY", R.drawable.intermediate_1),
        BadgeDefinition("intermediate_2", "Estratega", "I", "PRACTICE", R.drawable.intermediate_2),
        BadgeDefinition("intermediate_3", "Dominador", "I", "EXAM", R.drawable.intermediate_3),
        BadgeDefinition("advanced_1", "Experto", "A", "THEORY", R.drawable.advanced_1),
        BadgeDefinition("advanced_2", "Preciso", "A", "PRACTICE", R.drawable.advanced_2),
        BadgeDefinition("advanced_3", "Maestro Supremo", "A", "EXAM", R.drawable.advanced_3)
    )

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
        return definitions.filter { it.level == levelTag }.map { definition ->
            LearningBadgeUiModel(
                id = definition.id,
                moduleId = module?.moduleId,
                name = definition.name,
                levelName = levelName,
                trigger = definition.trigger,
                iconRes = definition.iconRes,
                isUnlocked = when (definition.trigger) {
                    "THEORY" -> module?.theoryCompleted == true
                    "PRACTICE" -> module?.practiceCompleted == true
                    "EXAM" -> module?.examPassed == true
                    else -> false
                }
            )
        }
    }

    fun resolveById(id: String): LearningBadgeUiModel? {
        val definition = definitions.firstOrNull { it.id == id } ?: return null
        return LearningBadgeUiModel(
            id = definition.id,
            moduleId = null,
            name = definition.name,
            levelName = BadgeLevelResources.levelNameFor(definition.level).orEmpty(),
            trigger = definition.trigger,
            iconRes = definition.iconRes,
            isUnlocked = true
        )
    }

    private data class BadgeDefinition(
        val id: String,
        val name: String,
        val level: String,
        val trigger: String,
        @get:DrawableRes val iconRes: Int
    )
}
