package com.neurotutor.app.mobile.ui.screens.learning

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.neurotutor.app.mobile.navigation.Screen
import com.neurotutor.app.mobile.ui.components.FloatingTutor
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePlayerScreen(
    studentId: String,
    studentName: String,
    moduleId: String,
    level: String,
    topicTitle: String,
    navController: NavHostController,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ExerciseViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var answerSubmitted by remember { mutableStateOf(false) }
    var lastAnswerWasCorrect by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(moduleId) {
        viewModel.loadExercises(moduleId)
        answerSubmitted = false
        lastAnswerWasCorrect = false
        selectedOption = null
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onFinish()
        }
    }

    LaunchedEffect(state.currentExerciseIndex) {
        answerSubmitted = false
        lastAnswerWasCorrect = false
        selectedOption = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val levelTitle = when(level) {
                            "B" -> "Básico"
                            "I" -> "Intermedio"
                            "A" -> "Avanzado"
                            else -> level
                        }
                        Text("Práctica: $levelTitle", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Resuelve para ganar puntos", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Text(
                        text = "${state.totalPointsEarned} pts",
                        modifier = Modifier.padding(end = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = MoradoActivo
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoPanelEstudiante)
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MoradoActivo)
            } else if (state.exercises.isNotEmpty()) {
                val currentExercise = state.exercises[state.currentExerciseIndex]
                val isLastQuestion = state.currentExerciseIndex == state.exercises.size - 1

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = {
                                if (state.exercises.isNotEmpty()) (state.currentExerciseIndex + 1).toFloat() / state.exercises.size
                                else 0f
                            },
                            modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = MoradoActivo,
                            trackColor = MoradoActivo.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("${state.currentExerciseIndex + 1}/${state.exercises.size}", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(text = currentExercise.question, fontSize = 18.sp, lineHeight = 26.sp, color = TextoBase)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    currentExercise.options.forEachIndexed { index, option ->
                        val isSelected = selectedOption == index
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).selectable(
                                selected = isSelected,
                                onClick = {
                                    if (!answerSubmitted) {
                                        selectedOption = index
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MoradoActivo.copy(alpha = 0.05f) else Color.White
                            ),
                            border = if (isSelected) BorderStroke(2.dp, MoradoActivo) else null
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        if (!answerSubmitted) {
                                            selectedOption = index
                                        }
                                    },
                                    enabled = !answerSubmitted
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option, fontSize = 16.sp, color = TextoBase)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!answerSubmitted) {
                        Button(
                            onClick = {
                                selectedOption?.let {
                                    val isCorrect = it == currentExercise.correctAnswerIndex
                                    lastAnswerWasCorrect = isCorrect
                                    viewModel.submitAnswer(it, studentId)
                                    answerSubmitted = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = selectedOption != null,
                            colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🔍 Verificar Respuesta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } else if (lastAnswerWasCorrect) {
                        Button(
                            onClick = { viewModel.goToNextExercise(studentId) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isLastQuestion) "🎉 Finalizar Práctica" else "➡️ Siguiente Ejercicio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                answerSubmitted = false
                                selectedOption = null
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🔄 Reintentar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            FloatingTutor(
                message = state.tutorMessage,
                isVisible = state.isTutorVisible,
                isLoading = state.isTutorLoading,
                onHelpRequested = {
                    if (state.exercises.isNotEmpty()) {
                        coroutineScope.launch {
                            val currentExercise = state.exercises[state.currentExerciseIndex]
                            val optionsJoined = currentExercise.options.joinToString("|")
                            val correctAnswerStr = currentExercise.options[currentExercise.correctAnswerIndex]

                            val encodedQuestion = URLEncoder.encode(currentExercise.question, StandardCharsets.UTF_8.toString())
                            val encodedOptions = URLEncoder.encode(optionsJoined, StandardCharsets.UTF_8.toString())
                            val encodedAnswer = URLEncoder.encode(correctAnswerStr, StandardCharsets.UTF_8.toString())

                            Log.d("DEBUG_TUTOR", "Subtema enviado al Tutor: ${currentExercise.subtema}")

                            // Navegación en modo PRACTICE con todos los datos contextuales
                            navController.navigate(
                                Screen.TutorHelp.createRoute(
                                    mode = "PRACTICE",
                                    studentName = studentName, 
                                    moduleName = topicTitle,
                                    topicName = currentExercise.subtema,
                                    questionStatus = "${state.currentExerciseIndex + 1} de ${state.exercises.size}",
                                    exerciseId = currentExercise.id,
                                    exerciseQuestion = encodedQuestion,
                                    exerciseOptions = encodedOptions,
                                    correctAnswer = encodedAnswer
                                )
                            )
                        }
                    }
                },
                onDismiss = { viewModel.hideTutor() }
            )
        }
    }
}
