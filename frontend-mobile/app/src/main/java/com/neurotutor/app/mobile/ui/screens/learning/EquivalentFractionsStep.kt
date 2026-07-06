package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun EquivalentFractionsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    // Paleta de colores consistente con el Nivel Básico
    val indigoVibrant = Color(0xFF4F46E5)
    val actionButtonColor = Color(0xFF6366F1)
    val titleColor = Color(0xFF1E293B)
    val textColor = Color(0xFF475569)
    val cardBg = Color(0xFFF8FAFF) // Mismo fondo de las cards principales del nivel básico
    val secondaryText = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Título principal (Mismo estilo que FractionIntroductionStep)
        Text(
            text = "1. Fracciones Equivalentes 👭",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = titleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Texto explicativo
        Text(
            text = "Las fracciones equivalentes son aquellas que parecen diferentes porque tienen números distintos, ¡pero representan la misma cantidad del entero!",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Card educativa "Truco Secreto" (Mismo estilo que las cards informativas del nivel básico)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💡 ¡Truco secreto!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = indigoVibrant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Puedes encontrar fracciones equivalentes multiplicando o dividiendo el número de arriba y el de abajo por el mismo número.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Bloque Ilustrativo: Pizzas + Fracciones (Renderizado con Compose)
        // Se aumenta el tamaño visual del bloque (aprox 20% más alto que los 240dp anteriores)
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de las pizzas
            Image(
                painter = painterResource(id = R.drawable.pizza3),
                contentDescription = "Pizzas equivalentes",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp), // Aumento visual para que se vea más grande y claro
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fila de fracciones alineadas perfectamente bajo cada pizza
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fracción 1/2
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "1", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    Box(modifier = Modifier.width(28.dp).height(2.5.dp).background(titleColor))
                    Text(text = "2", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }

                // Fracción 2/4
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "2", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    Box(modifier = Modifier.width(28.dp).height(2.5.dp).background(titleColor))
                    Text(text = "4", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }

                // Fracción 4/8
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "4", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    Box(modifier = Modifier.width(28.dp).height(2.5.dp).background(titleColor))
                    Text(text = "8", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = titleColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Card de conclusión (Estilo de mensajes de aprendizaje del nivel básico)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFDFDFD), RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "¡Mira! Aunque los cortes son diferentes, ¡en todas tienes exactamente la mitad de la pizza! 🍕",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 6. Botones Anterior / Siguiente (Patrón idéntico a las demás pantallas)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Anterior
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

            // Botón Siguiente
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
