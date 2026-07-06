package com.neurotutor.app.mobile.feature.groqexercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GroqExerciseRoute(
    studentId: Long,
    moduleId: Long,
    topic: String,
    modifier: Modifier = Modifier,
    viewModel: GroqExerciseViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId, moduleId, topic) {
        viewModel.loadExercise(studentId, moduleId, topic)
    }

    GroqExerciseScreen(
        state = state,
        onRetry = viewModel::retry,
        onOptionSelected = viewModel::selectOption,
        modifier = modifier
    )
}

@Composable
fun GroqExerciseScreen(
    state: GroqExerciseUiState,
    onRetry: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color(0xFFF8FAFC)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                GroqExerciseUiState.Idle -> OutlinedButton(onClick = onRetry) {
                    Text("Cargar ejercicio")
                }

                GroqExerciseUiState.Loading -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = Color(0xFF7C3AED))
                    Text("Preparando un ejercicio…")
                }

                is GroqExerciseUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = onRetry
                )

                is GroqExerciseUiState.Content -> ExerciseContent(
                    state = state,
                    onOptionSelected = onOptionSelected
                )
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    state: GroqExerciseUiState.Content,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Ejercicio generado por Neo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7C3AED)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = state.exercise.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(Modifier.height(18.dp))

            state.exercise.options.forEachIndexed { index, option ->
                ExerciseOptionButton(
                    text = option,
                    selected = state.selectedOptionIndex == index,
                    isCorrect = state.isCorrect,
                    enabled = state.selectedOptionIndex == null,
                    onClick = { onOptionSelected(index) }
                )
                Spacer(Modifier.height(10.dp))
            }

            state.isCorrect?.let { correct ->
                val feedback = if (correct) {
                    state.exercise.successMessage.ifBlank { "¡Respuesta correcta!" }
                } else {
                    state.exercise.hint.ifBlank { "Revisa la pregunta e inténtalo nuevamente." }
                }
                FeedbackMessage(isCorrect = correct, text = feedback)
            }
        }
    }
}

@Composable
private fun ExerciseOptionButton(
    text: String,
    selected: Boolean,
    isCorrect: Boolean?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val success = Color(0xFF15803D)
    val error = Color(0xFFB91C1C)
    val statusColor = when {
        !selected -> Color(0xFFCBD5E1)
        isCorrect == true -> success
        else -> error
    }
    val containerColor = when {
        !selected -> Color.White
        isCorrect == true -> Color(0xFFDCFCE7)
        else -> Color(0xFFFEE2E2)
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = when {
                    !selected -> "Opción: $text"
                    isCorrect == true -> "Opción correcta: $text"
                    else -> "Opción incorrecta: $text"
                }
            },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, statusColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color(0xFF1E293B),
            disabledContainerColor = containerColor,
            disabledContentColor = Color(0xFF1E293B)
        )
    ) {
        Text(text = text, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
        if (selected) {
            Icon(
                imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun FeedbackMessage(isCorrect: Boolean, text: String) {
    val color = if (isCorrect) Color(0xFF166534) else Color(0xFF991B1B)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = color
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp),
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = message.ifBlank { SAFE_EXERCISE_ERROR },
            textAlign = TextAlign.Center,
            color = Color(0xFF991B1B),
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Text("Reintentar", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
