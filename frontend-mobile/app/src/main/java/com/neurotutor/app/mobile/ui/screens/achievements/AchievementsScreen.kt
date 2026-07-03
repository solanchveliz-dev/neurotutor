package com.neurotutor.app.mobile.ui.screens.achievements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.components.AchievementBadge
import com.neurotutor.app.mobile.ui.components.DashboardBottomBar
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.NeuroWhite
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
    val lifecycleOwner = LocalLifecycleOwner.current

    // 🔄 SINCRONIZACIÓN EN TIEMPO REAL: Refresca al volver a la pantalla
    DisposableEffect(lifecycleOwner, studentId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadAchievements(studentId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF007AFF),
            Color(0xFF5AC8FA),
            Color(0xFFF1F5F9),
            Color(0xFFF8FAFC)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        if (uiState.isLoading) {
            Column(modifier = Modifier.fillMaxSize()) {
                AchievementsHeader(onBack = onBack, title = "Cargando...")
                AchievementsSkeleton()
            }
        } else if (uiState.errorMessage != null) {
            ErrorState(uiState.errorMessage!!) { viewModel.loadAchievements(studentId) }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp) 
            ) {
                // 1. Cálculo dinámico de la colección: Solo módulos reales (isComingSoon = false)
                val (realThemes, comingSoon) = uiState.themes.partition { !it.isComingSoon }
                
                val allRealMilestones = realThemes
                    .flatMap { it.levelGroups }
                    .flatMap { it.milestones }
                
                val unlockedCount = allRealMilestones.count { it.isUnlocked }
                val totalCount = allRealMilestones.size

                AchievementsHeader(
                    onBack = onBack, 
                    totalBadges = unlockedCount,
                    totalPossible = totalCount,
                    themeTitle = realThemes.firstOrNull()?.title ?: "Fracciones"
                )
                
                CollectionProgressCard(unlocked = unlockedCount, total = totalCount)

                Text(
                    text = "Tus Colecciones 🏆",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 12.dp)
                )

                // 2. Secciones del Álbum (Temas reales con estructura 3x3)
                realThemes.forEach { theme ->
                    AlbumThemeSection(theme)
                }

                // 3. Próximamente (Contenido futuro)
                if (comingSoon.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Próximamente 🚀",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                    )
                    comingSoon.forEach { theme ->
                        ComingSoonThemeSection(theme)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            DashboardBottomBar(
                selectedTab = "logros",
                onTabClick = onNavigateToTab,
                onNeoClick = onNavigateToTutor
            )
        }
    }
}

@Composable
fun AchievementsHeader(
    onBack: () -> Unit, 
    title: String = "Mis logros", 
    totalBadges: Int = 0,
    totalPossible: Int = 0,
    themeTitle: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = NeuroWhite, modifier = Modifier.size(26.dp))
            }
            Text(text = title, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = NeuroWhite, textAlign = TextAlign.Center, modifier = Modifier.weight(1f).padding(end = 48.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (title == "Mis logros") {
                    Text(
                        text = if(totalPossible > 0) "¡Ya tienes $totalBadges / $totalPossible trofeos de $themeTitle! 🦸‍♂️" else "¡Empieza a coleccionar tus trofeos matemáticos!",
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White, 
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "Cada examen aprobado es una nueva medalla.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
            Image(painter = painterResource(id = R.drawable.neo_achievement), contentDescription = null, modifier = Modifier.size(110.dp), contentScale = ContentScale.Fit)
        }
    }
}

@Composable
fun CollectionProgressCard(unlocked: Int, total: Int) {
    val progress = if (total > 0) unlocked.toFloat() / total.toFloat() else 0f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Maestría Global", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Text(text = "$unlocked / $total", fontSize = 16.sp, fontWeight = FontWeight.Black, color = MoradoActivo)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = MoradoActivo,
                    trackColor = Color(0xFFF1F5F9)
                )
            }
        }
    }
}

@Composable
fun AlbumThemeSection(theme: ThemeAchievementUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera del Tema (FRACCIONES)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(id = theme.iconRes), contentDescription = null, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = theme.title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    Text(text = theme.description, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 🎨 ÁLBUM 3x3: Grupos de Nivel (Básico, Intermedio, Avanzado)
            theme.levelGroups.forEachIndexed { index, levelGroup ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "NIVEL ${levelGroup.levelName.uppercase()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        levelGroup.milestones.forEach { milestone ->
                            AchievementBadge(milestone = milestone, showLabel = true)
                        }
                    }
                }
                
                if (index < theme.levelGroups.size - 1) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ComingSoonThemeSection(theme: ThemeAchievementUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 6.dp).alpha(0.7f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = theme.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                Text(text = "Mundo en construcción...", fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
fun AchievementsSkeleton() {
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(80.dp).shimmer().background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(20.dp)))
        Spacer(modifier = Modifier.height(24.dp))
        repeat(1) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp).shimmer().padding(top = 8.dp, bottom = 8.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp)))
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "Oops!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeuroWhite)
        Spacer(modifier = Modifier.height(8.dp)); Text(text = message, textAlign = TextAlign.Center, color = NeuroWhite.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(24.dp)); Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
            Text("Reintentar", color = MoradoActivo, fontWeight = FontWeight.Bold)
        }
    }
}
