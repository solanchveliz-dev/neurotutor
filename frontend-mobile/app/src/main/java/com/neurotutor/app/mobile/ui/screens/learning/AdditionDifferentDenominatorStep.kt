package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun AdditionDifferentDenominatorStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val actionButtonColor = Color(0xFF6366F1)
    val titleColor = Color(0xFF1E293B)
    val textColor = Color(0xFF475569)
    val neuroBlue = Color(0xFF0EA5E9)
    val neuroPurple = Color(0xFF8B5CF6)
    val neuroGreen = Color(0xFF22C55E)
    val cardBg = Color(0xFFF8FAFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Título Principal
        Text(
            text = "3. Suma de Fracciones\n(Diferente Denominador) 🦋",
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Black,
            color = titleColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card de Introducción con Neo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.neo_addition),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Cuando las fracciones tienen diferente denominador, primero las transformamos para que tengan el mismo número abajo. Luego podemos ")
                        withStyle(style = SpanStyle(color = neuroPurple, fontWeight = FontWeight.Bold)) {
                            append("unirlas")
                        }
                        append(" fácilmente.")
                    },
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- PASOS EDUCATIVOS ---

        // Paso 1: Operación inicial
        StepItem(number = "1", title = "Observamos la operación:") {
            Surface(
                modifier = Modifier.padding(top = 8.dp),
                color = cardBg,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FractionMathView("1", "2", titleColor)
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    FractionMathView("1", "4", titleColor)
                }
            }
        }

        // Paso 2: Denominador común
        StepItem(number = "2", title = "Buscamos un denominador común:") {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(color = neuroGreen, fontWeight = FontWeight.Black, fontSize = 20.sp)) {
                                    append("2 × 4 = 8")
                                }
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "El denominador común es 8.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = neuroGreen,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.icon_hint),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Paso 3: Transformar 1/2
        StepItem(number = "3", title = "Convertimos la primera fracción:") {
            TransformationRow("1", "2", "4", "8", neuroBlue, "1 × 4 = 4\n2 × 4 = 8")
        }

        // Paso 4: Transformar 1/4
        StepItem(number = "4", title = "Convertimos la segunda fracción:") {
            TransformationRow("1", "4", "2", "8", Color(0xFFEC4899), "1 × 2 = 2\n4 × 2 = 8")
        }

        // Paso 5: Sumar con barras
        StepItem(number = "5", title = "Sumamos las fracciones:") {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FractionMathView("4", "8", Color(0xFFD97706))
                        Text("+", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                        FractionMathView("2", "8", Color(0xFFD97706))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(horizontal = 8.dp), tint = Color(0xFFD97706))
                        FractionMathView("6", "8", Color(0xFFD97706), fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Representación visual con barras Compose
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FractionBar(4, 8, neuroBlue, Modifier.weight(1f))
                        Text("+", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        FractionBar(2, 8, neuroPurple, Modifier.weight(1f))
                        Text("=", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        FractionBar(6, 8, neuroBlue, Modifier.weight(1f))
                    }
                }
            }
        }

        // Paso 6: Simplificación
        StepItem(number = "6", title = "Simplificamos la fracción:", isLast = true) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    FractionMathView("6", "8", neuroPurple)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(horizontal = 8.dp))
                    FractionMathView("3", "4", neuroPurple)
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("¡Excelente!\nHas ")
                                withStyle(style = SpanStyle(color = neuroPurple, fontWeight = FontWeight.Bold)) {
                                    append("unido")
                                }
                                append("\nambas fracciones.")
                            },
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Botones de Navegación
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF64748B))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Anterior", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = actionButtonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Siguiente", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun StepItem(
    number: String,
    title: String,
    isLast: Boolean = false,
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(32.dp).background(Color(0xFF6366F1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(100.dp).background(Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            content()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TransformationRow(n1: String, d1: String, n2: String, d2: String, color: Color, calculation: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            FractionMathView(n1, d1, color)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(horizontal = 8.dp), tint = Color.LightGray)
            FractionMathView(n2, d2, color)
        }
        Surface(
            color = color.copy(alpha = 0.05f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = calculation,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun FractionBar(
    filledParts: Int,
    totalParts: Int,
    filledColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(20.dp)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
    ) {
        repeat(totalParts) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (index < filledParts) filledColor else Color.White)
                    .border(0.5.dp, Color(0xFFE2E8F0))
            )
        }
    }
}

@Composable
fun FractionMathView(
    numerator: String,
    denominator: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit = 22.sp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = numerator, fontSize = fontSize, fontWeight = FontWeight.Bold, color = color)
        Box(modifier = Modifier.width(24.dp).height(2.dp).background(color))
        Text(text = denominator, fontSize = fontSize, fontWeight = FontWeight.Bold, color = color)
    }
}