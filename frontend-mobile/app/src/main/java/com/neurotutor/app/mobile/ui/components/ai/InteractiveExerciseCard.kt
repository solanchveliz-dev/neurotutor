package com.neurotutor.app.mobile.ui.components.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.data.model.ai.InteractiveExercise
import com.neurotutor.app.mobile.ui.screens.learning.InteractiveExerciseUiState

@Composable
fun InteractiveExerciseCard(
    exercise: InteractiveExercise,
    state: InteractiveExerciseUiState?,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Prueba con este ejercicio parecido",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7C3AED)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = exercise.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(12.dp))

            exercise.options.forEachIndexed { index, option ->
                val isSelected = state?.selectedOptionIndex == index
                val optionColor = when {
                    !isSelected -> Color.White
                    state.isCorrect -> Color(0xFFDCFCE7)
                    else -> Color(0xFFFEE2E2)
                }
                val borderColor = when {
                    !isSelected -> Color(0xFFCBD5E1)
                    state.isCorrect -> Color(0xFF16A34A)
                    else -> Color(0xFFDC2626)
                }
                Surface(
                    onClick = { onOptionSelected(index) },
                    enabled = state == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = optionColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = if (state.isCorrect) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.Cancel
                                },
                                contentDescription = if (state.isCorrect) {
                                    "Respuesta correcta"
                                } else {
                                    "Respuesta por revisar"
                                },
                                tint = borderColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            state?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (it.isCorrect) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Cancel
                        },
                        contentDescription = null,
                        tint = if (it.isCorrect) Color(0xFF15803D) else Color(0xFFB91C1C),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = it.feedback,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (it.isCorrect) Color(0xFF166534) else Color(0xFF991B1B)
                    )
                }
            }
        }
    }
}
