package com.neurotutor.app.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key         // 🔥 IMPORTANTE PARA LA TECLA BORRAR
import androidx.compose.ui.input.key.key         // 🔥 IMPORTANTE PARA LA TECLA BORRAR
import androidx.compose.ui.input.key.onPreviewKeyEvent // 🔥 IMPORTANTE PARA LA TECLA BORRAR
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import com.neurotutor.app.mobile.ui.models.ResetPasswordRequest
import com.neurotutor.app.mobile.ui.network.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String,
    onNavigateToLogin: () -> Unit = {}
) {
    // Manejamos 6 estados independientes, uno para cada dígito
    val digits = remember { mutableStateListOf("", "", "", "", "", "") }

    // Creamos los controladores de foco para que salte automáticamente
    val focusRequesters = remember { List(6) { FocusRequester() } }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fun performResetPassword() {
        // Juntamos los 6 casilleros en un solo String
        val tokenCompleto = digits.joinToString("")

        if (tokenCompleto.length < 6) {
            errorMessage = "Por favor, ingresa los 6 dígitos del código"
            return
        }
        if (newPassword.length < 8) {
            errorMessage = "La contraseña debe tener al menos 8 caracteres"
            return
        }
        if (newPassword != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        isLoading = true
        errorMessage = null

        // Mandamos el token de 6 números ya juntado al backend
        val request = ResetPasswordRequest(email, tokenCompleto, newPassword, confirmPassword)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.resetPassword(request)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "Contraseña restablecida"
                        Toast.makeText(context, "✅ $message", Toast.LENGTH_LONG).show()
                        onNavigateToLogin()
                    } else {
                        errorMessage = "Código inválido o expirado"
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateToLogin) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "🔐 Restablecer contraseña",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E3A8A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa el código de 6 números enviado a tu correo y tu nueva contraseña",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ==================== 🛠️ LOS 6 CASILLEROS INTERACTIVOS ====================
        Text(
            text = "Código de verificación",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E3A8A),
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0..5) {
                OutlinedTextField(
                    value = digits[i],
                    onValueChange = { input ->
                        if (input.length <= 1) {
                            digits[i] = input
                            // Si escribe un número y no es el último casillero, salta al siguiente
                            if (input.isNotEmpty() && i < 5) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        } else if (input.length == 2) {
                            val nuevoChar = input.last().toString()
                            digits[i] = nuevoChar
                            if (i < 5) focusRequesters[i + 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .width(48.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[i])
                        // 🔥 Intercepta la tecla de retroceso: si borra estando vacío, va al cuadro anterior
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.key == Key.Backspace && digits[i].isEmpty() && i > 0) {
                                digits[i - 1] = ""
                                focusRequesters[i - 1].requestFocus()
                                true
                            } else {
                                false
                            }
                        },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1E3A8A)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E3A8A),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }

        // =========================================================================

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Nueva contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { performResetPassword() },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3A8A)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Restablecer contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}