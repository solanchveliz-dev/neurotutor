package com.neurotutor.app.mobile.ui.screens.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.theme.NeuroWhite

/**
 * Header del perfil rediseñado para consistencia absoluta con LevelSectionsScreen.
 */
@Composable
fun ProfileHeader(
    name: String,
    level: String,
    avatarUrl: String?,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onAvatarClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = NeuroWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = "Mi Perfil",
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeuroWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = NeuroWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ProfileAvatar(
            name = name,
            avatarUrl = avatarUrl,
            size = 96.dp,
            onClick = onAvatarClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = NeuroWhite,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Explorador Matemático",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = NeuroWhite.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            color = NeuroWhite.copy(alpha = 0.2f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFEAF4FF)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Ruta recomendada por el diagnóstico",
                        modifier = Modifier.padding(9.dp),
                        tint = Color(0xFF007AFF)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nivel recomendado: $level",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeuroWhite
                )
            }
        }
    }
}
