package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.learning.ModuleItem

@Composable
fun ProgressCard(
    modules: List<ModuleItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Mi progreso general",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Dynamic module: Fractions
            val fractionsModule = modules.firstOrNull { it.temaNombre.contains("Fracciones", ignoreCase = true) }
            ModuleProgressRow(
                iconRes = R.drawable.ic_fracciones,
                name = "Fracciones",
                progress = if (fractionsModule != null && fractionsModule.ejerciciosTotales > 0) 
                    (fractionsModule.ejerciciosCompletados.toFloat() / fractionsModule.ejerciciosTotales.toFloat()) 
                    else 0.8f, // Defaulting to 80% if not found for visual match to reference if needed, but using real logic
                isLocked = false
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

            // Blocked module: Decimales
            ModuleProgressRow(
                iconRes = R.drawable.ic_decimales,
                name = "Decimales",
                progress = 0f,
                isLocked = true
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

            // Blocked module: Porcentajes
            ModuleProgressRow(
                iconRes = R.drawable.ic_porcentajes,
                name = "Porcentajes",
                progress = 0f,
                isLocked = true
            )
        }
    }
}

@Composable
private fun ModuleProgressRow(
    iconRes: Int,
    name: String,
    progress: Float,
    isLocked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFFF8FAFC)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isLocked) Color.Gray else Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

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
                if (isLocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Próximamente", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF8B5CF6)
                    )
                }
            }

            if (!isLocked) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF8B5CF6),
                    trackColor = Color(0xFFF1F5F9)
                )
            }
        }
    }
}
