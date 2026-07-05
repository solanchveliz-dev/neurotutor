package com.neurotutor.app.mobile.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToReset: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(authViewModel.isForgotSuccess, authViewModel.forgotSuccessMessage) {
        authViewModel.forgotSuccessMessage?.let { msg ->
            Toast.makeText(context, "📧 $msg", Toast.LENGTH_LONG).show()
            authViewModel.clearForgotSuccessMessage()
        }
        if (authViewModel.isForgotSuccess) {
            onNavigateToReset(email)
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
                title = "Recuperar contraseña",
                onBack = onNavigateToLogin
            )

            Spacer(Modifier.height(8.dp))
            NeoSpeechHero(
                headline = "¡No te preocupes! 💜",
                message = "Ingresa tu correo y te enviaré un código para que puedas recuperar tu contraseña."
            )
            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = Color(0xFF2948E8),
                        modifier = Modifier
                            .size(42.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Correo electrónico",
                        color = AuthNavy,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                text = "ejemplo@correo.com",
                                color = AuthMuted,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = AuthMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                        text = "Enviar código",
                        enabled = !authViewModel.isLoading,
                        loading = authViewModel.isLoading,
                        onClick = { authViewModel.performForgotPassword(email) }
                    )

                    authViewModel.errorMessage?.let { error ->
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

                    Spacer(Modifier.height(18.dp))
                    androidx.compose.material3.Surface(
                        color = Color(0xFFF4F2FF),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE6E1FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🛡️", fontSize = 21.sp)
                            Spacer(Modifier.size(10.dp))
                            Text(
                                text = "Revisa tu bandeja de entrada y la carpeta de spam si no recibes el correo.",
                                color = AuthNavy,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Volver a Iniciar Sesión",
                    color = AuthNavy,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}
