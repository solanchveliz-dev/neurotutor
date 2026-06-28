package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSectionsScreen(
    studentId: String,
    studentName: String,
    moduleId: String,
    levelName: String,
    topicTitle: String,
    onNavigateToTheory: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToExam: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val viewModel: LevelSectionsViewModel = viewModel(
        factory = LevelSectionsViewModelFactory(context, studentId, moduleId)
    )

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProgress()
    }

    // CIELO CONTINUO UNIFICADO
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
            // NUBES DECORATIVAS AMBIENTALES DE FONDO
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = 220.dp)
                    .size(125.dp)
                    .alpha(0.18f)
            )
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-35).dp, y = (-20).dp)
                    .size(110.dp)
                    .alpha(0.15f)
            )
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 30.dp, y = 140.dp)
                    .size(95.dp)
                    .alpha(0.22f)
            )
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = (-60).dp)
                    .size(130.dp)
                    .alpha(0.12f)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HERO HEADER (Ajustado en jerarquía y posición vertical para emular TopicDetailScreen)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    // NUBES EN LAS ESQUINAS DEL HEADER
                    Image(
                        painter = painterResource(id = R.drawable.cloud_bottom),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-15).dp, y = 10.dp)
                            .size(55.dp)
                            .alpha(0.25f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cloud_bottom),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 15.dp, y = 8.dp)
                            .size(60.dp)
                            .alpha(0.20f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cloud_bottom),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .size(50.dp)
                            .alpha(0.20f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cloud_bottom),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 12.dp, y = (-8).dp)
                            .size(55.dp)
                            .alpha(0.22f)
                    )

                    // BOTÓN VOLVER: Reutilizando la estrategia exacta de TopicDetailScreen
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

                    // BLOQUE DE TEXTOS COMPACTO: Modificado para subir el bloque e incrementar jerarquía
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                start = 56.dp,
                                end = 56.dp,
                                top = 0.dp,     // Se quita el padding excedente para subir el bloque completo entre 35.dp y 45.dp
                                bottom = 36.dp   // Contrapeso inferior para elevar los textos hacia la línea de la flecha
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Procesamiento del nivel dinámico
                        val cleanLevelLabel = when {
                            levelName.contains("III:", true) || levelName.contains("Avanzado", true) -> "Avanzado"
                            levelName.contains("II:", true) || levelName.contains("Intermedio", true) -> "Intermedio"
                            else -> "Básico"
                        }

                        // Título principal: Fracciones - Nivel
                        Text(
                            text = "$topicTitle - $cleanLevelLabel",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeuroWhite,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )

                        // Separación reducida a 2.dp para cohesionar el bloque
                        Spacer(modifier = Modifier.height(2.dp))

                        // Subtítulo principal: Mayor tamaño (18.sp) y presencia para emular "Selecciona un nivel"
                        Text(
                            text = "¿Qué quieres hacer?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeuroWhite.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Pequeña descripción complementaria: Equivalente a "Completa cada isla..."
                        Text(
                            text = "Selecciona una actividad para continuar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = NeuroWhite.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // CONTENIDO SCROLLABLE
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // BARRA DE PROGRESO PREMIUM DE VIDEOJUEGO
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Progreso del nivel",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = TextoBase
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { state.progreso },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    color = NeuroPurple,
                                    trackColor = Color(0xFFE0F2FE)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${(state.progreso * 100).toInt()}%",
                                fontWeight = FontWeight.Black,
                                color = NeuroBlue,
                                fontSize = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- 📚 SECCIONES ---
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // TARJETA TEORÍA
                        ActionSectionCard(
                            title = "Teoría",
                            subtitle = if (state.teoriaCompletada) "✓ Completado" else "📖 Por estudiar",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            color = if (state.teoriaCompletada) NeuroGreen else MoradoActivo,
                            onClick = onNavigateToTheory
                        )

                        // TARJETA PRÁCTICA
                        ActionSectionCard(
                            title = "Práctica",
                            subtitle = "${state.ejerciciosCompletados}/${state.totalEjercicios} completados",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            color = MoradoActivo,
                            onClick = onNavigateToExercises
                        )

                        // TARJETA EXAMEN
                        ActionSectionCard(
                            title = "Examen Final",
                            subtitle = if (state.examenDisponible) "🎯 Disponible" else "🔒 Bloqueado",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            color = if (state.examenDisponible) Color(0xFFF59E0B) else Color(0xFF94A3B8),
                            isLocked = !state.examenDisponible,
                            onClick = onNavigateToExam
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // TUTOR IA
                    if (state.mensajeTutor.isNotEmpty()) {
                        TutorIAFeedbackMessage(message = state.mensajeTutor)
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ActionSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    val (iconRes, description, cardBgColor, borderStrokeColor) = when (title) {
        "Teoría" -> Quadruple(R.drawable.icon_theory, "Aprende los conceptos", Color(0xFFF0FDF4), Color(0xFF22C55E))
        "Ejercicios", "Práctica" -> Quadruple(R.drawable.icon_practice, "Ejercicios para mejorar", Color(0xFFF5F3FF), NeuroPurple)
        else -> {
            if (isLocked) {
                Quadruple(R.drawable.icon_exam, "Pon a prueba lo aprendido", Color(0xFFF8FAFC), Color(0xFFCBD5E1))
            } else {
                Quadruple(R.drawable.icon_exam, "Pon a prueba lo aprendido", Color(0xFFFFFBEB), Color(0xFFF59E0B))
            }
        }
    }

    val visualTitle = if (title == "Ejercicios") "Práctica" else title

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(26.dp))
            .clickable(enabled = !isLocked) { onClick() }
            .border(
                width = 2.dp,
                color = borderStrokeColor,
                shape = RoundedCornerShape(26.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 1.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.40f)
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
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    alpha = if (isLocked) 0.4f else 1.0f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = visualTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLocked) TextGray else TextoBase
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextoBase.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun TutorIAFeedbackMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier.padding(end = 45.dp, bottom = 12.dp),
            shape = RoundedCornerShape(22.dp, 22.dp, 4.dp, 22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🧠 Tutor IA", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MoradoActivo)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = message, fontSize = 13.sp, color = TextoBase.copy(alpha = 0.8f))
            }
        }
        Surface(
            modifier = Modifier
                .size(60.dp)
                .border(2.dp, Color.White, CircleShape),
            shape = CircleShape,
            color = Color(0xFFDCFCE7),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🤩", fontSize = 32.sp)
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)