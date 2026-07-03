package com.neurotutor.app.mobile.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.components.DashboardBottomBar
import com.neurotutor.app.mobile.ui.screens.profile.components.*
import com.neurotutor.app.mobile.ui.theme.*

@Composable
fun ProfileScreen(
    studentId: String,
    onBack: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showEditProfileDialog by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, studentId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProfileData(studentId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.updateSucceeded) {
        if (uiState.updateSucceeded) {
            showEditProfileDialog = false
            viewModel.clearUpdateStatus()
        }
    }

    // Cielo continuo unificado
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF007AFF),
            Color(0xFF5AC8FA),
            Color(0xFFF1F5F9),
            Color(0xFFF8FAFC)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            uiState.errorMessage != null -> {
                ProfileUnavailableState(
                    message = uiState.errorMessage ?: "No se pudo cargar el perfil",
                    actionLabel = "Reintentar",
                    onAction = { viewModel.loadProfileData(studentId) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.name.isEmpty() -> {
                ProfileUnavailableState(
                    message = "No encontramos información disponible para este perfil.",
                    actionLabel = "Volver a intentar",
                    onAction = { viewModel.loadProfileData(studentId) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 100.dp)
            ) {
                // Header con títulos alineados y sin fondo local (usa el global)
                ProfileHeader(
                    name = uiState.name,
                    level = uiState.level,
                    avatarUrl = uiState.avatarUrl,
                    onBack = onBack,
                    onSettings = { /* Navegar a settings */ }
                )

                // Tarjeta de estadísticas con offset unificado
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-15).dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 105.dp, max = 115.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryStatItem(
                                icon = R.drawable.icon_star,
                                value = uiState.points.toString(),
                                label = "Puntos",
                                iconSize = 38.dp
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                thickness = 1.dp,
                                color = Color(0xFFF1F5F9)
                            )

                            SummaryStatItem(
                                icon = R.drawable.theory_dashboard,
                                value = uiState.modulesCompleted.toString(),
                                label = "Módulos\ncompletados",
                                iconSize = 40.dp
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                thickness = 1.dp,
                                color = Color(0xFFF1F5F9)
                            )

                            SummaryStatItem(
                                icon = R.drawable.general_medal,
                                value = uiState.medalsCount.toString(),
                                label = "Insignias\nobtenidas",
                                iconSize = 40.dp
                            )
                        }
                    }
                }

                PersonalInfoCard(
                    name = uiState.name,
                    email = uiState.email,
                    level = uiState.level,
                    onEditProfile = {
                        viewModel.clearUpdateStatus()
                        showEditProfileDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProgressCard(thematicProgress = uiState.thematicProgress)

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.earnedBadges.isNotEmpty()) {
                    InsigniasCard(
                        insignias = uiState.earnedBadges,
                        onSeeAll = { onNavigateToTab("logros") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                AccountActionsCard(
                    onLogout = { viewModel.logout { onNavigateToTab("login") } },
                    onDeleteAccount = { /* Acción eliminar */ }
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            DashboardBottomBar(
                selectedTab = "perfil",
                onTabClick = { onNavigateToTab(it) },
                onNeoClick = { onNavigateToTab("tutor") }
            )
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            name = uiState.name,
            email = uiState.email,
            grade = uiState.grade,
            section = uiState.section,
            avatarUrl = uiState.avatarUrl.orEmpty(),
            gender = uiState.gender,
            isSaving = uiState.isUpdating,
            errorMessage = uiState.updateErrorMessage,
            onDismiss = {
                if (!uiState.isUpdating) {
                    showEditProfileDialog = false
                    viewModel.clearUpdateStatus()
                }
            },
            onSave = { name, grade, section, avatarUrl, gender ->
                viewModel.updateProfile(
                    studentId = studentId,
                    name = name,
                    grade = grade,
                    section = section,
                    avatarUrl = avatarUrl,
                    gender = gender
                )
            }
        )
    }
}

@Composable
private fun ProfileUnavailableState(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    name: String,
    email: String,
    grade: String,
    section: String,
    avatarUrl: String,
    gender: String,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var editedName by remember(name) { mutableStateOf(name) }
    var editedGrade by remember(grade) { mutableStateOf(grade) }
    var editedSection by remember(section) { mutableStateOf(section) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Editar perfil",
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    enabled = !isSaving,
                    isError = editedName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    readOnly = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedGrade,
                    onValueChange = { editedGrade = it },
                    label = { Text("Grado") },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedSection,
                    onValueChange = { editedSection = it },
                    label = { Text("Sección") },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        editedName,
                        editedGrade,
                        editedSection,
                        avatarUrl,
                        gender
                    )
                },
                enabled = editedName.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun SummaryStatItem(
    icon: Int,
    value: String,
    label: String,
    iconSize: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E293B)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B),
            lineHeight = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}
