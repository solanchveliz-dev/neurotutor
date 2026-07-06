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
fun ImproperFractionsStep(
    onFinish: () -> Unit,
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
            text = "Fracciones impropias 🚀",
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Color crema/amarillo claro
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Las fracciones impropias son aquellas donde el numerador es ")
                        withStyle(style = SpanStyle(color = mainAppColor, fontWeight = FontWeight.Black)) {
                            append("MAYOR o IGUAL")
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
            text = "Esto significa que tenemos más de una unidad completa. 🤩",
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

        // Ejemplo 1: 5/4
        ImproperFractionExampleCard(
            index = 1,
            numerator = "5",
            denominator = "4",
            fractionName = "(Cinco cuartos)",
            imageRes = R.drawable.circle_1_1cuarto,
            explanation = "Tenemos un entero completo y un cuarto adicional.",
            mixedNumber = "1 1/4",
            mixedName = "(Un entero y un cuarto)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo 2: 7/3
        ImproperFractionExampleCard(
            index = 2,
            numerator = "7",
            denominator = "3",
            fractionName = "(Siete tercios)",
            imageRes = R.drawable.circle_2_1tercio,
            explanation = "Tenemos dos enteros completos y un tercio adicional.",
            mixedNumber = "2 1/3",
            mixedName = "(Dos enteros y un tercio)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo 3: 9/8
        ImproperFractionExampleCard(
            index = 3,
            numerator = "9",
            denominator = "8",
            fractionName = "(Nueve octavos)",
            imageRes = R.drawable.circle_1_octavo,
            explanation = "Tenemos un entero completo y un octavo adicional.",
            mixedNumber = "1 1/8",
            mixedName = "(Un entero y un octavo)"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección inferior: Neo Explain
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.neo_explain),
                contentDescription = "Neo explica",
                modifier = Modifier.size(110.dp)
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
                        append("¡Muy bien! 🎉 Cuando el numerador es mayor que el denominador, tenemos ")
                        withStyle(style = SpanStyle(color = mainAppColor, fontWeight = FontWeight.Bold)) {
                            append("más de una unidad completa")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Anterior", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "¡Empezar desafío!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "🎮", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ImproperFractionExampleCard(
    index: Int,
    numerator: String,
    denominator: String,
    fractionName: String,
    imageRes: Int,
    explanation: String,
    mixedNumber: String,
    mixedName: String
) {
    val mainAppColor = Color(0xFF4F46E5)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Círculo con número
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(mainAppColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = index.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                
                // Fracción
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = numerator, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    HorizontalDivider(modifier = Modifier.width(16.dp), thickness = 2.dp, color = Color(0xFF1E293B))
                    Text(text = denominator, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = fractionName, fontSize = 11.sp, color = Color(0xFF64748B))

                Spacer(modifier = Modifier.weight(1f))

                // Resultado visual (Número mixto)
                Surface(
                    color = Color(0xFFF8FAFF),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEF2FF))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "= ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text(text = mixedNumber.split(" ")[0], fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.width(4.dp))
                            val frac = mixedNumber.split(" ")[1].split("/")
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = frac[0], fontSize = 14.sp, fontWeight = FontWeight.Bold, color = mainAppColor)
                                HorizontalDivider(modifier = Modifier.width(10.dp), thickness = 1.5.dp, color = mainAppColor)
                                Text(text = frac[1], fontSize = 14.sp, fontWeight = FontWeight.Bold, color = mainAppColor)
                            }
                        }
                        Text(text = mixedName, fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen y Explicación
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.height(80.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = explanation,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF475569),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
