package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.network.RetrofitClient
import kotlinx.coroutines.delay

data class Question(
    val textBeforeImage: String,
    val imageRes: Int? = null,
    val textAfterImage: String? = null,
    val options: List<String>,
    val correctAnswer: Int
)

@Composable
fun DiagnosticScreen(
    studentId: String,
    onNavigateToAssignment: (String, List<String>) -> Unit
) {
    val localQuestions = remember {
        DiagnosticEducationCatalog.lessons.map { lesson ->
            Question(
                textBeforeImage = lesson.prompt,
                imageRes = lesson.imageRes,
                textAfterImage = lesson.promptAfterImage,
                options = lesson.options,
                correctAnswer = lesson.correctIndex
            )
        }
    }
    var questions by remember { mutableStateOf(localQuestions) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getDiagnosticQuestions()
            val remoteQuestions = response.body()

            if (response.isSuccessful && remoteQuestions?.size == localQuestions.size) {
                questions = remoteQuestions
                    .sortedBy { it.order }
                    .mapIndexed { index, question ->
                        Question(
                            textBeforeImage = question.textBeforeImage.orEmpty(),
                            imageRes = when (question.order) {
                                4 -> R.drawable.imagen_pregunta4_frutas
                                5 -> R.drawable.imagen_pregunta5_sogas
                                6 -> R.drawable.imagen_pregunta6_ecuacion
                                7 -> R.drawable.imagen_pregunta7_terreno
                                8 -> R.drawable.imagen_pregunta8_tabla
                                10 -> R.drawable.imagen_pregunta10_jugonaranja
                                else -> null
                            },
                            textAfterImage = question.textAfterImage,
                            options = question.options,
                            correctAnswer = DiagnosticEducationCatalog
                                .lesson(question.order)
                                ?.correctIndex
                                ?: localQuestions[index].correctAnswer
                        )
                    }
            }
        } catch (_: Exception) {
            // Se conserva el contenido local para no interrumpir el flujo existente.
        }
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val selectedAnswers = remember {
        mutableStateListOf<Int?>().apply {
            repeat(localQuestions.size) { add(null) }
        }
    }
    val scrollState = rememberScrollState()
    val selectedOption = selectedAnswers.getOrNull(currentQuestionIndex)

    val calificaciones = remember { mutableStateListOf<String>() }
    val labels = listOf("a", "b", "c", "d")

    var showIntroOverlay by remember { mutableStateOf(true) }

    // Estados para controlar el overlay intermedio de análisis
    var showAnalyzingOverlay by remember { mutableStateOf(false) }
    var analyzingText by remember { mutableStateOf("Estoy analizando tus respuestas...") }

    // Animación de flotación vertical sutil compartida
    val infiniteTransition = rememberInfiniteTransition(label = "NeoFloatingTransition")
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "NeoFloatingOffset"
    )

    LaunchedEffect(currentQuestionIndex) {
        scrollState.scrollTo(0)
    }

    // Controlador del tiempo y textos secuenciales del overlay de análisis (2 segundos totales)
    LaunchedEffect(showAnalyzingOverlay) {
        if (showAnalyzingOverlay) {
            analyzingText = "Estoy analizando tus respuestas..."
            delay(1000)
            analyzingText = "Descubriendo tu nivel ideal..."
            delay(1000)
            showAnalyzingOverlay = false
            onNavigateToAssignment(studentId, calificaciones.toList())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        val currentQuestion = questions.getOrNull(currentQuestionIndex)

        if (currentQuestion != null) {
            DiagnosticHeader(
                currentQuestion = currentQuestionIndex + 1,
                totalQuestions = questions.size,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 126.dp, bottom = 88.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                Surface(
                    color = Color(0xFFEDE9FE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = "Pregunta ${currentQuestionIndex + 1}",
                        color = Color(0xFF7C3AED),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = currentQuestion.textBeforeImage,
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111B5C),
                    modifier = Modifier.fillMaxWidth()
                )

                if (currentQuestion.imageRes != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.dp, Color(0xFFE4E7F2))
                    ) {
                        Image(
                            painter = painterResource(id = currentQuestion.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 150.dp, max = 210.dp)
                                .padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                if (currentQuestion.textAfterImage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentQuestion.textAfterImage,
                        fontSize = 17.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF111B5C),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                currentQuestion.options.forEachIndexed { index, option ->
                    DiagnosticOptionCard(
                        label = ('A' + index).toString(),
                        text = option,
                        selected = selectedOption == index,
                        onClick = { selectedAnswers[currentQuestionIndex] = index }
                    )
                    if (index < currentQuestion.options.lastIndex) {
                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
            }

            DiagnosticNavigationButtons(
                currentQuestionIndex = currentQuestionIndex,
                isLastQuestion = currentQuestionIndex == questions.lastIndex,
                nextEnabled = selectedOption != null,
                onPrevious = {
                    if (currentQuestionIndex > 0) {
                        currentQuestionIndex--
                    }
                },
                onNext = {
                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                    } else {
                        calificaciones.clear()
                        calificaciones.addAll(
                            selectedAnswers.map { answerIndex ->
                                answerIndex?.let { labels[it].uppercase() }.orEmpty()
                            }
                        )
                        showAnalyzingOverlay = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

        // Overlay Introductorio inicial
        AnimatedVisibility(
            visible = showIntroOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xB30F172A))
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp))
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(2.dp, Color(0xFFEDF4FF), RoundedCornerShape(20.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "¡Hola! Vamos a descubrir tu nivel actual para ayudarte a aprender mejor.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Image(
                        painter = painterResource(id = R.drawable.neo_diagnostic_intro),
                        contentDescription = "Asistente Neo",
                        modifier = Modifier
                            .size(220.dp)
                            .offset(y = dy.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showIntroOverlay = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(54.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED), contentColor = Color.White)
                    ) {
                        Text(text = "Comenzar", fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // OVERLAY TEMPORAL DE ANÁLISIS INTERMEDIO
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        AnimatedVisibility(
            visible = showAnalyzingOverlay,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xD90F172A)) // Atenúa y bloquea la pantalla trasera de forma sutil
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.neo_explain),
                            contentDescription = "Neo explica que está analizando las respuestas",
                            modifier = Modifier.size(180.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "¡Terminaste el diagnóstico!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF5B21E8),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Neo está revisando cuidadosamente tus respuestas para encontrar tu nivel ideal.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111B5C),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFF5F1FF),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Top
                            ) {
                                DiagnosticAnalysisStage(
                                    icon = "📋",
                                    text = "Revisando tus\nrespuestas",
                                    modifier = Modifier.weight(1f)
                                )
                                DiagnosticAnalysisStage(
                                    icon = "🔍",
                                    text = "Analizando tu\ndesempeño",
                                    modifier = Modifier.weight(1f)
                                )
                                DiagnosticAnalysisStage(
                                    icon = "📊",
                                    text = "Calculando tu\nnivel ideal",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = analyzingText,
                            color = Color(0xFF5B21E8),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp),
                            color = Color(0xFF7C3AED),
                            trackColor = Color(0xFFE9E7F5),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Spacer(Modifier.height(14.dp))
                        Surface(
                            color = Color(0xFFF2EDFF),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "💡  Sigue así, cada paso te acerca a tu mejor versión.",
                                color = Color(0xFF111B5C),
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticAnalysisStage(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 28.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = text,
            color = Color(0xFF111B5C),
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DiagnosticHeader(
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(126.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Diagnóstico inicial",
                color = Color(0xFF111B5C),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp, start = 24.dp, end = 24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 28.dp, end = 28.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { currentQuestion.toFloat() / totalQuestions.coerceAtLeast(1) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = Color(0xFF7C3AED),
                    trackColor = Color(0xFFE9E7F5),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(Modifier.width(18.dp))
                Text(
                    text = "$currentQuestion/$totalQuestions",
                    color = Color(0xFF7C3AED),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun DiagnosticOptionCard(
    label: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = Color(0xFF7C3AED)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick),
        color = if (selected) Color(0xFFF5F1FF) else Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) accent else Color(0xFFDADDEF)
        ),
        shadowElevation = if (selected) 2.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = if (selected) Color(0xFF8661FA) else Color(0xFFE2D8FF)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        color = if (selected) Color.White else accent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = text,
                color = if (selected) accent else Color(0xFF111B5C),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DiagnosticNavigationButtons(
    currentQuestionIndex: Int,
    isLastQuestion: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentQuestionIndex > 0) {
            DiagnosticNavigationButton(
                text = "Anterior",
                enabled = true,
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            )
        }
        DiagnosticNavigationButton(
            text = if (isLastQuestion) "Finalizar examen  ✓" else "Siguiente  →",
            enabled = nextEnabled,
            onClick = onNext,
            modifier = if (currentQuestionIndex > 0) {
                Modifier.weight(1.35f)
            } else {
                Modifier.fillMaxWidth()
            }
        )
    }
}

@Composable
private fun DiagnosticNavigationButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(54.dp)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = RoundedCornerShape(18.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7C3AED),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFD9D0F8),
            disabledContentColor = Color(0xFF8276A8)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}
