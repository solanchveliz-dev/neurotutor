package com.neurotutor.app.mobile.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
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
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun StudentDashboardScreen(
    studentId: String,
    modifier: Modifier = Modifier,
    onModuloSeleccionado: (ModuleItem, String) -> Unit,
    onNavigateToTab: (String) -> Unit = {},
    onNavigateToTutor: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    val dashboardViewModel: StudentDashboardViewModel = remember {
        StudentDashboardViewModel(context.applicationContext)
    }

    val state by dashboardViewModel.uiState.collectAsState()

    // Control de persistencia por sesión de la bienvenida de NEO (5.5s)
    var yaSeMostroBienvenida by rememberSaveable { mutableStateOf(false) }
    var mostrarNeoOverlay by remember { mutableStateOf(!yaSeMostroBienvenida) }

    LaunchedEffect(studentId) {
        dashboardViewModel.cargarInformacionReal(studentId)
    }

    LaunchedEffect(mostrarNeoOverlay) {
        if (mostrarNeoOverlay) {
            delay(5500)
            mostrarNeoOverlay = false
            yaSeMostroBienvenida = true
        }
    }

    // Animación flotante de NEO en la bienvenida
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

    val azulDegradadoHero = Brush.verticalGradient(
        colors = listOf(Color(0xFF3A86F4), Color(0xFF63A1F7))
    )

    Box(modifier = modifier.fillMaxSize().background(FondoPanelEstudiante)) {
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
                    TextButton(onClick = { dashboardViewModel.cargarInformacionReal(studentId) }) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 110.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 🟦 CABECERA PRINCIPAL: HERO CARD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(azulDegradadoHero)
                            .padding(bottom = 32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_pegnnants),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = (-36).dp, y = 14.dp)
                                    .width(140.dp)
                                    .height(45.dp)
                                    .alpha(0.75f),
                                contentScale = ContentScale.Fit
                            )
                            Image(
                                painter = painterResource(id = R.drawable.cloud_bottom),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .width(70.dp)
                                    .height(35.dp)
                                    .alpha(0.25f),
                                contentScale = ContentScale.Fit
                            )
                            Image(
                                painter = painterResource(id = R.drawable.icon_pegnnants),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 36.dp, y = 14.dp)
                                    .width(140.dp)
                                    .height(45.dp)
                                    .alpha(0.75f)
                                    .graphicsLayer {
                                        scaleX = -1f
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 44.dp)
                        ) {
                            Text(
                                text = "Hola, ${state.nombreEstudiante}! 👋",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Continúa tu aventura matemática.",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                val nivelLimpio = state.nivelActual
                                    .substringBefore(" ")
                                    .uppercase()
                                    .trim()
                                val nombreDescriptivoNivel = when (nivelLimpio) {
                                    "BÁSICO" -> "Explorador Inicial"
                                    "INTERMEDIO" -> "Explorador Matemático"
                                    "AVANZADO" -> "Maestro Matemático"
                                    else -> state.nivelActual
                                }
                                val badgeResId = when (nivelLimpio) {
                                    "BÁSICO" -> R.drawable.badge_basic
                                    "INTERMEDIO" -> R.drawable.badge_intermediate
                                    "AVANZADO" -> R.drawable.badge_advanced
                                    else -> R.drawable.badge_basic
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Puntos",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF94A3B8),
                                            modifier = Modifier.weight(1.1f)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = nivelLimpio,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF94A3B8),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .weight(1.1f)
                                                .padding(top = 32.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.icon_star),
                                                contentDescription = null,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${state.puntosTotales}",
                                                fontSize = 37.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF1E293B)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .height(146.dp)
                                                .width(1.dp)
                                                .background(Color(0xFFE2E8F0))
                                        )
                                        Column(
                                            modifier = Modifier
                                                .weight(1.5f)
                                                .padding(start = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Image(
                                                painter = painterResource(id = badgeResId),
                                                contentDescription = "Insignia real del nivel",
                                                modifier = Modifier.size(114.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = nombreDescriptivoNivel,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF334155),
                                                textAlign = TextAlign.Center,
                                                maxLines = 2,
                                                lineHeight = 16.sp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 📈 SECCIÓN: TU PROGRESO
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
                    ) {
                        Text(
                            text = "Tu progreso",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoBase
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
                            ) {
                                val moduloActual = state.modulos.firstOrNull {
                                    it.temaNombre.contains("Fracciones", ignoreCase = true)
                                } ?: state.modulos.firstOrNull()
                                
                                val temaAMostrar = moduloActual?.temaNombre ?: "Fracciones"
                                val progresoFlotante = if (moduloActual != null && moduloActual.ejerciciosTotales > 0) {
                                    moduloActual.ejerciciosCompletados.toFloat() / moduloActual.ejerciciosTotales.toFloat()
                                } else {
                                    0f
                                }
                                val progresoPorcentaje = (progresoFlotante * 100).toInt()
                                Text(
                                    text = temaAMostrar,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LinearProgressIndicator(
                                        progress = { progresoFlotante },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(14.dp)
                                            .clip(RoundedCornerShape(7.dp)),
                                        color = Color(0xFF22C55E),
                                        trackColor = Color(0xFFE2E8F0)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "$progresoPorcentaje%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    // 🎯 SECCIÓN: ACCIONES RÁPIDAS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
                    ) {
                        Text(
                            text = "Acciones rápidas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoBase
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val targetModule = state.modulos.firstOrNull {
                                it.temaNombre.contains("Fracciones", ignoreCase = true)
                            } ?: state.modulos.firstOrNull()
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(96.dp)
                                    .clickable(enabled = targetModule != null) {
                                        targetModule?.let { onModuloSeleccionado(it, state.nombreEstudiante) }
                                    }
                                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Book,
                                        contentDescription = null,
                                        tint = MoradoActivo,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "📖 Continuar teoría",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(96.dp)
                                    .clickable(enabled = targetModule != null) {
                                        targetModule?.let { onModuloSeleccionado(it, state.nombreEstudiante) }
                                    }
                                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "🎯 Continuar práctica",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 🏠 BOTTOM NAVIGATION FLOTANTE
        Box(
            modifier = Modifier
                .fillMaxWidth(0.97f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.clickable { onNavigateToTab("inicio") }.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio", tint = MoradoActivo, modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("Inicio", fontSize = 11.sp, color = MoradoActivo, fontWeight = FontWeight.Bold)
                    }
                    Column(
                        modifier = Modifier.clickable { onNavigateToTab("modulos") }.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Módulos", tint = Color(0xFF94A3B8), modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("Módulos", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.weight(1.2f))
                    Column(
                        modifier = Modifier.clickable { onNavigateToTab("logros") }.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Logros", tint = Color(0xFF94A3B8), modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("Logros", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                    }
                    Column(
                        modifier = Modifier.clickable { onNavigateToTab("perfil") }.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color(0xFF94A3B8), modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("Perfil", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
                    .clickable { 
                        onNavigateToTutor(
                            state.nombreEstudiante, 
                            state.modulos.firstOrNull()?.temaNombre ?: "Fracciones"
                        ) 
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF60A5FA).copy(alpha = 0.65f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(43.dp)
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.neo_head),
                    contentDescription = "Neo AI",
                    modifier = Modifier.size(78.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // 💫 OVERLAY DE BIENVENIDA
        AnimatedVisibility(
            visible = mostrarNeoOverlay && !state.isLoading && state.errorMessage == null,
            exit = fadeOut(animationSpec = tween(durationMillis = 600))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.78f))
                    .clickable { mostrarNeoOverlay = false },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .offset { IntOffset(x = 0, y = offsetY.roundToInt()) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color(0xFF60A5FA).copy(alpha = 0.6f), Color.Transparent)
                                    ),
                                    shape = RoundedCornerShape(110.dp)
                                )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.neo_dashboard),
                            contentDescription = "Mentor Neo",
                            modifier = Modifier.size(240.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "¡Hola, ${state.nombreEstudiante}! 👋",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Estoy listo para acompañarte\nen tu aventura matemática.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
