package com.neurotutor.app.mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.learning.ModuleItem
import com.neurotutor.app.mobile.data.model.learning.ModuleStatus
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@Composable
fun StudentModuleCard(
    modulo: ModuleItem,
    onModuloClick: (ModuleItem) -> Unit
) {
    val isBloqueado = modulo.estado == ModuleStatus.BLOQUEADO
    val isEnCurso = modulo.estado == ModuleStatus.EN_CURSO
    
    // 🎨 ASIGNACIÓN DE ICONOS SEGÚN EL TEMA (HU-20)
    val iconRes = when {
        modulo.titulo.contains("Fracciones", ignoreCase = true) -> R.drawable.imagen_pregunta4_frutas // Pizza/Frutas
        modulo.titulo.contains("Decimales", ignoreCase = true) -> R.drawable.imagen_pregunta5_sogas // Gráfico
        modulo.titulo.contains("Porcentajes", ignoreCase = true) -> R.drawable.imagen_pregunta10_jugonaranja // "100"
        else -> R.drawable.ic_launcher_foreground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !isBloqueado) { onModuloClick(modulo) }
            .border(
                width = 1.dp,
                color = if (isEnCurso) Color.Transparent else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnCurso) MoradoActivo else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isBloqueado) 0.dp else 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isBloqueado) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(20.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(55.dp).padding(bottom = 8.dp)
                )
                
                Text(
                    text = modulo.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnCurso) Color.White else TextoBase,
                    textAlign = TextAlign.Center
                )

                if (!isBloqueado) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val progreso = if (modulo.ejerciciosTotales > 0) 
                        modulo.ejerciciosCompletados.toFloat() / modulo.ejerciciosTotales.toFloat() 
                        else 0f
                    
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso",
                                fontSize = 11.sp,
                                color = if (isEnCurso) Color.White.copy(alpha = 0.8f) else Color.Gray
                            )
                            Text(
                                text = "${(progreso * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEnCurso) Color.White else MoradoActivo
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (isEnCurso) Color.White else MoradoActivo,
                            trackColor = if (isEnCurso) Color.White.copy(alpha = 0.2f) else Color(0xFFF1F5F9)
                        )
                    }
                }
            }
        }
    }
}
