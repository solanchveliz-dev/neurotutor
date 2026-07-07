package com.neurotutor.app.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.theme.MoradoActivo

@Composable
fun DashboardBottomBar(
    selectedTab: String,
    onTabClick: (String) -> Unit,
    onNeoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.97f)
                .height(72.dp)
                .padding(bottom = 6.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    icon = Icons.Default.Home,
                    label = "Inicio",
                    selected = selectedTab == "inicio",
                    modifier = Modifier.weight(1f)
                ) { onTabClick("inicio") }

                NavItem(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = "Módulos",
                    selected = selectedTab == "modulos",
                    modifier = Modifier.weight(1f)
                ) { onTabClick("modulos") }

                Spacer(modifier = Modifier.weight(1.3f))

                NavItem(
                    icon = Icons.Default.EmojiEvents,
                    label = "Logros",
                    selected = selectedTab == "logros",
                    modifier = Modifier.weight(1f)
                ) { onTabClick("logros") }

                NavItem(
                    icon = Icons.Default.Person,
                    label = "Perfil",
                    selected = selectedTab == "perfil",
                    modifier = Modifier.weight(1f)
                ) { onTabClick("perfil") }
            }
        }

        // Botón Neo AI Flotante con Glow
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-15).dp)
                .size(80.dp)
                .clickable { onNeoClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF60A5FA).copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.neo_head),
                contentDescription = "Neo AI",
                modifier = Modifier.size(68.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MoradoActivo else Color(0xFF94A3B8),
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (selected) MoradoActivo else Color(0xFF94A3B8),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
