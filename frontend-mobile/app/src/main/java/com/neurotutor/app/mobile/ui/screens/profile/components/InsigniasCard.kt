package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.auth.AchievementResponse

@Composable
fun InsigniasCard(
    insignias: List<AchievementResponse>,
    onSeeAll: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis últimas insignias",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                TextButton(onClick = onSeeAll) {
                    Text(
                        text = "Ver mis logros >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val config = listOf(
                    InsigniaConfig("Fracciones Básico", R.drawable.achievement_basic, "BASIC"),
                    InsigniaConfig("Fracciones Intermedio", R.drawable.achievement_intermediate, "INTERMEDIATE"),
                    InsigniaConfig("Fracciones Avanzado", R.drawable.achievement_advanced, "ADVANCED")
                )

                config.forEach { itemConfig ->
                    val achievement = insignias.find { it.category == itemConfig.category }
                    InsigniaItem(
                        modifier = Modifier.weight(1f),
                        title = itemConfig.name,
                        iconRes = itemConfig.iconRes,
                        isUnlocked = achievement?.unlockedAt != null
                    )
                }
            }
        }
    }
}

private data class InsigniaConfig(
    val name: String,
    val iconRes: Int,
    val category: String
)

@Composable
private fun InsigniaItem(
    modifier: Modifier = Modifier,
    title: String,
    iconRes: Int,
    isUnlocked: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF8FAFC))
                .alpha(if (isUnlocked) 1f else 0.4f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) Color(0xFF334155) else Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            color = if (isUnlocked) Color(0xFFECFDF5) else Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (isUnlocked) "Obtenida" else "Bloqueada",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF94A3B8)
            )
        }
    }
}
