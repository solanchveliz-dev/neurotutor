package com.neurotutor.app.mobile.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onNavigateToDashboard: (String, Boolean) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // 👁️ ESTADO PARA CONTROLAR LA VISIBILIDAD DE LA CONTRASEÑA
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(authViewModel.isLoginSuccess, authViewModel.successMessage) {
        authViewModel.successMessage?.let { msg ->
            Toast.makeText(context, "✅ $msg", Toast.LENGTH_LONG).show()
            authViewModel.clearSuccessMessage()
        }
        if (authViewModel.isLoginSuccess) {
            onNavigateToDashboard(
                authViewModel.loggedStudentId ?: "",
                authViewModel.hasCompletedExam
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC),
                        Color(0xFFEDF4FF),
                        Color(0xFFF5F3FF)
                    )
                )
            )
    ) {

        // 📜 COLUMN PRINCIPAL CON SCROLL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            // 🏷️ TÍTULO NEUROTUTOR
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF0052FF), fontWeight = FontWeight.Black)) {
                        append("NEURO")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF8B5CF6), fontWeight = FontWeight.Black)) {
                        append("TUTOR")
                    }
                },
                fontSize = 42.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 📝 SUBTÍTULO
            Text(
                text = "Aprende matemáticas\njugando y explorando",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🤖 BOX DE LA MASCOTA REVERTIDO A NEO_IDLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.confetti_decorations),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.neo_idle),
                    contentDescription = "Neo Robot",
                    modifier = Modifier.size(280.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✉️ INPUT CORREO ELECTRÓNICO
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Correo electrónico", color = Color(0xFF94A3B8), fontSize = 16.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Icono Email",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF64748B)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    errorBorderColor = Color(0xFFEF4444)
                ),
                isError = authViewModel.errorMessage != null && authViewModel.errorMessage!!.contains("correo", ignoreCase = true)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 🔒 INPUT CONTRASEÑA
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                // 🛠️ SE TRANSFORMA DINÁMICAMENTE (Muestra texto si es verdadero, oculta si es falso)
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = { Text("Contraseña", color = Color(0xFF94A3B8), fontSize = 16.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Icono Candado",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF64748B)
                    )
                },
                // 🛠️ ICONO INTERACTIVO CON EVENTO CLICK
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Ver contraseña",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF8B5CF6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    errorBorderColor = Color(0xFFEF4444)
                ),
                isError = authViewModel.errorMessage != null && authViewModel.errorMessage!!.contains("contraseña", ignoreCase = true)
            )

            if (authViewModel.errorMessage != null && authViewModel.errorMessage!!.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authViewModel.errorMessage!!,
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🟣 BOTÓN INICIAR SESIÓN
            Button(
                onClick = { authViewModel.performLogin(email, password) },
                enabled = !authViewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C3AED),
                    contentColor = Color.White
                )
            ) {
                if (authViewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔗 ENLACES INFERIORES
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 15.sp,
                color = Color(0xFF334155),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF475569), fontWeight = FontWeight.Medium)) {
                        append("¿No tienes cuenta? ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)) {
                        append("Regístrate")
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}