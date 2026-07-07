package com.neurotutor.app.mobile.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResetPasswordScreen(
    email: String,
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    val digits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Observar éxito del reset
    LaunchedEffect(authViewModel.isResetSuccess) {
        if (authViewModel.isResetSuccess) {
            authViewModel.successMessage?.let { msg ->
                Toast.makeText(context, "✅ $msg", Toast.LENGTH_LONG).show()
            }
            authViewModel.clearSuccessMessage()
            onNavigateToLogin()
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
                                        digits[i] = input.last().toString()
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
                                        } else false
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
                        placeholder = { Text("Mínimo 8 caracteres", color = AuthMuted, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = AuthMuted) },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AuthPurple,
                            unfocusedBorderColor = AuthBorder,
                            focusedTextColor = AuthNavy,
                            unfocusedTextColor = AuthNavy
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
                        placeholder = { Text("Repite tu contraseña", color = AuthMuted, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = AuthMuted) },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AuthPurple,
                            unfocusedBorderColor = AuthBorder,
                            focusedTextColor = AuthNavy,
                            unfocusedTextColor = AuthNavy
                        )
                    )

                    Spacer(Modifier.height(20.dp))
                    AuthPrimaryButton(
                        text = "Restablecer contraseña",
                        enabled = !authViewModel.isLoading,
                        loading = authViewModel.isLoading,
                        onClick = {
                            authViewModel.performResetPassword(
                                email = email,
                                token = digits.joinToString(""),
                                newPass = newPassword,
                                confirmPass = confirmPassword
                            )
                        }
                    )

                    authViewModel.errorMessage?.let { error ->
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = error,
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
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
