package com.neurotutor.app.mobile.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neurotutor.app.mobile.ui.models.ForgotPasswordRequest
import com.neurotutor.app.mobile.ui.models.LoginRequest
import com.neurotutor.app.mobile.ui.models.RegisterRequest
import com.neurotutor.app.mobile.ui.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class AuthViewModel : ViewModel() {

    // 🧠 Estados globales de la autenticación
    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoginSuccess by mutableStateOf(false)
        private set
    
    var loggedStudentId by mutableStateOf<String?>(null)
        private set

    var hasCompletedExam by mutableStateOf(false) // 🚀 NUEVO: Flag para navegación
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    // 🔄 LÓGICA DE INICIAR SESIÓN
    fun performLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage = "Ingresa email y contraseña"
            return
        }

        isLoading = true
        errorMessage = null
        isLoginSuccess = false
        loggedStudentId = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        successMessage = authResponse?.mensaje ?: "¡Bienvenido!"
                        loggedStudentId = authResponse?.id 
                        hasCompletedExam = authResponse?.examenCompletado ?: false // 🚀 Capturamos el flag
                        isLoginSuccess = true
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorMessage = errorBody ?: "Error en el login"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = "Error de conexión: ${e.message}"
                }
            }
        }
    }

    // 🔐 LÓGICA DE RECUPERAR CONTRASEÑA
    var isForgotSuccess by mutableStateOf(false)
        private set

    var forgotSuccessMessage by mutableStateOf<String?>(null)
        private set

    fun performForgotPassword(email: String) {
        if (email.isEmpty()) {
            errorMessage = "Ingresa tu correo electrónico"
            return
        }

        isLoading = true
        errorMessage = null
        isForgotSuccess = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ForgotPasswordRequest(email)
                val response = RetrofitClient.apiService.forgotPassword(request)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "Revisa tu correo"
                        forgotSuccessMessage = message
                        isForgotSuccess = true
                    } else {
                        errorMessage = "Error al enviar solicitud"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = "Error de conexión: ${e.message}"
                }
            }
        }
    }

    fun clearForgotSuccessMessage() {
        forgotSuccessMessage = null
    }

    // ✨ LÓGICA DE REGISTRO
    var isRegisterSuccess by mutableStateOf(false)
        private set

    var registerSuccessMessage by mutableStateOf<String?>(null)
        private set

    fun performRegister(
        nombreCompleto: String,
        grado: String,
        seccion: String,
        email: String,
        password: String,
        password2: String
    ) {
        if (nombreCompleto.isEmpty() || grado.isEmpty() || seccion.isEmpty() ||
            email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            errorMessage = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        errorMessage = null
        isRegisterSuccess = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    email = email,
                    nombreCompleto = nombreCompleto,
                    grado = grado,
                    seccion = seccion,
                    password = password,
                    password2 = password2
                )
                val response = RetrofitClient.apiService.register(request)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        registerSuccessMessage = authResponse?.mensaje ?: "¡Registro exitoso!"
                        isRegisterSuccess = true
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorMessage = errorBody ?: "Error al registrar estudiante"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = "Error de conexión: ${e.message}"
                }
            }
        }
    }

    fun clearRegisterSuccessMessage() {
        registerSuccessMessage = null
    }

    fun clearSuccessMessage() {
        successMessage = null
    }
}
