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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.neurotutor.app.mobile.R

private val CombinedTitle = Color(0xFF111827)
private val CombinedNavy = Color(0xFF17117A)
private val CombinedPurple = Color(0xFF4F16F7)
private val CombinedPink = Color(0xFFE91E8C)
private val CombinedGreen = Color(0xFF159447)
private val CombinedGold = Color(0xFFF6A800)

@Composable
fun CombinedOperationsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val puzzleModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.puzzle)
            .crossfade(true)
            .build()
    }
    val neoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_varita)
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "4. Operaciones Combinadas",
                color = CombinedTitle,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(6.dp))
            AsyncImage(
                model = puzzleModel,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Cuando hay varias operaciones, seguimos este orden:",
            color = CombinedNavy,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFECE7FF)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OrderItem("( )", "Paréntesis", CombinedPurple)
                OrderArrow()
                OrderItem("× ÷", "Multiplicación\ny División", CombinedPink)
                OrderArrow()
                OrderItem("+ −", "Suma y Resta", CombinedPurple)
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFFFFBF1),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFF5D67C))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ejemplo:",
                    color = CombinedPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(9.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FractionMathView("1", "2", CombinedNavy, 25.sp)
                    MathOperator("+")
                    Text("(", color = CombinedPink, fontSize = 35.sp)
                    FractionMathView("2", "3", CombinedNavy, 25.sp)
                    MathOperator("×")
                    FractionMathView("3", "4", CombinedNavy, 25.sp)
                    Text(")", color = CombinedPink, fontSize = 35.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.35f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CombinedStepCard(
                    title = "Paso 1: Paréntesis\n(multiplicamos primero)",
                    background = Color(0xFFEEF6FF),
                    border = Color(0xFFBFD9FF)
                ) {
                    FirstStepEquation()
                }

                Text(
                    text = "↓",
                    color = CombinedPurple,
                    fontSize = 24.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Black
                )

                CombinedStepCard(
                    title = "Paso 2: Luego sumamos",
                    background = Color(0xFFFFF1F8),
                    border = Color(0xFFF5C6DE)
                ) {
                    SecondStepEquation()
                }
            }

            Spacer(Modifier.width(8.dp))

            AsyncImage(
                model = neoModel,
                contentDescription = "NEO explica el orden de las operaciones",
                modifier = Modifier
                    .weight(0.75f)
                    .height(270.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(14.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFFFFBF1),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFF5D67C))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⭐", fontSize = 26.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Recuerda: sigue siempre el orden de las operaciones.",
                    color = CombinedNavy,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold
                )
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
                Text("Anterior", color = CombinedPurple, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CombinedPurple),
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
private fun OrderItem(symbol: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(symbol, color = color, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Text(
            text = label,
            color = CombinedNavy,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OrderArrow() {
    Text("→", color = CombinedNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
}

@Composable
private fun CombinedStepCard(
    title: String,
    background: Color,
    border: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = CombinedPurple,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun FirstStepEquation() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FractionMathView("2", "3", CombinedNavy, 18.sp)
        SmallOperator("×")
        FractionMathView("3", "4", CombinedNavy, 18.sp)
        SmallOperator("=")
        FractionMathView("6", "12", CombinedNavy, 18.sp)
        SmallOperator("=")
        FractionMathView("1", "2", CombinedGreen, 18.sp)
    }
}

@Composable
private fun SecondStepEquation() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FractionMathView("1", "2", CombinedNavy, 18.sp)
        SmallOperator("+")
        FractionMathView("1", "2", CombinedNavy, 18.sp)
        SmallOperator("=")
        FractionMathView("2", "2", CombinedNavy, 18.sp)
        SmallOperator("=")
        Text(
            text = "1 ⭐",
            color = CombinedGreen,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun MathOperator(symbol: String) {
    Text(
        text = symbol,
        color = CombinedPurple,
        fontSize = 23.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun SmallOperator(symbol: String) {
    Text(
        text = symbol,
        color = CombinedNavy,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
    )
}
