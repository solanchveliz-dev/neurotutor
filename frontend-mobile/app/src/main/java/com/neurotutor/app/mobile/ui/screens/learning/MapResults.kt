package com.neurotutor.app.mobile.ui.screens.learning

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neurotutor.app.mobile.R
import com.neurotutor.app.mobile.data.model.common.QuestionResult
import com.neurotutor.app.mobile.ui.screens.diagnostic.DiagnosticEducationCatalog
import com.neurotutor.app.mobile.ui.screens.diagnostic.DiagnosticLesson
import com.neurotutor.app.mobile.ui.screens.diagnostic.DiagnosticLessonStep
import com.neurotutor.app.mobile.ui.screens.diagnostic.DiagnosticResultsViewModel

private val ReviewPurple = Color(0xFF7C3AED)
private val ReviewNavy = Color(0xFF111827)
private val ReviewGreen = Color(0xFF16A34A)
private val ReviewRed = Color(0xFFDC2626)

@Composable
fun MapResultsScreen(
    modifier: Modifier = Modifier,
    studentId: String = "",
    respuestas: List<String> = emptyList(),
    viewModel: DiagnosticResultsViewModel = viewModel(),
    onComenzarPractica: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    LaunchedEffect(Unit) {
        if (state.listaResultados.isEmpty() && respuestas.isNotEmpty()) {
            viewModel.procesarResultadoExamen(studentId, respuestas)
        }
    }

    LaunchedEffect(currentIndex) {
        scrollState.scrollTo(0)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    color = ReviewPurple,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage ?: "No se pudo cargar la revisión.",
                        color = ReviewRed,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.procesarResultadoExamen(studentId, respuestas) },
                        colors = ButtonDefaults.buttonColors(containerColor = ReviewPurple)
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            state.listaResultados.isNotEmpty() -> {
                val safeIndex = currentIndex.coerceIn(0, state.listaResultados.lastIndex)
                val result = state.listaResultados[safeIndex]
                val lesson = DiagnosticEducationCatalog.lesson(result.numeroPregunta)

                if (lesson != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ReviewHeader(onBack = { backDispatcher?.onBackPressed() })
                        Spacer(Modifier.height(20.dp))
                        QuestionIndicator(
                            total = state.listaResultados.size,
                            selectedIndex = safeIndex,
                            onSelect = { selected ->
                                currentIndex = selected
                                viewModel.seleccionarPregunta(state.listaResultados[selected])
                            }
                        )
                        Spacer(Modifier.height(20.dp))

                        QuestionReviewCard(
                            lesson = lesson,
                            result = result
                        )
                        Spacer(Modifier.height(16.dp))

                        val studentIndex = answerIndex(result.respuestaEstudiante)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ReviewAnswerCard(
                                modifier = Modifier.weight(1f),
                                title = "Tu respuesta",
                                letter = studentIndex?.let { ('A' + it).toString() } ?: "—",
                                answer = studentIndex
                                    ?.let { lesson.options.getOrNull(it) }
                                    ?: "Sin respuesta",
                                positive = result.esCorrecta
                            )
                            ReviewAnswerCard(
                                modifier = Modifier.weight(1f),
                                title = "Respuesta correcta",
                                letter = lesson.correctLetter,
                                answer = lesson.options[lesson.correctIndex],
                                positive = true
                            )
                        }
                        Spacer(Modifier.height(16.dp))

                        NeoLessonCard(
                            lesson = lesson,
                            neoRes = if (result.numeroPregunta % 2 == 1) {
                                R.drawable.neo_hint
                            } else {
                                R.drawable.neo_theory
                            }
                        )
                        Spacer(Modifier.height(14.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(1.dp, Color(0xFFF5D98B))
                        ) {
                            Text(
                                text = "⭐ ¡Ánimo! Cada explicación fortalece tu aprendizaje.",
                                color = Color(0xFF3F3A2B),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                        Spacer(Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (safeIndex < state.listaResultados.lastIndex) {
                                    currentIndex = safeIndex + 1
                                    viewModel.seleccionarPregunta(state.listaResultados[safeIndex + 1])
                                } else {
                                    onComenzarPractica()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ReviewPurple,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                text = if (safeIndex < state.listaResultados.lastIndex) {
                                    "Continuar con la siguiente  →"
                                } else {
                                    "Finalizar revisión  ✓"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopStart),
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 3.dp,
            border = BorderStroke(1.dp, Color(0xFFE6E7F2))
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF3157E8)
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 54.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Revisión del diagnóstico",
                color = ReviewNavy,
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Observa en qué preguntas acertaste\ny cuáles reforzaremos.",
                color = Color(0xFF596176),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuestionIndicator(
    total: Int,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(total) { index ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onSelect(index) },
                shape = CircleShape,
                color = if (selected) ReviewPurple else Color.White,
                border = BorderStroke(
                    1.dp,
                    if (selected) ReviewPurple else Color(0xFFDCD8F4)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        color = if (selected) Color.White else ReviewPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionReviewCard(
    lesson: DiagnosticLesson,
    result: QuestionResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (result.esCorrecta) "✓" else "×",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(
                            if (result.esCorrecta) ReviewGreen else ReviewRed,
                            CircleShape
                        )
                        .padding(horizontal = 11.dp, vertical = 5.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pregunta ${lesson.number}",
                        color = ReviewNavy,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "▣  ${lesson.topic}",
                        color = Color(0xFF636B80),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    color = if (result.esCorrecta) Color(0xFFECFDF3) else Color(0xFFFFEEEE),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (result.esCorrecta) "Correcta" else "Por mejorar",
                        color = if (result.esCorrecta) ReviewGreen else ReviewRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (result.esCorrecta) Color(0xFFF5FBF7) else Color(0xFFFFF7F7),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    if (result.esCorrecta) Color(0xFFCBEBD6) else Color(0xFFF4CCCC)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = lesson.prompt,
                        color = ReviewNavy,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    lesson.imageRes?.let { imageRes ->
                        Spacer(Modifier.height(12.dp))
                        Image(
                            painter = painterResource(imageRes),
                            contentDescription = "Imagen de la pregunta ${lesson.number}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 130.dp, max = 210.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    lesson.promptAfterImage?.let { after ->
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = after,
                            color = ReviewNavy,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewAnswerCard(
    modifier: Modifier,
    title: String,
    letter: String,
    answer: String,
    positive: Boolean
) {
    val color = if (positive) ReviewGreen else ReviewRed
    Column(modifier = modifier) {
        Text(
            text = title,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(7.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 86.dp),
            color = if (positive) Color(0xFFECFDF3) else Color(0xFFFFEEEE),
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(1.dp, color.copy(alpha = 0.38f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    color = color,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(Modifier.width(9.dp))
                Text(
                    text = answer,
                    color = ReviewNavy,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NeoLessonCard(
    lesson: DiagnosticLesson,
    neoRes: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8F6FF),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE3DCF9))
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(neoRes),
                    contentDescription = "Neo explica la solución",
                    modifier = Modifier.size(88.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "💡 Neo te explica",
                        color = ReviewPurple,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Vamos a resolverlo como en una pequeña clase.",
                        color = Color(0xFF565E72),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            LessonTextSection(
                number = "1",
                title = "¿Qué nos pide el problema?",
                body = lesson.requestExplanation
            )
            LessonTextSection(
                number = "2",
                title = "¿Qué información conocemos?",
                body = lesson.knownFacts.joinToString(separator = "\n") { "• $it" }
            )
            LessonTextSection(
                number = "3",
                title = "¿Qué operación debemos realizar?",
                body = lesson.operationReason
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "4. Resolvemos paso a paso",
                color = ReviewNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            lesson.steps.forEachIndexed { index, step ->
                LessonStepCard(step)
                if (index < lesson.steps.lastIndex) {
                    Text(
                        text = "↓",
                        color = ReviewPurple,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            LessonTextSection(
                number = "5",
                title = "Conclusión",
                body = lesson.conclusion
            )
        }
    }
}

@Composable
private fun LessonTextSection(
    number: String,
    title: String,
    body: String
) {
    Spacer(Modifier.height(13.dp))
    Text(
        text = "$number. $title",
        color = ReviewNavy,
        fontSize = 15.sp,
        fontWeight = FontWeight.Black
    )
    Spacer(Modifier.height(4.dp))
    Text(
        text = body,
        color = Color(0xFF3F475A),
        fontSize = 13.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun LessonStepCard(step: DiagnosticLessonStep) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFDED8F3))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = step.title,
                color = Color(0xFF5B6478),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = step.operation,
                color = ReviewNavy,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "↓",
                color = ReviewPurple,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = step.result,
                color = ReviewPurple,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun answerIndex(answer: String): Int? {
    val letter = Regex("""\(([A-D])\)""")
        .find(answer)
        ?.groupValues
        ?.getOrNull(1)
        ?.firstOrNull()
        ?: return null
    return letter - 'A'
}
