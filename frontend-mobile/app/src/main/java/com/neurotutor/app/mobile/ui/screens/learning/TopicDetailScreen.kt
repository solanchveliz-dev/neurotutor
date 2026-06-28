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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    LaunchedEffect(moduleId) {
        viewModel.loadTopicDetails(studentId, moduleId)
    }

    // CIELO CONTINUO
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            NeuroBlue,
            NeuroBlue,
            NeuroBlue,
            Color(0xFFBAE6FD),
            Color(0xFFE0F2FE)
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(paddingValues)
        ) {
            // NUBES AMBIENTALES DE FONDO
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-30).dp, y = (-120).dp)
                    .size(110.dp)
                    .alpha(0.18f)
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = 240.dp)
                    .size(95.dp)
                    .alpha(0.15f)
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-40).dp, y = 80.dp)
                    .size(120.dp)
                    .alpha(0.14f)
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 60.dp, y = 160.dp)
                    .size(45.dp)
                    .alpha(0.22f)
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = (-100).dp)
                    .size(100.dp)
                    .alpha(0.25f)
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-15).dp, y = (-40).dp)
                    .size(70.dp)
                    .alpha(0.12f)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // HERO HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cloud_bottom),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = 12.dp)
                            .size(50.dp)
                            .alpha(0.35f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cloud_bottom),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 12.dp, y = 8.dp)
                            .size(55.dp)
                            .alpha(0.30f)
                    )

                    // BOTÓN VOLVER
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .statusBarsPadding()
                            .padding(start = 8.dp, top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = NeuroWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // BLOQUE CENTRAL TOTALMENTE CENTRADO
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                start = 56.dp,
                                end = 56.dp,
                                bottom = 4.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                    // TÍTULO
                        Text(
                            text = topicTitle,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeuroWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // SUBTÍTULO
                        Text(
                            text = "Selecciona un nivel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeuroWhite.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // DESCRIPCIÓN
                        Text(
                            text = "Completa cada isla para desbloquear la siguiente",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeuroWhite.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                when {
                    state.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeuroPurple)
                        }
                    }
                    state.errorMessage != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = state.errorMessage ?: "Error", color = NeuroRed)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadTopicDetails(studentId, moduleId) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeuroPurple)
                            ) {
                                Text("Reintentar", color = NeuroWhite)
                            }
                        }
                    }
                    else -> {
                        // LISTA DE NIVELES
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 32.dp)
                        ) {
                            if (state.levels.isEmpty()) {
                                item {
                                    Text(
                                        text = "No se encontraron niveles asociados a este tema.",
                                        color = NeuroRed,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 20.dp)
                                    )
                                }
                            }

                            itemsIndexed(state.levels) { index, level ->
                                val showPath = index < state.levels.lastIndex

                                LevelCard(
                                    level = level,
                                    showPath = showPath,
                                    onClick = {
                                        val tag = when {
                                            level.name.contains("III:", true) -> "A"
                                            level.name.contains("II:", true) -> "I"
                                            level.name.contains("I:", true) -> "B"
                                            else -> "B"
                                        }
                                        onLevelSelected(level.levelId, tag, studentName)
                                    }
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

    val islandRes = when {
        level.name.contains("III:", true) -> R.drawable.island_advanced
        level.name.contains("II:", true) -> R.drawable.island_intermediate
        level.name.contains("I:", true) -> R.drawable.island_basic
        else -> R.drawable.island_basic
    }

    val visualLevelName = when {
        level.name.contains("III:", true) -> "AVANZADO"
        level.name.contains("II:", true) -> "INTERMEDIO"
        level.name.contains("I:", true) -> "BÁSICO"
        else -> level.name
    }

    val islandScale = when {
        level.name.contains("III:", true) -> 1.35f
        level.name.contains("II:", true) -> 1.35f
        else -> 1.00f
    }

    val cardColor = when {
        isLocked -> Color(0xFFF3F4F6).copy(alpha = 0.9f)
        isEnCurso -> Color.White
        else -> Color.White
    }

    val borderColor = when {
        isEnCurso -> NeuroGreen
        else -> Color.White
    }

    val pathColor = NeuroPurple.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (showPath) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    drawLine(
                        color = pathColor,
                        start = Offset(size.width / 2, size.height),
                        end = Offset(size.width / 2, size.height + 40.dp.toPx()),
                        strokeWidth = 4.dp.toPx(),
                        pathEffect = pathEffect
                    )
                }
            }
            .padding(bottom = if (showPath) 40.dp else 0.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(28.dp))
                .clickable(enabled = !isLocked) { onClick() }
                .border(
                    width = if (isEnCurso) 3.dp else 0.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(28.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 2.dp else 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.50f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isLocked) {
                                    listOf(Color(0xFFE5E7EB), Color(0xFFD1D5DB))
                                } else {
                                    listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = islandRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(islandScale)
                            .padding(12.dp),
                        alpha = if (isLocked) 0.5f else 1.0f
                    )

                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = visualLevelName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLocked) TextGray else TextoBase
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            isLocked -> "Completa el nivel anterior"
                            isEnCurso -> "¡En curso, tú puedes!"
                            else -> "¡Completado! Toca para repasar"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isLocked -> TextGray.copy(alpha = 0.8f)
                            isEnCurso -> NeuroGreen
                            else -> NeuroPurple
                        }
                    )

                    if (level.progress > 0 && !isLocked) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LinearProgressIndicator(
                                progress = { level.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = NeuroGreen,
                                trackColor = Color(0xFFE5E7EB)
                            )
                        }
                    }
                }
            }
        }
    }
}
