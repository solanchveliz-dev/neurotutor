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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun RegisterScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var grado by remember { mutableStateOf("") }
    var seccion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados de visibilidad para las contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var expandedGrado by remember { mutableStateOf(false) }
    var expandedSeccion by remember { mutableStateOf(false) }

    val listaGrados = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
    val listaSecciones = listOf("A", "B", "C", "D")

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
            newPassword.isNotEmpty() && !isValidPassword(newPassword) -> "Mínimo 8 caracteres"
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

    val isFormValid = nombre.isNotEmpty() &&
            email.isNotEmpty() &&
            emailError == null &&
            grado.isNotEmpty() &&
            seccion.isNotEmpty() &&
            password.length >= 8 &&
            passwordError == null &&
            confirmPasswordError == null

    LaunchedEffect(authViewModel.isRegisterSuccess, authViewModel.registerSuccessMessage) {
        authViewModel.registerSuccessMessage?.let { msg ->
            Toast.makeText(context, "✨ $msg", Toast.LENGTH_LONG).show()
            authViewModel.clearRegisterSuccessMessage()
        }
        if (authViewModel.isRegisterSuccess) {
            onNavigateToLogin()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            // 🏷️ TÍTULO NEUROTUTOR COHERENTE
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

            Text(
                text = "Crea tu cuenta y\ncomienza tu aventura",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🤖 BOX DE LA MASCOTA ACTUALIZADO (Con neo_register y confetti)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.confetti_decorations),
                    contentDescription = null,
                    modifier = Modifier.size(220.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = R.drawable.neo_register),
                    contentDescription = "Neo Registro",
                    modifier = Modifier.size(280.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 💳 TARJETA CONTENEDORA DE FORMULARIO MODERNA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    FormLabel("Nombre Completo")
                    CustomTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = "Ej: Ana García"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Correo Electrónico")
                    CustomTextField(
                        value = email,
                        onValueChange = { onEmailChange(it) },
                        placeholder = "ejemplo@correo.com",
                        isError = emailError != null,
                        errorText = emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Grado")
                    ExposedDropdownMenuBox(
                        expanded = expandedGrado,
                        onExpandedChange = { expandedGrado = !expandedGrado }
                    ) {
                        OutlinedTextField(
                            value = grado,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona tu grado", color = Color(0xFF94A3B8)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrado) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGrado,
                            onDismissRequest = { expandedGrado = false }
                        ) {
                            listaGrados.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        grado = option
                                        expandedGrado = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Sección")
                    ExposedDropdownMenuBox(
                        expanded = expandedSeccion,
                        onExpandedChange = { expandedSeccion = !expandedSeccion }
                    ) {
                        OutlinedTextField(
                            value = seccion,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona tu sección", color = Color(0xFF94A3B8)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeccion) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF8B5CF6),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSeccion,
                            onDismissRequest = { expandedSeccion = false }
                        ) {
                            listaSecciones.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        seccion = option
                                        expandedSeccion = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Contraseña")
                    CustomTextField(
                        value = password,
                        onValueChange = { onPasswordChange(it) },
                        placeholder = "********",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onVisibilityChange = { passwordVisible = !passwordVisible },
                        isError = passwordError != null,
                        errorText = passwordError,
                        successText = if (password.isNotEmpty() && passwordError == null) "✓ Contraseña válida" else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormLabel("Confirmar Contraseña")
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { onConfirmPasswordChange(it) },
                        placeholder = "********",
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                        isError = confirmPasswordError != null,
                        errorText = confirmPasswordError,
                        successText = if (confirmPassword.isNotEmpty() && confirmPasswordError == null) "✓ Las contraseñas coinciden" else null
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 🟣 BOTÓN REGISTRARSE ESTILIZADO
                    Button(
                        onClick = {
                            authViewModel.performRegister(
                                nombreCompleto = nombre,
                                grado = grado,
                                seccion = seccion,
                                email = email,
                                password = password,
                                password2 = confirmPassword
                            )
                        },
                        enabled = isFormValid && !authViewModel.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCBD5E1),
                            disabledContentColor = Color(0xFF64748B)
                        )
                    ) {
                        if (authViewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Crear cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    if (authViewModel.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = authViewModel.errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔗 ENLACE DE RETORNO AL LOGIN
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF475569), fontWeight = FontWeight.Medium)) {
                        append("¿Ya tienes una cuenta? ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)) {
                        append("Inicia sesión")
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B),
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    isError: Boolean = false,
    errorText: String? = null,
    successText: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8), fontSize = 15.sp) },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onVisibilityChange) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Cambiar visibilidad",
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedBorderColor = Color(0xFF8B5CF6),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = Color(0xFFEF4444)
            )
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        } else if (successText != null) {
            Text(
                text = successText,
                color = Color(0xFF10B981),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }
    }
}