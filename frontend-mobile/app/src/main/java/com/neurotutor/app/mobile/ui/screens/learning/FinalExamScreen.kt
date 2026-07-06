package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.components.*
import com.neurotutor.app.mobile.ui.theme.*
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

private data class ExamThemeConfig(
    val primaryColor: Color,
    val bgColor: Color,
    val ribbonColor: Color,
    val assetRes: Int,
    val tagline: String,
    val levelLabel: String
)

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

    if (state.showResultDialog && state.isPassed && !state.alreadyPassedBefore && state.pointsEarned > 0) {
        FinalExamCelebrationScreen(
            state = state,
            examLevel = level,
            onContinue = {
                viewModel.dismissResultDialog()
                onFinish()
            }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Examen Final",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = TextoBase
                            )
                            Text(
                                text = getLevelDisplayName(level),
                                fontSize = 12.sp,
                                color = getLevelColor(level),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
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
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeuroPurple, strokeWidth = 3.dp)
                    }
                } else if (state.questions.isNotEmpty() && !state.showResultDialog) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        LearningProgressHeader(
                            currentStep = state.currentQuestionIndex + 1,
                            totalSteps = state.questions.size,
                            label = "Progreso del examen"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        LearningQuestionCard {
                            Text(
                                text = state.questions[state.currentQuestionIndex].question,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp,
                                color = TextoBase
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        state.questions[state.currentQuestionIndex].options.forEachIndexed { index, option ->
                            val isSelected = state.answers[state.currentQuestionIndex] == index
                            LearningOptionCard(
                                index = index,
                                text = option,
                                isSelected = isSelected,
                                onClick = { viewModel.selectAnswer(index) }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        LearningActionButton(
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
                            enabled = state.answers[state.currentQuestionIndex] != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(48.dp))
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

                state.errorMessage?.let { message ->
                    AlertDialog(
                        onDismissRequest = viewModel::dismissSubmissionError,
                        title = { Text("Error al guardar", fontWeight = FontWeight.Bold) },
                        text = { Text(message) },
                        confirmButton = {
                            TextButton(onClick = viewModel::dismissSubmissionError) {
                                Text("Entendido")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FinalExamCelebrationScreen(
    state: FinalExamUiState,
    examLevel: String,
    onContinue: () -> Unit
) {
    val scrollState = rememberScrollState()
    val totalQuestions = state.totalQuestions
    val correctAnswers = state.correctAnswers
    val unlockedBadge = state.unlockedBadgeId?.let(BadgeMapper::resolveById)

    val theme = when (examLevel) {
        "B" -> ExamThemeConfig(NeuroGreen, Color(0xFFF0FDF4), Color(0xFF16A34A), R.drawable.final_exam_basic, "¡Sigue así, vas por más!", "Básico")
        "I" -> ExamThemeConfig(NeuroPurple, Color(0xFFF5F3FF), Color(0xFF7C3AED), R.drawable.final_exam_intermediate, "¡Sigue así, vas por más!", "Intermedio")
        "A" -> ExamThemeConfig(NeuroBlue, Color(0xFFE0F2FE), Color(0xFF2563EB), R.drawable.final_exam_advanced, "¡Sigue así, eres imparable!", "Avanzado")
        else -> ExamThemeConfig(NeuroBlue, Color.White, NeuroBlue, R.drawable.final_exam_basic, "¡Buen trabajo!", examLevel)
    }

    val party = remember {
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xFFF59E0B.toInt(), 0xFF7C3AED.toInt(), 0xFF3B82F6.toInt()),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(theme.bgColor)) {
        KonfettiView(modifier = Modifier.fillMaxSize(), parties = listOf(party))

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(220.dp), shape = CircleShape, color = theme.primaryColor.copy(alpha = 0.1f)) {}
                Image(
                    painter = painterResource(id = theme.assetRes),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(color = theme.ribbonColor, shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(bottom = 20.dp)) {
                Text(
                    text = "¡Examen final completado!",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 19.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Text("¡Felicidades!", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextoBase)
            Text("Has demostrado todo lo que sabes.", fontSize = 15.sp, color = TextGray)
            Text(text = theme.tagline, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = theme.primaryColor)

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                LearningStatCard(Modifier.weight(1f), "Puntaje final", "$correctAnswers / $totalQuestions")
                LearningStatCard(Modifier.weight(1f), "Precisión", "${state.score}%")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Recompensas obtenidas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 12.dp))

            if (state.pointsEarned > 0) {
                LearningRewardItem(iconRes = R.drawable.icon_star, label = "+${state.pointsEarned} puntos", iconTint = NeuroOrange)
            }

            unlockedBadge?.let { badge ->
                LearningRewardItem(iconRes = badge.iconRes, label = "Insignia: ${badge.name}", iconTint = theme.primaryColor)
            }

            if ("FIRST_EXAM_PASSED" in state.unlockedAchievementCodes) {
                SecondaryAchievementCard("También desbloqueaste: Primer examen aprobado", theme.primaryColor)
            }

            if (state.moduleProgress == 100) {
                LearningRewardItem(iconVector = Icons.Default.CheckCircle, label = "Nivel ${theme.levelLabel} completado", iconTint = NeuroGreen)
            }

            Spacer(modifier = Modifier.height(40.dp))

            LearningActionButton(
                text = "Continuar",
                onClick = onContinue,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                containerColor = theme.ribbonColor
            )
        }
    }
}

@Composable
private fun SecondaryAchievementCard(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 13.sp, color = TextoBase, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
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
        shape = RoundedCornerShape(32.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (passed) "🎉" else "💪",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (passed) "¡EXAMEN APROBADO!" else "¡SIGUE INTENTANDO!",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = if (passed) NeuroGreen else NeuroRed,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                val color = if (passed) NeuroGreen else NeuroRed
                Surface(
                    modifier = Modifier.size(110.dp),
                    shape = CircleShape,
                    color = color.copy(alpha = 0.05f),
                    border = BorderStroke(4.dp, color)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "$score%", fontSize = 32.sp, fontWeight = FontWeight.Black, color = color)
                        Text(text = "calificación", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
                
                if (passed && pointsEarned > 0) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(color = NeuroOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+$pointsEarned Puntos",
                                fontWeight = FontWeight.Black,
                                color = NeuroOrange
                            )
                        }
                    }
                }
                
                if (!passed) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No te rindas, repasa un poco más la teoría y vuelve a intentarlo.",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            LearningActionButton(
                text = "Continuar",
                onClick = onDismiss,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (passed) NeuroPurple else Color(0xFF64748B)
            )
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
