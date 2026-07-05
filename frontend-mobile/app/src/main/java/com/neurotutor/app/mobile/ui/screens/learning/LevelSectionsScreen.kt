package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onNavigateToTutor: (String, String, String, String) -> Unit, // 🆕 Fase 2
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val viewModel: LevelSectionsViewModel = viewModel(
        factory = LevelSectionsViewModelFactory(context, studentId, moduleId)
    )

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProgress()
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF007AFF),
            Color(0xFF5AC8FA),
            Color(0xFFF1F5F9),
            Color(0xFFF8FAFC)
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
            // NUBES DECORATIVAS
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = 220.dp)
                    .size(125.dp)
                    .alpha(0.18f)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HERO HEADER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(start = 62.dp, end = 62.dp, top = 22.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val cleanLevelLabel = when {
                            levelName.contains("III:", true) || levelName.contains("Avanzado", true) -> "Avanzado"
                            levelName.contains("II:", true) || levelName.contains("Intermedio", true) -> "Intermedio"
                            else -> "Básico"
                        }

                        Text(
                            text = "$topicTitle - $cleanLevelLabel",
                            fontSize = 27.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = NeuroWhite,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "¿Qué quieres hacer?",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeuroWhite.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // CONTENIDO SCROLLABLE
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- 📚 SECCIONES ---
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // TARJETA TEORÍA
                        ActionSectionCard(
                            title = "Teoría",
                            subtitle = if (state.teoriaCompletada) "✓ Completado" else "📖 Por estudiar",
                            color = if (state.teoriaCompletada) NeuroGreen else MoradoActivo,
                            onClick = onNavigateToTheory
                        )

                        // TARJETA PRÁCTICA
                        ActionSectionCard(
                            title = "Práctica",
                            subtitle = if (state.examenDisponible) "✓ Completado" else "${state.ejerciciosCompletados}/${state.totalEjercicios} completados",
                            color = if (state.examenDisponible) NeuroGreen else MoradoActivo,
                            onClick = onNavigateToExercises
                        )

                        // TARJETAS EXAMEN
                        ActionSectionCard(
                            title = "Examen Final",
                            subtitle = when {
                                state.examPassed -> "✓ Aprobado"
                                state.examenDisponible -> "🎯 Disponible"
                                else -> "🔒 Bloqueado"
                            },
                            color = when {
                                state.examPassed -> NeuroGreen
                                state.examenDisponible -> Color(0xFFF59E0B)
                                else -> Color(0xFF94A3B8)
                            },
                            isLocked = !state.examenDisponible,
                            onClick = onNavigateToExam
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // TUTOR IA (Fase 2: Interactividad Proactiva)
                    TutorIAFeedbackMessage(
                        message = state.mensajeTutor.ifEmpty { "¡Hola $studentName! Soy Neo. ¿Tienes alguna duda sobre este nivel?" },
                        onClick = {
                            onNavigateToTutor(studentId, studentName, moduleId, topicTitle)
                        }
                    )

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
    color: Color,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    val (iconRes, description, cardBgColor, borderStrokeColor) = when (title) {
        "Teoría" -> Quadruple(R.drawable.icon_theory, "Aprende los conceptos", Color(0xFFF0FDF4), if(subtitle.contains("✓")) NeuroGreen else Color(0xFF22C55E))
        "Ejercicios", "Práctica" -> Quadruple(R.drawable.icon_practice, "Ejercicios para mejorar", Color(0xFFF5F3FF), if(subtitle.contains("✓")) NeuroGreen else NeuroPurple)
        else -> {
            if (isLocked) {
                Quadruple(R.drawable.icon_exam, "Pon a prueba lo aprendido", Color(0xFFF8FAFC), Color(0xFFCBD5E1))
            } else {
                Quadruple(R.drawable.icon_exam, "Pon a prueba lo aprendido", if(subtitle.contains("✓")) Color(0xFFF0FDF4) else Color(0xFFFFFBEB), if(subtitle.contains("✓")) NeuroGreen else Color(0xFFF59E0B))
            }
        }
    }

    val visualTitle = if (title == "Ejercicios") "Práctica" else title

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable(enabled = !isLocked) { onClick() }
            .border(
                width = 1.dp,
                color = borderStrokeColor.copy(alpha = 0.72f),
                shape = RoundedCornerShape(28.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLocked) 3.dp else 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.43f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isLocked) {
                                listOf(Color(0xFFEFF3F7), Color(0xFFE4EAF1))
                            } else if (visualTitle == "Teoría") {
                                listOf(Color(0xFFECFDF5), Color(0xFFDCFCE7))
                            } else if (visualTitle == "Práctica") {
                                listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE))
                            } else {
                                listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE))
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
                        .padding(10.dp),
                    alpha = if (isLocked) 0.52f else 1.0f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = visualTitle,
                    fontSize = 23.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isLocked) TextGray else TextoBase
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextoBase.copy(alpha = 0.66f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = color.copy(alpha = 0.11f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.12f))
                ) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = color,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TutorIAFeedbackMessage(message: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier.padding(end = 45.dp, bottom = 12.dp),
            shape = RoundedCornerShape(22.dp, 22.dp, 4.dp, 22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🧠 Tutor Neo", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MoradoActivo)
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
                Image(
                    painter = painterResource(id = R.drawable.neo_head),
                    contentDescription = "Consultar a Neo",
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
