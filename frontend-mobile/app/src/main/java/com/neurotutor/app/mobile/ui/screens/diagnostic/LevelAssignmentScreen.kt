package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@Composable
fun LevelAssignmentScreen(
    studentId: String,
    respuestas: List<String>,
    viewModel: DiagnosticResultsViewModel,
    onNavigateToDetails: () -> Unit,
    onStartAdventure: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.procesarResultadoExamen(studentId, respuestas)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "NeoPremiumFloating")
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "NeoPremiumOffset"
    )

    val (textoNivel, illustrationResId, colorNivel, gradientCard, mensajeUnicoNeo, _) =
        when (state.nivelAsignado) {
            "COHETE", "AVANZADO" -> Sextuple(
                "AVANZADO",
                R.drawable.island_advanced,
                Color(0xFF2563EB),
                Brush.verticalGradient(listOf(Color(0xFFEFF6FF), Color.White)),
                "¡Excelente trabajo! Estás listo para afrontar retos matemáticos más avanzados.",
                ""
            )

            "FUEGO", "INTERMEDIO" -> Sextuple(
                "INTERMEDIO",
                R.drawable.island_intermediate,
                Color(0xFF7C3AED),
                Brush.verticalGradient(listOf(Color(0xFFF5F3FF), Color.White)),
                "¡Vas por buen camino! Ahora trabajaremos desafíos más interesantes para ayudarte a seguir creciendo.",
                ""
            )

            else -> Sextuple(
                "BÁSICO",
                R.drawable.island_basic,
                Color(0xFF10B981),
                Brush.verticalGradient(listOf(Color(0xFFECFDF5), Color.White)),
                "Buen intento. He preparado una ruta especial para ayudarte a fortalecer tus bases matemáticas.",
                ""
            )
        }

    val assignedIndex = when (textoNivel) {
        "AVANZADO" -> 2
        "INTERMEDIO" -> 1
        else -> 0
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF8FAFC),
                            Color(0xFFF5F3FF),
                            FondoPanelEstudiante
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MoradoActivo, strokeWidth = 4.dp)
                    Spacer(Modifier.height(16.dp))
                    Text("Evaluando tus respuestas...", color = TextoBase.copy(alpha = 0.65f))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 ¡Tu aventura comienza aquí!",
                        color = Color(0xFF111827),
                        fontSize = 27.sp,
                        lineHeight = 33.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Hemos encontrado el nivel perfecto para ti",
                        color = Color(0xFF566078),
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(26.dp)),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, colorNivel.copy(alpha = 0.18f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(gradientCard)
                                .padding(horizontal = 14.dp, vertical = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = colorNivel.copy(alpha = 0.09f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, colorNivel.copy(alpha = 0.18f))
                            ) {
                                Text(
                                    text = "🌱  NIVEL ASIGNADO",
                                    color = colorNivel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.7.sp,
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = textoNivel,
                                color = colorNivel,
                                fontSize = 38.sp,
                                lineHeight = 42.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Este es tu primer destino",
                                color = Color(0xFF303852),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Image(
                                painter = painterResource(illustrationResId),
                                contentDescription = "Isla del nivel asignado $textoNivel",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp),
                                contentScale = ContentScale.Fit
                            )
                            AssignedLevelPath(assignedIndex)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    ScoreSummaryCard(
                        score = state.totalAciertos,
                        levelColor = colorNivel
                    )

                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.neo_check),
                                contentDescription = "Neo celebra el nivel asignado",
                                modifier = Modifier
                                    .size(112.dp)
                                    .offset(y = dy.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¡Tu ruta está lista!",
                                    color = Color(0xFF111827),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = mensajeUnicoNeo,
                                    color = Color(0xFF46506A),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = onStartAdventure,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(5.dp, RoundedCornerShape(18.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C3AED),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = "🚀  Comenzar mi aventura",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    TextButton(
                        onClick = onNavigateToDetails,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📄  Ver revisión de respuestas",
                            color = Color(0xFF7C3AED),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun AssignedLevelPath(assignedIndex: Int) {
    val levels = listOf(
        Triple("Básico", R.drawable.island_basic, Color(0xFF10B981)),
        Triple("Intermedio", R.drawable.island_intermediate, Color(0xFF7C3AED)),
        Triple("Avanzado", R.drawable.island_advanced, Color(0xFF2563EB))
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        levels.forEachIndexed { index, level ->
            val assigned = index == assignedIndex
            val available = index <= assignedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .scale(if (assigned) 1.08f else 1f)
                    .alpha(if (available) 1f else 0.38f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(level.second),
                        contentDescription = "Nivel ${level.first}",
                        modifier = Modifier.size(if (assigned) 70.dp else 62.dp),
                        contentScale = ContentScale.Fit
                    )
                    if (!available) {
                        Text(text = "🔒", fontSize = 18.sp)
                    }
                }
                Text(
                    text = level.first,
                    color = if (assigned) level.third else Color(0xFF667085),
                    fontSize = 11.sp,
                    fontWeight = if (assigned) FontWeight.Black else FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            if (index < levels.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .height(3.dp)
                        .background(
                            color = if (assignedIndex > index) {
                                Color(0xFF7C3AED)
                            } else {
                                Color(0xFFD1D5DB)
                            },
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

@Composable
private fun ScoreSummaryCard(
    score: Int,
    levelColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.92f),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, Color(0xFFE8E5F5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🎯", fontSize = 38.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Puntaje obtenido",
                    color = Color(0xFF222B45),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$score / 10",
                    color = levelColor,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "respuestas correctas",
                    color = Color(0xFF5B647A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Sigue practicando,\ncada paso te acerca\na tu mejor versión.",
                color = Color(0xFF46506A),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private data class Sextuple<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
)
