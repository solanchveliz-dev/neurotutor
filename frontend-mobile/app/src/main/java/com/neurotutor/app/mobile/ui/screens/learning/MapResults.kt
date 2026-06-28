package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.common.QuestionResult
import com.neurotutor.app.mobile.ui.screens.diagnostic.DiagnosticResultsViewModel
import com.neurotutor.app.mobile.ui.theme.*

// Enum interno para manejar los estados de los filtros de pestañas
private enum class FiltroRespuesta { TODAS, CORRECTAS, INCORRECTAS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapResultsScreen(
    modifier: Modifier = Modifier,
    studentId: String = "",
    respuestas: List<String> = emptyList(),
    viewModel: DiagnosticResultsViewModel = viewModel(),
    onComenzarPractica: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Filtro activo del mockup ("Todas", "Correctas", "Incorrectas")
    var filtroActivo by remember { mutableStateOf(FiltroRespuesta.TODAS) }

    // Almacena el número de pregunta expandida en un momento dado (Solo una a la vez)
    var preguntaExpandidaId by remember { mutableStateOf<Int?>(null) }

    // Procesar diagnóstico cuando se carga la pantalla si es necesario
    LaunchedEffect(Unit) {
        if (state.listaResultados.isEmpty() && respuestas.isNotEmpty()) {
            viewModel.procesarResultadoExamen(studentId, respuestas)
        }
    }

    // Filtrar los ítems de la lista en base al estado de la pestaña seleccionada
    val resultadosFiltrados = remember(state.listaResultados, filtroActivo) {
        when (filtroActivo) {
            FiltroRespuesta.TODAS -> state.listaResultados
            FiltroRespuesta.CORRECTAS -> state.listaResultados.filter { it.esCorrecta }
            FiltroRespuesta.INCORRECTAS -> state.listaResultados.filter { !it.esCorrecta }
        }
    }

    // Animación de flotación infinita para Neo (se mueve arriba y abajo sutilmente)
    val infiniteTransition = rememberInfiniteTransition(label = "MovimientoNeo")
    val desplazamientoY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f, // Se eleva hasta 12dp hacia arriba
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DesplazamientoNeo"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mis respuestas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)) // Fondo ultra claro moderno tipo Duolingo
                .padding(paddingValues)
        ) {

            // CONTENIDO PRINCIPAL: Filtros + Lista vertical ordenada
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                // FILTROS DE PESTAÑAS (Estilo exacto al mockup)
                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val opciones = listOf(
                        Triple("Todas", FiltroRespuesta.TODAS, 0.28f),
                        Triple("Correctas", FiltroRespuesta.CORRECTAS, 0.36f),
                        Triple("Incorrectas", FiltroRespuesta.INCORRECTAS, 0.36f)
                    )

                    opciones.forEach { (texto, filtro, peso) ->
                        val esSeleccionado = filtroActivo == filtro

                        Box(
                            modifier = Modifier
                                .weight(peso)
                                .height(40.dp)
                                .background(
                                    color = if (esSeleccionado) NeuroPurple else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (esSeleccionado) Color.Transparent else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { filtroActivo = filtro },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = texto,
                                fontSize = 14.sp,
                                fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Medium,
                                color = if (esSeleccionado) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }

                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                // LISTA DE RESPUESTAS VERTICAL CON ANIMACIÓN Y EXPANSIONES
                // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                if (state.isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeuroPurple, strokeWidth = 4.dp)
                    }
                } else if (state.errorMessage != null) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.errorMessage ?: "Error", color = NeuroRed, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.procesarResultadoExamen(studentId, respuestas) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp), // Espacio abajo para no tapar el botón
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(resultadosFiltrados, key = { it.numeroPregunta }) { item ->
                            val estaExpandido = preguntaExpandidaId == item.numeroPregunta

                            // Animación de rotación para la flecha indicadora
                            val gradosRotacion by animateFloatAsState(targetValue = if (estaExpandido) 180f else 0f, label = "FlechaRotacion")

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = if (estaExpandido) NeuroPurple.copy(alpha = 0.3f) else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (estaExpandido) 2.dp else 0.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {

                                    // Encabezado del ítem clicable
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                preguntaExpandidaId = if (estaExpandido) null else item.numeroPregunta
                                            }
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Icono de estado (Check verde o Equis roja) según el mockup
                                        Icon(
                                            imageVector = if (item.esCorrecta) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (item.esCorrecta) NeuroGreen else NeuroRed,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Pregunta ${item.numeroPregunta}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = if (item.esCorrecta) "Correcta" else "Incorrecta",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (item.esCorrecta) NeuroGreen else NeuroRed
                                            )
                                        }

                                        // Flecha indicadora de expansión derecha
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.rotate(gradosRotacion)
                                        )
                                    }

                                    // Contenedor expandible suave con los detalles requeridos
                                    AnimatedVisibility(
                                        visible = estaExpandido,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFF8FAFC))
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Divider(color = Color(0xFFE2E8F0))

                                            // 1. Tema Evaluado
                                            Column {
                                                Text(text = "Tema evaluado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                                Text(text = item.temaEvaluado, fontSize = 14.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                                            }

                                            // 2. Tu Respuesta
                                            Column {
                                                Text(text = "Tu respuesta", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                                Text(
                                                    text = item.respuestaEstudiante,
                                                    fontSize = 14.sp,
                                                    color = if (item.esCorrecta) NeuroGreen else NeuroRed,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            // 3. Respuesta Correcta (Solo si falló)
                                            if (!item.esCorrecta) {
                                                Column {
                                                    Text(text = "Respuesta correcta", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                                    Text(text = item.respuestaCorrecta, fontSize = 14.sp, color = NeuroGreen, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            // 4. Explicación Completa de NEO
                                            Column {
                                                Text(text = "Explicación", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                                                Text(
                                                    text = item.explicacion,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF1E293B),
                                                    lineHeight = 19.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // ILUSTRACIÓN DE NEO: `neo_review` EN LA PARTE INFERIOR DERECHA (CON MOVIMIENTO)
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            if (!state.isLoading && state.errorMessage == null) {
                Image(
                    painter = painterResource(id = R.drawable.neo_review),
                    contentDescription = "Guía Tutor Neo en revisión",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 68.dp) // Manteniendo tu misma posición exacta de origen
                        .offset(y = desplazamientoY.dp) // Aplicamos la flotación en el eje Y
                        .size(135.dp), // Aumentado de 105.dp a 135.dp
                    contentScale = ContentScale.Fit
                )
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // BOTÓN PRINCIPAL DE PRÁCTICA
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Transparent)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = onComenzarPractica,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeuroPurple),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Comenzar a practicar 🚀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}