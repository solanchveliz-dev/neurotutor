package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R

@Composable
fun AdditionSameDenominatorStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    // Paleta de colores oficial de NeuroTutor
    val actionButtonColor = Color(0xFF6366F1)
    val titleColor = Color(0xFF1E293B)
    val textColor = Color(0xFF475569)
    val secondaryText = Color(0xFF64748B)
    val resultRed = Color(0xFFEF4444)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aumentamos el espacio superior para dar aire respecto al progress bar
        Spacer(modifier = Modifier.height(32.dp))

        // 1. Título Principal - Con máxima presencia visual
        Text(
            text = "2. Suma de Fracciones\n(Igual Denominador) 🍰",
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Black,
            color = titleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Texto Explicativo - Fuera de card para mejor jerarquía
        Text(
            text = "Cuando las fracciones tienen el mismo número abajo, ¡es el nivel más fácil! Solo tienes que sumar los números de arriba y dejar el número de abajo exactamente igual.",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Operación Matemática - Card destacada y grande
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FractionMathView("2", "5", titleColor, fontSize = 32.sp, lineSize = 36.dp)
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                FractionMathView("1", "5", titleColor, fontSize = 32.sp, lineSize = 36.dp)
                Text(
                    text = "=",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                FractionMathView("3", "5", resultRed, fontSize = 32.sp, lineSize = 36.dp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Representación Gráfica - Fila de barras con protagonismo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fila 1: 2/5 + barra
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.width(36.dp)) // Alineación con signos
                    FractionMathView("2", "5", titleColor, fontSize = 20.sp, lineSize = 24.dp)
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.bar_2_5),
                        contentDescription = null,
                        modifier = Modifier.height(65.dp).weight(1f),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fila 2: + 1/5 + barra
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "+",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                    FractionMathView("1", "5", titleColor, fontSize = 20.sp, lineSize = 24.dp)
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.bar_1_5),
                        contentDescription = null,
                        modifier = Modifier.height(65.dp).weight(1f),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Línea separadora suave
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE2E8F0))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fila 3: = 3/5 + barra resultado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "=",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                    FractionMathView("3", "5", resultRed, fontSize = 20.sp, lineSize = 24.dp)
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.bar_3_5),
                        contentDescription = null,
                        modifier = Modifier.height(65.dp).weight(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 5. Botones de Navegación - Consistentes con el sistema
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = secondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Anterior",
                        color = secondaryText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Siguiente",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FractionMathView(
    numerator: String,
    denominator: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    lineSize: androidx.compose.ui.unit.Dp = 28.dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = numerator,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Box(
            modifier = Modifier
                .width(lineSize)
                .height(2.5.dp)
                .background(color)
        )
        Text(
            text = denominator,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
