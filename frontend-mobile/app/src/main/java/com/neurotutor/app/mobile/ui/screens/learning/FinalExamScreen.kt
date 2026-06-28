package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalExamScreen(
    studentId: String,
    moduleId: String,
    level: String,
    viewModel: FinalExamViewModel = viewModel(),
    onFinish: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showCelebration by remember { mutableStateOf(false) }

    LaunchedEffect(state.levelUp, state.topicCompleted) {
        if (state.levelUp || state.topicCompleted) {
            showCelebration = true
            delay(3000)
            showCelebration = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Examen Final: ${getLevelDisplayName(level)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = NeuroBlue
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFEEF2FF), Color.White, Color(0xFFFAF5FF))
                    )
                )
                .padding(paddingValues)
        ) {
            // Animación de celebración (reemplazo de confetti)
            if (showCelebration) {
                CelebrationOverlay(
                    levelUp = state.levelUp,
                    topicCompleted = state.topicCompleted
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = NeuroPurple,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(60.dp)
                    )
                }
            } else if (state.questions.isNotEmpty() && !state.showResultDialog) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = getLevelColor(level).copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, getLevelColor(level))
                            ) {
                                Text(
                                    text = getLevelDisplayName(level),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = getLevelColor(level),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "${state.currentQuestionIndex + 1}/${state.questions.size}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeuroPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeuroPurple.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(
                                        (state.currentQuestionIndex + 1).toFloat() / state.questions.size
                                    )
                                    .fillMaxHeight()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(NeuroGreen, Color(0xFF10B981))
                                        )
                                    )
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = NeuroSky.copy(alpha = 0.3f)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "Pregunta ${state.currentQuestionIndex + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NeuroPurple
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = state.questions[state.currentQuestionIndex].question,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 26.sp,
                                    color = TextoBase
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        state.questions[state.currentQuestionIndex].options.forEachIndexed { index, option ->
                            val isSelected = state.answers[state.currentQuestionIndex] == index
                            AnimatedCardOption(
                                isSelected = isSelected,
                                onClick = { viewModel.selectAnswer(index) },
                                optionText = option,
                                index = index
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedButton(
                            text = if (state.currentQuestionIndex < state.questions.size - 1)
                                "Siguiente Pregunta"
                            else
                                "Finalizar Examen",
                            onClick = {
                                if (state.currentQuestionIndex < state.questions.size - 1) {
                                    viewModel.nextQuestion()
                                } else {
                                    viewModel.submitExam(studentId, moduleId, level)
                                }
                            },
                            enabled = state.answers[state.currentQuestionIndex] != null
                        )
                    }
                }
            }

            if (state.showResultDialog) {
                ExamResultDialog(
                    passed = state.isPassed,
                    score = state.score,
                    pointsEarned = state.pointsEarned,
                    levelUp = state.levelUp,
                    newLevel = state.newLevel,
                    topicCompleted = state.topicCompleted,
                    alreadyPassedBefore = state.alreadyPassedBefore,
                    examLevel = level,
                    onDismiss = {
                        viewModel.dismissResultDialog()
                        onFinish()
                    }
                )
            }
        }
    }
}

@Composable
fun CelebrationOverlay(levelUp: Boolean, topicCompleted: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale)
        ) {
            Text(
                text = if (topicCompleted) "🏆" else if (levelUp) "🚀" else "🎉",
                fontSize = 100.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (topicCompleted) "¡TEMA COMPLETADO!"
                else if (levelUp) "¡NIVEL SUPERADO!"
                else "¡FELICITACIONES!",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = if (topicCompleted) NeuroOrange
                else if (levelUp) NeuroGreen
                else NeuroPurple,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Círculos decorativos flotantes
            for (i in 0..5) {
                FloatingEmoji(
                    delayMillis = i * 200L,
                    alpha = alpha
                )
            }
        }
    }
}

@Composable
fun FloatingEmoji(delayMillis: Long, alpha: Float) {
    val emojis = listOf("🎉", "⭐", "🏆", "🚀", "🌟", "💪")
    val randomEmoji = emojis.random()

    val transition = rememberInfiniteTransition(label = "floating")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = delayMillis.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    Text(
        text = randomEmoji,
        fontSize = 32.sp,
        modifier = Modifier
            .offset(y = offsetY.dp)
            .alpha(alpha)
    )
}

