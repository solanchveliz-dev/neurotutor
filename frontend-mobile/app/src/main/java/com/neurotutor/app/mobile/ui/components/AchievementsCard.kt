package com.neurotutor.app.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.auth.AchievementResponse
import com.neurotutor.app.mobile.ui.theme.MoradoActivo

@Composable
fun AchievementsCard(
    achievements: List<AchievementResponse>,
    onSeeAll: () -> Unit,
    showSeeAll: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimos logros",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                if (showSeeAll) {
                    TextButton(onClick = onSeeAll) {
                        Text("Ver todos >", color = MoradoActivo, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (achievements.isEmpty()) {
                Text(
                    text = "Completa actividades para obtener tu primer logro.",
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            } else {
                achievements.take(2).forEach { achievement ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.general_medal),
                            contentDescription = achievement.description,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = achievement.description,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }
        }
    }
}
