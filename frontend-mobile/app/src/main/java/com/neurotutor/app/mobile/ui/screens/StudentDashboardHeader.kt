package com.neurotutor.app.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // <-- IMPORTACIÓN CORRECTA PARA COMPOSE
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.theme.FondoPanelEstudiante
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@Composable
fun StudentDashboardHeader(
    nombreEstudiante: String,
    gradoSeccion: String,    // Criterio 1: Ej. "6to B"
    nivelActual: String,     // Criterio 1: "Básico", "Intermedio" o "Avanzado"
    puntosTotales: Int       // Criterio 1: Puntos acumulados totales
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoPanelEstudiante)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- BLOQUE 1: Datos de Identificación Escolar ---
            Column {
                Text(
                    text = nombreEstudiante,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoBase
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Grado y Sección: $gradoSeccion de Primaria",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextoBase.copy(alpha = 0.6f)
                )
            }

            // --- BLOQUE 2: Estatus Académico y Gamificación ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Etiqueta/Badge del nivel asignado
                Box(
                    modifier = Modifier
                        .background(MoradoActivo.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
                        .border(1.5.dp, MoradoActivo, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Nivel: $nivelActual",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MoradoActivo
                    )
                }

                // Marcador de Puntos del Alumno CORREGIDO
                Column(horizontalAlignment = Alignment.End) { // <-- Lógica corregida aquí
                    Text(
                        text = "$puntosTotales pts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoActivo
                    )
                    Text(
                        text = "acumulados",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoBase.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}