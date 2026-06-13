package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import com.neurotutor.app.mobile.ui.viewmodels.FinalExamViewModel

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

    LaunchedEffect(moduleId, level) {
        viewModel.loadExam(moduleId, level)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Examen Final: $level", fontWeight = FontWeight.Bold) },
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
            } else if (state.isFinished) {
                ResultContent(
                    score = state.score,
                    isPassed = state.isPassed,
                    onFinish = onFinish
                )
            } else if (state.questions.isNotEmpty()) {
                val currentQuestion = state.questions[state.currentQuestionIndex]
                val selectedOption = state.answers[state.currentQuestionIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Pregunta ${state.currentQuestionIndex + 1} de ${state.questions.size}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = currentQuestion.question,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextoBase
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedOption == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .selectable(
                                    selected = isSelected,
                                    onClick = { viewModel.selectAnswer(index) }
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MoradoActivo.copy(alpha = 0.05f) else Color.White
                            ),
                            border = if (isSelected) BorderStroke(2.dp, MoradoActivo) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.selectAnswer(index) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option, fontSize = 16.sp, color = TextoBase)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { 
                            // 🚀 CORRECCIÓN: Pasamos los IDs para procesar el resultado en el Backend
                            viewModel.nextQuestion(studentId, moduleId) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (state.currentQuestionIndex < state.questions.size - 1) "Siguiente" else "Finalizar Examen",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultContent(score: Int, isPassed: Boolean, onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (isPassed) Color(0xFF22C55E) else Color(0xFFEF4444)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isPassed) "¡Felicidades! 🎉" else "Sigue intentando 💪",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoBase
        )
        
        Text(
            text = if (isPassed) "Has aprobado el nivel con éxito." else "No lograste alcanzar el puntaje mínimo.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = TextoBase.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Tu puntaje:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$score%",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = MoradoActivo
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Volver al Dashboard", fontWeight = FontWeight.Bold)
        }
    }
}
