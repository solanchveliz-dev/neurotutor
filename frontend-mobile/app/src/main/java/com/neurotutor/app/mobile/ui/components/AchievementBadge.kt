package com.neurotutor.app.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.screens.achievements.MilestoneUiModel

/**
 * Componente de Insignia (Hito Académico) rediseñado como ranura de álbum.
 * Soporta modo compacto (Dashboard) y modo álbum completo.
 */
@Composable
fun AchievementBadge(
    milestone: MilestoneUiModel,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val grayscaleMatrix = remember { ColorMatrix().apply { setToSaturation(0f) } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(if (showLabel) 95.dp else 70.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // 🎨 RANURA DEL ÁLBUM
            Surface(
                modifier = Modifier.size(if (showLabel) 72.dp else 60.dp),
                shape = CircleShape,
                color = if (milestone.isUnlocked) Color.White else Color(0xFFF1F5F9),
                shadowElevation = if (milestone.isUnlocked) 4.dp else 0.dp,
                border = if (!milestone.isUnlocked) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = milestone.badgeRes),
                        contentDescription = milestone.name,
                        modifier = Modifier.size(if (showLabel) 58.dp else 50.dp),
                        colorFilter = if (milestone.isUnlocked) null else ColorFilter.colorMatrix(grayscaleMatrix),
                        alpha = if (milestone.isUnlocked) 1f else 0.4f
                    )
                }
            }

            if (!milestone.isUnlocked && showLabel) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp).align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.White,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
        
        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = milestone.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (milestone.isUnlocked) Color(0xFF1E293B) else Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2
            )

            if (milestone.isUnlocked && milestone.unlockedDate != null) {
                Text(
                    text = milestone.unlockedDate,
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(top = 2.dp)
                )
            } else if (!milestone.isUnlocked) {
                Text(
                    text = "Bloqueada",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
