package com.neurotutor.app.mobile.ui.screens.learning

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

private val AdvancedPurple = Color(0xFF4F16F7)
private val AdvancedNavy = Color(0xFF17117A)
private val AdvancedYellow = Color(0xFFFBB217)

private data class AdvancedTopic(
    @param:DrawableRes val icon: Int,
    val title: String,
    val description: String
)

private val advancedTopics = listOf(
    AdvancedTopic(
        icon = R.drawable.scissors,
        title = "Simplificación de Fracciones",
        description = "Reduce fracciones a su forma más simple."
    ),
    AdvancedTopic(
        icon = R.drawable.balance,
        title = "Comparación de Fracciones",
        description = "Aprende a comparar y ordenar fracciones."
    ),
    AdvancedTopic(
        icon = R.drawable.target_bullseye,
        title = "Fracción de una Cantidad",
        description = "Encuentra una fracción de un número."
    ),
    AdvancedTopic(
        icon = R.drawable.puzzle,
        title = "Operaciones Combinadas",
        description = "Resuelve operaciones con varios pasos."
    ),
    AdvancedTopic(
        icon = R.drawable.detective,
        title = "Problemas de la Vida Real",
        description = "Aplica lo aprendido en situaciones reales."
    )
)

@Composable
fun AdvancedWelcomeTheoryStep(
    onStartLesson: () -> Unit
) {
    val context = LocalContext.current
    val neoModel = remember(context) {
        ImageRequest.Builder(context)
            .data(R.drawable.neo_advanced)
            .crossfade(true)
            .build()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = neoModel,
                contentDescription = "NEO presenta el nivel avanzado",
                modifier = Modifier
                    .weight(0.9f)
                    .height(260.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.width(8.dp))

            Surface(
                modifier = Modifier.weight(1.1f),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 5.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
                    Text(
                        text = "¡Hola de nuevo, matemático experto! 🧠✨",
                        color = AdvancedPurple,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Ya dominaste las operaciones básicas con fracciones.",
                        color = AdvancedNavy,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Ahora es momento de llevar tus habilidades al siguiente nivel con desafíos increíbles.",
                        color = AdvancedNavy,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "¡Prepárate para convertirte en una leyenda de las fracciones! 🏆",
                        color = Color(0xFFFF1770),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            shape = RoundedCornerShape(30.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Qué aprenderemos?",
                    color = AdvancedPurple,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    advancedTopics.forEachIndexed { index, topic ->
                        AdvancedTopicCard(number = index + 1, topic = topic)
                    }
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = onStartLesson,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AdvancedYellow),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Comenzar aventura 🚀",
                        color = Color(0xFF1E293B),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun AdvancedTopicCard(
    number: Int,
    topic: AdvancedTopic
) {
    val context = LocalContext.current
    val iconModel = remember(context, topic.icon) {
        ImageRequest.Builder(context)
            .data(topic.icon)
            .crossfade(true)
            .build()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8FAFF),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E7F5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AdvancedPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(14.dp),
                color = Color.White
            ) {
                AsyncImage(
                    model = iconModel,
                    contentDescription = null,
                    modifier = Modifier.padding(7.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.title,
                        color = AdvancedPurple,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = topic.description,
                    color = AdvancedNavy,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
