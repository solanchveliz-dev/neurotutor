package com.neurotutor.app.mobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState // 🚀 IMPORTACIÓN FALTANTE
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import com.neurotutor.app.mobile.ui.viewmodels.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePlayerScreen(
    studentId: String,
    moduleId: String,
    level: String,
    viewModel: ExerciseViewModel,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    // 🚀 Corregido: Ahora se puede leer el estado correctamente
    val state by viewModel.uiState.collectAsState()
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(moduleId) {
        viewModel.loadExercises(moduleId)
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onFinish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        val levelTitle = when(level) {
                            "B" -> "Básico 🌱"
                            "I" -> "Intermedio 🔥"
                            "A" -> "Avanzado 🚀"
                            else -> level
                        }
                        Text("Práctica: $levelTitle", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Resuelve para ganar puntos", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra de Progreso
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

                    Spacer(modifier = Modifier.height(32.dp))

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
                                onClick = { selectedOption = index }
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MoradoActivo.copy(alpha = 0.05f) else Color.White
                            ),
                            border = if (isSelected) BorderStroke(2.dp, MoradoActivo) else null
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = isSelected, onClick = { selectedOption = index })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option, fontSize = 16.sp, color = TextoBase)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AnimatedVisibility(visible = state.isTutorVisible) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Tutor IA dice:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF92400E))
                                    Text(state.tutorMessage, fontSize = 14.sp, color = Color(0xFF92400E))
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { 
                            selectedOption?.let { 
                                viewModel.submitAnswer(it, studentId)
                                selectedOption = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoActivo),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verificar Respuesta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