@Composable
fun AnimatedCardOption(
    isSelected: Boolean,
    onClick: () -> Unit,
    optionText: String,
    index: Int
) {
    val transition = updateTransition(targetState = isSelected, label = "option")
    val elevation by transition.animateDp(label = "elevation") { selected ->
        if (selected) 4.dp else 0.dp
    }
    val scaleValue by transition.animateFloat(label = "scale") { selected ->
        if (selected) 1.02f else 1f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .scale(scaleValue)
            .selectable(selected = isSelected, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeuroPurple.copy(alpha = 0.08f) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, NeuroPurple) else BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) NeuroPurple else NeuroPurple.copy(alpha = 0.1f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = ('A' + index).toString(),
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else NeuroPurple
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = optionText,
                fontSize = 15.sp,
                color = if (isSelected) NeuroPurple else TextoBase
            )
        }
    }
}

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }

    val scaleValue by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scaleValue),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) NeuroPurple else Color(0xFF9CA3AF)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.White,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ExamResultDialog(
    passed: Boolean,
    score: Int,
    pointsEarned: Int,
    levelUp: Boolean,
    newLevel: String?,
    topicCompleted: Boolean,
    alreadyPassedBefore: Boolean,
    examLevel: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val scaleValue by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )

                Text(
                    text = when {
                        topicCompleted -> "🏆"
                        levelUp -> "🚀"
                        passed && pointsEarned > 0 -> "🎉"
                        passed && alreadyPassedBefore -> "📚"
                        else -> "😅"
                    },
                    fontSize = 64.sp,
                    modifier = Modifier.scale(scaleValue)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when {
                        topicCompleted -> "¡TEMA COMPLETADO! 🏆"
                        levelUp -> "¡NIVEL SUPERADO! 🚀"
                        passed && pointsEarned > 0 -> "¡FELICITACIONES! 🎉"
                        passed && alreadyPassedBefore -> "¡Buen repaso! 📚"
                        else -> "¡CASI LO LOGRASTE! 💪"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = when {
                        topicCompleted -> NeuroOrange
                        levelUp -> NeuroGreen
                        passed -> NeuroBlue
                        else -> NeuroRed
                    },
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val animatedScore by animateIntAsState(
                    targetValue = score,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "score"
                )
                val color = if (passed) NeuroGreen else NeuroRed

                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = color.copy(alpha = 0.1f),
                    border = BorderStroke(3.dp, color)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "$animatedScore%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = "Mínimo 70%",
                            fontSize = 11.sp,
                            color = color
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (passed && pointsEarned > 0) {
                    val animatedPoints by animateIntAsState(
                        targetValue = pointsEarned,
                        animationSpec = tween(800, easing = FastOutSlowInEasing),
                        label = "points"
                    )

                    Surface(
                        color = NeuroOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⭐", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "+$animatedPoints puntos",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = NeuroOrange
                                )
                                Text(
                                    "¡Sigue así!",
                                    fontSize = 11.sp,
                                    color = NeuroOrange
                                )
                            }
                        }
                    }
                }

                when {
                    passed && alreadyPassedBefore && pointsEarned == 0 -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Ya habías aprobado este examen antes",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            "¡Sigue así acumulando experiencia!",
                            fontSize = 12.sp,
                            color = NeuroGreen
                        )
                    }
                    levelUp && newLevel != null -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = NeuroGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🚀", fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "¡Nuevo nivel desbloqueado!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NeuroGreen
                                    )
                                    Text(
                                        "Ahora eres ${getLevelDisplayName(newLevel)}",
                                        fontSize = 12.sp,
                                        color = NeuroGreen
                                    )
                                }
                            }
                        }
                    }
                    topicCompleted -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = NeuroOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🏆", fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "¡Completaste el tema!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NeuroOrange
                                    )
                                    Text(
                                        "Dominas completamente este tema",
                                        fontSize = 12.sp,
                                        color = NeuroOrange
                                    )
                                }
                            }
                        }
                    }
                    !passed -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Repasa la teoría y vuelve a intentarlo",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        topicCompleted -> NeuroOrange
                        levelUp -> NeuroGreen
                        passed -> NeuroPurple
                        else -> Color(0xFF9CA3AF)
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Continuar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }
    )
}

private fun getLevelColor(level: String): Color = when(level) {
    "B" -> NeuroGreen
    "I" -> NeuroOrange
    "A" -> NeuroPurple
    else -> NeuroBlue
}

private fun getLevelDisplayName(level: String): String = when(level) {
    "B" -> "Básico 🌱"
    "I" -> "Intermedio 🔥"
    "A" -> "Avanzado 🚀"
    else -> level
}