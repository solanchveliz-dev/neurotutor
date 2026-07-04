package com.neurotutor.app.mobile.ui.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.ui.theme.*

// Definición del modelo de datos
data class TheoryStep(
    val title: String,
    val description: String,
    val alertText: String? = null,
    val imageRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryScreen(
    studentId: String,
    studentName: String,
    moduleId: String,
    level: String, // "B", "I", "A"
    onStartExercise: () -> Unit,
    onBack: () -> Unit,
    viewModel: TheoryViewModel = viewModel()
) {
    var currentStepIndex by rememberSaveable(level) { mutableIntStateOf(0) }

    // Pasos asignados según nivel con tipos explícitos <TheoryStep>
    val theorySteps = remember(level, studentName) {
        when (level) {
            "I" -> listOf<TheoryStep>(
                TheoryStep(
                    title = "🚀 ¡Fracciones -\nNivel Intermedio! 🚀",
                    description = "¡Hola de nuevo, $studentName! 🦸‍♂️\nYa que conoces qué es el numerador y el denominador, es hora de subir de nivel.\nHoy aprenderemos a jugar y operar con las fracciones como todo un profesional. 🧠✨",
                    imageRes = R.drawable.neo_theory
                ),
                TheoryStep(title = "Fracciones equivalentes 👥", description = ""),
                TheoryStep(title = "Suma con igual denominador 🍰", description = ""),
                TheoryStep(title = "Suma con diferente denominador 🦋", description = ""),
                TheoryStep(title = "Resta de fracciones ➖", description = ""),
                TheoryStep(title = "Multiplicación de fracciones 🎯", description = "")
            )
            "A" -> listOf<TheoryStep>(
                TheoryStep(title = "Nivel Avanzado", description = "Próximamente")
            )
            else -> listOf<TheoryStep>( // "B" (Básico) original intacto
                TheoryStep(
                    title = "¡Bienvenidos al Mundo de las Fracciones! 🍕",
                    description = "¡Hola, $studentName! 👋\nHoy vamos a descubrir que las matemáticas pueden ser deliciosas.\n\n¿Alguna vez has tenido que repartir una pizza 🍕 o un chocolate 🍫?\n¡Entonces ya has usado fracciones sin saberlo! 😎",
                    imageRes = R.drawable.neo_theory
                ),
                TheoryStep(
                    title = "¿Qué es una fracción? 🤔",
                    description = "Una fracción es simplemente una forma de decir que dividimos un objeto entero en partes exactamente iguales y tomamos algunas de ellas.",
                    alertText = "💡 ¡Recuerda!\nSi las partes no tienen el mismo tamaño, no es una fracción justa.",
                    imageRes = R.drawable.pizza2_icon
                ),
                TheoryStep(
                    title = "Las partes de una fracción 🏛️",
                    description = "Una fracción se escribe con un número arriba, una línea en medio y un número abajo. ¡Cada uno tiene un súper trabajo!",
                    imageRes = R.drawable.fraction_icon
                ),
                TheoryStep(
                    title = "Fracciones propias ⭐",
                    description = "Las fracciones propias son aquellas donde el numerador es MENOR que el denominador.\n\nEsto significa que tenemos menos de una unidad completa. 🍕",
                    imageRes = R.drawable.icon_star
                ),
                TheoryStep(
                    title = "Fracciones impropias 🚀",
                    description = "Las fracciones impropias son aquellas donde el numerador es MAYOR o IGUAL que el denominador.\n\nEsto significa que tenemos más de una unidad completa (¡necesitamos más de una pizza entera!). 🤯",
                    imageRes = R.drawable.improper_icon
                )
            )
        }
    }

    val totalPages = theorySteps.size
    val currentStep = theorySteps.getOrNull(currentStepIndex) ?: TheoryStep("", "")

    // Función auxiliar para manejar el fin de la teoría y la notificación al backend
    val handleFinishTheory = {
        viewModel.markTheoryAsCompleted(studentId, moduleId)
        onStartExercise()
    }

    val welcomeGradient = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF0EA5E9), Color(0xFF38BDF8), Color(0xFFBAE6FD))
        )
    }
    val standardGradient = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFEDF4FF), Color(0xFFFFFFFF))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (currentStepIndex == 0) welcomeGradient else standardGradient)
    ) {
        if (currentStepIndex == 0 && level != "A") {
            Image(
                painter = painterResource(id = R.drawable.cloud_bottom),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = 40.dp)
                    .graphicsLayer { alpha = 0.3f },
                contentScale = ContentScale.FillWidth
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        val levelTitle = when(level) {
                            "B" -> "Teoría - Básico"
                            "I" -> "Fracciones - Nivel Intermedio"
                            "A" -> "Fracciones - Nivel Avanzado"
                            else -> "Teoría"
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(end = 48.dp)
                        ) {
                            Text(
                                text = levelTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (currentStepIndex == 0) Color.White else Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                val displayedStep = if (level == "A") 0 else currentStepIndex + 1
                                val displayedTotal = if (level == "A") 5 else totalPages
                                LinearProgressIndicator(
                                    progress = {
                                        if (level == "A") 0f
                                        else displayedStep.toFloat() / displayedTotal.toFloat()
                                    },
                                    modifier = Modifier.width(150.dp).height(10.dp).clip(RoundedCornerShape(5.dp)),
                                    color = if (currentStepIndex == 0) Color.White else MoradoActivo,
                                    trackColor = if (currentStepIndex == 0) Color.White.copy(alpha = 0.3f) else Color(0xFFE2E8F0),
                                )
                                Text(
                                    text = "$displayedStep/$displayedTotal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (currentStepIndex == 0) Color.White else TextGray
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (currentStepIndex > 0) currentStepIndex-- else onBack()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = if (currentStepIndex == 0) Color.White else Color(0xFF1E293B)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    modifier = Modifier.statusBarsPadding()
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                when (level) {
                    "B" -> {
                        when (currentStepIndex) {
                            0 -> WelcomeTheoryStep(
                                level = level,
                                title = currentStep.title,
                                description = currentStep.description,
                                imageRes = currentStep.imageRes,
                                onStartLesson = { currentStepIndex++ }
                            )
                            1 -> FractionIntroductionStep(onNext = { currentStepIndex++ }, onBack = { currentStepIndex-- })
                            2 -> FractionPartsStep(onNext = { currentStepIndex++ }, onBack = { currentStepIndex-- })
                            3 -> ProperFractionsStep(onNext = { currentStepIndex++ }, onBack = { currentStepIndex-- })
                            4 -> ImproperFractionsStep(onFinish = handleFinishTheory, onBack = { currentStepIndex-- })
                        }
                    }
                    "I" -> {
                        when (currentStepIndex) {
                            0 -> WelcomeTheoryStep(
                                level = level,
                                title = currentStep.title,
                                description = currentStep.description,
                                imageRes = currentStep.imageRes,
                                onStartLesson = { currentStepIndex++ }
                            )

                            1 -> EquivalentFractionsStep(
                                onNext = { currentStepIndex++ },
                                onBack = { currentStepIndex-- }
                            )

                            2 -> AdditionSameDenominatorStep(
                                onNext = { currentStepIndex++ },
                                onBack = { currentStepIndex-- }
                            )

                            3 -> AdditionDifferentDenominatorStep(
                                onNext = { currentStepIndex++ },
                                onBack = { currentStepIndex-- }
                            )

                            4 -> SubtractionFractionsStep(
                                onNext = { currentStepIndex++ },
                                onBack = { currentStepIndex-- }
                            )

                            5 -> MultiplyFractionsStep(
                                onNext = handleFinishTheory,
                                onBack = { currentStepIndex-- }
                            )
                        }
                    }
                    "A" -> {
                        AdvancedWelcomeTheoryStep(onStartLesson = {})
                    }
                }
            }
        }
    }
}
