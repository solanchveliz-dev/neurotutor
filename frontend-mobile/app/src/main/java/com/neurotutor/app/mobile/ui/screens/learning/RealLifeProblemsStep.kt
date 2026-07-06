package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

private val RealLifeTitle = Color(0xFF111827)
private val RealLifeNavy = Color(0xFF17117A)
private val RealLifePurple = Color(0xFF4F16F7)
private val RealLifeBlue = Color(0xFF1478DF)
private val RealLifeGreen = Color(0xFF159447)
private val RealLifePink = Color(0xFFB90A74)

@Composable
fun RealLifeProblemsStep(
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dailyModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.daily)
            .crossfade(true)
            .build()
    }
    val neoPointingModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_pointing)
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
            text = "5. Problemas de la Vida Real 🕵️",
            color = RealLifeTitle,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Usamos las fracciones para resolver situaciones de la vida diaria.",
            color = RealLifeNavy,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        MainProblemCard(dailyModel)
        Spacer(Modifier.height(12.dp))

        GuidedResolutionCard(
            number = "1",
            title = "Paso 1",
            description = "2 partes equivalen a 24 niños.",
            operation = buildAnnotatedString {
                withStyle(SpanStyle(color = RealLifeBlue, fontWeight = FontWeight.Black)) {
                    append("2")
                }
                append(" partes = 24")
            },
            background = Color(0xFFF1F7FF),
            border = Color(0xFFBFD9FF),
            accent = RealLifeBlue
        )
        ResolutionArrow()
        GuidedResolutionCard(
            number = "2",
            title = "Paso 2",
            description = "1 parte = 24 ÷ 2 = 12 niños.",
            operation = buildAnnotatedString {
                append("24 ÷ 2 = ")
                withStyle(SpanStyle(color = RealLifePink, fontWeight = FontWeight.Black)) {
                    append("12")
                }
            },
            background = Color(0xFFFFF2F8),
            border = Color(0xFFF4C4DD),
            accent = RealLifePink
        )
        ResolutionArrow()
        GuidedResolutionCard(
            number = "3",
            title = "Paso 3",
            description = "Total de estudiantes =\n12 × 3 = 36 niños.",
            operation = buildAnnotatedString {
                append("12 × 3 = ")
                withStyle(SpanStyle(color = RealLifePurple, fontWeight = FontWeight.Black)) {
                    append("36")
                }
            },
            background = Color(0xFFF6F2FF),
            border = Color(0xFFD8C8FF),
            accent = RealLifePurple
        )

        Spacer(Modifier.height(14.dp))
        FinalAnswerCard(neoPointingModel)
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
                Text("Anterior", color = RealLifePurple, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RealLifePurple),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "¡Soy una leyenda! 🏆",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MainProblemCard(dailyModel: Any) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF0FAF4),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFBCE8CA))
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, top = 14.dp, end = 8.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = RealLifeGreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "?",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Problema",
                    color = Color(0xFF117838),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "En un salón, ",
                            color = RealLifeNavy,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        ColoredProblemFraction()
                        Text(
                            text = " de los",
                            color = RealLifeNavy,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "estudiantes son 24 niños.",
                        color = RealLifeNavy,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "¿Cuántos estudiantes hay en total?",
                        color = Color(0xFF117838),
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                AsyncImage(
                    model = dailyModel,
                    contentDescription = "Estudiantes en un salón de clases",
                    modifier = Modifier
                        .weight(0.9f)
                        .height(160.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun GuidedResolutionCard(
    number: String,
    title: String,
    description: String,
    operation: androidx.compose.ui.text.AnnotatedString,
    background: Color,
    border: Color,
    accent: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = accent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1.15f)) {
                Text(
                    text = title,
                    color = accent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    color = RealLifeNavy,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                modifier = Modifier.weight(0.85f),
                color = Color.White.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, border)
            ) {
                Text(
                    text = operation,
                    color = RealLifeNavy,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ResolutionArrow() {
    Text(
        text = "↓",
        color = RealLifePurple,
        fontSize = 27.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    )
}

@Composable
private fun FinalAnswerCard(neoPointingModel: Any) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF0FAF4),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFBCE8CA))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = neoPointingModel,
                contentDescription = "Neo señalando la respuesta correcta",
                modifier = Modifier
                    .weight(0.9f)
                    .height(145.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(0.95f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆 Respuesta",
                    color = RealLifeGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "En el salón hay",
                    color = RealLifeNavy,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "36",
                    color = RealLifeGreen,
                    fontSize = 43.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "estudiantes.",
                    color = RealLifeNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Surface(
                modifier = Modifier
                    .width(1.dp)
                    .height(112.dp),
                color = Color(0xFFBCE8CA)
            ) {}

            Column(
                modifier = Modifier
                    .weight(0.85f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = "¡Excelente!",
                    color = RealLifeNavy,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Has resuelto el problema usando fracciones.",
                    color = RealLifeNavy,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "🎉",
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ColoredProblemFraction() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "3",
            color = RealLifeGreen,
            fontSize = 20.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Black
        )
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(2.dp)
        ) {
            HorizontalDivider(thickness = 2.dp, color = RealLifeNavy)
        }
        Text(
            text = "2",
            color = RealLifeBlue,
            fontSize = 20.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Black
        )
    }
}
