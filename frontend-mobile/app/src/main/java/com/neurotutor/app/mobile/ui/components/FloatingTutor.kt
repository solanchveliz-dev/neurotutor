package com.neurotutor.app.mobile.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.theme.NeuroPurple

@Composable
fun FloatingTutor(
    message: String,
    isVisible: Boolean,
    isLoading: Boolean = false,
    onHelpRequested: () -> Unit,
    onDismiss: () -> Unit
) {
    // Necesitamos un Box para usar align
    Box(modifier = Modifier.fillMaxSize()) {
        // Burbuja flotante
        AnimatedVisibility(
            visible = isVisible && message.isNotEmpty(),
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .width(280.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(NeuroPurple.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🧠", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("🧠 Tutor IA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                Text("Nivel básico", fontSize = 10.sp, color = Color(0xFFB45309))
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFF92400E),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mensaje
                    if (isLoading) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NeuroPurple, strokeWidth = 2.dp)
                        }
                    } else {
                        Text(text = message, fontSize = 12.sp, lineHeight = 16.sp, color = Color(0xFF78350F))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón de ayuda
                    Button(
                        onClick = onHelpRequested,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("📚 ¡Sí, ayúdame!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Icono minimizado (usa Info en lugar de SmartToy)
        AnimatedVisibility(
            visible = !isVisible || message.isEmpty(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(NeuroPurple)
                    .shadow(4.dp, CircleShape)
                    .clickable { onHelpRequested() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Tutor IA",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}