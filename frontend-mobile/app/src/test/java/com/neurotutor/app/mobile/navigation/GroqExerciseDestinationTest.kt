package com.neurotutor.app.mobile.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GroqExerciseDestinationTest {

    @Test
    fun routeRoundTripPreservesSpecialCharactersInTopic() {
        val topic = "Fracciones: 1/2 + 3/4, 100% & práctica 🚀"

        val route = GroqExerciseDestination.createRoute(
            studentId = 12,
            moduleId = 34,
            topic = topic
        )

        requireNotNull(route)
        val segments = route.split("/")
        assertEquals(4, segments.size)
        assertEquals("groqExercise", segments[0])
        assertFalse(segments[3].contains("/"))

        val parsed = GroqExerciseDestination.parse(
            studentId = segments[1],
            moduleId = segments[2],
            encodedTopic = segments[3]
        )

        assertEquals(GroqExerciseArguments(12, 34, topic), parsed)
    }

    @Test
    fun invalidIdentifiersDoNotCreateRoute() {
        assertNull(GroqExerciseDestination.createRoute(0, 2, "Fracciones"))
        assertNull(GroqExerciseDestination.createRoute(1, -1, "Fracciones"))
        assertNull(GroqExerciseDestination.createRoute(1, 2, "  "))
    }

    @Test
    fun malformedRouteArgumentsAreRejectedWithoutException() {
        assertNull(GroqExerciseDestination.parse("abc", "2", "invalid*base64"))
        assertNull(GroqExerciseDestination.parse("1", null, "topic"))
        assertTrue(
            GroqExerciseDestination.parse("1", "2", "invalid*base64") == null
        )
    }
}
