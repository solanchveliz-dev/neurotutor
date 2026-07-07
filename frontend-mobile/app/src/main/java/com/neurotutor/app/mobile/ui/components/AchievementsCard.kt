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
    latestAcademicBadge: LearningBadgeUiModel? = null,
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

            if (achievements.isEmpty() && latestAcademicBadge == null) {
                Text(
                    text = "Completa actividades para obtener tu primer logro.",
                    modifier = Modifier.padding(vertical = 12.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            } else {
                val achievementsToShow =
                    if (latestAcademicBadge != null) achievements.take(1) else achievements.take(2)
                achievementsToShow.forEach { achievement ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                if (achievement.icon == "icon_trophy") {
                                    R.drawable.icon_trophy
                                } else {
                                    R.drawable.general_medal
                                }
                            ),
                            contentDescription = achievement.description,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (achievement.code in LEVEL_COMPLETION_CODES) {
                                achievement.title
                            } else {
                                achievement.description
                            },
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
                latestAcademicBadge?.let { badge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(badge.iconRes),
                            contentDescription = badge.name,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = badge.name,
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

private val LEVEL_COMPLETION_CODES = setOf(
    "BASIC_LEVEL_COMPLETED",
    "INTERMEDIATE_LEVEL_COMPLETED",
    "ADVANCED_LEVEL_COMPLETED"
)
