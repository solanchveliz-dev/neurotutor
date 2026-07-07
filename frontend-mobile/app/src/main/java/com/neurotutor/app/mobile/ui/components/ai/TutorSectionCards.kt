package com.neurotutor.app.mobile.ui.components.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ValidationCard(text: String, modifier: Modifier = Modifier) {
    TutorSectionCard(
        title = "Lo que ya comprendiste",
        text = text,
        icon = Icons.Default.CheckCircle,
        accent = Color(0xFF15803D),
        background = Color(0xFFF0FDF4),
        modifier = modifier
    )
}

@Composable
fun HintCard(text: String, modifier: Modifier = Modifier) {
    TutorSectionCard(
        title = "Pista",
        text = text,
        icon = Icons.Default.Lightbulb,
        accent = Color(0xFFB45309),
        background = Color(0xFFFFFBEB),
        modifier = modifier
    )
}

@Composable
fun SocraticQuestionCard(text: String, modifier: Modifier = Modifier) {
    TutorSectionCard(
        title = "Piensa y responde",
        text = text,
        icon = Icons.Default.Psychology,
        accent = Color(0xFF6D28D9),
        background = Color(0xFFF5F3FF),
        modifier = modifier
    )
}

@Composable
private fun TutorSectionCard(
    title: String,
    text: String,
    icon: ImageVector,
    accent: Color,
    background: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(top = 6.dp),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}
