package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.BorderStroke
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

private val ComparisonTitle = Color(0xFF111827)
private val ComparisonNavy = Color(0xFF17117A)
private val ComparisonPurple = Color(0xFF4F16F7)
private val ComparisonBlue = Color(0xFF1478DF)
private val ComparisonGreen = Color(0xFF159447)

@Composable
fun ComparisonFractionsStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val balanceModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.balance_pizza)
            .crossfade(true)
            .build()
    }
    val neoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_check_2)
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
            text = "2. Comparación de Fracciones ⚖️",
            color = ComparisonTitle,
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
            Text(
                text = "Para comparar fracciones podemos usar el mismo denominador o convertirlas a decimales.",
                color = ComparisonNavy,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 17.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ComparisonCard(
                modifier = Modifier.weight(1f),
                title = "Mismo denominador",
                leftNumerator = "3",
                leftDenominator = "4",
                symbol = ">",
                rightNumerator = "2",
                rightDenominator = "4",
                reason = "Porque 3 > 2",
                background = Color(0xFFF0FAF4)
            )
            ComparisonCard(
                modifier = Modifier.weight(1f),
                title = "Diferente denominador",
                leftNumerator = "2",
                leftDenominator = "3",
                symbol = "<",
                rightNumerator = "5",
                rightDenominator = "6",
                reason = "Porque 0.66 < 0.83",
                background = Color(0xFFEEF6FF)
            )
        }

        Spacer(Modifier.height(14.dp))

        AsyncImage(
            model = balanceModel,
            contentDescription = "Balanza con pizzas que representa la comparación de fracciones",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier.weight(1.2f),
                color = Color(0xFFF0FAF4),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFDDF3E5))
            ) {
                Text(
                    text = "Recuerda: la fracción mayor es la que representa más partes del entero.",
                    color = ComparisonNavy,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 18.dp)
                )
            }

            Spacer(Modifier.width(8.dp))

            AsyncImage(
                model = neoModel,
                contentDescription = "NEO recuerda cómo identificar la fracción mayor",
                modifier = Modifier
                    .weight(0.8f)
                    .height(145.dp),
                contentScale = ContentScale.Fit
            )
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
                    color = ComparisonPurple,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ComparisonPurple),
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
private fun ComparisonCard(
    modifier: Modifier,
    title: String,
    leftNumerator: String,
    leftDenominator: String,
    symbol: String,
    rightNumerator: String,
    rightDenominator: String,
    reason: String,
    background: Color
) {
    Surface(
        modifier = modifier,
        color = background,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, background.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = ComparisonNavy,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.height(36.dp)
            )

            Spacer(Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ComparisonFraction(leftNumerator, leftDenominator)
                Text(
                    text = symbol,
                    color = ComparisonPurple,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Black
                )
                ComparisonFraction(rightNumerator, rightDenominator)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = reason,
                color = ComparisonGreen,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ComparisonFraction(
    numerator: String,
    denominator: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = numerator,
            color = ComparisonBlue,
            fontSize = 24.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Black
        )
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(2.dp)
                .background(ComparisonNavy)
        )
        Text(
            text = denominator,
            color = ComparisonGreen,
            fontSize = 24.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Black
        )
    }
}
