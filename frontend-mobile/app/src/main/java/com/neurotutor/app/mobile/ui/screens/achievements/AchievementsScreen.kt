package com.neurotutor.app.mobile.ui.screens.achievements

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

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
    val context = LocalContext.current
    val achievementNeoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_achievement)
            .crossfade(true)
            .build()
    }

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
            ErrorState(uiState.errorMessage!!) {
                viewModel.loadAchievements(studentId, force = true)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp) 
            ) {
                val realThemes = uiState.themes.filterNot { it.isComingSoon }
                val milestones = realThemes
                    .flatMap { it.levelGroups }
                    .flatMap { it.milestones }
                val unlockedCount = milestones.count { it.isUnlocked }

                AchievementsHeader(
                    onBack = onBack,
                    totalBadges = unlockedCount,
                    neoModel = achievementNeoModel
                )

                CollectionProgressCard(
                    unlocked = unlockedCount,
                    total = milestones.size
                )

                AchievementSectionTitle(
                    title = "Insignias",
                    subtitle = "Completa teoría, práctica y examen para llenar tu colección"
                )
                if (realThemes.isEmpty()) {
                    EmptyCollectionCard("Tu colección de insignias aparecerá aquí.")
                } else {
                    realThemes.forEach { theme ->
                        AlbumThemeSection(theme)
                    }
                }

                CollapsibleAchievementHistory(
                    history = uiState.achievementHistory
                )

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
private fun AchievementSectionTitle(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun CollapsibleAchievementHistory(history: List<AchievementHistoryItem>) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(20.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Historial de logros",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${history.size} acciones completadas",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (expanded) {
                        "Contraer historial"
                    } else {
                        "Expandir historial"
                    },
                    tint = MoradoActivo
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    AchievementHistoryList(
                        history = history,
                        embedded = true
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementHistoryList(
    history: List<AchievementHistoryItem>,
    embedded: Boolean
) {
    if (history.isEmpty()) {
        if (embedded) {
            Text(
                text = "Tus acciones completadas aparecerán aquí.",
                modifier = Modifier.padding(18.dp),
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        } else {
            EmptyCollectionCard("Tus acciones completadas aparecerán aquí.")
        }
        return
    }

    Card(
        modifier = if (embedded) Modifier.fillMaxWidth() else {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        },
        shape = if (embedded) RoundedCornerShape(0.dp) else RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (embedded) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            history.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MoradoActivo)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.action,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            lineHeight = 20.sp
                        )
                        item.completedAt?.let { date ->
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = date,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
                if (index < history.lastIndex) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                }
            }
        }
    }
}

@Composable
private fun EmptyCollectionCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(20.dp),
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchievementsHeader(
    onBack: () -> Unit, 
    title: String = "Mis logros", 
    totalBadges: Int = 0,
    neoModel: Any? = null
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
                        text = if (totalBadges > 0) {
                            "¡Ya desbloqueaste $totalBadges insignias!"
                        } else {
                            "¡Empieza a construir tu colección!"
                        },
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White, 
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "Tus avances importantes quedan guardados aquí.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
            AsyncImage(
                model = neoModel ?: R.drawable.neo_achievement,
                contentDescription = null,
                modifier = Modifier.size(110.dp),
                contentScale = ContentScale.Fit
            )
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
