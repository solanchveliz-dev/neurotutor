package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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

private val SubtractionPurple = Color(0xFF4F16F7)
private val SubtractionNavy = Color(0xFF17117A)
private val SubtractionBlue = Color(0xFF1687E8)
private val SubtractionPink = Color(0xFFE91E8C)
private val SubtractionGreen = Color(0xFF159447)
private val SubtractionOrange = Color(0xFFF59E0B)
private val SubtractionViolet = Color(0xFF6D3FD6)

@Composable
fun SubtractionFractionsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val neoPainter = painterResource(R.drawable.neo_theory_intermediate_4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        Text(
            text = "4. Resta de Fracciones\n(Diferente Denominador)",
            color = SubtractionNavy,
            fontSize = 24.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F7FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = neoPainter,
                    contentDescription = "NEO explica la resta de fracciones",
                    modifier = Modifier.size(108.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Cuando las fracciones tienen diferente número abajo, primero encontramos un denominador común para poder restarlas.",
                    color = SubtractionNavy,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        SubtractionStepCard(number = 1, title = "Observamos la operación:") {
            EquationSurface(background = Color(0xFFF2EFFF)) {
                FractionMathView("1", "2", SubtractionNavy, 25.sp)
                MathSymbol("−", SubtractionNavy)
                FractionMathView("1", "4", SubtractionNavy, 25.sp)
            }
        }

        SubtractionStepCard(number = 2, title = "Buscamos denominador común.") {
            Text(
                text = "Multiplicamos los denominadores:",
                color = SubtractionNavy,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEAF8EF),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "2 × 4 = 8",
                    color = SubtractionGreen,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF0FAF3),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Ahora ambas fracciones deberán expresarse en ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Black)) { append("octavos") }
                        append(".")
                    },
                    color = SubtractionGreen,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }

        SubtractionStepCard(number = 3, title = "Convertimos la primera fracción:") {
            ConversionEquation(
                numerator = "1",
                denominator = "2",
                resultNumerator = "4",
                resultDenominator = "8",
                calculation = "1 × 4 = 4\n2 × 4 = 8",
                color = SubtractionBlue
            )
        }

        SubtractionStepCard(number = 4, title = "Convertimos la segunda fracción:") {
            ConversionEquation(
                numerator = "1",
                denominator = "4",
                resultNumerator = "2",
                resultDenominator = "8",
                calculation = "1 × 2 = 2\n4 × 2 = 8",
                color = SubtractionPink
            )
        }

        SubtractionStepCard(
            number = 5,
            title = "Ahora restamos (ya tienen el mismo denominador):"
        ) {
            EquationSurface(background = Color(0xFFFFF7E3)) {
                FractionMathView("4", "8", SubtractionOrange, 22.sp)
                MathSymbol("−", SubtractionNavy)
                FractionMathView("2", "8", SubtractionOrange, 22.sp)
                MathSymbol("=", SubtractionNavy)
                FractionMathView("2", "8", SubtractionOrange, 22.sp)
            }
        }

        SubtractionStepCard(number = 6, title = "Simplificamos la fracción:") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FractionBox("2", "8", SubtractionViolet, Color(0xFFF3EEFF))
                MathSymbol("↓", SubtractionViolet)
                FractionBox("1", "4", SubtractionViolet, Color(0xFFF3EEFF))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Image(
                        painter = neoPainter,
                        contentDescription = "NEO celebra la respuesta correcta",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "¡Respuesta\ncorrecta!",
                        color = SubtractionPurple,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0EDFF)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFF8B74E8)
                )
                Spacer(Modifier.width(8.dp))
                Text("Anterior", color = SubtractionPurple, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SubtractionPurple),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Siguiente", color = Color.White, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun SubtractionStepCard(
    number: Int,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9E7F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SubtractionPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SubtractionNavy,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(9.dp))
                content()
            }
        }
    }
}

@Composable
private fun ConversionEquation(
    numerator: String,
    denominator: String,
    resultNumerator: String,
    resultDenominator: String,
    calculation: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FractionBox(numerator, denominator, color, color.copy(alpha = 0.08f))
        MathSymbol("→", SubtractionNavy)
        Surface(color = color.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp)) {
            Text(
                text = calculation,
                color = color,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
        MathSymbol("→", SubtractionNavy)
        FractionBox(resultNumerator, resultDenominator, color, color.copy(alpha = 0.08f))
    }
}

@Composable
private fun EquationSurface(
    background: Color,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            content = content
        )
    }
}

@Composable
private fun FractionBox(
    numerator: String,
    denominator: String,
    color: Color,
    background: Color
) {
    Surface(color = background, shape = RoundedCornerShape(12.dp)) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
            FractionMathView(numerator, denominator, color, 21.sp)
        }
    }
}

@Composable
private fun MathSymbol(symbol: String, color: Color) {
    Text(
        text = symbol,
        color = color,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(horizontal = 3.dp)
    )
}
