package com.neurotutor.app.mobile.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.data.model.auth.ResetPasswordRequest
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ResetPasswordScreen(
    email: String,
    onNavigateToLogin: () -> Unit = {}
) {
    val digits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    fun performResetPassword() {
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

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            AuthHeader(
                title = "Restablecer contraseña",
                onBack = onNavigateToLogin
            )

            Spacer(Modifier.height(8.dp))
            NeoSpeechHero(
                headline = "¡Ya casi terminamos! 🚀",
                message = "Ingresa el código que enviamos a tu correo y crea tu nueva contraseña."
            )
            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp)
                ) {
                    Text(
                        text = "Código de verificación",
                        color = AuthNavy,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..5) {
                            OutlinedTextField(
                                value = digits[i],
                                onValueChange = { input ->
                                    if (input.length <= 1) {
                                        digits[i] = input
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
                                    .weight(1f)
                                    .height(56.dp)
                                    .focusRequester(focusRequesters[i])
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
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center,
                                    color = AuthNavy
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = AuthPurple,
                                    unfocusedBorderColor = AuthBorder,
                                    cursorColor = AuthPurple
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "ℹ️  El código tiene 6 dígitos.",
                        color = AuthMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "Nueva contraseña",
                        color = AuthNavy,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(7.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = {
                            Text(
                                text = "Ingresa tu nueva contraseña",
                                color = AuthMuted,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = AuthMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AuthPurple,
                            unfocusedBorderColor = AuthBorder,
                            focusedTextColor = AuthNavy,
                            unfocusedTextColor = AuthNavy,
                            cursorColor = AuthPurple
                        )
                    )

                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Confirmar contraseña",
                        color = AuthNavy,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(7.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = {
                            Text(
                                text = "Confirma tu nueva contraseña",
                                color = AuthMuted,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = AuthMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AuthPurple,
                            unfocusedBorderColor = AuthBorder,
                            focusedTextColor = AuthNavy,
                            unfocusedTextColor = AuthNavy,
                            cursorColor = AuthPurple
                        )
                    )

                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = "Restablecer contraseña",
                        enabled = !isLoading,
                        loading = isLoading,
                        onClick = { performResetPassword() }
                    )

                    errorMessage?.let { error ->
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = error,
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = Color(0xFFF4F2FF),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE6E1FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🛡️", fontSize = 20.sp)
                            Spacer(Modifier.size(10.dp))
                            Text(
                                text = "Tu contraseña debe tener al menos 8 caracteres.",
                                color = AuthNavy,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
        }
    }
}
