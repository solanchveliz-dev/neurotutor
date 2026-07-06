package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.network.RetrofitClient
import com.neurotutor.app.mobile.ui.components.*
import com.neurotutor.app.mobile.ui.theme.*
import kotlinx.coroutines.delay

data class Question(
    val textBeforeImage: String,
    val imageRes: Int? = null,
    val textAfterImage: String? = null,
    val options: List<String>,
    val correctAnswer: Int
)

@OptIn(ExperimentalMaterial3Api::class)
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
        } catch (_: Exception) { }
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
    var showAnalyzingOverlay by remember { mutableStateOf(false) }
    var analyzingText by remember { mutableStateOf("Estoy analizando tus respuestas...") }

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Prueba Diagnóstica",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = TextoBase
                        )
                        Text(
                            text = "Evaluación inicial",
                            fontSize = 12.sp,
                            color = NeuroPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(1.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            val currentQuestion = questions.getOrNull(currentQuestionIndex)

            if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    LearningProgressHeader(
                        currentStep = currentQuestionIndex + 1,
                        totalSteps = questions.size,
                        label = "Progreso del diagnóstico"
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    LearningQuestionCard {
                        Text(
                            text = currentQuestion.textBeforeImage,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp,
                            color = TextoBase
                        )

                        if (currentQuestion.imageRes != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White
                            ) {
                                Image(
                                    painter = painterResource(id = currentQuestion.imageRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 150.dp, max = 240.dp)
                                        .padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        if (currentQuestion.textAfterImage != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = currentQuestion.textAfterImage,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 26.sp,
                                color = TextoBase
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    currentQuestion.options.forEachIndexed { index, option ->
                        LearningOptionCard(
                            index = index,
                            text = option,
                            isSelected = selectedOption == index,
                            onClick = { selectedAnswers[currentQuestionIndex] = index }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (currentQuestionIndex > 0) {
                            LearningActionButton(
                                text = "Anterior",
                                onClick = { currentQuestionIndex-- },
                                enabled = true,
                                modifier = Modifier.weight(1f),
                                containerColor = Color.White,
                                contentColor = NeuroPurple
                            )
                        }
                        LearningActionButton(
                            text = if (currentQuestionIndex == questions.lastIndex) "Finalizar ✓" else "Siguiente →",
                            onClick = {
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
                            enabled = selectedOption != null,
                            modifier = if (currentQuestionIndex > 0) Modifier.weight(1.8f) else Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            IntroOverlay(visible = showIntroOverlay, dy = dy, onStart = { showIntroOverlay = false })
            AnalyzingOverlay(visible = showAnalyzingOverlay, text = analyzingText)
        }
    }
}

@Composable
private fun IntroOverlay(visible: Boolean, dy: Float, onStart: () -> Unit) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xB30F172A)).clickable(enabled = true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Text(
                        text = "¡Hola! Vamos a descubrir tu nivel actual para ayudarte a aprender mejor.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoBase,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.neo_diagnostic_intro),
                    contentDescription = "Asistente Neo",
                    modifier = Modifier
                        .size(220.dp)
                        .offset { IntOffset(0, dy.dp.roundToPx()) },
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(24.dp))
                LearningActionButton(
                    text = "Comenzar",
                    onClick = onStart,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}

@Composable
private fun AnalyzingOverlay(visible: Boolean, text: String) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xD90F172A)).clickable(enabled = true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.neo_explain),
                        contentDescription = "Analizando",
                        modifier = Modifier.size(180.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "¡Excelente!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeuroPurple,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = text,
                        color = NeuroPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = NeuroPurple,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}
