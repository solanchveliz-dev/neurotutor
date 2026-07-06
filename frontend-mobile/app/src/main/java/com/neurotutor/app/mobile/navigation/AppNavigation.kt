package com.neurotutor.app.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neurotutor.app.mobile.ui.screens.auth.*
import com.neurotutor.app.mobile.ui.screens.dashboard.StudentDashboardScreen
import com.neurotutor.app.mobile.ui.screens.dashboard.StudentDashboardViewModel
import com.neurotutor.app.mobile.ui.screens.diagnostic.*
import com.neurotutor.app.mobile.ui.screens.learning.*
import com.neurotutor.app.mobile.ui.screens.profile.ProfileScreen
import com.neurotutor.app.mobile.ui.screens.achievements.AchievementsScreen
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.feature.groqexercise.GroqExerciseNavigationError
import com.neurotutor.app.mobile.feature.groqexercise.GroqExerciseRoute
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // 🚀 Bug 1: Scope ViewModel to AppNavigation (survives screen transitions)
    val aiTutorViewModel: AiTutorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {

        // ==================== AUTH ====================

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToDashboard = { studentId, examenCompletado ->
                    if (examenCompletado) {
                        navController.navigate(Screen.StudentDashboard.createRoute(studentId)) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Diagnostic.createRoute(studentId)) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(onNavigateToLogin = { navController.navigate(Screen.Login.route) })
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToReset = { email -> navController.navigate(Screen.ResetPassword.createRoute(email)) }
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(email = email, onNavigateToLogin = { navController.navigate(Screen.Login.route) })
        }

        // ==================== DIAGNÓSTICO ====================

        composable(
            route = Screen.Diagnostic.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            DiagnosticScreen(
                studentId = studentId,
                onNavigateToAssignment = { id, respuestasList ->
                    val respuestasString = respuestasList.joinToString(",")
                    navController.navigate(Screen.LevelAssignment.createRoute(id, respuestasString)) {
                        popUpTo(Screen.Diagnostic.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.LevelAssignment.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("respuestas") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val respuestasRaw = backStackEntry.arguments?.getString("respuestas") ?: ""
            val respuestasList = respuestasRaw.split(",").filter { it.isNotEmpty() }

            val sharedViewModel: DiagnosticResultsViewModel = viewModel(backStackEntry)

            LevelAssignmentScreen(
                studentId = studentId,
                respuestas = respuestasList,
                viewModel = sharedViewModel,
                onNavigateToDetails = {
                    navController.navigate(Screen.MapResults.createRoute(studentId))
                },
                onStartAdventure = {
                    navController.navigate(Screen.StudentDashboard.createRoute(studentId)) {
                        popUpTo(Screen.LevelAssignment.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.MapResults.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""

            val assignmentBackStackEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Screen.LevelAssignment.route)
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val sharedViewModel: DiagnosticResultsViewModel = viewModel(assignmentBackStackEntry)

            val respuestasRaw = assignmentBackStackEntry.arguments?.getString("respuestas") ?: ""
            val respuestasList = respuestasRaw.split(",").filter { it.isNotEmpty() }

            MapResultsScreen(
                studentId = studentId,
                respuestas = respuestasList,
                viewModel = sharedViewModel,
                onComenzarPractica = {
                    navController.navigate(Screen.StudentDashboard.createRoute(studentId)) {
                        popUpTo(Screen.LevelAssignment.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== DASHBOARD ====================

        composable(
            route = Screen.StudentDashboard.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""

            StudentDashboardScreen(
                studentId = studentId,
                onModuloSeleccionado = { modulo, studentName ->
                    navController.navigate(Screen.TopicDetail.createRoute(studentId, studentName, modulo.id, modulo.temaNombre))
                },
                onNavigateToTab = { tab ->
                    when (tab) {
                        "perfil" -> navController.navigate(Screen.Profile.createRoute(studentId))
                        "logros" -> navController.navigate(Screen.Achievements.createRoute(studentId))
                        "inicio" -> { /* Ya estamos aquí */ }
                    }
                },
                onNavigateToTutor = { name: String, module: String ->
                    navController.navigate(Screen.TutorHelp.createRoute(
                        mode = "DASHBOARD",
                        studentId = studentId,
                        studentName = name,
                        moduleName = module
                    ))
                }
            )
        }

        // ==================== LOGROS ====================

        composable(
            route = Screen.Achievements.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            AchievementsScreen(
                studentId = studentId,
                onBack = { navController.popBackStack() },
                onNavigateToTab = { tab ->
                    when (tab) {
                        "inicio" -> navController.navigate(Screen.StudentDashboard.createRoute(studentId)) {
                            popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                        }
                        "perfil" -> navController.navigate(Screen.Profile.createRoute(studentId)) {
                            popUpTo(Screen.StudentDashboard.route)
                        }
                        "logros" -> { /* Ya estamos aquí */ }
                    }
                },
                onNavigateToTutor = {
                    navController.navigate(Screen.TutorHelp.createRoute(
                        mode = "DASHBOARD",
                        studentId = studentId,
                        studentName = "",
                        moduleName = "Fracciones"
                    ))
                }
            )
        }

        // ==================== PERFIL ====================

        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            ProfileScreen(
                studentId = studentId,
                onBack = { navController.popBackStack() },
                onNavigateToTab = { tab ->
                    when (tab) {
                        "inicio" -> navController.navigate(Screen.StudentDashboard.createRoute(studentId)) {
                            popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                        }
                        "logros" -> navController.navigate(Screen.Achievements.createRoute(studentId)) {
                            popUpTo(Screen.StudentDashboard.route)
                        }
                        "login" -> {
                            RetrofitClient.clearAuthToken()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // ==================== APRENDIZAJE ====================

        composable(
            route = Screen.TopicDetail.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("moduleId") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val studentName = backStackEntry.arguments?.getString("studentName") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: "Tema"
            val topicViewModel: TopicDetailViewModel = viewModel()
            
            TopicDetailScreen(
                studentId = studentId,
                studentName = studentName,
                moduleId = moduleId,
                topicTitle = topicTitle,
                viewModel = topicViewModel,
                onLevelSelected = { levelId, levelTag, sName ->
                    val levelName = when(levelTag) {
                        "B" -> "Básico"
                        "I" -> "Intermedio"
                        else -> "Avanzado"
                    }
                    navController.navigate(Screen.LevelSections.createRoute(studentId, sName, levelId, levelTag, levelName, topicTitle))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LevelSections.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("moduleId") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("levelName") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val studentName = backStackEntry.arguments?.getString("studentName") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""
            val levelName = backStackEntry.arguments?.getString("levelName") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""

            LevelSectionsScreen(
                studentId = studentId,
                studentName = studentName,
                moduleId = moduleId,
                levelName = levelName,
                topicTitle = topicTitle,
                onNavigateToTheory = {
                    navController.navigate(Screen.Theory.createRoute(studentId, studentName, moduleId, level, topicTitle))
                },
                onNavigateToExercises = {
                    navController.navigate(Screen.ExercisePlayer.createRoute(studentId, studentName, moduleId, level, topicTitle))
                },
                onNavigateToExam = {
                    navController.navigate(Screen.FinalExam.createRoute(studentId, moduleId, level, topicTitle))
                },
                onNavigateToTutor = { sId, sName, mId, topic ->
                    navController.navigate(
                        Screen.TutorHelp.createRoute(
                            mode = "PRACTICE",
                            studentId = sId,
                            studentName = sName,
                            moduleId = mId,
                            moduleName = topic
                        )
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Theory.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("moduleId") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val studentName = backStackEntry.arguments?.getString("studentName") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""

            TheoryScreen(
                studentId = studentId,
                studentName = studentName,
                moduleId = moduleId,
                level = level,
                onStartExercise = {
                    navController.navigate(Screen.ExercisePlayer.createRoute(studentId, studentName, moduleId, level, topicTitle))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ExercisePlayer.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("moduleId") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val studentName = backStackEntry.arguments?.getString("studentName") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""
            val topicTitle = backStackEntry.arguments?.getString("topicTitle") ?: ""

            ExercisePlayerScreen(
                studentId = studentId,
                studentName = studentName,
                moduleId = moduleId,
                level = level,
                topicTitle = topicTitle,
                navController = navController,
                onFinish = {
                    val levelName = when(level) {
                        "B" -> "Básico"
                        "I" -> "Intermedio"
                        else -> "Avanzado"
                    }
                    navController.navigate(Screen.LevelSections.createRoute(studentId, studentName, moduleId, level, levelName, topicTitle)) {
                        popUpTo(Screen.ExercisePlayer.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FinalExam.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("moduleId") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("topicTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""

            val examViewModel: FinalExamViewModel = viewModel()

            LaunchedEffect(moduleId, level) {
                examViewModel.loadExam(moduleId, level)
            }

            FinalExamScreen(
                studentId = studentId,
                moduleId = moduleId,
                level = level,
                viewModel = examViewModel,
                onFinish = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = GroqExerciseDestination.route,
            arguments = listOf(
                navArgument(GroqExerciseDestination.STUDENT_ID_ARGUMENT) {
                    type = NavType.StringType
                },
                navArgument(GroqExerciseDestination.MODULE_ID_ARGUMENT) {
                    type = NavType.StringType
                },
                navArgument(GroqExerciseDestination.TOPIC_ARGUMENT) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val arguments = GroqExerciseDestination.parse(
                studentId = backStackEntry.arguments
                    ?.getString(GroqExerciseDestination.STUDENT_ID_ARGUMENT),
                moduleId = backStackEntry.arguments
                    ?.getString(GroqExerciseDestination.MODULE_ID_ARGUMENT),
                encodedTopic = backStackEntry.arguments
                    ?.getString(GroqExerciseDestination.TOPIC_ARGUMENT)
            )

            if (arguments == null) {
                GroqExerciseNavigationError(
                    onBack = { navController.popBackStack() }
                )
            } else {
                GroqExerciseRoute(
                    studentId = arguments.studentId,
                    moduleId = arguments.moduleId,
                    topic = arguments.topic
                )
            }
        }

        // ✅ TUTOR IA UNIFICADO
        composable(
            route = Screen.TutorHelp.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("studentId") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("moduleId") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("studentName") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("moduleName") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("topicName") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("questionStatus") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("exerciseId") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("exerciseQuestion") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("exerciseOptions") { 
                    type = NavType.StringType
                    defaultValue = "" 
                },
                navArgument("correctAnswer") { 
                    type = NavType.StringType
                    defaultValue = "" 
                }
            )
        ) { backStackEntry ->
            val modeStr = backStackEntry.arguments?.getString("mode") ?: "DASHBOARD"
            val mode = TutorMode.valueOf(modeStr)
            val studentIdStr = backStackEntry.arguments?.getString("studentId") ?: ""
            val moduleIdStr = backStackEntry.arguments?.getString("moduleId") ?: ""
            val studentName = backStackEntry.arguments?.getString("studentName") ?: ""
            val moduleName = backStackEntry.arguments?.getString("moduleName") ?: ""
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
            val questionStatus = backStackEntry.arguments?.getString("questionStatus") ?: ""
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            val exerciseQuestionRaw = backStackEntry.arguments?.getString("exerciseQuestion") ?: ""
            val exerciseQuestion = try {
                URLDecoder.decode(exerciseQuestionRaw, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                exerciseQuestionRaw
            }
            val exerciseOptionsRaw = backStackEntry.arguments?.getString("exerciseOptions") ?: ""
            val exerciseOptions = try {
                URLDecoder.decode(exerciseOptionsRaw, StandardCharsets.UTF_8.toString()).split("|")
            } catch (e: Exception) {
                emptyList()
            }
            val correctAnswer = backStackEntry.arguments?.getString("correctAnswer") ?: ""

            TutorHelpScreen(
                mode = mode,
                studentId = studentIdStr.toLongOrNull() ?: 1L,
                moduleId = moduleIdStr.toLongOrNull() ?: 1L,
                studentName = studentName,
                moduleName = moduleName,
                topicName = topicName,
                questionStatus = questionStatus,
                exerciseId = exerciseId,
                exerciseQuestion = exerciseQuestion,
                exerciseOptions = exerciseOptions,
                correctAnswer = correctAnswer,
                viewModel = aiTutorViewModel, // 🚀 Bug 1 Fixed: Using shared ViewModel
                onBack = { navController.popBackStack() }
            )
        }
    }
}
