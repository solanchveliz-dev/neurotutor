package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.neurotutor.app.mobile.R

private val QuantityTitle = Color(0xFF111827)
private val QuantityNavy = Color(0xFF17117A)
private val QuantityPurple = Color(0xFF4F16F7)
private val QuantityBlue = Color(0xFF1478DF)
private val QuantityGreen = Color(0xFF159447)
private val QuantityGold = Color(0xFFF6A800)

@Composable
fun FractionOfQuantityStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val targetModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.target_bullseye)
            .crossfade(true)
            .build()
    }
    val neoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_pointing)
            .crossfade(true)
            .build()
    }
    var showFirstAnswer by rememberSaveable { mutableStateOf(false) }
    var showSecondAnswer by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "3. Fracción de una Cantidad",
                color = QuantityTitle,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(6.dp))
            AsyncImage(
                model = targetModel,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6FF)),
            border = BorderStroke(1.dp, Color(0xFFECE7FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Para encontrar una fracción de una cantidad, dividimos la cantidad entre el denominador y multiplicamos por el numerador.",
                color = QuantityNavy,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 17.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        QuantityProcedureFlow()

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFECE7FF))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = neoModel,
                    contentDescription = "NEO señala la representación de un cuarto de veinte",
                    modifier = Modifier
                        .weight(0.85f)
                        .height(165.dp),
                    contentScale = ContentScale.Fit
                )
                Box(
                    modifier = Modifier.weight(0.7f),
                    contentAlignment = Alignment.Center
                ) {
                    PizzaInQuarters()
                }
                Column(
                    modifier = Modifier.weight(1.25f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuantityBar()
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFFFFBF1),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFF5D67C))
                    ) {
                        Text(
                            text = "¡Una de las 4 partes es 5!",
                            color = QuantityNavy,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF0FAF4),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFDDF3E5))
        ) {
            Text(
                text = "Recuerda: para hallar una fracción de una cantidad primero dividimos entre el denominador y luego multiplicamos por el numerador.",
                color = QuantityNavy,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 17.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFECE7FF))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)) {
                Text(
                    text = "Ejercicio ✏️",
                    color = QuantityPurple,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuantityExercise(
                        modifier = Modifier.weight(1f),
                        label = "a)",
                        numerator = "3",
                        denominator = "5",
                        quantity = "35",
                        answer = "21",
                        showAnswer = showFirstAnswer,
                        onToggleAnswer = { showFirstAnswer = !showFirstAnswer }
                    )
                    QuantityExercise(
                        modifier = Modifier.weight(1f),
                        label = "b)",
                        numerator = "2",
                        denominator = "3",
                        quantity = "18",
                        answer = "12",
                        showAnswer = showSecondAnswer,
                        onToggleAnswer = { showSecondAnswer = !showSecondAnswer }
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                Text("Anterior", color = QuantityPurple, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QuantityPurple),
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
private fun QuantityProcedureFlow() {
    ReadableQuantityStepCard(
        number = "1",
        label = "Paso 1",
        instruction = "Dividimos:",
        operation = "20 ÷ 4 = 5",
        background = Color(0xFFEEF6FF),
        border = Color(0xFFBFD9FF),
        accent = QuantityBlue,
        emphasizedNumber = "4"
    )
    FlowArrow()
    ReadableQuantityStepCard(
        number = "2",
        label = "Paso 2",
        instruction = "Multiplicamos:",
        operation = "5 × 1 = 5",
        background = Color(0xFFFFF1F8),
        border = Color(0xFFF4C4DD),
        accent = Color(0xFFB51D7E),
        emphasizedNumber = "1"
    )
    FlowArrow()
    ReadableQuantityResultCard()
}

@Composable
private fun ReadableQuantityStepCard(
    number: String,
    label: String,
    instruction: String,
    operation: String,
    background: Color,
    border: Color,
    accent: Color,
    emphasizedNumber: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = accent,
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(0.85f)) {
                Text(
                    text = label,
                    color = accent,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = instruction,
                    color = QuantityNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(8.dp))
            HighlightedOperation(
                operation = operation,
                emphasizedNumber = emphasizedNumber,
                accent = accent,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}

@Composable
private fun HighlightedOperation(
    operation: String,
    emphasizedNumber: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val emphasizedStart = operation.indexOf(emphasizedNumber)
    Text(
        text = androidx.compose.ui.text.buildAnnotatedString {
            append(operation)
            if (emphasizedStart >= 0) {
                addStyle(
                    style = androidx.compose.ui.text.SpanStyle(
                        color = accent,
                        fontWeight = FontWeight.Black
                    ),
                    start = emphasizedStart,
                    end = emphasizedStart + emphasizedNumber.length
                )
            }
        },
        color = QuantityNavy,
        fontSize = 22.sp,
        lineHeight = 27.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = modifier
    )
}

@Composable
private fun ReadableQuantityResultCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF0FAF4),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFBCE8CA))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = QuantityGold,
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🏆", fontSize = 22.sp)
                }
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = "Resultado",
                color = QuantityGreen,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(0.85f)
            )
            Spacer(Modifier.width(8.dp))
            Row(
                modifier = Modifier.weight(1.2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TwoColorQuantityFraction("1", "4", 22.sp)
                Text(
                    text = " de 20 = ",
                    color = QuantityNavy,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "5",
                    color = QuantityGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun QuantityExercise(
    modifier: Modifier,
    label: String,
    numerator: String,
    denominator: String,
    quantity: String,
    answer: String,
    showAnswer: Boolean,
    onToggleAnswer: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                color = QuantityNavy,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.width(7.dp))
            TwoColorQuantityFraction(numerator, denominator, 19.sp)
            Text(
                text = " de $quantity",
                color = QuantityGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(9.dp))
        Button(
            onClick = onToggleAnswer,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFF9B79FF)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = if (showAnswer) "Respuesta: $answer" else "Ver respuesta",
                color = QuantityPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuantityQuestionCard(targetModel: Any) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF0FAF4),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFDDF3E5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Cuál es",
                    color = QuantityNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TwoColorQuantityFraction("1", "4")
                    Text(
                        text = " de 20?",
                        color = QuantityNavy,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            AsyncImage(
                model = targetModel,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun QuantityProcedureCard(
    label: String,
    instruction: String,
    operation: String,
    background: Color,
    accent: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = instruction,
                    color = QuantityNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = operation,
                color = accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun QuantityResultCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFBF1),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(2.dp, QuantityGold)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Resultado",
                color = QuantityGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            TwoColorQuantityFraction("1", "4")
            Text(
                text = " de 20 = 5",
                color = QuantityGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun FlowArrow() {
    Text(
        text = "↓",
        color = QuantityPurple,
        fontSize = 25.sp,
        lineHeight = 27.sp,
        fontWeight = FontWeight.Black
    )
}

@Composable
private fun TwoColorQuantityFraction(
    numerator: String,
    denominator: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 22.sp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = numerator,
            color = QuantityBlue,
            fontSize = fontSize,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Black
        )
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(2.dp)
                .background(QuantityNavy)
        )
        Text(
            text = denominator,
            color = QuantityGreen,
            fontSize = fontSize,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun PizzaInQuarters() {
    Canvas(modifier = Modifier.size(108.dp)) {
        val radius = size.minDimension * 0.43f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color(0xFFFFB52E), radius, center)
        drawCircle(Color(0xFFFFE28A), radius * 0.86f, center)
        drawCircle(
            color = Color(0xFFE88B16),
            radius = radius,
            center = center,
            style = Stroke(width = 6f)
        )
        drawLine(
            color = Color.White,
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = 5f
        )
        drawLine(
            color = Color.White,
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y + radius),
            strokeWidth = 5f
        )
        val pepperoni = Color(0xFFE33A2C)
        listOf(
            Offset(center.x - radius * 0.45f, center.y - radius * 0.45f),
            Offset(center.x + radius * 0.45f, center.y - radius * 0.45f),
            Offset(center.x - radius * 0.45f, center.y + radius * 0.45f),
            Offset(center.x + radius * 0.45f, center.y + radius * 0.45f)
        ).forEach { drawCircle(pepperoni, radius * 0.14f, it) }
    }
}

@Composable
private fun QuantityBar() {
    Row(modifier = Modifier.fillMaxWidth()) {
        repeat(4) { index ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                color = if (index == 0) Color(0xFFFFD84D) else Color.White,
                border = BorderStroke(1.dp, Color(0xFF8B74E8))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "5",
                        color = if (index == 0) QuantityGreen else QuantityNavy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
