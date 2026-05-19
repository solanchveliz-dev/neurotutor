package com.neurotutor.app.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neurotutor.app.mobile.ui.screens.*
import com.neurotutor.app.mobile.ui.theme.NeuroTutorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroTutorTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",  // Cambiado a "login" como pantalla inicial
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // ==================== LOGIN ====================
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = {
                                    navController.navigate("register") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate("forgot-password")
                                }
                            )
                        }

                        // ==================== REGISTRO ====================
                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ==================== RECUPERACIÓN DE CONTRASEÑA ====================

                        // Pantalla para ingresar email
                        composable("forgot-password") {
                            ForgotPasswordScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("forgot-password") { inclusive = true }
                                    }
                                },
                                onNavigateToReset = { email ->
                                    navController.navigate("reset-password/$email")
                                }
                            )
                        }

                        // Pantalla para ingresar token y nueva contraseña
                        composable("reset-password/{email}") { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            ResetPasswordScreen(
                                email = email,
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("reset-password/{email}") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}