package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R

@Composable
fun WelcomeTheoryStep(
    level: String, // "B", "I", "A"
    title: String,
    description: String,
    imageRes: Int?,
    onStartLesson: () -> Unit
) {
    val indigoVibrant = Color(0xFF4F46E5)
    val startButtonColor = Color(0xFFFBB217) // Amarillo vibrante

    val finalImageId = imageRes ?: R.drawable.neo_theory

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- SECCIÓN SUPERIOR: GLOBOS + NEO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        ) {
            // Globos de Diálogo (Lado izquierdo)
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .padding(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Globo 1: Título
                Surface(
                    shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = title,
                        color = indigoVibrant,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 22.sp
                    )
                }

                // Globo 2: Descripción
                Surface(
                    shape = RoundedCornerShape(24.dp, 24.dp, 4.dp, 24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = description,
                        color = Color(0xFF334155),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // NEO
            Image(
                painter = painterResource(id = finalImageId),
                contentDescription = "Mascota Neo",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(210.dp)
                    .offset(x = 15.dp, y = 35.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- SECCIÓN INFERIOR: TARJETA BLANCA FLOTANTE ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Qué aprenderemos?",
                    color = indigoVibrant,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Aquí integramos condicionalmente los temas según el nivel
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (level) {
                        "B" -> {
                            IndexRowItem(R.drawable.pizza_icon, "Qué es una fracción")
                            IndexRowItem(R.drawable.fraction_icon, "Partes de una fracción")
                            IndexRowItem(R.drawable.icon_star, "Fracciones propias")
                            IndexRowItem(R.drawable.improper_icon, "Fracciones impropias")
                        }
                        "I" -> {
                            IndexRowItem(R.drawable.equivalent_fractions, "Fracciones equivalentes")
                            IndexRowItem(R.drawable.cake_slice, "Suma de fracciones (igual denominador)")
                            IndexRowItem(R.drawable.butterfly, "Suma de fracciones (diferente denominador)")
                            IndexRowItem(R.drawable.subtraction_minus, "Resta de fracciones")
                            IndexRowItem(R.drawable.target_bullseye, "Multiplicación de fracciones")
                        }
                        "A" -> {
                            IndexRowItem(R.drawable.icon_star, "Contenido Avanzado (Próximamente)")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de acción
                Button(
                    onClick = onStartLesson,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = startButtonColor),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Comenzar lección",
                            color = Color(0xFF1E293B),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF1E293B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IndexRowItem(iconResId: Int, title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8FAFF),
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = Color(0xFF1E293B),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}