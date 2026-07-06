package com.neurotutor.app.mobile.ui.screens.diagnostic

import androidx.annotation.DrawableRes
import com.neurotutor.app.mobile.R

internal data class DiagnosticLessonStep(
    val title: String,
    val operation: String,
    val result: String
)

internal data class DiagnosticLesson(
    val number: Int,
    val topic: String,
    val prompt: String,
    val promptAfterImage: String? = null,
    @get:DrawableRes val imageRes: Int? = null,
    val options: List<String>,
    val correctIndex: Int,
    val requestExplanation: String,
    val knownFacts: List<String>,
    val operationReason: String,
    val steps: List<DiagnosticLessonStep>,
    val conclusion: String
) {
    val correctLetter: String
        get() = ('A' + correctIndex).toString()
}

internal object DiagnosticEducationCatalog {
    val lessons = listOf(
        DiagnosticLesson(
            number = 1,
            topic = "Problemas aditivos",
            prompt = "Como parte de una campaña de reciclaje, los estudiantes de secundaria de una escuela recolectaron 1826 botellas de plástico. Ellos recolectaron 478 botellas de plástico menos que los estudiantes de primaria. ¿Cuántas botellas de plástico recolectaron los estudiantes de primaria?",
            options = listOf(
                "478 botellas de plástico.",
                "1348 botellas de plástico.",
                "2294 botellas de plástico.",
                "2304 botellas de plástico."
            ),
            correctIndex = 3,
            requestExplanation = "Debemos descubrir cuántas botellas recolectaron los estudiantes de primaria.",
            knownFacts = listOf(
                "Secundaria recolectó 1826 botellas.",
                "Secundaria recolectó 478 botellas menos que primaria."
            ),
            operationReason = "Primaria recolectó más que secundaria. Para recuperar la cantidad mayor, sumamos a 1826 la diferencia de 478.",
            steps = listOf(
                DiagnosticLessonStep(
                    "Paso 1 · Representamos la relación",
                    "Primaria = Secundaria + diferencia",
                    "Primaria = 1826 + 478"
                ),
                DiagnosticLessonStep(
                    "Paso 2 · Sumamos",
                    "1826 + 478",
                    "2304"
                ),
                DiagnosticLessonStep(
                    "Paso 3 · Respondemos",
                    "Cantidad de primaria",
                    "2304 botellas"
                )
            ),
            conclusion = "Como primaria recolectó 478 botellas más que secundaria, debíamos sumar ambas cantidades."
        ),
        DiagnosticLesson(
            number = 2,
            topic = "Multiplicación",
            prompt = "Mariana recibió 8 cajas con latas de pintura para su ferretería. En cada caja, hay media docena de latas de pintura. Ella venderá cada lata a S/20. ¿Cuánto dinero recibirá Mariana por la venta de todas las latas de pintura?",
            options = listOf("S/34", "S/160", "S/960", "S/1920"),
            correctIndex = 2,
            requestExplanation = "Debemos calcular cuánto dinero recibirá Mariana al vender todas las latas.",
            knownFacts = listOf(
                "Hay 8 cajas.",
                "Media docena equivale a 6 latas por caja.",
                "Cada lata cuesta S/20."
            ),
            operationReason = "Primero multiplicamos para hallar cuántas latas hay en total. Después multiplicamos ese total por el precio de cada lata.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Hallamos las latas", "8 × 6", "48 latas"),
                DiagnosticLessonStep("Paso 2 · Hallamos el dinero", "48 × S/20", "S/960"),
                DiagnosticLessonStep("Paso 3 · Respondemos", "Dinero total", "S/960")
            ),
            conclusion = "Mariana venderá 48 latas y recibirá S/960 por todas ellas."
        ),
        DiagnosticLesson(
            number = 3,
            topic = "División y redondeo",
            prompt = "Sergio tiene una piscigranja y necesita comprar 1980 kg de alimento balanceado para peces. El tipo de alimento que utiliza para sus peces solo se vende en bolsas de 50 kg. ¿Cuántas bolsas de alimento balanceado debe comprar Sergio?",
            options = listOf("98 bolsas.", "50 bolsas.", "40 bolsas.", "39 bolsas."),
            correctIndex = 2,
            requestExplanation = "Debemos averiguar cuántas bolsas completas necesita para reunir al menos 1980 kg.",
            knownFacts = listOf(
                "Sergio necesita 1980 kg.",
                "Cada bolsa contiene 50 kg.",
                "Solo puede comprar bolsas completas."
            ),
            operationReason = "Dividimos la cantidad total entre lo que contiene cada bolsa. Como el resultado no es entero, redondeamos hacia arriba para que no falte alimento.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Dividimos", "1980 ÷ 50", "39,6 bolsas"),
                DiagnosticLessonStep("Paso 2 · Redondeamos hacia arriba", "39,6 → 40", "40 bolsas completas"),
                DiagnosticLessonStep("Paso 3 · Comprobamos", "40 × 50", "2000 kg")
            ),
            conclusion = "Con 39 bolsas no alcanza; Sergio debe comprar 40 bolsas."
        ),
        DiagnosticLesson(
            number = 4,
            topic = "Fracciones",
            prompt = "En la bandeja, hay frutas. Algunas son naranjas y otras son manzanas. Observa la imagen.",
            promptAfterImage = "¿Qué parte del total de frutas de la bandeja son naranjas?",
            imageRes = R.drawable.imagen_pregunta4_frutas,
            options = listOf("9/5", "9/14", "5/14", "1/14"),
            correctIndex = 1,
            requestExplanation = "Debemos representar con una fracción cuántas frutas son naranjas.",
            knownFacts = listOf(
                "En la imagen hay 9 naranjas.",
                "También hay 5 manzanas."
            ),
            operationReason = "Una fracción compara las partes elegidas con el total. El numerador será la cantidad de naranjas y el denominador será el total de frutas.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Contamos el total", "9 + 5", "14 frutas"),
                DiagnosticLessonStep("Paso 2 · Formamos la fracción", "naranjas / total", "9/14"),
                DiagnosticLessonStep("Paso 3 · Respondemos", "Parte que son naranjas", "9/14")
            ),
            conclusion = "De las 14 frutas, 9 son naranjas; por eso la fracción es 9/14."
        ),
        DiagnosticLesson(
            number = 5,
            topic = "Adición de decimales",
            prompt = "Abigail tiene dos piezas de soga de diferente longitud sobre una mesa. Observa.",
            promptAfterImage = "Ella usó completamente las dos piezas de soga para amarrar unos troncos de su corral de ovejas. ¿Qué longitud de soga usó Abigail en total?",
            imageRes = R.drawable.imagen_pregunta5_sogas,
            options = listOf("1,5 m", "1,8 m", "4,2 m", "15 m"),
            correctIndex = 2,
            requestExplanation = "Debemos hallar la longitud total de las dos sogas que Abigail utilizó.",
            knownFacts = listOf(
                "La primera soga mide 3 m.",
                "La segunda soga mide 1,2 m."
            ),
            operationReason = "Como utilizó las dos sogas completas, sumamos sus longitudes. Escribimos 3 como 3,0 para alinear la coma decimal.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Igualamos decimales", "3 m", "3,0 m"),
                DiagnosticLessonStep("Paso 2 · Sumamos", "3,0 + 1,2", "4,2 m"),
                DiagnosticLessonStep("Paso 3 · Respondemos", "Longitud total", "4,2 m")
            ),
            conclusion = "Al juntar una soga de 3 m y otra de 1,2 m, Abigail utilizó 4,2 m."
        ),
        DiagnosticLesson(
            number = 6,
            topic = "Igualdades y operaciones",
            prompt = "¿Qué número debe escribirse dentro del recuadro para que se cumpla la igualdad?",
            imageRes = R.drawable.imagen_pregunta6_ecuacion,
            options = listOf("13", "16", "22", "30"),
            correctIndex = 1,
            requestExplanation = "Debemos encontrar el número que hace verdadera la igualdad 2 × □ + 6 = 38.",
            knownFacts = listOf(
                "El número desconocido se multiplica por 2.",
                "Después se suman 6 y el resultado debe ser 38."
            ),
            operationReason = "Deshacemos las operaciones en orden inverso: primero restamos 6 y luego dividimos entre 2.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Quitamos el 6", "38 − 6", "32"),
                DiagnosticLessonStep("Paso 2 · Dividimos entre 2", "32 ÷ 2", "16"),
                DiagnosticLessonStep("Paso 3 · Comprobamos", "2 × 16 + 6", "38")
            ),
            conclusion = "El número es 16 porque al multiplicarlo por 2 y sumar 6 obtenemos 38."
        ),
        DiagnosticLesson(
            number = 7,
            topic = "Perímetro",
            prompt = "El siguiente gráfico representa el terreno que utilizará Corina para construir un restaurante.",
            promptAfterImage = "Corina colocará un cerco en el contorno de todo el terreno. ¿Cuál es la longitud del cerco que colocará Corina?",
            imageRes = R.drawable.imagen_pregunta7_terreno,
            options = listOf("24 m", "27 m", "72 m", "180 m"),
            correctIndex = 2,
            requestExplanation = "Debemos calcular cuánto mide todo el borde del terreno.",
            knownFacts = listOf(
                "5 cuadros verticales equivalen a 15 m.",
                "4 cuadros horizontales equivalen a 12 m.",
                "Cada lado de un cuadro mide 3 m."
            ),
            operationReason = "El cerco rodea el terreno, así que calculamos el perímetro sumando todos los segmentos del contorno.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Hallamos la escala", "15 ÷ 5 = 3 y 12 ÷ 4 = 3", "Cada cuadro mide 3 m"),
                DiagnosticLessonStep("Paso 2 · Sumamos horizontales", "15 + 6 + 9 + 12", "42 m"),
                DiagnosticLessonStep("Paso 3 · Sumamos verticales", "6 + 6 + 3 + 15", "30 m"),
                DiagnosticLessonStep("Paso 4 · Hallamos el perímetro", "42 + 30", "72 m")
            ),
            conclusion = "Al sumar todos los lados exteriores, el cerco debe medir 72 m."
        ),
        DiagnosticLesson(
            number = 8,
            topic = "Proporcionalidad",
            prompt = "Juan vende tres paquetes de mantequilla por S/5. Él elaboró la siguiente tabla para calcular la cantidad de dinero que tendría que cobrar según la cantidad de paquetes que venda.",
            promptAfterImage = "Juan vendió una docena y media de paquetes de mantequilla. ¿Cuánto dinero cobrará por esa venta?",
            imageRes = R.drawable.imagen_pregunta8_tabla,
            options = listOf("S/60", "S/30", "S/20", "S/18"),
            correctIndex = 1,
            requestExplanation = "Debemos calcular cuánto cuestan una docena y media de paquetes.",
            knownFacts = listOf(
                "Una docena y media son 18 paquetes.",
                "Cada grupo de 3 paquetes cuesta S/5."
            ),
            operationReason = "Primero averiguamos cuántos grupos de 3 hay en 18. Luego multiplicamos los grupos por S/5.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Convertimos la cantidad", "12 + 6", "18 paquetes"),
                DiagnosticLessonStep("Paso 2 · Formamos grupos", "18 ÷ 3", "6 grupos"),
                DiagnosticLessonStep("Paso 3 · Calculamos el precio", "6 × S/5", "S/30")
            ),
            conclusion = "Dieciocho paquetes forman seis grupos de tres; Juan debe cobrar S/30."
        ),
        DiagnosticLesson(
            number = 9,
            topic = "División con residuo",
            prompt = "Eloísa preparó 56 bizcochos. Luego, los colocó en 4 cajas con igual cantidad de bizcochos en cada una. Al terminar de guardarlos, le sobraron 8 bizcochos. ¿Cuántos bizcochos colocó en cada caja?",
            options = listOf("12 bizcochos", "14 bizcochos", "16 bizcochos", "22 bizcochos"),
            correctIndex = 0,
            requestExplanation = "Debemos averiguar cuántos bizcochos quedaron dentro de cada una de las cuatro cajas.",
            knownFacts = listOf(
                "Eloísa preparó 56 bizcochos.",
                "Quedaron 8 sin guardar.",
                "Los demás se repartieron por igual en 4 cajas."
            ),
            operationReason = "Primero restamos los que sobraron. Después dividimos los bizcochos guardados entre las cuatro cajas.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Quitamos los sobrantes", "56 − 8", "48 bizcochos guardados"),
                DiagnosticLessonStep("Paso 2 · Repartimos", "48 ÷ 4", "12 bizcochos por caja"),
                DiagnosticLessonStep("Paso 3 · Comprobamos", "12 × 4 + 8", "56 bizcochos")
            ),
            conclusion = "Cada caja contiene 12 bizcochos y los 8 restantes quedaron fuera."
        ),
        DiagnosticLesson(
            number = 10,
            topic = "Promedio y reparto equitativo",
            prompt = "Cuatro amigos quieren tomar un vaso de jugo de naranja cada uno, pero tienen diferentes cantidades de dinero. Mario tiene S/5, Eliana tiene S/7, José tiene S/8 y Lucía tiene S/4. Todos están de acuerdo en prestarse dinero entre ellos para que cada uno pueda comprar un vaso de jugo del mismo precio. Observa el cartel.",
            promptAfterImage = "¿Cuál es el mayor precio que podrán pagar los cuatro amigos por cada vaso de jugo de naranja?",
            imageRes = R.drawable.imagen_pregunta10_jugonaranja,
            options = listOf("S/3", "S/4", "S/5", "S/6"),
            correctIndex = 3,
            requestExplanation = "Debemos encontrar el precio máximo que cada amigo puede pagar después de repartir todo el dinero por igual.",
            knownFacts = listOf(
                "Los amigos tienen S/5, S/7, S/8 y S/4.",
                "Son 4 amigos y cada uno comprará un vaso.",
                "En el cartel hay vasos de S/4, S/5, S/6 y S/8."
            ),
            operationReason = "Sumamos todo el dinero y lo dividimos entre los cuatro amigos. Así sabemos cuánto puede gastar cada uno.",
            steps = listOf(
                DiagnosticLessonStep("Paso 1 · Sumamos el dinero", "5 + 7 + 8 + 4", "S/24"),
                DiagnosticLessonStep("Paso 2 · Repartimos por igual", "24 ÷ 4", "S/6 por amigo"),
                DiagnosticLessonStep("Paso 3 · Elegimos del cartel", "Mayor precio que no supera S/6", "S/6")
            ),
            conclusion = "Al compartir el dinero, cada amigo dispone de S/6; ese es el vaso más caro que todos pueden comprar."
        )
    )

    fun lesson(number: Int): DiagnosticLesson? = lessons.getOrNull(number - 1)
}
