package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
fun FractionPartsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val actionButtonColor = Color(0xFF6366F1)
    val numeratorColor = Color(0xFF3B82F6) // Azul
    val denominatorColor = Color(0xFFEC4899) // Rosa/Fucsia
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
            text = "Las partes de una fracción 🏛️",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto introductorio
        Text(
            text = "Una fracción se escribe con un número arriba, una línea en medio y un número abajo. ¡Cada uno tiene un súper trabajo!",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Imagen Principal (3/8)
        Surface(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Transparent),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE2E8F0))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.fraction_3_8),
                    contentDescription = "Fracción 3/8",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card Educativo 1: Numerador
        PartCard(
            icon = Icons.Default.ArrowUpward,
            iconColor = numeratorColor,
            title = "Numerador",
            description = "Nos dice cuántas partes hemos tomado o pintado del entero.",
            label = "¡Pintamos 3 rebanadas!",
            labelBgColor = Color(0xFFEFF6FF),
            labelTextColor = Color(0xFF3B82F6)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card Educativo 2: Denominador
        PartCard(
            icon = Icons.Default.ArrowDownward,
            iconColor = denominatorColor,
            title = "Denominador",
            description = "Nos dice en cuántas partes iguales dividimos todo el entero.",
            label = "¡La pizza fue dividida en 8 partes!",
            labelBgColor = Color(0xFFFDF2F8),
            labelTextColor = Color(0xFFEC4899)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección inferior: Neo Teacher
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.neo_teacher),
                contentDescription = "Neo Profesor",
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
                    text = "El numerador siempre va arriba y el denominador abajo.",
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de Navegación
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
fun PartCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    label: String,
    labelBgColor: Color,
    labelTextColor: Color
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
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp).padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = labelBgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = labelTextColor
                    )
                }
            }
        }
    }
}
