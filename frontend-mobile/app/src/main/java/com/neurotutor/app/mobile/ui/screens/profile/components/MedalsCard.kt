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
fun MedalsCard(
    medals: List<AchievementResponse>,
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
                    text = "Mis últimas medallas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                TextButton(onClick = onSeeAll) {
                    Text(
                        text = "Ver todas mis medallas >",
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
                    MedalConfig("Fracciones Básico", R.drawable.basic_medal, "BASIC"),
                    MedalConfig("Fracciones Intermedio", R.drawable.intermediate_medal, "INTERMEDIATE"),
                    MedalConfig("Fracciones Avanzado", R.drawable.advanced_medal, "ADVANCED")
                )

                config.forEach { medalConfig ->
                    val achievement = medals.find { it.category == medalConfig.category }
                    MedalItem(
                        modifier = Modifier.weight(1f),
                        title = medalConfig.name,
                        iconRes = medalConfig.iconRes,
                        isUnlocked = achievement?.unlockedAt != null
                    )
                }
            }
        }
    }
}

private data class MedalConfig(
    val name: String,
    val iconRes: Int,
    val category: String
)

@Composable
private fun MedalItem(
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
