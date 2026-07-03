package com.neurotutor.app.mobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.ui.theme.MoradoActivo
import com.neurotutor.app.mobile.ui.theme.TextoBase

@Composable
fun StudentDashboardHeader(
    nombreEstudiante: String,
    gradoSeccion: String,
    nivelActual: String,
    puntosTotales: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, $nombreEstudiante!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextoBase
                    )
                    Text(
                        text = "$gradoSeccion de Primaria",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = MoradoActivo.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⭐ $puntosTotales pts",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        color = MoradoActivo,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Nivel recomendado:", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = nivelActual,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoradoActivo
                    )
                }
            }
        }
    }
}
