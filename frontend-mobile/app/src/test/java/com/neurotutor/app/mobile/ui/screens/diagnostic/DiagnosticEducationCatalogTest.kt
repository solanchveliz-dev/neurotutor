package com.neurotutor.app.mobile.ui.screens.diagnostic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticEducationCatalogTest {

    @Test
    fun `catalog contains the ten questions in order`() {
        val lessons = DiagnosticEducationCatalog.lessons

        assertEquals(10, lessons.size)
        assertEquals((1..10).toList(), lessons.map { it.number })
    }

    @Test
    fun `verified answer key matches the educational content`() {
        val answerKey = DiagnosticEducationCatalog.lessons.joinToString("") { it.correctLetter }

        assertEquals("DCCBCBCBAD", answerKey)
    }

    @Test
    fun `every lesson contains a complete teaching sequence`() {
        DiagnosticEducationCatalog.lessons.forEach { lesson ->
            assertTrue(lesson.requestExplanation.isNotBlank())
            assertTrue(lesson.knownFacts.isNotEmpty())
            assertTrue(lesson.operationReason.isNotBlank())
            assertTrue(lesson.steps.size >= 3)
            assertTrue(lesson.conclusion.isNotBlank())
            assertTrue(lesson.correctIndex in lesson.options.indices)
        }
    }
}
