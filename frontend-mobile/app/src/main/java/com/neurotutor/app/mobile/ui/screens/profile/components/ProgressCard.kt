package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.domain.mapper.ThemeProgress

@Composable
fun ProgressCard(
    thematicProgress: List<ThemeProgress>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mi progreso por tema",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (thematicProgress.isEmpty()) {
                Text(
                    text = "Aún no tienes actividad registrada.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            thematicProgress.forEachIndexed { index, theme ->
                // 🎨 ASIGNACIÓN DINÁMICA DE COLORES E ICONOS
                val barColor = when {
                    theme.name.contains("Fracciones", true) -> Color(0xFF8B5CF6)
                    theme.name.contains("Decimales", true) -> Color(0xFF3B82F6)
                    else -> Color(0xFF10B981)
                }

                ModuleProgressRow(
                    iconRes = R.drawable.fraction_neo_chat,
                    name = theme.name,
                    progress = theme.progressPercentage / 100f,
                    color = barColor
                )
                
                if (index < thematicProgress.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp), 
                        color = Color(0xFFF1F5F9)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleProgressRow(
    iconRes: Int,
    name: String,
    progress: Float,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = Color(0xFFF1F5F9)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (progress > 0) color else Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = Color(0xFFF1F5F9)
            )
        }
    }
}
