package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neurotutor.app.mobile.R

data class Question(
    val textBeforeImage: String,
    val imageRes: Int? = null,
    val textAfterImage: String? = null,
    val options: List<String>,
    val correctAnswer: Int
)

@Composable
fun DiagnosticScreen(
    studentId: String,
    onNavigateToAssignment: (String, List<String>) -> Unit
) {
    val questions = remember {
        listOf(
            Question("Como parte de una campaña de reciclaje, los estudiantes de secundaria de una escuela recolectaron 1826 botellas de plástico. Ellos recolectaron 478 botellas de plástico menos que los estudiantes de primaria. ¿Cuántas botellas de plástico recolectaron los estudiantes de primaria?", options = listOf("478 botellas de plástico.", "1348 botellas de plástico.", "2294 botellas de plástico.", "2304 botellas de plástico."), correctAnswer = 2),
            Question("Mariana recibió 8 cajas con latas de pintura para su ferretería. En cada caja, hay media docena de latas de pintura. Ella venderá cada lata a S/20. ¿Cuánto dinero recibirá Mariana por la venta de todas las latas de pintura?", options = listOf("S/34", "S/160", "S/960", "S/1920"), correctAnswer = 2),
            Question("Sergio tiene una piscigranja y necesita comprar 1980 kg de alimento balanceado para peces. El tipo de alimento que utiliza para sus peces solo se vende en bolsas de 50 kg. ¿Cuántas bolsas de alimento balanceado debe comprar Sergio?", options = listOf("98 bolsas.", "50 bolsas.", "40 bolsas.", "39 bolsas."), correctAnswer = 2),
            Question("En la bandeja, hay frutas. Algunas son naranjas y antes son manzanas. Observa la imagen.", imageRes = R.drawable.imagen_pregunta4_frutas, textAfterImage = "¿Qué parte del total de frutas de la bandeja son naranjas?", options = listOf("9/5", "9/14", "5/14", "1/14"), correctAnswer = 1),
            Question("Abigail tiene dos piezas de soga de diferente longitud sobre una mesa. Observa.", imageRes = R.drawable.imagen_pregunta5_sogas, textAfterImage = "Ella usó completamente las dos piezas de soga para amarrar unos troncos de su corral de ovejas. ¿Qué longitud de soga usó Abigail en total?", options = listOf("1,5 m", "1,8 m", "4,2 m", "15 m"), correctAnswer = 2),
            Question("¿Qué número debe escribirse dentro del recuadro para que se cumpla la igualdad?", imageRes = R.drawable.imagen_pregunta6_ecuacion, options = listOf("13", "16", "22", "30"), correctAnswer = 1),
            Question("El siguiente gráfico representa el terreno que utilizará Corina para construir un restaurante.", imageRes = R.drawable.imagen_pregunta7_terreno, textAfterImage = "Corina colocará un cerco en el contorno de todo el terreno. ¿Cuál es la longitud del cerco que colocará Corina?", options = listOf("24 m", "27 m", "72 m", "180 m"), correctAnswer = 2),
            Question("Juan vende tres paquetes de mantequilla por S/5. Él elaboró la siguiente tabla para calcular la cantidad de dinero que tendría que cobrar según la cantidad de paquetes que venda.", imageRes = R.drawable.imagen_pregunta8_tabla, textAfterImage = "Juan vendió una docena y media de paquetes de mantequilla. ¿Cuánto dinero cobrará por esa venta?", options = listOf("S/60", "S/30", "S/20", "S/18"), correctAnswer = 1),
            Question("Eloísa preparó 56 bizcochos. Luego, los colocó en 4 cajas con igual cantidad de bizcochos en cada una. Al terminar de guardarlos, le sobraron 8 bizcochos. ¿Cuántos bizcochos colocó en cada caja?", options = listOf("12 bizcochos", "14 bizcochos", "16 bizcochos", "22 bizcochos"), correctAnswer = 2),
            Question("Cuatro amigos quieren tomar un vaso de jugo de naranja cada uno, pero tienen diferentes cantidades de dinero. Mario tiene S/5, Eliana tiene S/7, José tiene S/8 y Lucía tiene S/4. Todos están de acuerdo en prestarse dinero entre ellos...", imageRes = R.drawable.imagen_pregunta10_jugonaranja, textAfterImage = "¿Cuál es el mayor precio que podrán pagar los cuatro amigos por cada vaso de jugo de naranja?", options = listOf("S/3", "S/4", "S/5", "S/6"), correctAnswer = 3)
        )
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val scrollState = rememberScrollState()

    val calificaciones = remember { mutableStateListOf<String>() }
    val labels = listOf("a", "b", "c", "d")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentQuestion = questions.getOrNull(currentQuestionIndex)

        if (currentQuestion != null) {
            Text(text = "Evaluación de Diagnóstico", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), modifier = Modifier.padding(top = 8.dp))
            Text(text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 20.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = currentQuestion.textBeforeImage, fontSize = 17.sp, lineHeight = 24.sp, color = Color(0xFF334155))

                    if (currentQuestion.imageRes != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Image(painter = painterResource(id = currentQuestion.imageRes), contentDescription = null, modifier = Modifier.padding(8.dp), contentScale = ContentScale.Fit)
                        }
                    }

                    if (currentQuestion.textAfterImage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = currentQuestion.textAfterImage, fontSize = 17.sp, lineHeight = 24.sp, color = Color(0xFF334155))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    currentQuestion.options.forEachIndexed { index, option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(selected = (selectedOption == index), onClick = { selectedOption = index })
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (selectedOption == index), onClick = { selectedOption = index })
                            Text(text = "${labels[index]})  $option", fontSize = 16.sp, color = if (selectedOption == index) MaterialTheme.colorScheme.primary else Color(0xFF1E293B), fontWeight = if (selectedOption == index) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.padding(start = 10.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    selectedOption?.let { idx ->
                        calificaciones.add(labels[idx].uppercase())
                    }

                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        selectedOption = null
                    } else {
                        onNavigateToAssignment(studentId, calificaciones.toList())
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
                enabled = selectedOption != null
            ) {
                Text(
                    text = if (currentQuestionIndex < questions.size - 1) "Siguiente" else "Finalizar Examen",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
