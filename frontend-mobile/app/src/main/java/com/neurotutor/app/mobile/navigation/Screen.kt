package com.neurotutor.app.mobile.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot-password")

    object ResetPassword : Screen("reset-password/{email}") {
        fun createRoute(email: String) = "reset-password/$email"
    }

    object Diagnostic : Screen("diagnostic/{studentId}") {
        fun createRoute(studentId: String) = "diagnostic/$studentId"
    }

    object LevelAssignment : Screen("level_assignment/{studentId}/{respuestas}") {
        fun createRoute(studentId: String, respuestas: String) = "level_assignment/$studentId/$respuestas"
    }

    object MapResults : Screen("map_results/{studentId}") {
        fun createRoute(studentId: String) = "map_results/$studentId"
    }

    object StudentDashboard : Screen("student_dashboard/{studentId}") {
        fun createRoute(studentId: String) = "student_dashboard/$studentId"
    }
}
