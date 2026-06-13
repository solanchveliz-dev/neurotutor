package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.neurotutor.app.mobile.ui.viewmodels.DiagnosticResultsViewModel

@Composable
fun LevelAssignmentScreen(
    studentId: String,
    respuestas: List<String>,
    viewModel: DiagnosticResultsViewModel,
    onNavigateToDetails: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // 🚀 Ejecutamos el procesamiento al entrar a la pantalla
    LaunchedEffect(studentId) {
        viewModel.procesarResultadoExamen(studentId, respuestas)
    }

    // 🚀 ASIGNACIÓN DINÁMICA DE IMAGEN SEGÚN NIVEL
    val imagenNivel = when (state.nivelAsignado) {
        "COHETE", "AVANZADO" -> R.drawable.imagen_pregunta10_jugonaranja 
        "FUEGO", "INTERMEDIO" -> R.drawable.imagen_pregunta5_sogas
        else -> R.drawable.imagen_pregunta4_frutas // Planta/Básico
    }

    val tituloNivel = when (state.nivelAsignado) {
        "COHETE", "AVANZADO" -> "Nivel Avanzado 🚀"
        "FUEGO", "INTERMEDIO" -> "Nivel Intermedio 🔥"
        else -> "Nivel Básico 🌱"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPanelEstudiante)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MoradoActivo, strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Evaluando tus respuestas...", color = TextoBase.copy(alpha = 0.6f))
                }
            } else if (state.errorMessage != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Text(text = "Error: ${state.errorMessage}", color = Color.Red, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.procesarResultadoExamen(studentId, respuestas) }) {
                        Text("Reintentar")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "¡Diagnóstico Concluido!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoBase
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Puntaje obtenido: ${state.totalAciertos} / 10",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MoradoActivo
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = imagenNivel),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = tituloNivel,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoBase
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (state.mensaje.isNotEmpty()) state.mensaje else "Hemos calculado tu punto de partida ideal en NeuroTutor.",
                            fontSize = 16.sp,
                            color = TextoBase.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Button(
                        onClick = onNavigateToDetails,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Ver Revisión de Respuestas 📊",
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
