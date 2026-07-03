package com.neurotutor.app.mobile.ui.components

import com.neurotutor.app.mobile.data.model.auth.ModuleProgressResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class BadgeMapperTest {

    @Test
    fun basicLevelCountsTheoryPracticeAndExamIndependently() {
        assertEquals(1, unlockedCount(theory = true))
        assertEquals(2, unlockedCount(theory = true, practice = true))
        assertEquals(3, unlockedCount(theory = true, practice = true, exam = true))
    }

    @Test
    fun basicLevelUsesApprovedBadgeNames() {
        val names = BadgeMapper
            .fromModules(listOf(module(theory = true, practice = true, exam = true)))
            .map { it.name }

        assertEquals(
            listOf("Explorador", "Practicante", "Primer Maestro"),
            names
        )
    }

    private fun unlockedCount(
        theory: Boolean = false,
        practice: Boolean = false,
        exam: Boolean = false
    ): Int = BadgeMapper
        .unlockedFromModules(listOf(module(theory, practice, exam)))
        .size

    private fun module(
        theory: Boolean,
        practice: Boolean,
        exam: Boolean
    ) = ModuleProgressResponse(
        moduleId = "1",
        title = "Fracciones I",
        level = "BASICO",
        theoryCompleted = theory,
        practiceCompleted = practice,
        examPassed = exam,
        practiceCompletedCount = 0,
        practiceTotalCount = 0,
        examBestScore = 0,
        progressPercentage = 0,
        lastActivityAt = null,
        completedAt = null
    )
}
