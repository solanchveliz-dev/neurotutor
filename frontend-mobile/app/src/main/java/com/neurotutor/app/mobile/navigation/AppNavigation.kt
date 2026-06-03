package com.neurotutor.app.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neurotutor.app.mobile.ui.screens.*
import com.neurotutor.app.mobile.ui.viewmodels.DiagnosticResultsViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToDashboard = { studentId, examenCompletado ->
                    // 🚀 FLUJO INTELIGENTE (HU-10):
                    if (examenCompletado) {
                        // Si ya lo hizo, va directo al Dashboard
                        navController.navigate(Screen.StudentDashboard.createRoute(studentId)) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        // Si es nuevo, va al Diagnóstico
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

        composable(Screen.ResetPassword.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(email = email, onNavigateToLogin = { navController.navigate(Screen.Login.route) })
        }

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

        composable(
            route = Screen.StudentDashboard.route,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""

            StudentDashboardScreen(
                studentId = studentId,
                onModuloSeleccionado = { modulo ->
                    println("🚀 MÓDULO CLICKEADO: ${modulo.titulo}")
                }
            )
        }
    }
}
