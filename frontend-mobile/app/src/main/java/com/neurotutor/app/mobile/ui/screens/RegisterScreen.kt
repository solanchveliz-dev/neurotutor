package com.neurotutor.app.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.models.RegisterRequest
import com.neurotutor.app.mobile.ui.network.RetrofitClient
import com.neurotutor.app.mobile.ui.theme.*
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {}
) {
    // --- Lógica original del usuario ---
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var grado by remember { mutableStateOf("") }
    var seccion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Variables para la conexión con Spring Boot
    var isLoading by remember { mutableStateOf(false) }
    var serverError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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

    val isFormValid = nombre.isNotEmpty() &&
            email.isNotEmpty() &&
            emailError == null &&
            grado.isNotEmpty() &&
            seccion.isNotEmpty() &&
            password.length >= 8 &&
            passwordError == null &&
            confirmPasswordError == null

    // Función para registrar en Spring Boot
    fun performRegister() {
        if (!isFormValid) return
        isLoading = true
        serverError = null

        val request = RegisterRequest(
            email = email,
            nombreCompleto = nombre,
            grado = grado,
            seccion = seccion,
            password = password,
            password2 = confirmPassword
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.register(request)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        Toast.makeText(context, "✅ ${authResponse?.mensaje}", Toast.LENGTH_LONG).show()
                        onNavigateToLogin()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        serverError = errorBody ?: "Error en el registro"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    serverError = "Error de conexión: ${e.message}"
                }
            }
        }
    }

    // --- Interfaz con el estilo de la imagen ---
    val scrollState = rememberScrollState()

    var expandedGrado by remember { mutableStateOf(false) }
    var expandedSeccion by remember { mutableStateOf(false) }

    val listaGrados = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
    val listaSecciones = listOf("A", "B", "C", "D")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF8E7CFE), Color(0xFF5E49B0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "NeuroTutor",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )

            Text(
                text = "Aprende matemáticas de forma inteligente",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(TabUnselected)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = TextDark
                    ),
                    elevation = null
                ) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                        .background(TabSelected, RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TabSelected,
                        contentColor = TextDark
                    ),
                    elevation = null
                ) {
                    Text("Registrarse", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Nombre Completo
                    FormLabel("Nombre Completo")
                    CustomTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = "Ej: Ana García"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email
                    FormLabel("Correo Electrónico")
                    CustomTextField(
                        value = email,
                        onValueChange = { onEmailChange(it) },
                        placeholder = "ejemplo@correo.com",
                        isError = emailError != null,
                        errorText = emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grado
                    FormLabel("Grado")
                    ExposedDropdownMenuBox(
                        expanded = expandedGrado,
                        onExpandedChange = { expandedGrado = !expandedGrado }
                    ) {
                        TextField(
                            value = grado,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona tu grado", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrado) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = InputBackground,
                                unfocusedContainerColor = InputBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
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

                    // Sección
                    FormLabel("Sección")
                    ExposedDropdownMenuBox(
                        expanded = expandedSeccion,
                        onExpandedChange = { expandedSeccion = !expandedSeccion }
                    ) {
                        TextField(
                            value = seccion,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona tu sección", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeccion) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .clip(RoundedCornerShape(16.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = InputBackground,
                                unfocusedContainerColor = InputBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
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

                    // Password
                    FormLabel("Contraseña")
                    CustomTextField(
                        value = password,
                        onValueChange = { onPasswordChange(it) },
                        placeholder = "********",
                        isPassword = true,
                        isError = passwordError != null,
                        errorText = passwordError,
                        successText = if (password.isNotEmpty() && passwordError == null) "✓ Contraseña válida" else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    FormLabel("Confirmar Contraseña")
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { onConfirmPasswordChange(it) },
                        placeholder = "********",
                        isPassword = true,
                        isError = confirmPasswordError != null,
                        errorText = confirmPasswordError,
                        successText = if (confirmPassword.isNotEmpty() && confirmPasswordError == null) "✓ Contraseñas coinciden" else null
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón de Registro
                    Button(
                        onClick = { performRegister() },
                        enabled = isFormValid && !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) PrimaryPurple else ButtonGray,
                            contentColor = if (isFormValid) Color.White else TextDark
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    }

                    // Mensaje de error
                    if (serverError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = serverError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        fontSize = 14.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorText: String? = null,
    successText: String? = null
) {
    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            isError = isError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                disabledContainerColor = InputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        } else if (successText != null) {
            Text(
                text = successText,
                color = PrimaryPurple,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}