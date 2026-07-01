package com.neurotutor.app.mobile.ui.screens.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.components.DashboardBottomBar
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.valentinilk.shimmer.shimmer

@Composable
fun AchievementsScreen(
    studentId: String,
    onBack: () -> Unit,
    onNavigateToTab: (String) -> Unit,
    onNavigateToTutor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Insignias, 1: Certificaciones

    LaunchedEffect(studentId) {
        viewModel.loadAchievements(studentId)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            DashboardBottomBar(
                selectedTab = "logros",
                onTabClick = onNavigateToTab,
                onNeoClick = onNavigateToTutor
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // --- HERO HEADER ---
                AchievementsHeader(onBack = onBack)

                // --- TABS ---
                AchievementsTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                if (uiState.isLoading) {
                    AchievementsSkeleton()
                } else if (uiState.errorMessage != null) {
                    ErrorState(uiState.errorMessage!!) { viewModel.loadAchievements(studentId) }
                } else {
                    // --- INFO CARD ---
                    InfoCard()

                    Text(
                        text = "Módulos y niveles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                    )

                    // --- MODULE SECTIONS ---
                    uiState.modules.forEach { module ->
                        ModuleAchievementSection(module)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun AchievementsHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // Fondo con gradiente azul
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF007AFF), Color(0xFF5AC8FA))
                    )
                )
        )

        // Botón volver
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mis logros",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cada logro es una muestra\nde tu esfuerzo y dedicación.",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 22.sp
                )
            }
            Image(
                painter = painterResource(id = R.drawable.neo_achievement),
                contentDescription = null,
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun AchievementsTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TabItem(
            label = "Insignias",
            icon = Icons.Default.EmojiEvents,
            selected = selectedTab == 0,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(0) }
        )
        TabItem(
            label = "Certificados",
            icon = Icons.Default.WorkspacePremium,
            selected = selectedTab == 1,
            isComingSoon = true,
            modifier = Modifier.weight(1f),
            onClick = { /* No hacer nada por ahora */ }
        )
    }
}

@Composable
fun TabItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    isComingSoon: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = if (selected) MoradoActivo else Color.Transparent
    val contentColor = if (selected) Color.White else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                if (isComingSoon) {
                    Text(
                        text = "Próximamente",
                        fontSize = 9.sp,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MoradoActivo,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "¡Obtén insignias aprobando los exámenes finales de cada módulo y nivel!",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MoradoActivo,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ModuleAchievementSection(module: ModuleAchievementUiModel) {
    var expanded by remember { mutableStateOf(module.title == "Fracciones") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!module.isComingSoon) expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = module.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .alpha(if (module.isComingSoon) 0.4f else 1f)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (module.isComingSoon) Color.Gray else Color(0xFF1E293B)
                    )
                    Text(
                        text = module.description,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 2
                    )
                }
                if (module.isComingSoon) {
                    Icon(Icons.Default.ExpandMore, null, tint = Color.LightGray)
                } else {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MoradoActivo
                    )
                }
            }

            if (expanded && !module.isComingSoon) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    module.levels.forEach { level ->
                        BadgeItem(level)
                    }
                }
            }
            
            if (module.isComingSoon) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BadgeSkeleton()
                    BadgeSkeleton()
                    BadgeSkeleton()
                }
            }
        }
    }
}

@Composable
fun BadgeItem(level: LevelAchievementUiModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = level.badgeRes),
                contentDescription = level.levelName,
                modifier = Modifier.size(70.dp),
                colorFilter = if (level.isUnlocked) null else ColorFilter.tint(Color.Gray, BlendMode.SrcIn),
                alpha = if (level.isUnlocked) 1f else 0.4f
            )
            if (!level.isUnlocked) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_question),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = level.levelName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (level.isUnlocked) Color(0xFF1E293B) else Color.Gray
        )
        
        Surface(
            modifier = Modifier.padding(top = 4.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (level.isUnlocked) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
        ) {
            Text(
                text = if (level.isUnlocked) "Obtenida" else "Bloqueada",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (level.isUnlocked) Color(0xFF10B981) else Color(0xFF94A3B8)
            )
        }
        
        if (level.isUnlocked && level.unlockedDate != null) {
            Text(
                text = level.unlockedDate,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun BadgeSkeleton() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp).shimmer()
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color(0xFFE2E8F0), CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(14.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(18.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun AchievementsSkeleton() {
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(80.dp).shimmer().background(Color(0xFFE2E8F0), RoundedCornerShape(16.dp)))
        Spacer(modifier = Modifier.height(24.dp))
        repeat(3) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).shimmer().padding(vertical = 8.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(24.dp)))
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Oops!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MoradoActivo)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, textAlign = TextAlign.Center, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo)) {
            Text("Reintentar")
        }
    }
}
