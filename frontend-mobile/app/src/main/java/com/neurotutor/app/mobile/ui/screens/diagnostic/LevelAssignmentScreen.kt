package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    onNavigateToDetails: () -> Unit
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

    // 🎨 PROPUESTA VISUAL UNIFICADA:
    // Se reemplazan las INSIGNIAS por las ISLAS para representar el mundo desbloqueado.
    val (textoNivel, illustrationResId, colorNivel, gradientCard, mensajeUnicoNeo, textoCelebracion) = when (state.nivelAsignado) {
        "COHETE", "AVANZADO" -> Sextuple(
            "AVANZADO",
            R.drawable.island_advanced,
            Color(0xFF0052FF),
            Brush.verticalGradient(colors = listOf(Color(0xFFE6EEFF), Color.White)),
            "¡Excelente trabajo! Estás listo para afrontar retos matemáticos más avanzados.",
            "🎉 ¡Excelente trabajo!"
        )
        "FUEGO", "INTERMEDIO" -> Sextuple(
            "INTERMEDIO",
            R.drawable.island_intermediate,
            Color(0xFF7C3AED),
            Brush.verticalGradient(colors = listOf(Color(0xFFF3E8FF), Color.White)),
            "¡Vas por buen camino! Ahora trabajaremos desafíos más interesantes para ayudarte a seguir creciendo.",
            "🎉 ¡Excelente trabajo!"
        )
        else -> Sextuple(
            "BÁSICO",
            R.drawable.island_basic,
            Color(0xFF10B981),
            Brush.verticalGradient(colors = listOf(Color(0xFFECFDF5), Color.White)),
            "Buen intento. He preparado una ruta especial para ayudarte a fortalecer tus bases matemáticas.",
            "🌟 ¡Diagnóstico completado!"
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPanelEstudiante)
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MoradoActivo, strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Evaluando tus respuestas...", color = TextoBase.copy(alpha = 0.6f))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = textoCelebracion,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    // 2. TARJETA PRINCIPAL (Nivel + Ilustración Isla + Puntaje)
                    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(24.dp))
                            .border(1.5.dp, colorNivel.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(gradientCard)
                                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "NIVEL ASIGNADO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colorNivel.copy(alpha = 0.7f),
                                letterSpacing = 1.2.sp
                            )

                            Text(
                                text = textoNivel,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = colorNivel,
                                letterSpacing = 1.sp
                            )

                            // Ilustración de la Isla como meta/destino
                            Image(
                                painter = painterResource(id = illustrationResId),
                                contentDescription = "Ilustración de la isla",
                                modifier = Modifier
                                    .height(240.dp)
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )

                            Text(
                                text = "🎯 Puntaje: ${state.totalAciertos} / 10 aciertos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.3f))

                    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    // 3. TARJETA NEO (Guía Tutor)
                    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.neo_diagnostic_result),
                                contentDescription = "Tutor Neo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .offset(y = dy.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = mensajeUnicoNeo,
                                fontSize = 14.sp,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.5f))

                    Button(
                        onClick = onNavigateToDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Ver revisión de respuestas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private data class Sextuple<A, B, C, D, E, F>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F)
