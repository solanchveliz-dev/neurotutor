package com.neurotutor.app.mobile.ui.screens.profile.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.screens.dashboard.DashboardBadgeUiModel

@Composable
fun InsigniasCard(
    insignias: List<DashboardBadgeUiModel>,
    onSeeAll: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis últimas insignias",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                TextButton(
                    onClick = onSeeAll,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Ver mis logros >",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
            }

            Text(
                text = "Supera los exámenes de cada nivel para obtenerlas.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val latestBadges = insignias.take(3)
            if (latestBadges.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    latestBadges.forEach { badge ->
                        ProfileBadgeItem(
                            badge = badge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileBadgeItem(
    badge: DashboardBadgeUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = badge.badgeRes),
            contentDescription = "${badge.topic} ${badge.level}",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = badge.topic,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = badge.level,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6366F1),
            textAlign = TextAlign.Center
        )
    }
}
