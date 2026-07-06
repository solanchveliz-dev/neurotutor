package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.neurotutor.app.mobile.R

private val MultiplyPurple = Color(0xFF4F16F7)
private val MultiplyNavy = Color(0xFF17117A)
private val MultiplyBlue = Color(0xFF1478DF)
private val MultiplyGreen = Color(0xFF159447)
private val MultiplyGold = Color(0xFFFFC928)

@Composable
fun MultiplyFractionsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val neoMultiplyModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_multiply)
            .crossfade(true)
            .build()
    }
    val neoCheckModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_check)
            .crossfade(true)
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(18.dp))

        Text(
            text = "5. Multiplicación\nde Fracciones 🎯",
            color = MultiplyNavy,
            fontSize = 25.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF)),
            border = BorderStroke(1.dp, Color(0xFFECE6FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = neoMultiplyModel,
                    contentDescription = "NEO explica cómo multiplicar fracciones",
                    modifier = Modifier
                        .weight(0.95f)
                        .height(174.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.weight(1.05f),
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 17.dp)) {
                        Text(
                            text = "¡La multiplicación es fácil!",
                            color = MultiplyNavy,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = buildAnnotatedString {
                                append("Multiplicamos el número de ")
                                withStyle(SpanStyle(color = MultiplyBlue, fontWeight = FontWeight.Bold)) {
                                    append("arriba")
                                }
                                append("\npor el número de ")
                                withStyle(SpanStyle(color = MultiplyBlue, fontWeight = FontWeight.Bold)) {
                                    append("arriba")
                                }
                                append(".\n\nY el número de ")
                                withStyle(SpanStyle(color = MultiplyGreen, fontWeight = FontWeight.Bold)) {
                                    append("abajo")
                                }
                                append("\npor el número de ")
                                withStyle(SpanStyle(color = MultiplyGreen, fontWeight = FontWeight.Bold)) {
                                    append("abajo")
                                }
                                append(".")
                            },
                            color = MultiplyNavy,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        MultiplyStepCard(number = 1, title = AnnotatedString("Observamos\nla operación:")) {
            EquationPanel(Color(0xFFF1EEFF)) {
                FractionMathView("2", "3", MultiplyNavy, 25.sp)
                MathSymbol("×")
                FractionMathView("4", "5", MultiplyNavy, 25.sp)
            }
        }

        MultiplyStepCard(
            number = 2,
            title = buildAnnotatedString {
                append("Multiplicamos\nlos ")
                withStyle(SpanStyle(color = MultiplyBlue, fontWeight = FontWeight.Black)) {
                    append("numeradores:")
                }
            },
            numberColor = MultiplyBlue
        ) {
            OperationRow(
                operation = "2 × 4 = 8",
                explanation = "2 por 4 es igual a 8",
                accent = MultiplyBlue
            )
        }

        MultiplyStepCard(
            number = 3,
            title = buildAnnotatedString {
                append("Multiplicamos\nlos ")
                withStyle(SpanStyle(color = MultiplyGreen, fontWeight = FontWeight.Black)) {
                    append("denominadores:")
                }
            },
            numberColor = MultiplyGreen
        ) {
            OperationRow(
                operation = "3 × 5 = 15",
                explanation = "3 por 5 es igual a 15",
                accent = MultiplyGreen
            )
        }

        MultiplyStepCard(
            number = 4,
            title = AnnotatedString("Construimos\nla nueva fracción:")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    modifier = Modifier.widthIn(min = 62.dp),
                    color = Color(0xFFEAF7F0),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 15.dp, vertical = 11.dp)) {
                        TwoColorFraction("8", "15")
                    }
                }
                Text(
                    text = "→",
                    color = MultiplyPurple,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )
                Surface(
                    modifier = Modifier
                        .widthIn(min = 72.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp))
                        .border(2.dp, MultiplyGold, RoundedCornerShape(14.dp)),
                    color = Color(0xFFFFF5B8),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 13.dp)) {
                        FractionMathView("8", "15", MultiplyNavy, 25.sp)
                    }
                }
            }
        }

        MultiplyStepCard(
            number = 5,
            title = AnnotatedString("Resultado final:"),
            alwaysStackContent = true
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF4EFFF),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = neoCheckModel,
                        contentDescription = "NEO valida el resultado ocho quinceavos",
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier
                            .weight(1.15f)
                            .widthIn(min = 136.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(13.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "¡Excelente!",
                                color = MultiplyPurple,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                            Text(
                                text = "La fracción resultante es:",
                                color = MultiplyNavy,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                            Spacer(Modifier.height(7.dp))
                            Box(
                                modifier = Modifier.widthIn(min = 62.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FractionMathView("8", "15", MultiplyNavy, 23.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

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
                Text("Anterior", color = MultiplyPurple, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MultiplyPurple),
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
private fun MultiplyStepCard(
    number: Int,
    title: AnnotatedString,
    numberColor: Color = MultiplyPurple,
    alwaysStackContent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE9E7F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val stackContent = alwaysStackContent || maxWidth < 400.dp
            if (stackContent) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
                    StepHeading(number, title, numberColor)
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = content
                    )
                }
            } else {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepNumber(number, numberColor)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = title,
                        color = MultiplyNavy,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(96.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = content
                    )
                }
            }
        }
    }
}

@Composable
private fun StepHeading(
    number: Int,
    title: AnnotatedString,
    numberColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StepNumber(number, numberColor)
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            color = MultiplyNavy,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StepNumber(number: Int, numberColor: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(numberColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun OperationRow(
    operation: String,
    explanation: String,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                color = accent.copy(alpha = 0.08f),
                shape = RoundedCornerShape(13.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 9.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FractionMathView("2", "3", MultiplyNavy, 19.sp)
                    MathSymbol("×")
                    FractionMathView("4", "5", MultiplyNavy, 19.sp)
                }
            }
            Text(
                text = "→",
                color = MultiplyPurple,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(min = 90.dp),
                color = accent.copy(alpha = 0.1f),
                shape = RoundedCornerShape(13.dp)
            ) {
                Text(
                    text = operation,
                    color = accent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 17.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = explanation,
            color = MultiplyNavy,
            fontSize = 11.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EquationPanel(
    background: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
private fun TwoColorFraction(
    numerator: String,
    denominator: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = numerator,
            color = MultiplyBlue,
            fontSize = 25.sp,
            fontWeight = FontWeight.Black
        )
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(2.dp)
                .background(MultiplyNavy)
        )
        Text(
            text = denominator,
            color = MultiplyGreen,
            fontSize = 25.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun MathSymbol(symbol: String) {
    Text(
        text = symbol,
        color = MultiplyNavy,
        fontSize = 21.sp,
        fontWeight = FontWeight.Black
    )
}
