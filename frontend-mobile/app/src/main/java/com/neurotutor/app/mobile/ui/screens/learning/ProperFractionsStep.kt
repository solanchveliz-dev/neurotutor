package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R

@Composable
fun ProperFractionsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val actionButtonColor = Color(0xFF6366F1)
    val mainAppColor = Color(0xFF4F46E5)
    val textColor = Color(0xFF475569)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "Fracciones propias ⭐",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card informativo superior
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Color crema/amarillo muy claro
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Las fracciones propias son aquellas donde el numerador es ")
                        withStyle(style = SpanStyle(color = mainAppColor, fontWeight = FontWeight.Black)) {
                            append("MENOR")
                        }
                        append(" que el denominador.")
                    },
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Esto significa que tenemos menos de una unidad completa. 🍕",
            fontSize = 15.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ejemplos:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ejemplo 1: 2/5
        ProperFractionExampleCard(
            index = 1,
            fractionText = "2",
            denominatorText = "5",
            fractionName = "(Dos quintos)",
            imageRes = R.drawable.circle_2_5,
            description = buildAnnotatedString {
                append("La pizza está dividida en ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = mainAppColor)) { append("5") }
                append(" partes iguales y hemos tomado ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = mainAppColor)) { append("2") }
                append(".")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo 2: 3/8
        ProperFractionExampleCard(
            index = 2,
            fractionText = "3",
            denominatorText = "8",
            fractionName = "(Tres octavos)",
            imageRes = R.drawable.circle_3_8,
            description = buildAnnotatedString {
                append("La pizza está dividida en ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = mainAppColor)) { append("8") }
                append(" partes iguales y hemos tomado ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = mainAppColor)) { append("3") }
                append(".")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo 3: 4/7
        ProperFractionExampleCard(
            index = 3,
            fractionText = "4",
            denominatorText = "7",
            fractionName = "(Cuatro séptimos)",
            imageRes = R.drawable.circle_4_7,
            description = buildAnnotatedString {
                append("La pizza está dividida en ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = mainAppColor)) { append("7") }
                append(" partes iguales y hemos tomado ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = mainAppColor)) { append("4") }
                append(".")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección inferior: Neo Explain 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.neo_explain2),
                contentDescription = "Neo explica",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF8FAFF), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("¡Así se ven las fracciones propias! Siempre tenemos ")
                        withStyle(style = SpanStyle(color = mainAppColor, fontWeight = FontWeight.Bold)) {
                            append("menos de una unidad completa")
                        }
                        append(". 😄")
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de Navegación
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Anterior", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f).height(54.dp),
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
                        Icons.AutoMirrored.Filled.ArrowForward,
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
fun ProperFractionExampleCard(
    index: Int,
    fractionText: String,
    denominatorText: String,
    fractionName: String,
    imageRes: Int,
    description: androidx.compose.ui.text.AnnotatedString
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con número de ejemplo
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF6366F1), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = index.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Fracción Visual
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = fractionText, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                HorizontalDivider(modifier = Modifier.width(20.dp), thickness = 2.dp, color = Color(0xFF1E293B))
                Text(text = denominatorText, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = fractionName, fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Imagen del círculo fraccionario
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Descripción y etiqueta
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Menos de una unidad",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
            }
        }
    }
}
