package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.learning.ModuleStatus
import com.neurotutor.app.mobile.ui.theme.*

@Composable
fun TopicDetailScreen(
    studentId: String,
    studentName: String,
    moduleId: String,
    topicTitle: String,
    viewModel: TopicDetailViewModel,
    onLevelSelected: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, studentId, moduleId) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadTopicDetails(studentId, moduleId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // CIELO CONTINUO UNIFICADO (Transición suave NeuroTutor)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF007AFF), // NeuroBlue
            Color(0xFF5AC8FA), // Sky
            Color(0xFFF1F5F9), // Content BG
            Color(0xFFF8FAFC)
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(paddingValues)
        ) {
            // NUBES AMBIENTALES
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = 180.dp)
                    .size(120.dp)
                    .alpha(0.12f)
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-30).dp, y = 80.dp)
                    .size(140.dp)
                    .alpha(0.10f)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // --- HERO HEADER ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 0.dp)
                ) {
                    // BOTÓN VOLVER
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 8.dp, top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Distribución Horizontal: [Icono + Textos] [Neo]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Icono del Tema
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.fraction_neo_chat),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            // Textos asociados al Icono
                            Text(
                                text = topicTitle,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Selecciona un nivel",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Completa cada nivel para desbloquear el siguiente",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        // Neo sobre nube
                        Image(
                            painter = painterResource(id = R.drawable.neo_cloud),
                            contentDescription = null,
                            modifier = Modifier.size(180.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // --- LISTA DE NIVELES REFINADA ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(state.levels) { index, level ->
                        val showPath = index < state.levels.lastIndex

                        LevelCard(
                            level = level,
                            showPath = showPath,
                            onClick = {
                                val tag = when {
                                    level.name.contains("III:", true) -> "A"
                                    level.name.contains("II:", true) -> "I"
                                    else -> "B"
                                }
                                onLevelSelected(level.levelId, tag, studentName)
                            }
                        )
                    }

                    // Footer Decorativo
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_star),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "¡Completa todos los niveles y conviértete en un Maestro de las Fracciones!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(
    level: LevelItem,
    showPath: Boolean,
    onClick: () -> Unit
) {
    val isLocked = level.status == ModuleStatus.BLOQUEADO
    val isEnCurso = level.status == ModuleStatus.EN_CURSO
    val isCompletado = level.status == ModuleStatus.COMPLETADO

    val islandRes = when {
        level.name.contains("III:", true) -> R.drawable.island_advanced
        level.name.contains("II:", true) -> R.drawable.island_intermediate
        else -> R.drawable.island_basic
    }

    val visualLevelName = when {
        level.name.contains("III:", true) -> "Avanzado"
        level.name.contains("II:", true) -> "Intermedio"
        else -> "Básico"
    }

    val levelLabel = when {
        level.name.contains("III:", true) -> "Nivel 3"
        level.name.contains("II:", true) -> "Nivel 2"
        else -> "Nivel 1"
    }

    val levelDescription = when {
        level.name.contains("III:", true) -> "Desafíos avanzados"
        level.name.contains("II:", true) -> "Operaciones combinadas"
        else -> "Conceptos fundamentales"
    }

    val grayscaleMatrix = remember { ColorMatrix().apply { setToSaturation(0f) } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (showPath) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    drawLine(
                        color = NeuroPurple.copy(alpha = 0.20f),
                        start = Offset(size.width / 2, size.height),
                        end = Offset(size.width / 2, size.height + 40.dp.toPx()),
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = pathEffect
                    )
                }
            }
            .padding(bottom = if (showPath) 40.dp else 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // Altura simétrica
                .clip(RoundedCornerShape(28.dp))
                .clickable(enabled = !isLocked) { onClick() }
                .border(
                    width = if (isEnCurso) 2.5.dp else 1.dp,
                    color = when {
                        isEnCurso -> NeuroPurple
                        isCompletado -> NeuroGreen.copy(alpha = 0.6f)
                        else -> Color(0xFFE2E8F0)
                    },
                    shape = RoundedCornerShape(28.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isEnCurso) 6.dp else 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // --- LADO IZQUIERDO: ISLA PROTAGONISTA ---
                Box(
                    modifier = Modifier
                        .weight(0.48f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = islandRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .alpha(if (isLocked) 0.6f else 1.0f),
                        colorFilter = if (isLocked) ColorFilter.colorMatrix(grayscaleMatrix) else null
                    )

                    if (isLocked) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.4f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else if (isCompletado) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeuroGreen,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 12.dp, end = 12.dp)
                                .background(Color.White, CircleShape)
                        )
                    }
                }

                // --- LADO DERECHO: INFORMACIÓN JERÁRQUICA ---
                Column(
                    modifier = Modifier
                        .weight(0.52f)
                        .fillMaxHeight()
                        .padding(start = 12.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = levelLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) TextGray else NeuroGreen
                    )
                    
                    Text(
                        text = visualLevelName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLocked) TextGray else TextoBase
                    )

                    Text(
                        text = levelDescription,
                        fontSize = 13.sp,
                        color = TextGray.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ESTADO Y PROGRESO INTEGRADO
                    when {
                        isLocked -> {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Bloqueado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                        isEnCurso -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = NeuroPurple.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "En curso",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeuroPurple,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                    Text(
                                        text = "${(level.progress * 100).toInt()}%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NeuroPurple
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { level.progress },
                                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                    color = NeuroPurple,
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }
                        }
                        isCompletado -> {
                            Surface(
                                color = NeuroGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Completado",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeuroGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
