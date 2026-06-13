package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.neurotutor.app.mobile.ui.models.QuestionResult
import com.neurotutor.app.mobile.ui.viewmodels.DiagnosticResultsViewModel
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@Composable
fun MapResultsScreen(
    modifier: Modifier = Modifier,
     studentId: String = "",
    respuestas: List<String> = emptyList(), 
    viewModel: DiagnosticResultsViewModel = viewModel(),
    onComenzarPractica: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(state.listaResultados) {
        if (state.preguntaSeleccionada == null && state.listaResultados.isNotEmpty()) {
            viewModel.seleccionarPregunta(state.listaResultados.first())
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPanelEstudiante)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = MoradoActivo, strokeWidth = 4.dp)
                }
                state.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(text = state.errorMessage ?: "Error desconocido", color = Color.Red, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        // 🚀 CORREGIDO: Usando el nombre de función exacto del ViewModel
                        Button(
                            onClick = { viewModel.procesarResultadoExamen(studentId, respuestas) },
                            colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Revisión de Respuestas",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoBase,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )

                            Text(
                                text = "Mira en qué preguntas acertaste y en cuáles fallaste",
                                fontSize = 14.sp,
                                color = TextoBase.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            MapResultsGrid(
                                resultados = state.listaResultados,
                                preguntaSeleccionada = state.preguntaSeleccionada,
                                onPreguntaClick = { res ->
                                    viewModel.seleccionarPregunta(res)
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            QuestionDetailCard(pregunta = state.preguntaSeleccionada)
                        }
                        Button(
                            onClick = onComenzarPractica,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Comenzar a practicar 🚀",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapResultsGrid(
    resultados: List<QuestionResult>,
    preguntaSeleccionada: QuestionResult?,
    onPreguntaClick: (QuestionResult) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(resultados) { item ->
                val esSeleccionado = item.numeroPregunta == preguntaSeleccionada?.numeroPregunta
                val colorCirculo = if (item.esCorrecta) Color(0xFF22C55E) else Color(0xFFEF4444)

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            color = if (esSeleccionado) colorCirculo else colorCirculo.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                        .clickable { onPreguntaClick(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.numeroPregunta.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esSeleccionado) Color.White else colorCirculo
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionDetailCard(pregunta: QuestionResult?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (pregunta != null) {
                Text(
                    text = "Pregunta ${pregunta.numeroPregunta}: ${pregunta.temaEvaluado}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoBase
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (pregunta.esCorrecta) "✨ ¡Respuesta correcta!" else "❌ Respuesta incorrecta",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (pregunta.esCorrecta) Color(0xFF22C55E) else Color(0xFFEF4444)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Resolución:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoBase.copy(alpha = 0.5f)
                )
                Text(
                    text = pregunta.explicacion,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextoBase
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tu respuesta: ${pregunta.respuestaEstudiante}",
                    fontSize = 15.sp,
                    color = TextoBase.copy(alpha = 0.8f)
                )
                if (!pregunta.esCorrecta) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Respuesta correcta: ${pregunta.respuestaCorrecta}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF22C55E)
                    )
                }
            } else {
                Text(
                    text = "Selecciona un número arriba para ver el detalle del ejercicio.",
                    fontSize = 14.sp,
                    color = TextoBase.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                )
            }
        }
    }
}
