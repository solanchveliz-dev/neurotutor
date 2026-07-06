package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.neurotutor.app.mobile.R

private val SimplificationPurple = Color(0xFF4F16F7)
private val SimplificationTitle = Color(0xFF111827)
private val SimplificationNavy = Color(0xFF17117A)
private val SimplificationGold = Color(0xFFF6A800)

@Composable
fun SimplificationFractionsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val neoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_scissors)
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
            text = "1. Simplificación de Fracciones ✂️",
            color = SimplificationTitle,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F6FF)),
            border = BorderStroke(1.dp, Color(0xFFECE7FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Simplificamos una fracción dividiendo el numerador y el denominador por el mismo número hasta obtener la fracción más simple.",
                    color = SimplificationNavy,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                Spacer(Modifier.height(18.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFEDE9FA))
                ) {
                    SimplificationExample()
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "También podemos encontrar el MCD (máximo común divisor) para simplificar más rápido.",
                    color = SimplificationNavy,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "💡 La fracción más simple representa la misma cantidad pero con números más pequeños.",
                            color = SimplificationNavy,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 18.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    AsyncImage(
                        model = neoModel,
                        contentDescription = "NEO explica la simplificación de fracciones",
                        modifier = Modifier
                            .weight(0.8f)
                            .height(150.dp),
                        contentScale = ContentScale.Fit
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
                Text(
                    text = "Anterior",
                    color = SimplificationPurple,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimplificationPurple),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Siguiente",
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
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
private fun SimplificationExample() {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val fractionSize = if (maxWidth < 320.dp) 25.sp else 28.sp
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SimplificationFractionBox(
                numerator = "12",
                denominator = "18",
                fontSize = fractionSize,
                modifier = Modifier.weight(1f)
            )
            DivisionStep("÷ 2")
            SimplificationFractionBox(
                numerator = "6",
                denominator = "9",
                fontSize = fractionSize,
                modifier = Modifier.weight(1f)
            )
            DivisionStep("÷ 3")
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            SimplificationGold,
                            RoundedCornerShape(16.dp)
                        ),
                    color = Color(0xFFFFFBF1),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FractionMathView(
                            numerator = "2",
                            denominator = "3",
                            color = SimplificationNavy,
                            fontSize = fractionSize
                        )
                    }
                }
                Text(
                    text = "✦",
                    color = SimplificationGold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun SimplificationFractionBox(
    numerator: String,
    denominator: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.widthIn(min = 58.dp),
        color = Color(0xFFFBFAFF),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFEAE6F8))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            FractionMathView(
                numerator = numerator,
                denominator = denominator,
                color = SimplificationNavy,
                fontSize = fontSize
            )
        }
    }
}

@Composable
private fun DivisionStep(label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = Color(0xFF8B19B7),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "↓",
            color = SimplificationPurple,
            fontSize = 25.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Black
        )
    }
}
