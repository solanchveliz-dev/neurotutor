package com.neurotutor.app.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel = viewModel(), // 🧠 Compartimos el mismo cerebro
    onNavigateToLogin: () -> Unit = {},
    onNavigateToReset: (String) -> Unit = {}  // pasa el email
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    // 🔔 EFECTO: Reacciona cuando el ViewModel avisa que el correo se envió con éxito
    LaunchedEffect(authViewModel.isForgotSuccess, authViewModel.forgotSuccessMessage) {
        authViewModel.forgotSuccessMessage?.let { msg ->
            Toast.makeText(context, "📧 $msg", Toast.LENGTH_LONG).show()
            authViewModel.clearForgotSuccessMessage()
        }
        if (authViewModel.isForgotSuccess) {
            onNavigateToReset(email) // 🚀 Viaja a la pantalla de reset pasando el correo
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón volver
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
            text = "🔐 ¿Olvidaste tu contraseña?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E3A8A)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔵 EL BOTÓN: Ahora delega el trabajo al ViewModel
        Button(
            onClick = { authViewModel.performForgotPassword(email) },
            enabled = !authViewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3A8A)
            )
        ) {
            if (authViewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Enviar enlace", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Mensaje de error controlado por el ViewModel
        if (authViewModel.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = authViewModel.errorMessage!!,
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Volver a Iniciar Sesión", color = Color(0xFF1E3A8A))
        }
    }
}