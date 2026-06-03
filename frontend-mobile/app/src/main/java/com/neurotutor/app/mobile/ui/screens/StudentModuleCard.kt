package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importamos tus dos modelos limpios desde su paquete separado
import com.neurotutor.app.mobile.ui.models.ModuleItem
import com.neurotutor.app.mobile.ui.models.ModuleStatus
// Tus colores oficiales de la app
import com.neurotutor.app.mobile.ui.theme.CardBackground
import com.neurotutor.app.mobile.ui.theme.GrisBloqueado
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase
import com.neurotutor.app.mobile.ui.theme.VerdeProgreso

@Composable
fun StudentModuleCard(
    modulo: ModuleItem,
    onModuloClick: (ModuleItem) -> Unit
) {
    val isBloqueado = modulo.estado == ModuleStatus.BLOQUEADO
    val isEnCurso = modulo.estado == ModuleStatus.EN_CURSO

    // HU-11 / CA-4: El borde se resalta con MoradoActivo solo si el tema está en curso
    val borderColor = when {
        isEnCurso -> MoradoActivo
        isBloqueado -> GrisBloqueado
        else -> Color.Transparent
    }

    // HU-11 / CA-2: Si está bloqueado, congelamos el evento de clic e inhabilitamos la tarjeta
    val cardModifier = Modifier
        .fillMaxWidth()
        .height(105.dp)
        .border(
            width = if (isEnCurso) 2.5.dp else 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(16.dp)
        )
        .clip(RoundedCornerShape(16.dp))
        .background(if (isBloqueado) GrisBloqueado.copy(alpha = 0.15f) else CardBackground)
        .then(
            if (!isBloqueado) {
                Modifier.clickable { onModuloClick(modulo) }
            } else {
                Modifier // No se inyecta el .clickable {}, por ende el clic no hace absolutamente nada
            }
        )
        .padding(16.dp)

    Row(
        modifier = cardModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // --- COLUMNA DE CONTENIDO (Título + Progreso) ---
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = modulo.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBloqueado) TextoBase.copy(alpha = 0.4f) else TextoBase
            )

            Spacer(modifier = Modifier.height(10.dp))

            // HU-11 / CA-3: Barra e indicador numérico solo si está desbloqueado
            if (!isBloqueado) {
                val progresoFlotante = if (modulo.ejerciciosTotales > 0) {
                    modulo.ejerciciosCompletados.toFloat() / modulo.ejerciciosTotales.toFloat()
                } else 0f

                val colorBarra = if (progresoFlotante == 1f) VerdeProgreso else MoradoActivo

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LinearProgressIndicator(
                        progress = progresoFlotante,
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = colorBarra,
                        trackColor = colorBarra.copy(alpha = 0.15f)
                    )

                    // Formato string exacto exigido por el criterio de aceptación
                    Text(
                        text = "${modulo.ejerciciosCompletados}/${modulo.ejerciciosTotales} ejercicios",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoBase.copy(alpha = 0.6f)
                    )
                }
            } else {
                Text(
                    text = "Módulo bloqueado",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextoBase.copy(alpha = 0.35f)
                )
            }
        }

        // --- ICONO DE CANDADO CONDICIONAL (HU-11 / CA-2) ---
        if (isBloqueado) {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Candado de bloqueo",
                tint = TextoBase.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}