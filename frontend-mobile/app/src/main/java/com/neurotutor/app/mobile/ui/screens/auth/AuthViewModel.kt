package com.neurotutor.app.mobile.ui.screens.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.neurotutor.app.mobile.data.model.auth.*
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoginSuccess by mutableStateOf(false)
        private set
    
    var loggedStudentId by mutableStateOf<String?>(null)
        private set

    var hasCompletedExam by mutableStateOf(false)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun performLogin(email: String, password: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            errorMessage = "Ingresa email y contraseña"
            return
        }

        isLoading = true
        errorMessage = null
        isLoginSuccess = false
        loggedStudentId = null
        RetrofitClient.clearAuthToken()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(cleanEmail, password)
                val response = RetrofitClient.apiService.login(request)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        RetrofitClient.updateAuthToken(authResponse?.token)
                        successMessage = authResponse?.mensaje ?: "¡Bienvenido!"
                        loggedStudentId = authResponse?.id 
                        hasCompletedExam = authResponse?.examenCompletado ?: false
                        isLoginSuccess = true
                    } else {
                        errorMessage = parseErrorResponse(response.errorBody()?.string())
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

    var isForgotSuccess by mutableStateOf(false)
        private set

    var forgotSuccessMessage by mutableStateOf<String?>(null)
        private set

    fun performForgotPassword(email: String) {
        val cleanEmail = email.trim()
        
        if (cleanEmail.isEmpty()) {
            errorMessage = "Ingresa tu correo electrónico"
            return
        }
        
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorMessage = "Formato de correo inválido"
            return
        }

        isLoading = true
        errorMessage = null
        isForgotSuccess = false
        
        RetrofitClient.clearAuthToken()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ForgotPasswordRequest(cleanEmail)
                val response = RetrofitClient.apiService.forgotPassword(request)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val body = response.body()
                        // Usamos .success (según el modelo ForgotPasswordResponse.kt)
                        if (body?.success == true) {
                            forgotSuccessMessage = body.message ?: "Código enviado"
                            isForgotSuccess = true
                        } else {
                            errorMessage = body?.message ?: "El correo no está registrado o hubo un error SMTP."
                        }
                    } else {
                        errorMessage = parseErrorResponse(response.errorBody()?.string())
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

    var isResetSuccess by mutableStateOf(false)
        private set

    fun performResetPassword(email: String, token: String, newPass: String, confirmPass: String) {
        if (token.length < 6) {
            errorMessage = "Ingresa el código de 6 dígitos"
            return
        }
        if (newPass.length < 8) {
            errorMessage = "La contraseña debe tener al menos 8 caracteres"
            return
        }
        if (newPass != confirmPass) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        isLoading = true
        errorMessage = null
        isResetSuccess = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ResetPasswordRequest(email.trim(), token, newPass, confirmPass)
                val response = RetrofitClient.apiService.resetPassword(request)

                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        successMessage = response.body()?.message ?: "Contraseña restablecida correctamente"
                        isResetSuccess = true
                    } else {
                        val errorText = parseErrorResponse(response.errorBody()?.string())
                        errorMessage = if (errorText == "Error desconocido") "Código inválido o expirado" else errorText
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

    private fun parseErrorResponse(errorJson: String?): String {
        if (errorJson.isNullOrBlank()) return "Error desconocido"
        return try {
            val response = Gson().fromJson(errorJson, ForgotPasswordResponse::class.java)
            response.message ?: "Error en el servidor"
        } catch (e: Exception) {
            "Ocurrió un problema en el servidor"
        }
    }

    fun clearForgotSuccessMessage() {
        forgotSuccessMessage = null
    }

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
        val cleanEmail = email.trim()
        if (nombreCompleto.isEmpty() || grado.isEmpty() || seccion.isEmpty() ||
            cleanEmail.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            errorMessage = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        errorMessage = null
        isRegisterSuccess = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    email = cleanEmail,
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
                        errorMessage = parseErrorResponse(response.errorBody()?.string())
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
        isResetSuccess = false
    }
}
