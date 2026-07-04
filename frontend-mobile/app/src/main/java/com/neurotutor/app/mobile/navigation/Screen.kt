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

    object Profile : Screen("profile/{studentId}") {
        fun createRoute(studentId: String) = "profile/$studentId"
    }

    object Achievements : Screen("achievements/{studentId}") {
        fun createRoute(studentId: String) = "achievements/$studentId"
    }

    object TopicDetail : Screen("topic_detail/{studentId}/{studentName}/{moduleId}/{topicTitle}") {
        fun createRoute(studentId: String, studentName: String, moduleId: String, topicTitle: String) = "topic_detail/$studentId/$studentName/$moduleId/$topicTitle"
    }

    object LevelSections : Screen("level_sections/{studentId}/{studentName}/{moduleId}/{level}/{levelName}/{topicTitle}") {
        fun createRoute(studentId: String, studentName: String, moduleId: String, level: String, levelName: String, topicTitle: String) =
            "level_sections/$studentId/$studentName/$moduleId/$level/$levelName/$topicTitle"
    }

    object Theory : Screen("theory/{studentId}/{studentName}/{moduleId}/{level}/{topicTitle}") {
        fun createRoute(studentId: String, studentName: String, moduleId: String, level: String, topicTitle: String) = "theory/$studentId/$studentName/$moduleId/$level/$topicTitle"
    }

    object ExercisePlayer : Screen("exercise_player/{studentId}/{studentName}/{moduleId}/{level}/{topicTitle}") {
        fun createRoute(studentId: String, studentName: String, moduleId: String, level: String, topicTitle: String) = "exercise_player/$studentId/$studentName/$moduleId/$level/$topicTitle"
    }

    object FinalExam : Screen("final_exam/{studentId}/{moduleId}/{level}/{topicTitle}") {
        fun createRoute(studentId: String, moduleId: String, level: String, topicTitle: String) = "final_exam/$studentId/$moduleId/$level/$topicTitle"
    }

    // ✅ RUTA UNIFICADA PARA TUTOR IA: Soporta modo DASHBOARD y PRACTICE
    object TutorHelp : Screen("tutor_help/{mode}?studentName={studentName}&moduleName={moduleName}&topicName={topicName}&questionStatus={questionStatus}&exerciseId={exerciseId}&exerciseQuestion={exerciseQuestion}&exerciseOptions={exerciseOptions}&correctAnswer={correctAnswer}") {
        fun createRoute(
            mode: String,
            studentName: String? = null,
            moduleName: String? = null,
            topicName: String? = null,
            questionStatus: String? = null,
            exerciseId: String? = null,
            exerciseQuestion: String? = null,
            exerciseOptions: String? = null,
            correctAnswer: String? = null
        ) = "tutor_help/$mode" +
                "?studentName=${studentName ?: ""}" +
                "&moduleName=${moduleName ?: ""}" +
                "&topicName=${topicName ?: ""}" +
                "&questionStatus=${questionStatus ?: ""}" +
                "&exerciseId=${exerciseId ?: ""}" +
                "&exerciseQuestion=${exerciseQuestion ?: ""}" +
                "&exerciseOptions=${exerciseOptions ?: ""}" +
                "&correctAnswer=${correctAnswer ?: ""}"
    }
}
