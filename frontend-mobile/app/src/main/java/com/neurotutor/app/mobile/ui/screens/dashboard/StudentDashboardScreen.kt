package com.neurotutor.app.mobile.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.ui.components.DashboardBottomBar
import com.neurotutor.app.mobile.ui.components.AchievementsCard
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun StudentDashboardScreen(
    studentId: String,
    modifier: Modifier = Modifier,
    onModuloSeleccionado: (ModuleItem, String) -> Unit,
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToTutor: (String, String) -> Unit = { _, _ -> }
) {
    val dashboardViewModel: StudentDashboardViewModel = viewModel()
    val state by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val dashboardNeoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_dashboard2)
            .crossfade(true)
            .build()
    }

    var yaSeMostroBienvenida by rememberSaveable { mutableStateOf(false) }
    var mostrarNeoOverlay by remember { mutableStateOf(!yaSeMostroBienvenida) }

    DisposableEffect(lifecycleOwner, studentId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                dashboardViewModel.refreshProgress(studentId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(mostrarNeoOverlay) {
        if (mostrarNeoOverlay) {
            delay(5500)
            mostrarNeoOverlay = false
            yaSeMostroBienvenida = true
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "FlotacionNeo")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DesplazamientoY"
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF007AFF),
            Color(0xFF5AC8FA),
            Color(0xFFF1F5F9),
            Color(0xFFF8FAFC)
        )
    )

    Box(modifier = modifier.fillMaxSize().background(backgroundGradient)) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MoradoActivo)
            }
            state.errorMessage != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.errorMessage!!, color = MoradoActivo)
                    TextButton(onClick = { dashboardViewModel.cargarInformacionReal(studentId, force = true) }) {
                        Text("Reintentar")
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 🟦 HEADER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 45.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¡Hola, ${state.nombreEstudiante}! 👋",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Continúa tu aventura matemática.",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            AsyncImage(
                                model = dashboardNeoModel,
                                contentDescription = null,
                                modifier = Modifier.size(140.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // ⚪ TARJETA SUPERIOR
                    Box(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-35).dp)) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatItem(
                                        icon = R.drawable.icon_star,
                                        value = state.puntosTotales.toString(),
                                        label = "Puntos"
                                    )
                                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)))
                                    RecommendedLevelStat(
                                        level = state.nivelActual.replace(" 🚀", "").replace(" 🔥", "").replace(" 🌱", "")
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tu progreso general",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "${state.overallProgress}%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF6366F1)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { state.overallProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = Color(0xFF6366F1),
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }
                        }
                    }

                    SectionTitle("Tu progreso por módulo")

                    val moduloFracciones = state.modulos.firstOrNull { it.temaNombre.contains("Fracciones", true) }

                    ModuleProgressItem(
                        title = "Fracciones",
                        // 🚀 SINCRONIZACIÓN REAL: Usar el progreso promediado del ViewModel
                        progress = moduloFracciones?.progressPercentage?.div(100f) ?: 0f,
                        icon = R.drawable.fraction_neo_chat,
                        isLocked = false,
                        onClick = { moduloFracciones?.let { onModuloSeleccionado(it, state.nombreEstudiante) } }
                    )

                    ModuleProgressItem(
                        title = "Decimales",
                        progress = 0f,
                        icon = R.drawable.ic_decimales,
                        isLocked = true
                    )

                    ModuleProgressItem(
                        title = "Porcentajes",
                        progress = 0f,
                        icon = R.drawable.ic_porcentajes,
                        isLocked = true
                    )

                    SectionTitle("Acciones rápidas")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionItem(Modifier.weight(1f), "Teoría", R.drawable.theory_dashboard, Color(0xFFF5F3FF)) {
                            moduloFracciones?.let { onModuloSeleccionado(it, state.nombreEstudiante) }
                        }
                        QuickActionItem(Modifier.weight(1f), "Práctica", R.drawable.practice_dashboard, Color(0xFFECFDF5)) {
                            moduloFracciones?.let { onModuloSeleccionado(it, state.nombreEstudiante) }
                        }
                        QuickActionItem(Modifier.weight(1f), "Logros", R.drawable.achievements_dashboard, Color(0xFFFFFBEB)) {
                            onNavigateToTab("logros")
                        }
                    }

                    AchievementsCard(
                        achievements = state.unlockedAchievements,
                        latestAcademicBadge = state.latestAcademicBadge,
                        onSeeAll = { onNavigateToTab("logros") }
                    )
                }
            }
        }

        // 🏠 BOTTOM NAVIGATION REUTILIZABLE
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            DashboardBottomBar(
                selectedTab = "inicio",
                onTabClick = { onNavigateToTab(it) },
                onNeoClick = { onNavigateToTutor(state.nombreEstudiante, state.modulos.firstOrNull()?.temaNombre ?: "Fracciones") }
            )
        }

        // 💫 OVERLAY DE BIENVENIDA
        AnimatedVisibility(
            visible = mostrarNeoOverlay && !state.isLoading && state.errorMessage == null,
            exit = fadeOut(animationSpec = tween(durationMillis = 600))
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.78f)).clickable { mostrarNeoOverlay = false },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp)
                ) {
                    Box(modifier = Modifier.size(260.dp).offset { IntOffset(x = 0, y = offsetY.roundToInt()) }, contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(220.dp).background(Brush.radialGradient(colors = listOf(Color(0xFF60A5FA).copy(alpha = 0.6f), Color.Transparent)), RoundedCornerShape(110.dp)))
                        AsyncImage(model = dashboardNeoModel, contentDescription = "Mentor Neo", modifier = Modifier.size(240.dp), contentScale = ContentScale.Fit)
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Card(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "¡Hola, ${state.nombreEstudiante}! 👋", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Estoy listo para acompañarte\nen tu aventura matemática.", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569), textAlign = TextAlign.Center, lineHeight = 22.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(icon: Int, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(38.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun RecommendedLevelStat(
    level: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFFEAF4FF)
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Ruta recomendada por el diagnóstico",
                modifier = Modifier.padding(9.dp),
                tint = Color(0xFF007AFF)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = level,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Nivel recomendado",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B)
    )
}

@Composable
fun ModuleProgressItem(title: String, progress: Float, icon: Int, isLocked: Boolean, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isLocked) Color.White.copy(alpha = 0.5f) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).alpha(if (isLocked) 0.3f else 1f)
                )
                if (isLocked) Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = if (isLocked) Color.Gray else Color(0xFF1E293B))
                if (!isLocked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFF1F5F9)
                    )
                } else {
                    Text("Próximamente", fontSize = 12.sp, color = Color.Gray)
                }
            }
            if (!isLocked) {
                Spacer(modifier = Modifier.width(12.dp))
                Text("${(progress * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun QuickActionItem(modifier: Modifier, title: String, icon: Int, bgColor: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(105.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}
