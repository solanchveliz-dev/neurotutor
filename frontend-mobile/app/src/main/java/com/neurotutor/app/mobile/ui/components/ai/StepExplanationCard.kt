package com.neurotutor.app.mobile.ui.components.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.data.model.ai.StepExplanation

@Composable
fun StepExplanationCard(
    explanation: StepExplanation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            explanation.title?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            explanation.introduction?.let {
                Text(text = it, fontSize = 16.sp, lineHeight = 22.sp, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(10.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                explanation.steps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF7C3AED), CircleShape),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = step,
                            modifier = Modifier.padding(start = 10.dp, top = 3.dp),
                            color = Color(0xFF334155),
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
            explanation.conclusion?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = it,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E3A8A)
                )
            }
        }
    }
}
