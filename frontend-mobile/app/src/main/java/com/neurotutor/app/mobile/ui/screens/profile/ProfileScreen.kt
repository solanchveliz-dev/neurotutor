package com.neurotutor.app.mobile.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    LaunchedEffect(studentId) {
        viewModel.loadProfileData(studentId)
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
        // AJUSTE: Control de carga para eliminar el parpadeo inicial ("?" y nivel vacío)
        if (uiState.isLoading || uiState.name.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            Column(
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
                    onEditProfile = { /* Navegar a editar */ }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProgressCard(thematicProgress = uiState.thematicProgress)

                Spacer(modifier = Modifier.height(12.dp))

                InsigniasCard(
                    insignias = uiState.earnedBadges,
                    onSeeAll = { onNavigateToTab("logros") }
                )

                Spacer(modifier = Modifier.height(12.dp))

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
