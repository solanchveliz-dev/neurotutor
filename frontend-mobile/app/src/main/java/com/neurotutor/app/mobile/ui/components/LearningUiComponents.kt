package com.neurotutor.app.mobile.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.theme.*

/**
 * NeuroTutor Premium Design System - Reusable Learning & Assessment Components
 * Shared between Diagnostic Test and Final Exam
 */

@Composable
fun LearningProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    label: String = "Progreso"
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
            )
            Text(
                text = "$currentStep de $totalSteps",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = NeuroPurple
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0))
        ) {
            val progress = if (totalSteps > 0) currentStep.toFloat() / totalSteps else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(NeuroPurple, NeuroBlue)
                        )
                    )
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun LearningQuestionCard(
    modifier: Modifier = Modifier,
    tag: String = "PREGUNTA",
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = tag,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = NeuroPurple.copy(alpha = 0.6f),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun LearningOptionCard(
    index: Int,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val transition = updateTransition(targetState = isSelected, label = "option")
    val elevation by transition.animateDp(label = "elevation") { selected -> if (selected) 6.dp else 0.dp }
    val scaleValue by transition.animateFloat(label = "scale") { selected -> if (selected) 1.02f else 1f }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .scale(scaleValue)
            .selectable(selected = isSelected, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeuroPurple.copy(alpha = 0.06f) else Color.White
        ),
        border = if (isSelected) BorderStroke(1.5.dp, NeuroPurple) else BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) NeuroPurple else Color(0xFFF8FAFC),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = ('A' + index).toString(),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = if (isSelected) Color.White else NeuroPurple
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = text,
                color = if (isSelected) NeuroPurple else TextoBase,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LearningActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = NeuroPurple,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(60.dp)
            .shadow(if (enabled && containerColor != Color.White) 12.dp else 0.dp, RoundedCornerShape(24.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = Color(0xFFE2E8F0),
            disabledContentColor = Color(0xFF94A3B8)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun LearningStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 12.sp, color = TextGray)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextoBase)
        }
    }
}

@Composable
fun LearningRewardItem(
    iconRes: Int? = null,
    iconVector: ImageVector? = null,
    label: String,
    iconTint: Color
) {
    Surface(
        color = iconTint.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Black,
                color = TextoBase,
                fontSize = 15.sp
            )
        }
    }
}
