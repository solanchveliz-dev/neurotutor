package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier  // 🆕 Parámetro para recibir padding del Scaffold
) {

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var grado by remember { mutableStateOf("") }
    var seccion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
        emailError = when {
            newEmail.isNotEmpty() && !isValidEmail(newEmail) -> "Correo inválido"
            else -> null
        }
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        passwordError = when {
            newPassword.isNotEmpty() && !isValidPassword(newPassword) -> "La contraseña debe tener mínimo 8 caracteres"
            else -> null
        }
        if (confirmPassword.isNotEmpty()) {
            confirmPasswordError = if (password == confirmPassword) null else "Las contraseñas no coinciden"
        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
        confirmPasswordError = when {
            newConfirmPassword.isNotEmpty() && password != newConfirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    val isFormValid =
        nombre.isNotEmpty() &&
                email.isNotEmpty() &&
                emailError == null &&
                grado.isNotEmpty() &&
                seccion.isNotEmpty() &&
                password.length >= 8 &&
                passwordError == null &&
                confirmPasswordError == null

    Column(
        modifier = modifier  // 🆕 Usamos el modifier que viene desde MainActivity
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Registro", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { onEmailChange(it) },
            label = { Text("Correo electrónico") },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = grado,
            onValueChange = { grado = it },
            label = { Text("Grado (4to - 7mo)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = seccion,
            onValueChange = { seccion = it },
            label = { Text("Sección (A - D)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (password.isNotEmpty() && passwordError == null) {
                    Text(
                        text = "✓ Contraseña válida",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { onConfirmPasswordChange(it) },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = confirmPasswordError != null,
            supportingText = {
                if (confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (confirmPassword.isNotEmpty() && confirmPasswordError == null) {
                    Text(
                        text = "✓ Contraseñas coinciden",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { /* luego conectamos backend */ },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("REGISTRARSE")
        }
    }
}