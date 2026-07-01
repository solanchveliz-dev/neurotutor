package com.neurotutor.app.mobile.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.screens.profile.components.*

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            ProfileHeader(
                name = uiState.name,
                level = uiState.level,
                avatarUrl = uiState.avatarUrl,
                onBack = onBack,
                onSettings = { /* Future settings */ }
            )

            // ⚪ Summary Stats Card (3 Columns)
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SummaryStatItem(
                            icon = R.drawable.icon_star,
                            value = uiState.points.toString(),
                            label = "Puntos"
                        )
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFF1F5F9)))
                        SummaryStatItem(
                            icon = R.drawable.icon_theory,
                            value = uiState.modulesCompleted.toString(),
                            label = "Módulos\ncompletados"
                        )
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFF1F5F9)))
                        SummaryStatItem(
                            icon = R.drawable.general_medal,
                            value = uiState.medalsCount.toString(),
                            label = "Medallas\nobtenidas"
                        )
                    }
                }
            }

            PersonalInfoCard(
                name = uiState.name,
                email = uiState.email,
                level = uiState.level,
                onEditProfile = { /* Prepare for PUT api/students/{id}/profile */ }
            )

            ProgressCard(modules = uiState.modules)

            MedalsCard(
                medals = uiState.achievements,
                onSeeAll = { /* Future navigation to all achievements */ }
            )

            AccountActionsCard(
                onLogout = { viewModel.logout { onNavigateToTab("login") } },
                onDeleteAccount = { /* Future deletion with dialog */ }
            )
        }

        // 🏠 BOTTOM NAVIGATION
        Box(
            modifier = Modifier
                .fillMaxWidth(0.97f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .shadow(12.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(Icons.Default.Home, "Inicio", false, Modifier.weight(1f)) { onNavigateToTab("inicio") }
                    BottomNavItem(Icons.AutoMirrored.Filled.MenuBook, "Módulos", false, Modifier.weight(1f)) { onNavigateToTab("modulos") }
                    Spacer(modifier = Modifier.weight(1.3f))
                    BottomNavItem(Icons.Default.EmojiEvents, "Logros", false, Modifier.weight(1f)) { onNavigateToTab("logros") }
                    BottomNavItem(Icons.Default.Person, "Perfil", true, Modifier.weight(1f)) { onNavigateToTab("perfil") }
                }
            }
            
            // Central Neo Button to match reference bottom nav style
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
                    .size(86.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFF60A5FA).copy(alpha = 0.65f), Color.Transparent)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .clickable { onNavigateToTab("tutor") },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.neo_head),
                    contentDescription = "Neo AI",
                    modifier = Modifier.size(78.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryStatItem(icon: Int, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
        Text(
            text = label, 
            fontSize = 11.sp, 
            color = Color(0xFF64748B), 
            lineHeight = 14.sp, 
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon, 
            null, 
            tint = if (selected) Color(0xFF6366F1) else Color(0xFF94A3B8), 
            modifier = Modifier.size(28.dp)
        )
        Text(
            label, 
            fontSize = 10.sp, 
            color = if (selected) Color(0xFF6366F1) else Color(0xFF94A3B8), 
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
